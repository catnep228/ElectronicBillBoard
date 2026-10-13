package ru.vlsu.ispi.dao;

import java.util.List;

import ru.vlsu.ispi.beans.User;


public interface UserDAO {
	public void create(User user);
	public void update(User user);
	public void delete(User user);
	public User getById(long id);
	public User getByEmail(String email);
	public List<User> getAll();
}
