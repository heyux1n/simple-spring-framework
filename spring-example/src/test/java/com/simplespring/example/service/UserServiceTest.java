package com.simplespring.example.service;

import com.simplespring.example.entity.User;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 用户服务单元测试
 * 验证用户服务的业务逻辑正确性
 */
public class UserServiceTest {

  private UserService userService;

  @Before
  public void setUp() {
    userService = new UserServiceImpl();
  }

  @Test
  public void testCreateUser() {
    // 测试创建用户
    User user = new User("testuser", "test@example.com", "password123");
    User createdUser = userService.createUser(user);

    assertNotNull("创建的用户不应为空", createdUser);
    assertNotNull("用户ID应该被自动生成", createdUser.getId());
    assertEquals("用户名应该匹配", "testuser", createdUser.getUsername());
    assertEquals("邮箱应该匹配", "test@example.com", createdUser.getEmail());
    assertTrue("新用户应该是激活状态", createdUser.isActive());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserWithNullUser() {
    // 测试创建空用户应该抛出异常
    userService.createUser(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserWithDuplicateUsername() {
    // 测试创建重复用户名应该抛出异常
    User user1 = new User("duplicate", "user1@example.com", "password123");
    User user2 = new User("duplicate", "user2@example.com", "password456");

    userService.createUser(user1);
    userService.createUser(user2); // 应该抛出异常
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserWithDuplicateEmail() {
    // 测试创建重复邮箱应该抛出异常
    User user1 = new User("user1", "duplicate@example.com", "password123");
    User user2 = new User("user2", "duplicate@example.com", "password456");

    userService.createUser(user1);
    userService.createUser(user2); // 应该抛出异常
  }

  @Test
  public void testFindById() {
    // 测试根据ID查找用户
    User user = new User("findbyid", "findbyid@example.com", "password123");
    User createdUser = userService.createUser(user);

    User foundUser = userService.findById(createdUser.getId());
    assertNotNull("应该能找到用户", foundUser);
    assertEquals("用户ID应该匹配", createdUser.getId(), foundUser.getId());
    assertEquals("用户名应该匹配", "findbyid", foundUser.getUsername());
  }

  @Test
  public void testFindByIdWithNullId() {
    // 测试使用空ID查找用户
    User foundUser = userService.findById(null);
    assertNull("空ID应该返回null", foundUser);
  }

  @Test
  public void testFindByIdWithNonExistentId() {
    // 测试查找不存在的用户
    User foundUser = userService.findById(999L);
    assertNull("不存在的ID应该返回null", foundUser);
  }

  @Test
  public void testFindByUsername() {
    // 测试根据用户名查找用户
    User user = new User("findbyname", "findbyname@example.com", "password123");
    userService.createUser(user);

    User foundUser = userService.findByUsername("findbyname");
    assertNotNull("应该能找到用户", foundUser);
    assertEquals("用户名应该匹配", "findbyname", foundUser.getUsername());
  }

  @Test
  public void testFindByUsernameWithNullUsername() {
    // 测试使用空用户名查找用户
    User foundUser = userService.findByUsername(null);
    assertNull("空用户名应该返回null", foundUser);
  }

  @Test
  public void testFindByUsernameWithEmptyUsername() {
    // 测试使用空字符串用户名查找用户
    User foundUser = userService.findByUsername("");
    assertNull("空字符串用户名应该返回null", foundUser);
  }

  @Test
  public void testFindByEmail() {
    // 测试根据邮箱查找用户
    User user = new User("findbyemail", "findbyemail@example.com", "password123");
    userService.createUser(user);

    User foundUser = userService.findByEmail("findbyemail@example.com");
    assertNotNull("应该能找到用户", foundUser);
    assertEquals("邮箱应该匹配", "findbyemail@example.com", foundUser.getEmail());
  }

  @Test
  public void testFindAll() {
    // 测试查找所有用户（包括初始化的测试数据）
    List<User> allUsers = userService.findAll();
    assertNotNull("用户列表不应为空", allUsers);
    assertTrue("应该有初始化的测试用户", allUsers.size() >= 3);

    // 创建新用户并验证列表增长
    User newUser = new User("newuser", "newuser@example.com", "password123");
    userService.createUser(newUser);

    List<User> updatedUsers = userService.findAll();
    assertEquals("用户数量应该增加1", allUsers.size() + 1, updatedUsers.size());
  }

  @Test
  public void testUpdateUser() {
    // 测试更新用户
    User user = new User("updateuser", "update@example.com", "password123");
    User createdUser = userService.createUser(user);

    // 更新用户信息
    createdUser.setUsername("updateduser");
    createdUser.setEmail("updated@example.com");

    User updatedUser = userService.updateUser(createdUser);
    assertNotNull("更新的用户不应为空", updatedUser);
    assertEquals("用户名应该被更新", "updateduser", updatedUser.getUsername());
    assertEquals("邮箱应该被更新", "updated@example.com", updatedUser.getEmail());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateUserWithNullUser() {
    // 测试更新空用户应该抛出异常
    userService.updateUser(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateUserWithNullId() {
    // 测试更新没有ID的用户应该抛出异常
    User user = new User("noId", "noid@example.com", "password123");
    userService.updateUser(user);
  }

  @Test
  public void testDeleteUser() {
    // 测试删除用户
    User user = new User("deleteuser", "delete@example.com", "password123");
    User createdUser = userService.createUser(user);

    boolean deleted = userService.deleteUser(createdUser.getId());
    assertTrue("删除应该成功", deleted);

    User foundUser = userService.findById(createdUser.getId());
    assertNull("删除后应该找不到用户", foundUser);
  }

  @Test
  public void testDeleteUserWithNullId() {
    // 测试删除空ID用户
    boolean deleted = userService.deleteUser(null);
    assertFalse("删除空ID应该返回false", deleted);
  }

  @Test
  public void testDeleteUserWithNonExistentId() {
    // 测试删除不存在的用户
    boolean deleted = userService.deleteUser(999L);
    assertFalse("删除不存在的用户应该返回false", deleted);
  }

  @Test
  public void testActivateUser() {
    // 测试激活用户
    User user = new User("activateuser", "activate@example.com", "password123");
    User createdUser = userService.createUser(user);

    // 先禁用用户
    createdUser.setActive(false);

    // 然后激活用户
    boolean activated = userService.activateUser(createdUser.getId());
    assertTrue("激活应该成功", activated);
    assertTrue("用户应该是激活状态", createdUser.isActive());
  }

  @Test
  public void testDeactivateUser() {
    // 测试禁用用户
    User user = new User("deactivateuser", "deactivate@example.com", "password123");
    User createdUser = userService.createUser(user);

    boolean deactivated = userService.deactivateUser(createdUser.getId());
    assertTrue("禁用应该成功", deactivated);
    assertFalse("用户应该是禁用状态", createdUser.isActive());
  }

  @Test
  public void testAuthenticate() {
    // 测试用户认证
    User user = new User("authuser", "auth@example.com", "password123");
    userService.createUser(user);

    User authenticatedUser = userService.authenticate("authuser", "password123");
    assertNotNull("认证应该成功", authenticatedUser);
    assertEquals("认证的用户应该匹配", "authuser", authenticatedUser.getUsername());
  }

  @Test
  public void testAuthenticateWithWrongPassword() {
    // 测试错误密码认证
    User user = new User("wrongpassuser", "wrongpass@example.com", "password123");
    userService.createUser(user);

    User authenticatedUser = userService.authenticate("wrongpassuser", "wrongpassword");
    assertNull("错误密码认证应该失败", authenticatedUser);
  }

  @Test
  public void testAuthenticateWithNonExistentUser() {
    // 测试不存在用户的认证
    User authenticatedUser = userService.authenticate("nonexistent", "password123");
    assertNull("不存在用户的认证应该失败", authenticatedUser);
  }

  @Test
  public void testAuthenticateWithInactiveUser() {
    // 测试禁用用户的认证
    User user = new User("inactiveuser", "inactive@example.com", "password123");
    User createdUser = userService.createUser(user);

    // 禁用用户
    userService.deactivateUser(createdUser.getId());

    User authenticatedUser = userService.authenticate("inactiveuser", "password123");
    assertNull("禁用用户的认证应该失败", authenticatedUser);
  }

  @Test
  public void testAuthenticateWithNullCredentials() {
    // 测试空凭据认证
    User authenticatedUser1 = userService.authenticate(null, "password123");
    assertNull("空用户名认证应该失败", authenticatedUser1);

    User authenticatedUser2 = userService.authenticate("username", null);
    assertNull("空密码认证应该失败", authenticatedUser2);

    User authenticatedUser3 = userService.authenticate(null, null);
    assertNull("空凭据认证应该失败", authenticatedUser3);
  }
}
