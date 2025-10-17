package com.simplespring.example.controller;

import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 用户控制器
 * 提供用户相关的 HTTP 接口，演示 MVC 框架的使用
 */
@Controller
@RequestMapping("/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  public UserController() {
    logger.info("UserController 初始化完成");
  }

  /**
   * 获取所有用户
   * GET /users
   */
  @RequestMapping(method = RequestMethod.GET)
  public String getAllUsers(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理获取所有用户请求");

    try {
      List<User> users = userService.findAll();

      // 构建JSON响应
      StringBuilder json = new StringBuilder();
      json.append("{\"success\":true,\"data\":[");

      for (int i = 0; i < users.size(); i++) {
        if (i > 0) {
          json.append(",");
        }
        User user = users.get(i);
        json.append("{")
            .append("\"id\":").append(user.getId()).append(",")
            .append("\"username\":\"").append(user.getUsername()).append("\",")
            .append("\"email\":\"").append(user.getEmail()).append("\",")
            .append("\"active\":").append(user.isActive())
            .append("}");
      }

      json.append("],\"count\":").append(users.size()).append("}");

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json.toString());

      logger.info("成功返回 {} 个用户", users.size());
      return null; // 直接写入响应，不需要视图

    } catch (Exception e) {
      logger.error("获取用户列表失败", e);
      return handleError(response, "获取用户列表失败: " + e.getMessage());
    }
  }

  /**
   * 根据ID获取用户
   * GET /users/{id}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String getUserById(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理根据ID获取用户请求");

    try {
      String pathInfo = request.getPathInfo();
      if (pathInfo == null || pathInfo.length() <= 1) {
        return handleError(response, "缺少用户ID参数");
      }

      // 解析路径中的ID参数
      String[] pathParts = pathInfo.split("/");
      if (pathParts.length < 3) {
        return handleError(response, "无效的请求路径");
      }

      Long userId;
      try {
        userId = Long.parseLong(pathParts[2]);
      } catch (NumberFormatException e) {
        return handleError(response, "无效的用户ID格式");
      }

      User user = userService.findById(userId);
      if (user == null) {
        return handleError(response, "用户不存在: " + userId);
      }

      // 构建JSON响应
      String json = "{\"success\":true,\"data\":{" +
          "\"id\":" + user.getId() + "," +
          "\"username\":\"" + user.getUsername() + "\"," +
          "\"email\":\"" + user.getEmail() + "\"," +
          "\"active\":" + user.isActive() +
          "}}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功返回用户信息: {}", user.getUsername());
      return null;

    } catch (Exception e) {
      logger.error("获取用户信息失败", e);
      return handleError(response, "获取用户信息失败: " + e.getMessage());
    }
  }

  /**
   * 创建用户
   * POST /users
   */
  @RequestMapping(method = RequestMethod.POST)
  public String createUser(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理创建用户请求");

    try {
      // 获取请求参数
      String username = request.getParameter("username");
      String email = request.getParameter("email");
      String password = request.getParameter("password");

      // 参数验证
      if (username == null || username.trim().isEmpty()) {
        return handleError(response, "用户名不能为空");
      }
      if (email == null || email.trim().isEmpty()) {
        return handleError(response, "邮箱不能为空");
      }
      if (password == null || password.trim().isEmpty()) {
        return handleError(response, "密码不能为空");
      }

      // 创建用户
      User user = new User(username.trim(), email.trim(), password);
      User createdUser = userService.createUser(user);

      // 构建JSON响应
      String json = "{\"success\":true,\"message\":\"用户创建成功\",\"data\":{" +
          "\"id\":" + createdUser.getId() + "," +
          "\"username\":\"" + createdUser.getUsername() + "\"," +
          "\"email\":\"" + createdUser.getEmail() + "\"," +
          "\"active\":" + createdUser.isActive() +
          "}}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功创建用户: {}", createdUser.getUsername());
      return null;

    } catch (Exception e) {
      logger.error("创建用户失败", e);
      return handleError(response, "创建用户失败: " + e.getMessage());
    }
  }

  /**
   * 用户登录
   * POST /users/login
   */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理用户登录请求");

    try {
      String username = request.getParameter("username");
      String password = request.getParameter("password");

      if (username == null || username.trim().isEmpty()) {
        return handleError(response, "用户名不能为空");
      }
      if (password == null || password.trim().isEmpty()) {
        return handleError(response, "密码不能为空");
      }

      User user = userService.authenticate(username.trim(), password);

      if (user == null) {
        return handleError(response, "用户名或密码错误");
      }

      // 构建JSON响应
      String json = "{\"success\":true,\"message\":\"登录成功\",\"data\":{" +
          "\"id\":" + user.getId() + "," +
          "\"username\":\"" + user.getUsername() + "\"," +
          "\"email\":\"" + user.getEmail() + "\"" +
          "}}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("用户登录成功: {}", user.getUsername());
      return null;

    } catch (Exception e) {
      logger.error("用户登录失败", e);
      return handleError(response, "登录失败: " + e.getMessage());
    }
  }

  /**
   * 更新用户信息
   * PUT /users/{id}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public String updateUser(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理更新用户请求");

    try {
      String pathInfo = request.getPathInfo();
      if (pathInfo == null || pathInfo.length() <= 1) {
        return handleError(response, "缺少用户ID参数");
      }

      // 解析路径中的ID参数
      String[] pathParts = pathInfo.split("/");
      if (pathParts.length < 3) {
        return handleError(response, "无效的请求路径");
      }

      Long userId;
      try {
        userId = Long.parseLong(pathParts[2]);
      } catch (NumberFormatException e) {
        return handleError(response, "无效的用户ID格式");
      }

      // 查找现有用户
      User existingUser = userService.findById(userId);
      if (existingUser == null) {
        return handleError(response, "用户不存在: " + userId);
      }

      // 获取更新参数
      String username = request.getParameter("username");
      String email = request.getParameter("email");

      if (username != null && !username.trim().isEmpty()) {
        existingUser.setUsername(username.trim());
      }
      if (email != null && !email.trim().isEmpty()) {
        existingUser.setEmail(email.trim());
      }

      User updatedUser = userService.updateUser(existingUser);

      // 构建JSON响应
      String json = "{\"success\":true,\"message\":\"用户更新成功\",\"data\":{" +
          "\"id\":" + updatedUser.getId() + "," +
          "\"username\":\"" + updatedUser.getUsername() + "\"," +
          "\"email\":\"" + updatedUser.getEmail() + "\"," +
          "\"active\":" + updatedUser.isActive() +
          "}}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功更新用户: {}", updatedUser.getUsername());
      return null;

    } catch (Exception e) {
      logger.error("更新用户失败", e);
      return handleError(response, "更新用户失败: " + e.getMessage());
    }
  }

  /**
   * 删除用户
   * DELETE /users/{id}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public String deleteUser(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理删除用户请求");

    try {
      String pathInfo = request.getPathInfo();
      if (pathInfo == null || pathInfo.length() <= 1) {
        return handleError(response, "缺少用户ID参数");
      }

      // 解析路径中的ID参数
      String[] pathParts = pathInfo.split("/");
      if (pathParts.length < 3) {
        return handleError(response, "无效的请求路径");
      }

      Long userId;
      try {
        userId = Long.parseLong(pathParts[2]);
      } catch (NumberFormatException e) {
        return handleError(response, "无效的用户ID格式");
      }

      boolean deleted = userService.deleteUser(userId);

      if (deleted) {
        String json = "{\"success\":true,\"message\":\"用户删除成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功删除用户: {}", userId);
      } else {
        return handleError(response, "用户不存在或删除失败: " + userId);
      }

      return null;

    } catch (Exception e) {
      logger.error("删除用户失败", e);
      return handleError(response, "删除用户失败: " + e.getMessage());
    }
  }

  /**
   * 处理错误响应
   */
  private String handleError(HttpServletResponse response, String message) {
    try {
      String json = "{\"success\":false,\"error\":\"" + message + "\"}";
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(json);
    } catch (IOException e) {
      logger.error("写入错误响应失败", e);
    }
    return null;
  }
}
