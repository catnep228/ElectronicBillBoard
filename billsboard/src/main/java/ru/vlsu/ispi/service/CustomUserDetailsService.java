package ru.vlsu.ispi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.UserDAO;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userDAO.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "Пользователь не найден: " + email
            );
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(
                        user.getRoles().stream()
                                .map(role -> role.getName())
                                .toArray(String[]::new)
                )
                .disabled(!user.getEnabled())
                .build();
    }
}
