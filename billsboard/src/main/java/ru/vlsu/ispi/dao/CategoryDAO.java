package ru.vlsu.ispi.dao;

import java.util.List;

import ru.vlsu.ispi.beans.Category;

public interface CategoryDAO {

    void create(Category category);

    void update(Category category);

    void delete(Category category);

    Category getById(long id);

    List<Category> getAll();
}

