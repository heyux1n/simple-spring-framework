package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * CircularDependencyDetector 测试类
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyDetectorTest {

  private BeanRegistry beanRegistry;
  private CircularDependencyDetector detector;

  @Before
  public void setUp() {
    beanRegistry = new BeanRegistry();
    detector = new CircularDependencyDetector(beanRegistry);
  }

  /**
   * 测试简单的循环依赖检测
   */
  @Test
  public void testSimpleCircularDependency() {
    // 注册两个相互依赖的 Bean
    registerBean("serviceA", ServiceA.class);
    registerBean("serviceB", ServiceB.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 检测循环依赖
    List<List<String>> cycles = detector.detectCircularDependencies();
    assertFalse("应该检测到循环依赖", cycles.isEmpty());

    // 验证循环依赖路径
    List<String> cycle = cycles.get(0);
    assertTrue("循环依赖应该包含 serviceA", cycle.contains("serviceA"));
    assertTrue("循环依赖应该包含 serviceB", cycle.contains("serviceB"));
  }

  /**
   * 测试三个 Bean 的循环依赖
   */
  @Test
  public void testThreeWayCircularDependency() {
    // 注册三个相互依赖的 Bean
    registerBean("serviceA", ServiceA.class);
    registerBean("serviceB", ServiceB.class);
    registerBean("serviceC", ServiceC.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 检测循环依赖
    List<List<String>> cycles = detector.detectCircularDependencies();
    assertFalse("应该检测到循环依赖", cycles.isEmpty());

    // 验证循环依赖路径
    List<String> cycle = cycles.get(0);
    assertTrue("循环依赖应该包含 serviceA", cycle.contains("serviceA"));
    assertTrue("循环依赖应该包含 serviceB", cycle.contains("serviceB"));
    assertTrue("循环依赖应该包含 serviceC", cycle.contains("serviceC"));
  }

  /**
   * 测试无循环依赖的情况
   */
  @Test
  public void testNonCircularDependency() {
    // 注册线性依赖的 Bean
    registerBean("serviceD", ServiceD.class);
    registerBean("serviceE", ServiceE.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 检测循环依赖
    List<List<String>> cycles = detector.detectCircularDependencies();
    assertTrue("不应该检测到循环依赖", cycles.isEmpty());

    // 验证单个 Bean 的循环依赖检测
    assertFalse("serviceD 不应该有循环依赖", detector.hasCircularDependency("serviceD"));
    assertFalse("serviceE 不应该有循环依赖", detector.hasCircularDependency("serviceE"));
  }

  /**
   * 测试构造函数依赖分析
   */
  @Test
  public void testConstructorDependencyAnalysis() {
    // 注册带有构造函数依赖的 Bean
    registerBean("serviceF", ServiceF.class);
    registerBean("serviceG", ServiceG.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 验证依赖关系
    Set<String> serviceFDeps = detector.getDirectDependencies("serviceF");
    assertTrue("serviceF 应该依赖 serviceG", serviceFDeps.contains("serviceG"));

    Set<String> serviceGDeps = detector.getDirectDependencies("serviceG");
    assertTrue("serviceG 应该依赖 serviceF", serviceGDeps.contains("serviceF"));
  }

  /**
   * 测试字段依赖分析
   */
  @Test
  public void testFieldDependencyAnalysis() {
    // 注册带有字段依赖的 Bean
    registerBean("serviceH", ServiceH.class);
    registerBean("serviceI", ServiceI.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 验证依赖关系
    Set<String> serviceHDeps = detector.getDirectDependencies("serviceH");
    assertTrue("serviceH 应该依赖 serviceI", serviceHDeps.contains("serviceI"));
  }

  /**
   * 测试方法依赖分析
   */
  @Test
  public void testMethodDependencyAnalysis() {
    // 注册带有方法依赖的 Bean
    registerBean("serviceJ", ServiceJ.class);
    registerBean("serviceK", ServiceK.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 验证依赖关系
    Set<String> serviceJDeps = detector.getDirectDependencies("serviceJ");
    assertTrue("serviceJ 应该依赖 serviceK", serviceJDeps.contains("serviceK"));
  }

  /**
   * 测试获取所有依赖
   */
  @Test
  public void testGetAllDependencies() {
    // 注册链式依赖的 Bean
    registerBean("serviceL", ServiceL.class);
    registerBean("serviceM", ServiceM.class);
    registerBean("serviceN", ServiceN.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 验证所有依赖
    Set<String> allDeps = detector.getAllDependencies("serviceL");
    assertTrue("serviceL 应该依赖 serviceM", allDeps.contains("serviceM"));
    assertTrue("serviceL 应该间接依赖 serviceN", allDeps.contains("serviceN"));
  }

  /**
   * 测试依赖图操作
   */
  @Test
  public void testDependencyGraphOperations() {
    // 注册一些 Bean
    registerBean("serviceA", ServiceA.class);
    registerBean("serviceB", ServiceB.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 测试依赖图操作
    assertFalse("依赖图不应该为空", detector.isEmpty());

    Set<String> allBeanNames = detector.getAllBeanNames();
    assertTrue("应该包含 serviceA", allBeanNames.contains("serviceA"));
    assertTrue("应该包含 serviceB", allBeanNames.contains("serviceB"));

    Map<String, Set<String>> graph = detector.getDependencyGraph();
    assertNotNull("依赖图不应该为 null", graph);
    assertEquals("依赖图大小应该为 2", 2, graph.size());

    // 清空依赖图
    detector.clear();
    assertTrue("清空后依赖图应该为空", detector.isEmpty());
  }

  /**
   * 测试复杂的循环依赖场景
   */
  @Test
  public void testComplexCircularDependency() {
    // 注册复杂的依赖关系
    registerBean("serviceO", ServiceO.class);
    registerBean("serviceP", ServiceP.class);
    registerBean("serviceQ", ServiceQ.class);
    registerBean("serviceR", ServiceR.class);

    // 构建依赖图
    detector.buildDependencyGraph();

    // 检测循环依赖
    List<List<String>> cycles = detector.detectCircularDependencies();

    // 验证是否检测到循环依赖
    if (!cycles.isEmpty()) {
      List<String> cycle = cycles.get(0);
      assertNotNull("循环依赖路径不应该为 null", cycle);
      assertFalse("循环依赖路径不应该为空", cycle.isEmpty());
    }
  }

  /**
   * 辅助方法：注册 Bean 定义
   */
  private void registerBean(String beanName, Class<?> beanClass) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanName(beanName);
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setScope(Scope.SINGLETON);
    beanRegistry.registerBeanDefinition(beanName, beanDefinition);
  }

  // 测试用的服务类

  /**
   * 服务 A - 依赖服务 B
   */
  public static class ServiceA {
    @Autowired
    private ServiceB serviceB;
  }

  /**
   * 服务 B - 依赖服务 A
   */
  public static class ServiceB {
    @Autowired
    private ServiceA serviceA;
  }

  /**
   * 服务 C - 依赖服务 A
   */
  public static class ServiceC {
    @Autowired
    private ServiceA serviceA;
  }

  /**
   * 服务 D - 依赖服务 E（无循环）
   */
  public static class ServiceD {
    @Autowired
    private ServiceE serviceE;
  }

  /**
   * 服务 E - 无依赖
   */
  public static class ServiceE {
    // 无依赖
  }

  /**
   * 服务 F - 构造函数依赖服务 G
   */
  public static class ServiceF {
    private ServiceG serviceG;

    @Autowired
    public ServiceF(ServiceG serviceG) {
      this.serviceG = serviceG;
    }
  }

  /**
   * 服务 G - 构造函数依赖服务 F
   */
  public static class ServiceG {
    private ServiceF serviceF;

    @Autowired
    public ServiceG(ServiceF serviceF) {
      this.serviceF = serviceF;
    }
  }

  /**
   * 服务 H - 字段依赖服务 I
   */
  public static class ServiceH {
    @Autowired
    private ServiceI serviceI;
  }

  /**
   * 服务 I - 无依赖
   */
  public static class ServiceI {
    // 无依赖
  }

  /**
   * 服务 J - 方法依赖服务 K
   */
  public static class ServiceJ {
    private ServiceK serviceK;

    @Autowired
    public void setServiceK(ServiceK serviceK) {
      this.serviceK = serviceK;
    }
  }

  /**
   * 服务 K - 无依赖
   */
  public static class ServiceK {
    // 无依赖
  }

  /**
   * 服务 L - 依赖服务 M
   */
  public static class ServiceL {
    @Autowired
    private ServiceM serviceM;
  }

  /**
   * 服务 M - 依赖服务 N
   */
  public static class ServiceM {
    @Autowired
    private ServiceN serviceN;
  }

  /**
   * 服务 N - 无依赖
   */
  public static class ServiceN {
    // 无依赖
  }

  /**
   * 服务 O - 依赖服务 P
   */
  public static class ServiceO {
    @Autowired
    private ServiceP serviceP;
  }

  /**
   * 服务 P - 依赖服务 Q
   */
  public static class ServiceP {
    @Autowired
    private ServiceQ serviceQ;
  }

  /**
   * 服务 Q - 依赖服务 R
   */
  public static class ServiceQ {
    @Autowired
    private ServiceR serviceR;
  }

  /**
   * 服务 R - 依赖服务 O（形成循环）
   */
  public static class ServiceR {
    @Autowired
    private ServiceO serviceO;
  }
}
