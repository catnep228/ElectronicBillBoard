package ru.vlsu.ispi.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.dao.CategoryDAO;

public class CategoryDAOImpl implements CategoryDAO {



    private static final String INSERT_CATEGORY =
            "INSERT INTO categories (name, parent_id) VALUES (?, ?)";

    private static final String UPDATE_CATEGORY =
            "UPDATE categories SET name=?, parent_id=? WHERE id=?";

    private static final String SELECT_CATEGORY =
            "SELECT id, name, parent_id FROM categories";

    @Override
    public void create(Category category) {

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    INSERT_CATEGORY,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, category.getName());

                if (category.getParent() != null
                        && category.getParent().getId() != null) {

                    ps.setLong(2, category.getParent().getId());

                } else {
                    ps.setNull(2, Types.BIGINT);
                }

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        category.setId(keys.getLong(1));
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

            throw new RuntimeException(
                    "Ошибка при создании категории", e);
        }
    }

    @Override
    public void update(Category category) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_CATEGORY)) {

            ps.setString(1, category.getName());

            if (category.getParent() != null
                    && category.getParent().getId() != null) {

                ps.setLong(2, category.getParent().getId());

            } else {
                ps.setNull(2, Types.BIGINT);
            }

            ps.setLong(3, category.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при обновлении категории id="
                    + category.getId(), e);
        }
    }

    @Override
    public void delete(Category category) {

        if (category == null || category.getId() == null) {
            return;
        }

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM categories WHERE id=?")) {

                ps.setLong(1, category.getId());
                ps.executeUpdate();

                con.commit();

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при удалении категории id="
                    + category.getId(), e);
        }
    }

    @Override
    public Category getById(long id) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(SELECT_CATEGORY + " WHERE id=?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                return mapRow(rs);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при получении категории id=" + id, e);
        }
    }

    @Override
    public List<Category> getAll() {

        List<Category> result = new ArrayList<>();

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT_CATEGORY + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при получении списка категорий", e);
        }

        return result;
    }

    private Category mapRow(ResultSet rs) throws SQLException {

        Category category = new Category();

        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));

        long parentId = rs.getLong("parent_id");

        if (!rs.wasNull()) {

            Category parent = new Category();
            parent.setId(parentId);

            category.setParent(parent);
        }

        return category;
    }
}
