package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean 定义注解，用于标识配置类中的 Bean 创建方法
 * 
 * 当方法被 @Bean 注解标记时，Spring IoC 容器会调用该方法来创建、
 * 配置并返回一个 Bean 实例。该方法的返回值会被注册到容器中，
 * 方法名默认作为 Bean 的名称。
 * 
 * @Bean 注解只能用在 @Configuration 注解的类中的方法上，
 * 这些方法负责实例化、配置和初始化要由 Spring IoC 容器管理的对象。
 * 
 * Bean 方法可以有参数，这些参数会被 Spring 容器自动注入，
 * 实现方法级别的依赖注入。
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Configuration
 * public class DatabaseConfig {
 *     
 *     @Bean
 *     public DataSource dataSource() {
 *         HikariDataSource dataSource = new HikariDataSource();
 *         dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
 *         dataSource.setUsername("root");
 *         dataSource.setPassword("password");
 *         return dataSource;
 *     }
 *     
 *     @Bean("customUserDao")
 *     public UserDao userDao(DataSource dataSource) {
 *         return new UserDaoImpl(dataSource);
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    
    /**
     * 指定 Bean 的名称
     * 
     * 如果不指定，则使用方法名作为 Bean 名称。
     * 如果指定了名称，则使用指定的名称作为 Bean 名称。
     * 
     * @return Bean 名称，如果为空字符串则使用方法名
     */
    String value() default "";
}
