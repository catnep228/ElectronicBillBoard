package ru.vlsu.ispi.beans;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (nullable = false, unique = false, length = 50)
	private String name;
	
	@Column (nullable = false, unique = false, length = 50)
	private String second_name;
	
	@Column (nullable = false, unique = true, length = 50)
	private String email;
	
	@Column (nullable = false, length = 255)
	private String passwordHash;
	
	private boolean enabled = true;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	
	
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setSecondName(String second_name) {
		this.second_name = second_name;
	}
	public String getSecondName() {
		return second_name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public Set<Role> getRoles(){
		return roles;
	}
}
