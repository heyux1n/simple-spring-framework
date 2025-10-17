package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.*;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.BeanPostProcessor;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.util.ClassUtils;
import com.simplespring.core.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认 Bean 工厂实现
 * 提供 Bean 的创建、缓存和生命周期管理，集成构造函数、字段和方法注入功能
 * 
 * @author SimpleSpring Framework
 */
public class DefaultBeanFactory implements BeanFactory {

  /**
   * Bean 注册表
   */
  private final BeanRegistry beanRegistry;

  /**
   * 构造函数注入器
   */
  private final ConstructorInjector constructorInjector;

  /**
   * 字段注入器
   */
  private final FieldInjector fieldInjector;

  /**
   * 方法注入器
   */
  private final MethodInjector methodInjector;

  /**
   * Bean 后处理器列表
   */
  private final List<BeanPostProcessor> beanPostProcessors;

  /**
   * 生命周期处理器
   */
  private final LifecycleProcessor lifecycleProcessor;

  /**
   * 构造函数
   */
  public DefaultBeanFactory() {
    this.beanRegistry = new BeanRegistry();
    this.constructorInjector = new ConstructorInjector(beanRegistry);
    this.fieldInjector = new FieldInjector(beanRegistry);
    this.methodInjector = new MethodInjector(beanRegistry);
    this.beanPostProcessors = new ArrayList<BeanPostProcessor>();
    this.lifecycleProcessor = new LifecycleProcessor();

    // 设置 BeanFactory 引用以支持依赖创建
    this.constructorInjector.setBeanFactory(this);
    this.fieldInjector.setBeanFactory(this);
    this.methodInjector.setBeanFactory(this);

    // 注册默认的生命周期处理器
    addBeanPostProcessor(this.lifecycleProcessor);
  }

  @Override
  public Object getBean(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("Bean 名称不能为空");
    }

    // 首先检查单例缓存
    Object singleton = beanRegistry.getSingleton(name);
    if (singleton != null) {
      return singleton;
    }

    // 获取 Bean 定义
    BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(name);
    if (beanDefinition == null) {
      throw new NoSuchBeanDefinitionException(name);
    }

