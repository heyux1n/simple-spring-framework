package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置类注解，用于标识一个类为 Spring 配置类
 * 
 * 当类被 @Configuration 注解标记时，表示该类包含一个或多个 @Bean 方法，
 * 这些方法会被 Spring IoC 容器调用来创建和配置 Bean 实例。
 * 
 * 配置类是基于 Java 的配置方式，提供了一种类型安全的方式来定义 Bean
 * 和它们之间的依赖关系，是 XML 配置的替代方案。
 * 
 * 配置类本身也会被注册为一个 Bean，因此可以使用 @Autowired 等注解
 * 来注入其他依赖。
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Configuration
 * public class AppConfig {
 *     
 *     @Bean
 *     public UserService userService() {
 *         return new UserServiceImpl();
 *     }
 *     
 *     @Bean
 *     public UserController userController() {
 *         UserController controller = new UserController();
 *         controller.setUserService(userService()); // 方法调用注入
 *         return controller;
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    // 配置类注解不需要额外的属性
}
