package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.CircularDependencyException;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.Autowired;

import java.util.List;

/**
 * 循环依赖测试运行器
 * 用于手动测试循环依赖检测功能
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyTestRunner {

  public static void main(String[] args) {
    System.out.println("开始测试循环依赖检测功能...");

    // 测试基本的循环依赖检测
    testBasicCircularDependency();

    // 测试无循环依赖的情况
    testNonCircularDependency();

    // 测试复杂的循环依赖
    testComplexCircularDependency();

    System.out.println("所有测试完成！");
  }

  /**
   * 测试基本的循环依赖检测
   */
  private static void testBasicCircularDependency() {
    System.out.println("\n=== 测试基本循环依赖检测 ===");

    try {
      BeanRegistry beanRegistry = new BeanRegistry();
      CircularDependencyDetector detector = new CircularDependencyDetector(beanRegistry);

      // 注册两个相互依赖的 Bean
      registerBean(beanRegistry, "serviceA", TestServiceA.class);
      registerBean(beanRegistry, "serviceB", TestServiceB.class);

      // 构建依赖图
      detector.buildDependencyGraph();
      System.out.println("依赖图构建完成");

      // 检测循环依赖
      List<List<String>> cycles = detector.detectCircularDependencies();
      if (!cycles.isEmpty()) {
        System.out.println("检测到循环依赖:");
        for (List<String> cycle : cycles) {
          System.out.println("  循环路径: " + cycle);
        }
      } else {
        System.out.println("未检测到循环依赖");
      }

      // 测试单个 Bean 的循环依赖检测
      boolean hasCircular = detector.hasCircularDependency("serviceA");
      System.out.println("serviceA 是否有循环依赖: " + hasCircular);

    } catch (Exception e) {
      System.out.println("测试失败: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * 测试无循环依赖的情况
   */
  private static void testNonCircularDependency() {
    System.out.println("\n=== 测试无循环依赖情况 ===");

    try {
      BeanRegistry beanRegistry = new BeanRegistry();
      CircularDependencyDetector detector = new CircularDependencyDetector(beanRegistry);

      // 注册线性依赖的 Bean
      registerBean(beanRegistry, "serviceC", TestServiceC.class);
      registerBean(beanRegistry, "serviceD", TestServiceD.class);

      // 构建依赖图
      detector.buildDependencyGraph();
      System.out.println("依赖图构建完成");

      // 检测循环依赖
      List<List<String>> cycles = detector.detectCircularDependencies();
      if (!cycles.isEmpty()) {
        System.out.println("意外检测到循环依赖:");
        for (List<String> cycle : cycles) {
          System.out.println("  循环路径: " + cycle);
        }
      } else {
        System.out.println("正确：未检测到循环依赖");
      }

    } catch (Exception e) {
      System.out.println("测试失败: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * 测试复杂的循环依赖
   */
  private static void testComplexCircularDependency() {
    System.out.println("\n=== 测试复杂循环依赖 ===");

    try {
      DefaultBeanFactory beanFactory = new DefaultBeanFactory();

      // 注册三个相互依赖的 Bean
      registerBean(beanFactory, "serviceX", TestServiceX.class);
      registerBean(beanFactory, "serviceY", TestServiceY.class);
      registerBean(beanFactory, "serviceZ", TestServiceZ.class);

      // 验证依赖关系
      try {
        beanFactory.validateDependencies();
        System.out.println("意外：未检测到循环依赖");
      } catch (CircularDependencyException e) {
        System.out.println("正确：检测到循环依赖");
        System.out.println("异常消息: " + e.getMessage());
        System.out.println("依赖路径: " + e.getFormattedDependencyPath());
      }

    } catch (Exception e) {
      System.out.println("测试失败: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * 辅助方法：注册 Bean 定义到 BeanRegistry
   */
  private static void registerBean(BeanRegistry beanRegistry, String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.SINGLETON);
    beanRegistry.registerBeanDefinition(beanName, beanDefinition);
  }

  /**
   * 辅助方法：注册 Bean 定义到 DefaultBeanFactory
   */
  private static void registerBean(DefaultBeanFactory beanFactory, String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  // 测试用的服务类

  /**
   * 测试服务 A - 依赖服务 B
   */
  public static class TestServiceA {
    @Autowired
    private TestServiceB serviceB;
  }

  /**
   * 测试服务 B - 依赖服务 A
   */
  public static class TestServiceB {
    @Autowired
    private TestServiceA serviceA;
  }

  /**
   * 测试服务 C - 依赖服务 D
   */
  public static class TestServiceC {
    @Autowired
    private TestServiceD serviceD;
  }

  /**
   * 测试服务 D - 无依赖
   */
  public static class TestServiceD {
    // 无依赖
  }

  /**
   * 测试服务 X - 依赖服务 Y
   */
  public static class TestServiceX {
    @Autowired
    private TestServiceY serviceY;
  }

  /**
   * 测试服务 Y - 依赖服务 Z
   */
  public static class TestServiceY {
    @Autowired
    private TestServiceZ serviceZ;
  }

  /**
   * 测试服务 Z - 依赖服务 X
   */
  public static class TestServiceZ {
    @Autowired
    private TestServiceX serviceX;
  }
}
