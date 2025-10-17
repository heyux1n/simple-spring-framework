package com.simplespring.example.controller;

import com.simplespring.example.controller.UserController;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 用户控制器测试
 * 验证 HTTP 请求的处理逻辑
 */
public class UserControllerTest {

  private UserController userController;
  private UserService userService;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter responseWriter;

  @Before
  public void setUp() throws Exception {
    // 创建真实的用户服务
    userService = new UserServiceImpl();

    // 创建控制器并设置依赖
    userController = new UserController();
    java.lang.reflect.Field userServiceField = UserController.class.getDeclaredField("userService");
    userServiceField.setAccessible(true);
    userServiceField.set(userController, userService);

    // 创建模拟的请求和响应对象
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    // 设置响应写入器
    responseWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(responseWriter);
    when(response.getWriter()).thenReturn(printWriter);
  }

  @Test
  public void testGetAllUsers() throws Exception {
    // 测试获取所有用户
    String result = userController.getAllUsers(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含data字段", responseContent.contains("\"data\":["));
    assertTrue("响应应包含count字段", responseContent.contains("\"count\":"));

    // 验证响应头设置
    verify(response).setContentType("application/json;charset=UTF-8");
  }

  @Test
  public void testGetUserById() throws Exception {
    // 先创建一个用户
    User user = userService.createUser(new User("testuser", "test@example.com", "password123"));

    // 模拟请求路径
    when(request.getPathInfo()).thenReturn("/users/" + user.getId());

    String result = userController.getUserById(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含用户数据", responseContent.contains("\"username\":\"testuser\""));
    assertTrue("响应应包含邮箱", responseContent.contains("\"email\":\"test@example.com\""));
  }

  @Test
  public void testGetUserByIdNotFound() throws Exception {
    // 模拟请求不存在的用户ID
    when(request.getPathInfo()).thenReturn("/users/999");

    String result = userController.getUserById(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("用户不存在"));

    // 验证状态码设置
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testGetUserByIdInvalidPath() throws Exception {
    // 模拟无效的请求路径
    when(request.getPathInfo()).thenReturn("/users/");

    String result = userController.getUserById(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("缺少用户ID参数"));
  }

  @Test
  public void testCreateUser() throws Exception {
    // 模拟请求参数
    when(request.getParameter("username")).thenReturn("newuser");
    when(request.getParameter("email")).thenReturn("newuser@example.com");
    when(request.getParameter("password")).thenReturn("password123");

    String result = userController.createUser(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含成功消息", responseContent.contains("用户创建成功"));
    assertTrue("响应应包含用户数据", responseContent.contains("\"username\":\"newuser\""));

    // 验证用户确实被创建
    User createdUser = userService.findByUsername("newuser");
    assertNotNull("用户应该被创建", createdUser);
    assertEquals("邮箱应该匹配", "newuser@example.com", createdUser.getEmail());
  }

  @Test
  public void testCreateUserMissingParameters() throws Exception {
    // 模拟缺少参数的请求
    when(request.getParameter("username")).thenReturn("");
    when(request.getParameter("email")).thenReturn("test@example.com");
    when(request.getParameter("password")).thenReturn("password123");

    String result = userController.createUser(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("用户名不能为空"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testLogin() throws Exception {
    // 先创建一个用户
    userService.createUser(new User("loginuser", "login@example.com", "password123"));

    // 模拟登录请求参数
    when(request.getParameter("username")).thenReturn("loginuser");
    when(request.getParameter("password")).thenReturn("password123");

    String result = userController.login(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含登录成功消息", responseContent.contains("登录成功"));
    assertTrue("响应应包含用户数据", responseContent.contains("\"username\":\"loginuser\""));
  }

  @Test
  public void testLoginInvalidCredentials() throws Exception {
    // 模拟错误的登录凭据
    when(request.getParameter("username")).thenReturn("nonexistent");
    when(request.getParameter("password")).thenReturn("wrongpassword");

    String result = userController.login(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("用户名或密码错误"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testUpdateUser() throws Exception {
    // 先创建一个用户
    User user = userService.createUser(new User("updateuser", "update@example.com", "password123"));

    // 模拟更新请求
    when(request.getPathInfo()).thenReturn("/users/" + user.getId());
    when(request.getParameter("username")).thenReturn("updateduser");
    when(request.getParameter("email")).thenReturn("updated@example.com");

    String result = userController.updateUser(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含更新成功消息", responseContent.contains("用户更新成功"));
    assertTrue("响应应包含更新后的用户名", responseContent.contains("\"username\":\"updateduser\""));

    // 验证用户确实被更新
    User updatedUser = userService.findById(user.getId());
    assertEquals("用户名应该被更新", "updateduser", updatedUser.getUsername());
    assertEquals("邮箱应该被更新", "updated@example.com", updatedUser.getEmail());
  }

  @Test
  public void testDeleteUser() throws Exception {
    // 先创建一个用户
    User user = userService.createUser(new User("deleteuser", "delete@example.com", "password123"));

    // 模拟删除请求
    when(request.getPathInfo()).thenReturn("/users/" + user.getId());

    String result = userController.deleteUser(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含删除成功消息", responseContent.contains("用户删除成功"));

    // 验证用户确实被删除
    User deletedUser = userService.findById(user.getId());
    assertNull("用户应该被删除", deletedUser);
  }

  @Test
  public void testDeleteUserNotFound() throws Exception {
    // 模拟删除不存在的用户
    when(request.getPathInfo()).thenReturn("/users/999");

    String result = userController.deleteUser(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("用户不存在或删除失败"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
