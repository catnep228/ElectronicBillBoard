package ru.vlsu.ispi.dao;

import java.util.List;

import ru.vlsu.ispi.beans.Role;

public interface RoleDAO {

    void create(Role role);

    void update(Role role);

    void delete(Role role);
    Role getByName(String name);
    Role getById(long id);

    List<Role> getAll();
}

