package ru.vlsu.ispi.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.vlsu.ispi.beans.Role;
import ru.vlsu.ispi.dao.RoleDAO;

public class RoleDAOImpl implements RoleDAO {

    private DBImpl impl;

    public void setImpl(DBImpl impl) {
        this.impl = impl;
    }

    private static final String SELECT_ROLE_BY_NAME =
            "SELECT id, name FROM roles WHERE name=?";

    private static final String INSERT_ROLE =
            "INSERT INTO roles (name) VALUES (?)";

    private static final String UPDATE_ROLE =
            "UPDATE roles SET name=? WHERE id=?";

    private static final String DELETE_USER_ROLES =
            "DELETE FROM user_role WHERE role_id=?";

    private static final String DELETE_ROLE =
            "DELETE FROM roles WHERE id=?";

    private static final String SELECT_ROLE =
            "SELECT id, name FROM roles";


    @Override
    public void create(Role role) {

        try (Connection con = impl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_ROLE,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, role.getName());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    role.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании роли", e);
        }
    }


    @Override
    public void update(Role role) {

        try (Connection con = impl.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_ROLE)) {

            ps.setString(1, role.getName());
            ps.setLong(2, role.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при обновлении роли id=" + role.getId(), e);
        }
    }


    @Override
    public void delete(Role role) {

        if (role == null || role.getId() == null) {
            return;
        }

        try (Connection con = impl.getConnection()) {

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(DELETE_USER_ROLES)) {
                    ps.setLong(1, role.getId());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(DELETE_ROLE)) {
                    ps.setLong(1, role.getId());
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
                    "Ошибка при удалении роли id=" + role.getId(), e);
        }
    }


    @Override
    public Role getById(long id) {

        try (Connection con = impl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT_ROLE + " WHERE id=?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении роли id=" + id, e);
        }
    }


    @Override
    public List<Role> getAll() {

        List<Role> result = new ArrayList<>();

        try (Connection con = impl.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT_ROLE + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении списка ролей", e);
        }

        return result;
    }


    @Override
    public Role getByName(String name) {

        try (Connection con = impl.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ROLE_BY_NAME)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении роли name=" + name, e);
        }
    }


    private Role mapRow(ResultSet rs) throws SQLException {

        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        return role;
    }
}