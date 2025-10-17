package com.simplespring.example.entity;

/**
 * 用户实体类
 * 演示基本的业务实体，包含用户的基本信息
 */
public class User {
  private Long id;
  private String username;
  private String email;
  private String password;
  private boolean active;

  public User() {
  }

  public User(Long id, String username, String email) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.active = true;
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.active = true;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", active=" + active +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    User user = (User) o;

    if (id != null ? !id.equals(user.id) : user.id != null)
      return false;
    return username != null ? username.equals(user.username) : user.username == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    return result;
  }
}
