package ru.vlsu.ispi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.vlsu.ispi.beans.Role;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.RoleDAO;
import ru.vlsu.ispi.dao.UserDAO;
import ru.vlsu.ispi.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDAO userDAO,
                           RoleDAO roleDAO,
                           PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(UserDTO dto) {

        System.out.println("=== register() вызван для email: " + dto.getEmail());

        if (userDAO.getByEmail(dto.getEmail()) != null) {
            System.out.println("=== email уже занят");
            throw new IllegalArgumentException(
                    "Пользователь с таким email уже существует");
        }

        Role userRole = roleDAO.getByName("USER");
        System.out.println("=== роль USER: " + userRole);

        if (userRole == null) {
            throw new IllegalStateException(
                    "Роль USER не найдена в базе данных");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        System.out.println("=== сохраняем пользователя: " + user.getEmail());
        userDAO.create(user);
        System.out.println("=== пользователь сохранён с id: " + user.getId());
    }
    
}