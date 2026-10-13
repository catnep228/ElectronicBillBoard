package ru.vlsu.ispi.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import ru.vlsu.ispi.beans.Role;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.UserDAO;


public class UserDAOImpl implements UserDAO {

    private static final String INSERT_USER =
            "INSERT INTO users " +
            "(name, second_name, email, passwordHash, enabled, createdAt) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_USER =
            "UPDATE users SET " +
            "name=?, second_name=?, email=?, passwordHash=?, enabled=? " +
            "WHERE id=?";

    private static final String DELETE_USER_ROLES =
            "DELETE FROM user_role WHERE user_id=?";

    private static final String DELETE_USER =
            "DELETE FROM users WHERE id=?";

    private static final String SELECT_USER =
            "SELECT id, name, second_name, email, passwordHash, " +
            "enabled, createdAt " +
            "FROM users";

    private static final String SELECT_ROLES =
            "SELECT r.id, r.name " +
            "FROM roles r " +
            "JOIN user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id=?";

    private static final String SELECT_USER_BY_EMAIL =
            "SELECT id, name, second_name, email, passwordHash, " +
            "enabled, createdAt " +
            "FROM users " +
            "WHERE email=?";

    @Override
    public User getByEmail(String email) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(SELECT_USER_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                User user = mapRow(rs);
                user.setRoles(loadRoles(con, user.getId()));

                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при поиске пользователя по email", e);
        }
    }

    @Override
    public void create(User user) {

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps = con.prepareStatement(
                        INSERT_USER,
                        Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, user.getName());
                    ps.setString(2, user.getSecondName());
                    ps.setString(3, user.getEmail());
                    ps.setString(4, user.getPasswordHash());
                    ps.setBoolean(5, user.getEnabled());
                    ps.setTimestamp(
                            6,
                            Timestamp.valueOf(user.getCreatedAt())
                    );

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (keys.next()) {
                            user.setId(keys.getLong(1));
                        }
                    }
                }

                saveRoles(con, user);

                con.commit();

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при создании пользователя", e);
        }
    }

    @Override
    public void update(User user) {

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(UPDATE_USER)) {

                    ps.setString(1, user.getName());
                    ps.setString(2, user.getSecondName());
                    ps.setString(3, user.getEmail());
                    ps.setString(4, user.getPasswordHash());
                    ps.setBoolean(5, user.getEnabled());
                    ps.setLong(6, user.getId());

                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(DELETE_USER_ROLES)) {

                    ps.setLong(1, user.getId());
                    ps.executeUpdate();
                }

                saveRoles(con, user);

                con.commit();

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при обновлении пользователя id="
                            + user.getId(), e);
        }
    }

    @Override
    public void delete(User user) {

        if (user == null || user.getId() == null) {
            return;
        }

        try (Connection con = DBImpl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(DELETE_USER_ROLES)) {

                    ps.setLong(1, user.getId());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(DELETE_USER)) {

                    ps.setLong(1, user.getId());
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
                    "Ошибка при удалении пользователя id="
                            + user.getId(), e);
        }
    }

    @Override
    public User getById(long id) {

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(
                             SELECT_USER + " WHERE id=?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                User user = mapRow(rs);
                user.setRoles(loadRoles(con, id));

                return user;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при получении пользователя id="
                            + id, e);
        }
    }

    @Override
    public List<User> getAll() {

        List<User> result = new ArrayList<>();

        try (Connection con = DBImpl.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(
                             SELECT_USER + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User user = mapRow(rs);
                user.setRoles(loadRoles(con, user.getId()));
                result.add(user);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Ошибка при получении списка пользователей", e);
        }

        return result;
    }

    private User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setSecondName(rs.getString("second_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("passwordHash"));
        user.setEnabled(rs.getBoolean("enabled"));

        Timestamp createdAt = rs.getTimestamp("createdAt");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }

    private Set<Role> loadRoles(
            Connection con,
            long userId) throws SQLException {

        Set<Role> roles = new HashSet<>();

        try (PreparedStatement ps =
                     con.prepareStatement(SELECT_ROLES)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    roles.add(role);
                }
            }
        }

        return roles;
    }

    private void saveRoles(
            Connection con,
            User user) throws SQLException {

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO user_role " +
                "(user_id, role_id) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (Role role : user.getRoles()) {

                if (role == null || role.getId() == null) {
                    continue;
                }

                ps.setLong(1, user.getId());
                ps.setLong(2, role.getId());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}