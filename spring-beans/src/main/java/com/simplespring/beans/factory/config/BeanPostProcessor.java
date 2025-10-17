package com.simplespring.beans.factory.config;

/**
 * Bean 后处理器接口
 * 
 * 允许自定义修改新的 Bean 实例，例如检查标记接口或用代理包装它们。
 * 通常，通过标记接口等填充 Bean 的后处理器将实现 postProcessBeforeInitialization，
 * 而用代理包装 Bean 的后处理器通常将实现 postProcessAfterInitialization。
 * 
 * BeanPostProcessor 的典型用途包括：
 * - 处理生命周期注解（如 @PostConstruct、@PreDestroy）
 * - 创建代理对象（如 AOP 代理）
 * - 验证 Bean 的配置
 * - 注入额外的依赖
 * 
 * @author SimpleSpring Framework
 */
public interface BeanPostProcessor {

  /**
   * 在任何 Bean 初始化回调（如 @PostConstruct 注解的方法）之前，
   * 将此 BeanPostProcessor 应用到给定的新 Bean 实例。
   * 
   * Bean 已经填充了属性值。返回的 Bean 实例可能是原始实例的包装器。
   * 
   * @param bean     新的 Bean 实例
   * @param beanName Bean 的名称
   * @return 要使用的 Bean 实例，可能是原始实例或包装后的实例；
   *         如果返回 null，则不会调用后续的 BeanPostProcessor
   * @throws RuntimeException 如果处理过程中发生错误
   */
  Object postProcessBeforeInitialization(Object bean, String beanName);

  /**
   * 在任何 Bean 初始化回调（如 @PostConstruct 注解的方法）之后，
   * 将此 BeanPostProcessor 应用到给定的新 Bean 实例。
   * 
   * Bean 已经填充了属性值。返回的 Bean 实例可能是原始实例的包装器。
   * 
   * 对于 FactoryBean，此回调将同时为 FactoryBean 实例和由 FactoryBean 创建的对象调用。
   * 后处理器可以通过相应的 bean instanceof FactoryBean 检查来决定是应用于 FactoryBean 还是创建的对象或两者。
   * 
   * @param bean     新的 Bean 实例
   * @param beanName Bean 的名称
   * @return 要使用的 Bean 实例，可能是原始实例或包装后的实例；
   *         如果返回 null，则不会调用后续的 BeanPostProcessor
   * @throws RuntimeException 如果处理过程中发生错误
   */
  Object postProcessAfterInitialization(Object bean, String beanName);
}
