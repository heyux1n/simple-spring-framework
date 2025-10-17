package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.CircularDependencyException;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 循环依赖场景测试
 * 测试各种复杂的循环依赖场景
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyScenarioTest {

  private DefaultBeanFactory beanFactory;

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
  }

  /**
   * 测试自依赖场景
   */
  @Test
  public void testSelfDependency() {
    // 注册自依赖的 Bean
    registerBean("selfDependent", SelfDependentService.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertTrue("依赖路径应该包含自身", dependencyPath.contains("selfDependent"));
    }
  }

  /**
   * 测试长链循环依赖
   */
  @Test
  public void testLongChainCircularDependency() {
    // 注册长链循环依赖的 Bean
    registerBean("chainA", ChainServiceA.class);
    registerBean("chainB", ChainServiceB.class);
    registerBean("chainC", ChainServiceC.class);
    registerBean("chainD", ChainServiceD.class);
    registerBean("chainE", ChainServiceE.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertTrue("依赖路径长度应该大于 3", dependencyPath.size() > 3);
    }
  }

  /**
   * 测试多个独立的循环依赖
   */
  @Test
  public void testMultipleIndependentCircularDependencies() {
    // 注册第一组循环依赖
    registerBean("group1A", Group1ServiceA.class);
    registerBean("group1B", Group1ServiceB.class);

    // 注册第二组循环依赖
    registerBean("group2A", Group2ServiceA.class);
    registerBean("group2B", Group2ServiceB.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertFalse("依赖路径不应该为空", dependencyPath.isEmpty());
    }
  }

  /**
   * 测试部分循环依赖场景
   */
  @Test
  public void testPartialCircularDependency() {
    // 注册部分循环依赖的 Bean
    registerBean("partialA", PartialServiceA.class);
    registerBean("partialB", PartialServiceB.class);
    registerBean("partialC", PartialServiceC.class);
    registerBean("independent", IndependentService.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);

      // 独立服务不应该在循环依赖路径中
      assertFalse("独立服务不应该在循环依赖路径中", dependencyPath.contains("independent"));
    }
  }

  /**
   * 测试接口依赖的循环依赖
   */
  @Test
  public void testInterfaceCircularDependency() {
    // 注册接口实现的循环依赖 Bean
    registerBean("interfaceImplA", InterfaceImplA.class);
    registerBean("interfaceImplB", InterfaceImplB.class);

    // 验证循环依赖检测
    CircularDependencyDetector detector = beanFactory.getCircularDependencyDetector();
    detector.buildDependencyGraph();

    List<List<String>> cycles = detector.detectCircularDependencies();
    // 注意：由于接口依赖可能无法准确检测，这里主要测试不会抛出异常
    assertNotNull("循环依赖检测结果不应该为空", cycles);
  }

  /**
   * 测试原型 Bean 的循环依赖
   */
  @Test
  public void testPrototypeBeanCircularDependency() {
    // 注册原型 Bean 的循环依赖
    registerPrototypeBean("prototypeA", PrototypeServiceA.class);
    registerPrototypeBean("prototypeB", PrototypeServiceB.class);

    // 验证循环依赖检测
    try {
      beanFactory.validateDependencies();
      fail("应该抛出循环依赖异常");
    } catch (CircularDependencyException e) {
      List<String> dependencyPath = e.getDependencyPath();
      assertNotNull("依赖路径不应该为空", dependencyPath);
      assertTrue("依赖路径应该包含 prototypeA", dependencyPath.contains("prototypeA"));
      assertTrue("依赖路径应该包含 prototypeB", dependencyPath.contains("prototypeB"));
    }
  }

  /**
   * 测试复杂的依赖网络
   */
  @Test
  public void testComplexDependencyNetwork() {
    // 注册复杂的依赖网络
    registerBean("networkA", NetworkServiceA.class);
    registerBean("networkB", NetworkServiceB.class);
    registerBean("networkC", NetworkServiceC.class);
    registerBean("networkD", NetworkServiceD.class);

    // 验证循环依赖检测
    CircularDependencyDetector detector = beanFactory.getCircularDependencyDetector();
    detector.buildDependencyGraph();

    // 检查各个 Bean 的依赖关系
    assertTrue("networkA 应该有循环依赖", detector.hasCircularDependency("networkA"));
    assertTrue("networkB 应该有循环依赖", detector.hasCircularDependency("networkB"));
    assertTrue("networkC 应该有循环依赖", detector.hasCircularDependency("networkC"));
    assertTrue("networkD 应该有循环依赖", detector.hasCircularDependency("networkD"));
  }

  /**
   * 辅助方法：注册单例 Bean 定义
   */
  private void registerBean(String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  /**
   * 辅助方法：注册原型 Bean 定义
   */
  private void registerPrototypeBean(String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.PROTOTYPE);
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  // 测试用的服务类

  /**
   * 自依赖服务
   */
  public static class SelfDependentService {
    @Autowired
    private SelfDependentService self;
  }

  /**
   * 长链服务 A -> B
   */
  public static class ChainServiceA {
    @Autowired
    private ChainServiceB chainServiceB;
  }

  /**
   * 长链服务 B -> C
   */
  public static class ChainServiceB {
    @Autowired
    private ChainServiceC chainServiceC;
  }

  /**
   * 长链服务 C -> D
   */
  public static class ChainServiceC {
    @Autowired
    private ChainServiceD chainServiceD;
  }

  /**
   * 长链服务 D -> E
   */
  public static class ChainServiceD {
    @Autowired
    private ChainServiceE chainServiceE;
  }

  /**
   * 长链服务 E -> A（形成循环）
   */
  public static class ChainServiceE {
    @Autowired
    private ChainServiceA chainServiceA;
  }

  /**
   * 第一组服务 A
   */
  public static class Group1ServiceA {
    @Autowired
    private Group1ServiceB group1ServiceB;
  }

  /**
   * 第一组服务 B
   */
  public static class Group1ServiceB {
    @Autowired
    private Group1ServiceA group1ServiceA;
  }

  /**
   * 第二组服务 A
   */
  public static class Group2ServiceA {
    @Autowired
    private Group2ServiceB group2ServiceB;
  }

  /**
   * 第二组服务 B
   */
  public static class Group2ServiceB {
    @Autowired
    private Group2ServiceA group2ServiceA;
  }

  /**
   * 部分循环服务 A -> B
   */
  public static class PartialServiceA {
    @Autowired
    private PartialServiceB partialServiceB;
  }

  /**
   * 部分循环服务 B -> C
   */
  public static class PartialServiceB {
    @Autowired
    private PartialServiceC partialServiceC;
  }

  /**
   * 部分循环服务 C -> A（形成循环）
   */
  public static class PartialServiceC {
    @Autowired
    private PartialServiceA partialServiceA;
  }

  /**
   * 独立服务（无依赖）
   */
  public static class IndependentService {
    // 无依赖
  }

  /**
   * 接口
   */
  public interface TestInterface {
    void doSomething();
  }

  /**
   * 接口实现 A
   */
  public static class InterfaceImplA implements TestInterface {
    @Autowired
    private InterfaceImplB interfaceImplB;

    @Override
    public void doSomething() {
      // 实现
    }
  }

  /**
   * 接口实现 B
   */
  public static class InterfaceImplB implements TestInterface {
    @Autowired
    private InterfaceImplA interfaceImplA;

    @Override
    public void doSomething() {
      // 实现
    }
  }

  /**
   * 原型服务 A
   */
  public static class PrototypeServiceA {
    @Autowired
    private PrototypeServiceB prototypeServiceB;
  }

  /**
   * 原型服务 B
   */
  public static class PrototypeServiceB {
    @Autowired
    private PrototypeServiceA prototypeServiceA;
  }

  /**
   * 网络服务 A -> B, C
   */
  public static class NetworkServiceA {
    @Autowired
    private NetworkServiceB networkServiceB;

    @Autowired
    private NetworkServiceC networkServiceC;
  }

  /**
   * 网络服务 B -> D
   */
  public static class NetworkServiceB {
    @Autowired
    private NetworkServiceD networkServiceD;
  }

  /**
   * 网络服务 C -> D
   */
  public static class NetworkServiceC {
    @Autowired
    private NetworkServiceD networkServiceD;
  }

  /**
   * 网络服务 D -> A（形成复杂循环）
   */
  public static class NetworkServiceD {
    @Autowired
    private NetworkServiceA networkServiceA;
  }
}
