package com.simplespring.example.service;

import com.simplespring.example.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {

  /**
   * 创建用户
   * 
   * @param user 用户信息
   * @return 创建的用户
   */
  User createUser(User user);

  /**
   * 根据ID查找用户
   * 
   * @param id 用户ID
   * @return 用户信息，如果不存在返回null
   */
  User findById(Long id);

  /**
   * 根据用户名查找用户
   * 
   * @param username 用户名
   * @return 用户信息，如果不存在返回null
   */
  User findByUsername(String username);

  /**
   * 根据邮箱查找用户
   * 
   * @param email 邮箱
   * @return 用户信息，如果不存在返回null
   */
  User findByEmail(String email);

  /**
   * 获取所有用户
   * 
   * @return 用户列表
   */
  List<User> findAll();

  /**
   * 更新用户信息
   * 
   * @param user 用户信息
   * @return 更新后的用户信息
   */
  User updateUser(User user);

  /**
   * 删除用户
   * 
   * @param id 用户ID
   * @return 是否删除成功
   */
  boolean deleteUser(Long id);

  /**
   * 激活用户
   * 
   * @param id 用户ID
   * @return 是否激活成功
   */
  boolean activateUser(Long id);

  /**
   * 禁用用户
   * 
   * @param id 用户ID
   * @return 是否禁用成功
   */
  boolean deactivateUser(Long id);

  /**
   * 验证用户登录
   * 
   * @param username 用户名
   * @param password 密码
   * @return 验证成功返回用户信息，失败返回null
   */
  User authenticate(String username, String password);
}
