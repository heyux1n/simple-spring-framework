package com.simplespring.context;

import com.simplespring.beans.factory.BeanFactory;

/**
 * 应用上下文接口
 * 
 * ApplicationContext 是 Spring 框架的核心接口，它扩展了 BeanFactory 的功能，
 * 提供了更高级的容器特性，包括：
 * 1. 自动扫描和注册组件
 * 2. 容器生命周期管理
 * 3. 事件发布和监听
 * 4. 资源加载
 * 5. 国际化支持
 * 
 * ApplicationContext 在启动时会自动扫描指定包路径下的组件类，
 * 创建 Bean 定义并注册到容器中，然后初始化所有单例 Bean。
 * 
 * 使用示例：
 * 
 * <pre>
 * {@code
 * // 创建应用上下文
 * ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
 * 
 * // 获取 Bean
 * UserService userService = context.getBean(UserService.class);
 * 
 * // 关闭上下文
 * context.close();
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public interface ApplicationContext extends BeanFactory {

  /**
   * 刷新容器
   * 
   * 执行容器的完整初始化流程，包括：
   * 1. 扫描组件类
   * 2. 注册 Bean 定义
   * 3. 实例化单例 Bean
   * 4. 执行初始化回调
   * 
   * @throws IllegalStateException 如果容器已经关闭
   * @throws RuntimeException      如果刷新过程中发生错误
   */
  void refresh();

  /**
   * 关闭容器
   * 
   * 执行容器的关闭流程，包括：
   * 1. 发布容器关闭事件
   * 2. 执行销毁回调
   * 3. 清理资源
   * 
   * 容器关闭后不能再使用，需要重新创建。
   */
  void close();

  /**
   * 检查容器是否处于活动状态
   * 
   * @return 如果容器已启动且未关闭返回 true，否则返回 false
   */
  boolean isActive();

  /**
   * 获取容器启动时间戳
   * 
   * @return 容器启动时间戳（毫秒），如果容器未启动返回 0
   */
  long getStartupDate();

  /**
   * 获取容器显示名称
   * 
   * @return 容器显示名称
   */
  String getDisplayName();

  /**
   * 获取容器 ID
   * 
   * @return 容器唯一标识符
   */
  String getId();
}
