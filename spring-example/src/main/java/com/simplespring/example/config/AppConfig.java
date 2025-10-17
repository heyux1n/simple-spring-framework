package com.simplespring.example.config;

import com.simplespring.core.annotation.Bean;
import com.simplespring.core.annotation.Configuration;
import com.simplespring.example.aspect.LoggingAspect;
import com.simplespring.example.aspect.PerformanceAspect;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用配置类
 * 使用 @Configuration 和 @Bean 注解进行配置
 * 演示基于Java配置的Bean定义方式
 */
@Configuration
public class AppConfig {

  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

  public AppConfig() {
    logger.info("AppConfig 配置类初始化完成");
  }

  /**
   * 配置用户服务Bean
   * 演示通过@Bean注解定义Bean
   */
  @Bean
  public UserService userService() {
    logger.info("创建 UserService Bean");
    return new UserServiceImpl();
  }

  /**
   * 配置日志切面Bean
   * 演示AOP切面的Bean配置
   */
  @Bean
  public LoggingAspect loggingAspect() {
    logger.info("创建 LoggingAspect Bean");
    return new LoggingAspect();
  }

  /**
   * 配置性能监控切面Bean
   * 演示AOP切面的Bean配置
   */
  @Bean
  public PerformanceAspect performanceAspect() {
    logger.info("创建 PerformanceAspect Bean");
    return new PerformanceAspect();
  }

  /**
   * 配置应用属性Bean
   * 演示配置属性的管理
   */
  @Bean
  public AppProperties appProperties() {
    logger.info("创建 AppProperties Bean");
    AppProperties properties = new AppProperties();
    properties.setAppName("Simple Spring Framework Example");
    properties.setVersion("1.0.0");
    properties.setDescription("简易Spring框架示例应用");
    properties.setAuthor("Simple Spring Team");
    return properties;
  }

  /**
   * 应用属性配置类
   */
  public static class AppProperties {
    private String appName;
    private String version;
    private String description;
    private String author;

    // Getters and Setters
    public String getAppName() {
      return appName;
    }

    public void setAppName(String appName) {
      this.appName = appName;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    @Override
    public String toString() {
      return "AppProperties{" +
          "appName='" + appName + '\'' +
          ", version='" + version + '\'' +
          ", description='" + description + '\'' +
          ", author='" + author + '\'' +
          '}';
    }
  }
}
