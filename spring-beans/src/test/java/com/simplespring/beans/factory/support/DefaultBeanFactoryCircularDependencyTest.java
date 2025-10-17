package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanCreationException;
import com.simplespring.beans.factory.CircularDependencyException;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * DefaultBeanFactory 循环依赖检测集成测试
 * 
 * @author SimpleSpring Framework
 */
public class DefaultBeanFactoryCircularDependencyTest {

  private DefaultBeanFactory beanFactory;

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
  }

  /**
   * 测试简单的循环依赖检测
   */
  @Test
  public void testSimpleCircularDependencyDetection() {
    // 注册两个相互依赖的 Bean
    registerBean("circularA", CircularServiceA.class);
    registerBean("circularB", CircularServiceB.class);

    // 验证依赖关系
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      assertNotNull("异常消息不应该为空", e.getMessage());
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertFalse("依赖路径不应该为空", dependencyPath.isEmpty());

      String formattedPath = e.getFormattedDependencyPath();
      assertNotNull("格式化的依赖路径不应该为空", formattedPath);
      assertTrue("格式化的依赖路径应该包含箭头", formattedPath.contains("->"));
    }
  }

  /**
   * 测试在 Bean 创建时检测循环依赖
   */
  @Test
  public void testCircularDependencyDetectionDuringBeanCreation() {
    // 注册两个相互依赖的 Bean
    registerBean("circularA", CircularServiceA.class);
    registerBean("circularB", CircularServiceB.class);

    // 尝试获取 Bean，应该抛出循环依赖异常
    try {
      beanFactory.getBean("circularA");
      fail("应该抛出循环依赖异常");
    } catch (BeanCreationException e) {
      // 验证异常类型
      assertTrue("应该是循环依赖异常或包含循环依赖信息",
          e instanceof CircularDependencyException ||
              e.getMessage().contains("循环依赖"));
    }
  }

  /**
   * 测试三个 Bean 的循环依赖
   */
  @Test
  public void testThreeWayCircularDependency() {
    // 注册三个相互依赖的 Bean
    registerBean("circularX", CircularServiceX.class);
    registerBean("circularY", CircularServiceY.class);
    registerBean("circularZ", CircularServiceZ.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertTrue("依赖路径长度应该大于 2", dependencyPath.size() > 2);
    }
  }

  /**
   * 测试无循环依赖的正常情况
   */
  @Test
  public void testNonCircularDependencyValidation() {
    // 注册线性依赖的 Bean
    registerBean("normalA", NormalServiceA.class);
    registerBean("normalB", NormalServiceB.class);

    // 验证依赖关系（不应该抛出异常）
    try {
      beanFactory.validateDependencies();
    } catch (CircularDependencyException e) {
      fail("不应该抛出循环依赖异常: " + e.getMessage());
    }

    // 验证 Bean 可以正常创建
    Object beanA = beanFactory.getBean("normalA");
    assertNotNull("Bean A 应该能够正常创建", beanA);
    assertTrue("Bean A 应该是正确的类型", beanA instanceof NormalServiceA);

    Object beanB = beanFactory.getBean("normalB");
    assertNotNull("Bean B 应该能够正常创建", beanB);
    assertTrue("Bean B 应该是正确的类型", beanB instanceof NormalServiceB);
  }

  /**
   * 测试单个 Bean 的循环依赖检测
   */
  @Test
  public void testSingleBeanCircularDependencyCheck() {
    // 注册一些 Bean
    registerBean("circularA", CircularServiceA.class);
    registerBean("circularB", CircularServiceB.class);
    registerBean("normalA", NormalServiceA.class);

    // 检查单个 Bean 的循环依赖
    assertTrue("circularA 应该有循环依赖", beanFactory.hasCircularDependency("circularA"));
    assertTrue("circularB 应该有循环依赖", beanFactory.hasCircularDependency("circularB"));
    assertFalse("normalA 不应该有循环依赖", beanFactory.hasCircularDependency("normalA"));
  }

  /**
   * 测试构造函数循环依赖
   */
  @Test
  public void testConstructorCircularDependency() {
    // 注册带有构造函数循环依赖的 Bean
    registerBean("constructorA", ConstructorCircularA.class);
    registerBean("constructorB", ConstructorCircularB.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      assertNotNull("异常消息不应该为空", e.getMessage());
    }
  }

  /**
   * 测试混合依赖类型的循环依赖
   */
  @Test
  public void testMixedDependencyTypeCircularDependency() {
    // 注册混合依赖类型的 Bean
    registerBean("mixedA", MixedDependencyA.class);
    registerBean("mixedB", MixedDependencyB.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertTrue("依赖路径应该包含 mixedA", dependencyPath.contains("mixedA"));
      assertTrue("依赖路径应该包含 mixedB", dependencyPath.contains("mixedB"));
    }
  }

  /**
   * 测试循环依赖检测器的访问
   */
  @Test
  public void testCircularDependencyDetectorAccess() {
    CircularDependencyDetector detector = beanFactory.getCircularDependencyDetector();
    assertNotNull("循环依赖检测器不应该为空", detector);

    // 注册一些 Bean
    registerBean("testA", CircularServiceA.class);
    registerBean("testB", CircularServiceB.class);

    // 验证检测器功能
    detector.buildDependencyGraph();
    assertFalse("依赖图不应该为空", detector.isEmpty());

    List<List<String>> cycles = detector.detectCircularDependencies();
    assertFalse("应该检测到循环依赖", cycles.isEmpty());
  }

  /**
   * 辅助方法：注册 Bean 定义
   */
  private void registerBean(String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  // 测试用的服务类

  /**
   * 循环服务 A - 依赖循环服务 B
   */
  public static class CircularServiceA {
    @Autowired
    private CircularServiceB circularServiceB;

    public CircularServiceB getCircularServiceB() {
      return circularServiceB;
    }
  }

  /**
   * 循环服务 B - 依赖循环服务 A
   */
  public static class CircularServiceB {
    @Autowired
    private CircularServiceA circularServiceA;

    public CircularServiceA getCircularServiceA() {
      return circularServiceA;
    }
  }

  /**
   * 循环服务 X - 依赖循环服务 Y
   */
  public static class CircularServiceX {
    @Autowired
    private CircularServiceY circularServiceY;
  }

  /**
   * 循环服务 Y - 依赖循环服务 Z
   */
  public static class CircularServiceY {
    @Autowired
    private CircularServiceZ circularServiceZ;
  }

  /**
   * 循环服务 Z - 依赖循环服务 X
   */
  public static class CircularServiceZ {
    @Autowired
    private CircularServiceX circularServiceX;
  }

  /**
   * 正常服务 A - 依赖正常服务 B
   */
  public static class NormalServiceA {
    @Autowired
    private NormalServiceB normalServiceB;

    public NormalServiceB getNormalServiceB() {
      return normalServiceB;
    }
  }

  /**
   * 正常服务 B - 无依赖
   */
  public static class NormalServiceB {
    // 无依赖
  }

  /**
   * 构造函数循环 A - 构造函数依赖构造函数循环 B
   */
  public static class ConstructorCircularA {
    private ConstructorCircularB constructorCircularB;

    @Autowired
    public ConstructorCircularA(ConstructorCircularB constructorCircularB) {
      this.constructorCircularB = constructorCircularB;
    }
  }

  /**
   * 构造函数循环 B - 构造函数依赖构造函数循环 A
   */
  public static class ConstructorCircularB {
    private ConstructorCircularA constructorCircularA;

    @Autowired
    public ConstructorCircularB(ConstructorCircularA constructorCircularA) {
      this.constructorCircularA = constructorCircularA;
    }
  }

  /**
   * 混合依赖 A - 字段依赖混合依赖 B
   */
  public static class MixedDependencyA {
    @Autowired
    private MixedDependencyB mixedDependencyB;
  }

  /**
   * 混合依赖 B - 方法依赖混合依赖 A
   */
  public static class MixedDependencyB {
    private MixedDependencyA mixedDependencyA;

    @Autowired
    public void setMixedDependencyA(MixedDependencyA mixedDependencyA) {
      this.mixedDependencyA = mixedDependencyA;
    }
  }
}
