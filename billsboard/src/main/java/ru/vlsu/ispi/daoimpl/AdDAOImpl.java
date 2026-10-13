package ru.vlsu.ispi.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.AdPhoto;
import ru.vlsu.ispi.beans.AdStatus;
import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.beans.User;

public class AdDAOImpl implements AdDAO {


    private static final String SELECT_ADS_BY_SELLER =
            "SELECT a.id, a.title, a.description, a.price, a.status, " +
            "a.created_at, a.seller_id, a.category_id, " +
            "u.name AS seller_name, u.second_name AS seller_second_name, " +
            "c.name AS category_name " +
            "FROM ads a " +
            "JOIN users u ON u.id = a.seller_id " +
            "JOIN categories c ON c.id = a.category_id " +
            "WHERE a.seller_id=? " +
            "ORDER BY a.created_at DESC";
    private static final String INSERT_AD =
            "INSERT INTO ads " +
            "(title, description, price, status, created_at, seller_id, category_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PHOTO =
            "INSERT INTO ad_photos " +
            "(ad_id, file_name, position) " +
            "VALUES (?, ?, ?)";

    private static final String UPDATE_AD =
            "UPDATE ads SET " +
            "title=?, description=?, price=?, status=?, category_id=? " +
            "WHERE id=?";

    private static final String SELECT_AD =
            "SELECT id, title, description, price, status, " +
            "created_at, seller_id, category_id " +
            "FROM ads";

    private static final String SELECT_PHOTOS =
            "SELECT id, file_name, position " +
            "FROM ad_photos " +
            "WHERE ad_id=? " +
            "ORDER BY position";


    @Override
    public List<Ad> getBySellerId(long sellerId) {

        List<Ad> result = new ArrayList<>();

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ADS_BY_SELLER)) {

            ps.setLong(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Ad ad = new Ad();

                    ad.setId(rs.getLong("id"));
                    ad.setTitle(rs.getString("title"));
                    ad.setDescription(rs.getString("description"));
                    ad.setPrice(rs.getBigDecimal("price"));
                    ad.setStatus(
                            AdStatus.valueOf(rs.getString("status"))
                    );

                    Timestamp createdAt = rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        ad.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    User seller = new User();
                    seller.setId(rs.getLong("seller_id"));
                    seller.setName(rs.getString("seller_name"));
                    seller.setSecondName(
                            rs.getString("seller_second_name")
                    );

                    ad.setSeller(seller);

                    Category category = new Category();
                    category.setId(rs.getLong("category_id"));
                    category.setName(rs.getString("category_name"));

                    ad.setCategory(category);

                    ad.setPhotos(loadPhotos(con, ad));

                    result.add(ad);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении объявлений пользователя id="
                            + sellerId,
                    e
            );
        }

        return result;
    }


    @Override
    public void create(Ad ad) {

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     INSERT_AD,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, ad.getTitle());
                    ps.setString(2, ad.getDescription());
                    ps.setBigDecimal(3, ad.getPrice());
                    ps.setString(4, ad.getStatus().name());
                    ps.setTimestamp(5, Timestamp.valueOf(ad.getCreatedAt()));
                    ps.setLong(6, ad.getSeller().getId());
                    ps.setLong(7, ad.getCategory().getId());

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            ad.setId(keys.getLong(1));
                        }
                    }
                }

                if (ad.getPhotos() != null && !ad.getPhotos().isEmpty()) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(INSERT_PHOTO)) {

                        for (AdPhoto photo : ad.getPhotos()) {
                            ps.setLong(1, ad.getId());
                            ps.setString(2, photo.getFileName());
                            ps.setInt(3, photo.getPosition());
                            ps.addBatch();
                        }

                        ps.executeBatch();
                    }
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании объявления", e);
        }
    }


    @Override
    public void update(Ad ad) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_AD)) {

            ps.setString(1, ad.getTitle());
            ps.setString(2, ad.getDescription());
            ps.setBigDecimal(3, ad.getPrice());
            ps.setString(4, ad.getStatus().name());
            ps.setLong(5, ad.getCategory().getId());
            ps.setLong(6, ad.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при обновлении объявления id=" + ad.getId(), e);
        }
    }


    @Override
    public void delete(long id) {

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     "DELETE FROM ad_photos WHERE ad_id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     "DELETE FROM ads WHERE id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при удалении объявления id=" + id, e);
        }
    }


    @Override
    public Ad getById(long id) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT_AD + " WHERE id=?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Ad ad = mapRow(rs);
                ad.setPhotos(loadPhotos(con, ad));

                return ad;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при чтении объявления id=" + id, e);
        }
    }

    @Override
    public void addPhoto(AdPhoto photo) {

        String sql =
                "INSERT INTO ad_photos (ad_id, file_name, position) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, photo.getAd().getId());
            ps.setString(2, photo.getFileName());
            ps.setInt(3, photo.getPosition());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при добавлении фотографии объявления",
                    e
            );
        }
    }

    @Override
    public void deletePhoto(long photoId) {

        String sql =
                "DELETE FROM ad_photos WHERE id=?";

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, photoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при удалении фотографии id=" + photoId,
                    e
            );
        }
    }
    @Override
    public List<Ad> getAll() {
        List<Ad> result = new ArrayList<>();

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT_AD + " ORDER BY created_at DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ad ad = mapRow(rs);

                // ВАЖНО: загружаем фотографии объявления
                ad.setPhotos(loadPhotos(con, ad));

                result.add(ad);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при чтении списка объявлений", e);
        }

        return result;
    }


    private Ad mapRow(ResultSet rs) throws SQLException {

        Ad ad = new Ad();

        ad.setId(rs.getLong("id"));
        ad.setTitle(rs.getString("title"));
        ad.setDescription(rs.getString("description"));
        ad.setPrice(rs.getBigDecimal("price"));
        ad.setStatus(AdStatus.valueOf(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ad.setCreatedAt(createdAt.toLocalDateTime());
        }

        User seller = new User();
        seller.setId(rs.getLong("seller_id"));
        ad.setSeller(seller);

        Category category = new Category();
        category.setId(rs.getLong("category_id"));
        ad.setCategory(category);

        return ad;
    }


    private List<AdPhoto> loadPhotos(
            Connection con,
            Ad ad) throws SQLException {

        List<AdPhoto> photos = new ArrayList<>();

        try (PreparedStatement ps =
                     con.prepareStatement(SELECT_PHOTOS)) {

            ps.setLong(1, ad.getId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    AdPhoto photo = new AdPhoto();
                    photo.setId(rs.getLong("id"));
                    photo.setFileName(rs.getString("file_name"));
                    photo.setPosition(rs.getInt("position"));
                    photo.setAd(ad);
                    photos.add(photo);
                }
            }
        }

        return photos;
    }
}