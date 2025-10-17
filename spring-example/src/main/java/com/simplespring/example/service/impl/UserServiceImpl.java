package com.simplespring.example.service.impl;

import com.simplespring.core.annotation.Component;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现类
 * 演示依赖注入功能，使用内存存储模拟数据库操作
 */
@Component
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  // 使用内存存储模拟数据库
  private final Map<Long, User> userStorage = new ConcurrentHashMap<Long, User>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public UserServiceImpl() {
    logger.info("UserServiceImpl 初始化完成");
    // 初始化一些测试数据
    initTestData();
  }

  /**
   * 初始化测试数据
   */
  private void initTestData() {
    User admin = new User("admin", "admin@example.com", "admin123");
    admin.setId(idGenerator.getAndIncrement());
    userStorage.put(admin.getId(), admin);

    User user1 = new User("zhangsan", "zhangsan@example.com", "123456");
    user1.setId(idGenerator.getAndIncrement());
    userStorage.put(user1.getId(), user1);

    User user2 = new User("lisi", "lisi@example.com", "123456");
    user2.setId(idGenerator.getAndIncrement());
    userStorage.put(user2.getId(), user2);

    logger.info("初始化了 {} 个测试用户", userStorage.size());
  }

  @Override
  public User createUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("用户信息不能为空");
    }

    // 检查用户名是否已存在
    if (findByUsername(user.getUsername()) != null) {
      throw new IllegalArgumentException("用户名已存在: " + user.getUsername());
    }

    // 检查邮箱是否已存在
    if (findByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("邮箱已存在: " + user.getEmail());
    }

    // 生成ID并保存
    user.setId(idGenerator.getAndIncrement());
    userStorage.put(user.getId(), user);

    logger.info("创建用户成功: {}", user.getUsername());
    return user;
  }

  @Override
  public User findById(Long id) {
    if (id == null) {
      return null;
    }
    User user = userStorage.get(id);
    logger.debug("根据ID查找用户: {} -> {}", id, user != null ? user.getUsername() : "未找到");
    return user;
  }

  @Override
  public User findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return null;
    }

    for (User user : userStorage.values()) {
      if (username.equals(user.getUsername())) {
        logger.debug("根据用户名查找用户: {} -> 找到", username);
        return user;
      }
    }

    logger.debug("根据用户名查找用户: {} -> 未找到", username);
    return null;
  }

  @Override
  public User findByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }

    for (User user : userStorage.values()) {
      if (email.equals(user.getEmail())) {
        logger.debug("根据邮箱查找用户: {} -> 找到", email);
        return user;
      }
    }

    logger.debug("根据邮箱查找用户: {} -> 未找到", email);
    return null;
  }

  @Override
  public List<User> findAll() {
    List<User> users = new ArrayList<User>(userStorage.values());
    logger.debug("查找所有用户，共 {} 个", users.size());
    return users;
  }

  @Override
  public User updateUser(User user) {
    if (user == null || user.getId() == null) {
      throw new IllegalArgumentException("用户信息或ID不能为空");
    }

    User existingUser = userStorage.get(user.getId());
    if (existingUser == null) {
      throw new IllegalArgumentException("用户不存在: " + user.getId());
    }

    // 检查用户名是否被其他用户使用
    User userWithSameName = findByUsername(user.getUsername());
    if (userWithSameName != null && !userWithSameName.getId().equals(user.getId())) {
      throw new IllegalArgumentException("用户名已被其他用户使用: " + user.getUsername());
    }

    // 检查邮箱是否被其他用户使用
    User userWithSameEmail = findByEmail(user.getEmail());
    if (userWithSameEmail != null && !userWithSameEmail.getId().equals(user.getId())) {
      throw new IllegalArgumentException("邮箱已被其他用户使用: " + user.getEmail());
    }

    userStorage.put(user.getId(), user);
    logger.info("更新用户成功: {}", user.getUsername());
    return user;
  }

  @Override
  public boolean deleteUser(Long id) {
    if (id == null) {
      return false;
    }

    User removedUser = userStorage.remove(id);
    boolean success = removedUser != null;

    if (success) {
      logger.info("删除用户成功: {}", removedUser.getUsername());
    } else {
      logger.warn("删除用户失败，用户不存在: {}", id);
    }

    return success;
  }

  @Override
  public boolean activateUser(Long id) {
    User user = findById(id);
    if (user == null) {
      logger.warn("激活用户失败，用户不存在: {}", id);
      return false;
    }

    user.setActive(true);
    logger.info("激活用户成功: {}", user.getUsername());
    return true;
  }

  @Override
  public boolean deactivateUser(Long id) {
    User user = findById(id);
    if (user == null) {
      logger.warn("禁用用户失败，用户不存在: {}", id);
      return false;
    }

    user.setActive(false);
    logger.info("禁用用户成功: {}", user.getUsername());
    return true;
  }

  @Override
  public User authenticate(String username, String password) {
    if (username == null || password == null) {
      logger.warn("用户认证失败：用户名或密码为空");
      return null;
    }

    User user = findByUsername(username);
    if (user == null) {
      logger.warn("用户认证失败：用户不存在 - {}", username);
      return null;
    }

    if (!user.isActive()) {
      logger.warn("用户认证失败：用户已被禁用 - {}", username);
      return null;
    }

    if (!password.equals(user.getPassword())) {
      logger.warn("用户认证失败：密码错误 - {}", username);
      return null;
    }

    logger.info("用户认证成功: {}", username);
    return user;
  }
}
