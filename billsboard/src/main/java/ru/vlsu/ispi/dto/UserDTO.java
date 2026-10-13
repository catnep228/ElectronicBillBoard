package ru.vlsu.ispi.dto;

import ru.vlsu.ispi.dto.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    private Long id;
    private String name;
    private String secondName;
    private String email;
    private String password;
    private String passwordConfirm;
    private String passwordHash;
    private boolean enabled = true;
    private LocalDateTime createdAt;
    private Set<RoleDTO> roles = new HashSet<>();


    public UserDTO() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean getEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<RoleDTO> getRoles() { return roles; }
    public void setRoles(Set<RoleDTO> roles) { this.roles = roles; }
}