    // 创建 Bean 实例
    return createBean(name, beanDefinition);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) {
    Object bean = getBean(name);

    if (requiredType != null && !requiredType.isInstance(bean)) {
      throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
    }

    return requiredType.cast(bean);
  }

  @Override
  public <T> T getBean(Class<T> requiredType) {
    if (requiredType == null) {
      throw new IllegalArgumentException("Bean 类型不能为空");
    }

    List<String> beanNames = beanRegistry.getBeanNamesForType(requiredType);

    if (beanNames.isEmpty()) {
      throw new NoSuchBeanDefinitionException(requiredType);
    }

    if (beanNames.size() > 1) {
      throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
    }

    String beanName = beanNames.get(0);
    return getBean(beanName, requiredType);
  }

  @Override
  public boolean containsBean(String name) {
    return beanRegistry.containsBeanDefinition(name) || beanRegistry.containsSingleton(name);
  }

  @Override
  public boolean isSingleton(String name) {
    BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(name);
    if (beanDefinition == null) {
      throw new NoSuchBeanDefinitionException(name);
    }
    return beanDefinition.isSingleton();
  }

  @Override
  public boolean isPrototype(String name) {
    BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(name);
    if (beanDefinition == null) {
      throw new NoSuchBeanDefinitionException(name);
    }
    return beanDefinition.getScope() == Scope.PROTOTYPE;
  }

  @Override
  public Class<?> getType(String name) {
    return beanRegistry.getType(name);
  }

  @Override
  public Object createBean(BeanDefinition beanDefinition) {
    if (beanDefinition == null) {
      throw new IllegalArgumentException("Bean 定义不能为空");
    }

    String beanName = beanDefinition.getBeanName();
    if (beanName == null) {
      beanName = generateBeanName(beanDefinition.getBeanClass());
    }

    return createBean(beanName, beanDefinition);
  }

  /**
   * 创建 Bean 实例（内部方法）
   * 
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   * @return Bean 实例
   */
  private Object createBean(String beanName, BeanDefinition beanDefinition) {
    try {
      // 检查循环依赖
      if (beanDefinition.isSingleton()) {
        beanRegistry.beforeSingletonCreation(beanName);
      }

      // 创建 Bean 实例
      Object beanInstance = doCreateBean(beanName, beanDefinition);

      // 如果是单例，缓存实例
      if (beanDefinition.isSingleton()) {
        beanRegistry.registerSingleton(beanName, beanInstance);
        beanRegistry.afterSingletonCreation(beanName);
      }

      return beanInstance;

    } catch (Exception e) {
      if (beanDefinition.isSingleton()) {
        beanRegistry.afterSingletonCreation(beanName);
      }

      if (e instanceof BeanCreationException) {
        throw (BeanCreationException) e;
      }
      throw new BeanCreationException(beanName, e.getMessage(), e);
    }
  }

  /**
   * 执行 Bean 创建的核心逻辑
   * 
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   * @return Bean 实例
   */
  private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
    Class<?> beanClass = beanDefinition.getBeanClass();

    // 1. 创建 Bean 实例（构造函数注入）
    Object beanInstance = createBeanInstance(beanName, beanDefinition);

    // 2. 字段注入
    populateBean(beanInstance, beanDefinition);

    // 3. 初始化 Bean
    initializeBean(beanInstance, beanName, beanDefinition);

    return beanInstance;
  }

  /**
   * 创建 Bean 实例
   * 
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   * @return Bean 实例
   */
  private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
    Class<?> beanClass = beanDefinition.getBeanClass();

    try {
      // 如果指定了构造函数，使用指定的构造函数
      Constructor<?> constructor = beanDefinition.getConstructor();
      if (constructor != null) {
        return constructorInjector.createBeanInstance(beanClass);
      }

      // 否则使用构造函数注入器选择合适的构造函数
      return constructorInjector.createBeanInstance(beanClass);

    } catch (Exception e) {
      throw new BeanCreationException(beanName, "无法创建 Bean 实例", e);
    }
  }

  /**
   * 填充 Bean 属性（字段注入和方法注入）
   * 
   * @param beanInstance   Bean 实例
   * @param beanDefinition Bean 定义
   */
  private void populateBean(Object beanInstance, BeanDefinition beanDefinition) {
    // 字段注入
    fieldInjector.injectFields(beanInstance, beanDefinition);

    // 方法注入
    methodInjector.injectMethods(beanInstance, beanDefinition);
  }

  /**
   * 初始化 Bean
   * 
   * @param beanInstance   Bean 实例
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   * @return 初始化后的 Bean 实例
   */
  private Object initializeBean(Object beanInstance, String beanName, BeanDefinition beanDefinition) {
    Object wrappedBean = beanInstance;

    // 调用 BeanPostProcessor 的 postProcessBeforeInitialization 方法
    wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);

    // 调用 BeanPostProcessor 的 postProcessAfterInitialization 方法
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);

    return wrappedBean;
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
    if (!StringUtils.hasText(beanName)) {
      throw new IllegalArgumentException("Bean 名称不能为空");
    }
    if (beanDefinition == null) {
      throw new IllegalArgumentException("Bean 定义不能为空");
    }

    // 如果 Bean 定义中没有设置名称，设置名称
    if (beanDefinition.getBeanName() == null) {
      beanDefinition.setBeanName(beanName);
    }

    // 扫描并设置自动装配信息
    setupAutowiredInfo(beanDefinition);

    // 注册到注册表
    beanRegistry.registerBeanDefinition(beanName, beanDefinition);
  }

  /**
   * 设置自动装配信息
   * 
   * @param beanDefinition Bean 定义
   */
  private void setupAutowiredInfo(BeanDefinition beanDefinition) {
    Class<?> beanClass = beanDefinition.getBeanClass();
    if (beanClass == null) {
      return;
    }

    // 扫描自动装配字段
    if (!beanDefinition.hasAutowiredFields()) {
      List<Field> autowiredFields = FieldInjector.scanAutowiredFields(beanClass);
      beanDefinition.setAutowiredFields(autowiredFields);
    }

    // 扫描自动装配方法
    if (!beanDefinition.hasAutowiredMethods()) {
      List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(beanClass);
      beanDefinition.setAutowiredMethods(autowiredMethods);
    }

    // 设置首选构造函数
    if (beanDefinition.getConstructor() == null) {
      Constructor<?> preferredConstructor = ConstructorInjector.getPreferredConstructor(beanClass);
      beanDefinition.setConstructor(preferredConstructor);
    }
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) {
    return beanRegistry.getBeanDefinition(beanName);
  }

  @Override
  public String[] getBeanDefinitionNames() {
    return beanRegistry.getBeanDefinitionNames();
  }

  @Override
  public int getBeanDefinitionCount() {
    return beanRegistry.getBeanDefinitionCount();
  }

  /**
   * 生成 Bean 名称
   * 
   * @param beanClass Bean 类
   * @return Bean 名称
   */
  private String generateBeanName(Class<?> beanClass) {
    if (beanClass == null) {
      return null;
    }

    String shortName = ClassUtils.getShortName(beanClass);
    return Character.toLowerCase(shortName.charAt(0)) + shortName.substring(1);
  }

  /**
   * 应用 BeanPostProcessor 的 postProcessBeforeInitialization 方法
   * 
   * @param existingBean 现有的 Bean 实例
   * @param beanName     Bean 名称
   * @return 处理后的 Bean 实例
   */
  private Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
    Object result = existingBean;
    for (BeanPostProcessor processor : beanPostProcessors) {
      Object current = processor.postProcessBeforeInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }
    return result;
  }

  /**
   * 应用 BeanPostProcessor 的 postProcessAfterInitialization 方法
   * 
   * @param existingBean 现有的 Bean 实例
   * @param beanName     Bean 名称
   * @return 处理后的 Bean 实例
   */
  private Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
    Object result = existingBean;
    for (BeanPostProcessor processor : beanPostProcessors) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }
    return result;
  }

  /**
   * 添加 BeanPostProcessor
   * 
   * @param beanPostProcessor Bean 后处理器
   */
  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    if (beanPostProcessor != null && !beanPostProcessors.contains(beanPostProcessor)) {
      beanPostProcessors.add(beanPostProcessor);
    }
  }

  /**
   * 移除 BeanPostProcessor
   * 
   * @param beanPostProcessor Bean 后处理器
   */
  public void removeBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    beanPostProcessors.remove(beanPostProcessor);
  }

  /**
   * 获取所有 BeanPostProcessor
   * 
   * @return BeanPostProcessor 列表
   */
  public List<BeanPostProcessor> getBeanPostProcessors() {
    return new ArrayList<BeanPostProcessor>(beanPostProcessors);
  }

  /**
   * 销毁 Bean（调用 @PreDestroy 方法）
   * 
   * @param beanName Bean 名称
   */
  public void destroyBean(String beanName) {
    Object bean = beanRegistry.getSingleton(beanName);
    if (bean != null) {
      destroyBean(bean, beanName);
      beanRegistry.removeSingleton(beanName);
    }
  }

  /**
   * 销毁 Bean 实例
   * 
   * @param bean     Bean 实例
   * @param beanName Bean 名称
   */
  public void destroyBean(Object bean, String beanName) {
    if (bean != null) {
      lifecycleProcessor.invokePreDestroyMethods(bean, beanName);
    }
  }

  /**
   * 销毁所有单例 Bean
   */
  public void destroySingletons() {
    String[] beanNames = beanRegistry.getSingletonNames();
    for (String beanName : beanNames) {
      destroyBean(beanName);
    }
  }

  /**
   * 获取生命周期处理器
   * 
   * @return 生命周期处理器
   */
  public LifecycleProcessor getLifecycleProcessor() {
    return lifecycleProcessor;
  }

  /**
   * 获取 Bean 注册表（用于测试和内部访问）
   * 
   * @return Bean 注册表
   */
  public BeanRegistry getBeanRegistry() {
    return beanRegistry;
  }
}
