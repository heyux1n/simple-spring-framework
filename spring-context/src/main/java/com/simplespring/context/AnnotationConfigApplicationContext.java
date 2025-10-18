package com.simplespring.context;

import com.simplespring.beans.factory.BeanCreationException;
import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.beans.factory.NoSuchBeanDefinitionException;
import com.simplespring.beans.factory.NoUniqueBeanDefinitionException;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.beans.factory.support.DefaultBeanFactory;
import com.simplespring.core.annotation.Component;
import com.simplespring.core.annotation.Configuration;
import com.simplespring.core.annotation.Controller;
import com.simplespring.core.util.StringUtils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于注解配置的应用上下文实现
 * 
 * 支持通过包扫描自动发现和注册带有 @Component、@Controller、@Configuration
 * 等注解的类，并管理它们的生命周期。
 * 
 * 主要功能：
 * 1. 自动扫描指定包路径下的组件类
 * 2. 创建和注册 Bean 定义
 * 3. 管理 Bean 的生命周期
 * 4. 支持依赖注入
 * 5. 容器生命周期管理
 * 
 * 使用示例：
 * 
 * <pre>
 * {@code
 * // 扫描单个包
 * ApplicationContext context = new AnnotationConfigApplicationContext("com.example.service");
 * 
 * // 扫描多个包
 * ApplicationContext context = new AnnotationConfigApplicationContext(
 *     "com.example.service", "com.example.controller");
 * 
 * // 获取 Bean
 * UserService userService = context.getBean(UserService.class);
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {

  /** 内部 Bean 工厂 */
  private final DefaultBeanFactory beanFactory;

  /** 类路径扫描器 */
  private final ClassPathScanner classPathScanner;

  /** 切面处理器 */
  private final AspectProcessor aspectProcessor;

  /** 要扫描的基础包路径 */
  private final String[] basePackages;

  /** 容器是否已启动 */
  private final AtomicBoolean active = new AtomicBoolean(false);

  /** 容器是否已关闭 */
  private final AtomicBoolean closed = new AtomicBoolean(false);

  /** 容器启动时间戳 */
  private long startupDate;

  /** 容器显示名称 */
  private String displayName;

  /** 容器 ID */
  private final String id;

  /**
   * 默认构造函数 - 不自动刷新
   */
  public AnnotationConfigApplicationContext() {
    this.basePackages = new String[0];
    this.beanFactory = new DefaultBeanFactory();
    this.classPathScanner = new ClassPathScanner();
    this.aspectProcessor = new AspectProcessor(beanFactory);
    this.id = generateId();
    this.displayName = generateDisplayName();
  }

  /**
   * 构造函数 - 扫描单个包
   * 
   * @param basePackage 基础包路径
   */
  public AnnotationConfigApplicationContext(String basePackage) {
    this(new String[] { basePackage });
  }

  /**
   * 构造函数 - 扫描多个包
   * 
   * @param basePackages 基础包路径数组
   */
  public AnnotationConfigApplicationContext(String... basePackages) {
    if (basePackages == null || basePackages.length == 0) {
      throw new IllegalArgumentException("基础包路径不能为空");
    }

    for (String basePackage : basePackages) {
      if (!StringUtils.hasText(basePackage)) {
        throw new IllegalArgumentException("基础包路径不能为空字符串");
      }
    }

    this.basePackages = basePackages.clone();
    this.beanFactory = new DefaultBeanFactory();
    this.classPathScanner = new ClassPathScanner();
    this.aspectProcessor = new AspectProcessor(beanFactory);
    this.id = generateId();
    this.displayName = generateDisplayName();

    // 自动刷新容器
    refresh();
  }

  @Override
  public void refresh() {
    synchronized (this) {
      if (closed.get()) {
        throw new IllegalStateException("容器已关闭，无法刷新");
      }

      try {
        // 记录启动时间
        this.startupDate = System.currentTimeMillis();

        // 1. 清理现有的 Bean 定义和单例缓存（如果是重新刷新）
        if (active.get()) {
          clearBeanDefinitions();
        }

        // 2. 扫描组件类
        scanComponents();

        // 3. 处理切面
        processAspects();

        // 4. 实例化所有单例 Bean
        preInstantiateSingletons();

        // 5. 标记容器为活动状态
        active.set(true);

      } catch (Exception e) {
        // 刷新失败，清理状态
        active.set(false);
        throw new RuntimeException("容器刷新失败", e);
      }
    }
  }

  @Override
  public void close() {
    synchronized (this) {
      if (closed.get()) {
        return; // 已经关闭
      }

      try {
        // 标记为非活动状态
        active.set(false);

        // 执行销毁逻辑
        destroyBeans();

        // 标记为已关闭
        closed.set(true);

      } catch (Exception e) {
        // 记录错误但不抛出异常
        System.err.println("容器关闭时发生错误: " + e.getMessage());
      }
    }
  }

  @Override
  public boolean isActive() {
    return active.get() && !closed.get();
  }

  @Override
  public long getStartupDate() {
    return startupDate;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getId() {
    return id;
  }

  // ========== BeanFactory 接口实现 ==========

  @Override
  public Object getBean(String name) {
    checkActive();
    return beanFactory.getBean(name);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) {
    checkActive();
    return beanFactory.getBean(name, requiredType);
  }

  @Override
  public <T> T getBean(Class<T> requiredType) {
    checkActive();
    return beanFactory.getBean(requiredType);
  }

  @Override
  public boolean containsBean(String name) {
    return beanFactory.containsBean(name);
  }

  @Override
  public boolean isSingleton(String name) {
    return beanFactory.isSingleton(name);
  }

  @Override
  public boolean isPrototype(String name) {
    return beanFactory.isPrototype(name);
  }

  @Override
  public Class<?> getType(String name) {
    return beanFactory.getType(name);
  }

  @Override
  public Object createBean(BeanDefinition beanDefinition) {
    checkActive();
    return beanFactory.createBean(beanDefinition);
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) {
    return beanFactory.getBeanDefinition(beanName);
  }

  @Override
  public String[] getBeanDefinitionNames() {
    return beanFactory.getBeanDefinitionNames();
  }

  @Override
  public int getBeanDefinitionCount() {
    return beanFactory.getBeanDefinitionCount();
  }

  // ========== 私有方法 ==========

  /**
   * 扫描组件类并注册 Bean 定义
   */
  private void scanComponents() {
    for (String basePackage : basePackages) {
      Set<Class<?>> componentClasses = classPathScanner.scanPackage(basePackage);

      for (Class<?> componentClass : componentClasses) {
        registerComponent(componentClass);
      }
    }
  }

  /**
   * 注册组件类为 Bean 定义
   * 
   * @param componentClass 组件类
   */
  private void registerComponent(Class<?> componentClass) {
    // 获取组件名称
    String beanName = classPathScanner.getComponentName(componentClass);

    // 创建 Bean 定义
    BeanDefinition beanDefinition = createBeanDefinition(componentClass);
    beanDefinition.setBeanName(beanName);

    // 注册 Bean 定义
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  /**
   * 创建 Bean 定义
   * 
   * @param beanClass Bean 类
   * @return Bean 定义
   */
  private BeanDefinition createBeanDefinition(Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(beanClass);

    // 设置作用域（默认为单例）
    beanDefinition.setScope(Scope.SINGLETON);

    return beanDefinition;
  }

  /**
   * 处理切面
   */
  private void processAspects() {
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);
  }

  /**
   * 预实例化所有单例 Bean
   */
  private void preInstantiateSingletons() {
    String[] beanNames = beanFactory.getBeanDefinitionNames();

    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

      if (beanDefinition != null && beanDefinition.isSingleton()) {
        try {
          // 触发 Bean 创建
          Object bean = beanFactory.getBean(beanName);

          // 应用 AOP 代理
          Object proxiedBean = aspectProcessor.postProcessAfterInitialization(beanName, bean);

          // 如果创建了代理，更新单例缓存
          if (proxiedBean != bean) {
            beanFactory.getBeanRegistry().registerSingleton(beanName, proxiedBean);
          }
        } catch (Exception e) {
          throw new BeanCreationException(beanName, "预实例化单例 Bean 失败", e);
        }
      }
    }
  }

  /**
   * 销毁 Bean
   */
  private void destroyBeans() {
    // 这里可以添加 Bean 销毁逻辑
    // 例如调用 @PreDestroy 注解的方法
  }

  /**
   * 检查容器是否处于活动状态
   */
  private void checkActive() {
    if (!isActive()) {
      throw new IllegalStateException("容器未启动或已关闭");
    }
  }

  /**
   * 生成容器 ID
   * 
   * @return 容器 ID
   */
  private String generateId() {
    return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
  }

  /**
   * 生成容器显示名称
   * 
   * @return 容器显示名称
   */
  private String generateDisplayName() {
    StringBuilder sb = new StringBuilder();
    sb.append("AnnotationConfigApplicationContext");
    sb.append(" (packages: ");

    for (int i = 0; i < basePackages.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(basePackages[i]);
    }

    sb.append(")");
    return sb.toString();
  }

  /**
   * 清理 Bean 定义和单例缓存
   */
  private void clearBeanDefinitions() {
    // 获取所有 Bean 名称
    String[] beanNames = beanFactory.getBeanDefinitionNames();

    // 清理单例缓存
    for (String beanName : beanNames) {
      beanFactory.getBeanRegistry().removeSingleton(beanName);
    }

    // 清理 Bean 定义
    for (String beanName : beanNames) {
      beanFactory.getBeanRegistry().removeBeanDefinition(beanName);
    }
  }

  /**
   * 获取内部 Bean 工厂（用于测试）
   * 
   * @return Bean 工厂
   */
  protected DefaultBeanFactory getBeanFactory() {
    return beanFactory;
  }

  /**
   * 获取切面处理器（用于测试）
   * 
   * @return 切面处理器
   */
  protected AspectProcessor getAspectProcessor() {
    return aspectProcessor;
  }

  /**
   * 注册配置类
   * 
   * @param configClass 配置类
   */
  public void register(Class<?> configClass) {
    if (configClass == null) {
      throw new IllegalArgumentException("配置类不能为空");
    }
    registerComponent(configClass);
  }

  /**
   * 扫描指定包路径
   * 
   * @param basePackage 基础包路径
   */
  public void scan(String basePackage) {
    if (!StringUtils.hasText(basePackage)) {
      throw new IllegalArgumentException("基础包路径不能为空");
    }

    Set<Class<?>> componentClasses = classPathScanner.scanPackage(basePackage);
    for (Class<?> componentClass : componentClasses) {
      registerComponent(componentClass);
    }
  }
}
