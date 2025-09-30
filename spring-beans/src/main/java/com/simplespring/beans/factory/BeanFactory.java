package com.simplespring.beans.factory;

import com.simplespring.beans.factory.config.BeanDefinition;

/**
 * Bean 工厂接口
 * 定义 Bean 创建和管理的核心方法，提供 Bean 的获取、创建和生命周期管理功能
 * 
 * @author SimpleSpring Framework
 */
public interface BeanFactory {

  /**
   * 根据名称获取 Bean 实例
   * 
   * @param name Bean 名称
   * @return Bean 实例
   * @throws NoSuchBeanDefinitionException 如果找不到指定名称的 Bean 定义
   * @throws BeanCreationException         如果 Bean 创建失败
   */
  Object getBean(String name);

  /**
   * 根据名称和类型获取 Bean 实例
   * 
   * @param name         Bean 名称
   * @param requiredType 期望的 Bean 类型
   * @param <T>          Bean 类型
   * @return Bean 实例
   * @throws NoSuchBeanDefinitionException  如果找不到指定名称的 Bean 定义
   * @throws BeanNotOfRequiredTypeException 如果 Bean 类型不匹配
   * @throws BeanCreationException          如果 Bean 创建失败
   */
  <T> T getBean(String name, Class<T> requiredType);

  /**
   * 根据类型获取 Bean 实例
   * 
   * @param requiredType 期望的 Bean 类型
   * @param <T>          Bean 类型
   * @return Bean 实例
   * @throws NoSuchBeanDefinitionException   如果找不到指定类型的 Bean 定义
   * @throws NoUniqueBeanDefinitionException 如果找到多个匹配的 Bean 定义
   * @throws BeanCreationException           如果 Bean 创建失败
   */
  <T> T getBean(Class<T> requiredType);

  /**
   * 检查是否包含指定名称的 Bean
   * 
   * @param name Bean 名称
   * @return 如果包含返回 true，否则返回 false
   */
  boolean containsBean(String name);

  /**
   * 检查指定名称的 Bean 是否为单例
   * 
   * @param name Bean 名称
   * @return 如果是单例返回 true，否则返回 false
   * @throws NoSuchBeanDefinitionException 如果找不到指定名称的 Bean 定义
   */
  boolean isSingleton(String name);

  /**
   * 检查指定名称的 Bean 是否为原型
   * 
   * @param name Bean 名称
   * @return 如果是原型返回 true，否则返回 false
   * @throws NoSuchBeanDefinitionException 如果找不到指定名称的 Bean 定义
   */
  boolean isPrototype(String name);

  /**
   * 获取指定名称的 Bean 类型
   * 
   * @param name Bean 名称
   * @return Bean 类型，如果找不到返回 null
   */
  Class<?> getType(String name);

  /**
   * 创建 Bean 实例
   * 
   * @param beanDefinition Bean 定义
   * @return Bean 实例
   * @throws BeanCreationException 如果 Bean 创建失败
   */
  Object createBean(BeanDefinition beanDefinition);

  /**
   * 注册 Bean 定义
   * 
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   * @throws IllegalArgumentException 如果参数无效
   * @throws IllegalStateException    如果已存在同名的 Bean 定义
   */
  void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

  /**
   * 获取 Bean 定义
   * 
   * @param beanName Bean 名称
   * @return Bean 定义，如果不存在返回 null
   */
  BeanDefinition getBeanDefinition(String beanName);

  /**
   * 获取所有 Bean 定义的名称
   * 
   * @return Bean 名称数组
   */
  String[] getBeanDefinitionNames();

  /**
   * 获取 Bean 定义数量
   * 
   * @return Bean 定义数量
   */
  int getBeanDefinitionCount();
}
