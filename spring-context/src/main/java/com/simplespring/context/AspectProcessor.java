package com.simplespring.context;

import com.simplespring.aop.AdviceDefinition;
import com.simplespring.aop.AdviceType;
import com.simplespring.aop.AspectDefinition;
import com.simplespring.aop.ProxyFactory;
import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.AfterReturning;
import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 切面处理器
 * 负责扫描和注册切面 Bean，在 Bean 创建过程中集成 AOP 代理生成
 * 
 * @author SimpleSpring
 */
public class AspectProcessor {

  /**
   * Bean 工厂
   */
  private final BeanFactory beanFactory;

  /**
   * 切面定义缓存
   */
  private final Map<String, AspectDefinition> aspectDefinitions;

  /**
   * 需要代理的 Bean 缓存
   */
  private final Map<String, Boolean> proxyCache;

  /**
   * 构造函数
   * 
   * @param beanFactory Bean 工厂
   */
  public AspectProcessor(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
    this.aspectDefinitions = new ConcurrentHashMap<String, AspectDefinition>();
    this.proxyCache = new ConcurrentHashMap<String, Boolean>();
  }

  /**
   * 扫描和注册切面 Bean
   * 
   * @param beanNames 所有 Bean 名称
   */
  public void processAspects(String[] beanNames) {
    // 清理缓存
    aspectDefinitions.clear();
    proxyCache.clear();

    // 扫描切面 Bean
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
      if (beanDefinition != null && isAspectClass(beanDefinition.getBeanClass())) {
        registerAspect(beanName, beanDefinition);
      }
    }
  }

  /**
   * 判断是否需要为指定 Bean 创建代理
   * 
   * @param beanName Bean 名称
   * @param bean     Bean 实例
   * @return 如果需要代理返回代理对象，否则返回原始对象
   */
  public Object postProcessAfterInitialization(String beanName, Object bean) {
    if (bean == null) {
      return null;
    }

    // 检查缓存
    Boolean needsProxy = proxyCache.get(beanName);
    if (needsProxy == null) {
      needsProxy = shouldCreateProxy(bean.getClass());
      proxyCache.put(beanName, needsProxy);
    }

    if (needsProxy) {
      return createProxy(bean);
    }

    return bean;
  }

  /**
   * 判断类是否是切面类
   * 
   * @param clazz 类
   * @return 如果是切面类返回 true，否则返回 false
   */
  private boolean isAspectClass(Class<?> clazz) {
    return clazz != null && clazz.isAnnotationPresent(Aspect.class);
  }

  /**
   * 注册切面
   * 
   * @param beanName       Bean 名称
   * @param beanDefinition Bean 定义
   */
  private void registerAspect(String beanName, BeanDefinition beanDefinition) {
    try {
      // 获取切面实例
      Object aspectInstance = beanFactory.getBean(beanName);
      Class<?> aspectClass = beanDefinition.getBeanClass();

      // 创建切面定义
      AspectDefinition aspectDefinition = new AspectDefinition(aspectInstance, aspectClass);
      aspectDefinition.setAspectName(beanName);

      // 扫描通知方法
      scanAdviceMethods(aspectDefinition, aspectClass, aspectInstance);

      // 注册切面定义
      aspectDefinitions.put(beanName, aspectDefinition);

    } catch (Exception e) {
      throw new RuntimeException("注册切面失败: " + beanName, e);
    }
  }

  /**
   * 扫描通知方法
   * 
   * @param aspectDefinition 切面定义
   * @param aspectClass      切面类
   * @param aspectInstance   切面实例
   */
  private void scanAdviceMethods(AspectDefinition aspectDefinition, Class<?> aspectClass, Object aspectInstance) {
    Method[] methods = aspectClass.getDeclaredMethods();

    for (Method method : methods) {
      // 扫描前置通知
      if (method.isAnnotationPresent(Before.class)) {
        Before before = method.getAnnotation(Before.class);
        AdviceDefinition advice = new AdviceDefinition(method, AdviceType.BEFORE,
            before.value(), aspectInstance);
        aspectDefinition.addAdvice(advice);
      }

      // 扫描后置通知
      if (method.isAnnotationPresent(After.class)) {
        After after = method.getAnnotation(After.class);
        AdviceDefinition advice = new AdviceDefinition(method, AdviceType.AFTER,
            after.value(), aspectInstance);
        aspectDefinition.addAdvice(advice);
      }

      // 扫描返回后通知
      if (method.isAnnotationPresent(AfterReturning.class)) {
        AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);
        AdviceDefinition advice = new AdviceDefinition(method, AdviceType.AFTER_RETURNING,
            afterReturning.value(), aspectInstance);
        advice.setReturningParameter(afterReturning.returning());
        aspectDefinition.addAdvice(advice);
      }
    }
  }

  /**
   * 判断是否应该为指定类创建代理
   * 
   * @param targetClass 目标类
   * @return 如果应该创建代理返回 true，否则返回 false
   */
  private boolean shouldCreateProxy(Class<?> targetClass) {
    // 切面类本身不需要代理
    if (isAspectClass(targetClass)) {
      return false;
    }

    // 检查是否有匹配的切面
    for (AspectDefinition aspectDefinition : aspectDefinitions.values()) {
      if (hasMatchingAdvice(aspectDefinition, targetClass)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 检查切面是否有匹配指定类的通知
   * 
   * @param aspectDefinition 切面定义
   * @param targetClass      目标类
   * @return 如果有匹配的通知返回 true，否则返回 false
   */
  private boolean hasMatchingAdvice(AspectDefinition aspectDefinition, Class<?> targetClass) {
    List<AdviceDefinition> advices = aspectDefinition.getAdvices();

    for (AdviceDefinition advice : advices) {
      if (matchesClass(advice, targetClass)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 检查通知是否匹配指定类
   * 
   * @param advice      通知定义
   * @param targetClass 目标类
   * @return 如果匹配返回 true，否则返回 false
   */
  private boolean matchesClass(AdviceDefinition advice, Class<?> targetClass) {
    try {
      String pointcutExpression = advice.getPointcutExpression();
      if (pointcutExpression == null || pointcutExpression.trim().isEmpty()) {
        return false;
      }

      // 简单的类名匹配逻辑
      // 这里可以集成更复杂的切点表达式解析
      if (pointcutExpression.contains("*")) {
        // 通配符匹配
        String pattern = pointcutExpression.replace("*", ".*");
        return targetClass.getName().matches(pattern) ||
            targetClass.getSimpleName().matches(pattern);
      } else {
        // 精确匹配
        return targetClass.getName().contains(pointcutExpression) ||
            targetClass.getSimpleName().contains(pointcutExpression);
      }

    } catch (Exception e) {
      // 匹配失败，返回 false
      return false;
    }
  }

  /**
   * 创建代理对象
   * 
   * @param target 目标对象
   * @return 代理对象
   */
  private Object createProxy(Object target) {
    try {
      ProxyFactory proxyFactory = new ProxyFactory(target);

      // 添加匹配的切面定义
      for (AspectDefinition aspectDefinition : aspectDefinitions.values()) {
        if (hasMatchingAdvice(aspectDefinition, target.getClass())) {
          proxyFactory.addAspectDefinition(aspectDefinition);
        }
      }

      return proxyFactory.createProxy();

    } catch (Exception e) {
      // 代理创建失败，返回原始对象
      System.err.println("创建代理失败，返回原始对象: " + e.getMessage());
      return target;
    }
  }

  /**
   * 获取所有切面定义
   * 
   * @return 切面定义列表
   */
  public List<AspectDefinition> getAspectDefinitions() {
    return new ArrayList<AspectDefinition>(aspectDefinitions.values());
  }

  /**
   * 获取切面定义数量
   * 
   * @return 切面定义数量
   */
  public int getAspectCount() {
    return aspectDefinitions.size();
  }

  /**
   * 清理缓存
   */
  public void clearCache() {
    aspectDefinitions.clear();
    proxyCache.clear();
  }
}
