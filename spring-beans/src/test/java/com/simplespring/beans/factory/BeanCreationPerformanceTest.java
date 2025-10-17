package com.simplespring.beans.factory;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.beans.factory.support.DefaultBeanFactory;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Component;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Bean 创建和依赖注入性能测试
 * 
 * @author SimpleSpring Framework
 */
public class BeanCreationPerformanceTest {

  private DefaultBeanFactory beanFactory;
  private static final int ITERATIONS = 1000;
  private static final long MAX_ACCEPTABLE_TIME_MS = 2000; // 2秒

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
  }

  @Test
  public void testSingletonBeanCreationPerformance() {
    // 测试单例 Bean 创建性能

    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(SimpleTestBean.class);
    beanDefinition.setBeanName("simpleTestBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("simpleTestBean", beanDefinition);

    long startTime = System.currentTimeMillis();

    // 多次获取单例 Bean，应该很快
    for (int i = 0; i < ITERATIONS * 10; i++) {
      SimpleTestBean bean = (SimpleTestBean) beanFactory.getBean("simpleTestBean");
      assertNotNull("Bean should be created", bean);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Singleton bean access performance: " + duration + "ms for " +
        (ITERATIONS * 10) + " iterations");
    assertTrue("Singleton bean access should be fast", duration < MAX_ACCEPTABLE_TIME_MS / 4);
  }

  @Test
  public void testPrototypeBeanCreationPerformance() {
    // 测试原型 Bean 创建性能

    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(SimpleTestBean.class);
    beanDefinition.setBeanName("prototypeTestBean");
    beanDefinition.setScope(Scope.PROTOTYPE);

    beanFactory.registerBeanDefinition("prototypeTestBean", beanDefinition);

    long startTime = System.currentTimeMillis();

    // 多次创建原型 Bean
    for (int i = 0; i < ITERATIONS; i++) {
      SimpleTestBean bean = (SimpleTestBean) beanFactory.getBean("prototypeTestBean");
      assertNotNull("Bean should be created", bean);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Prototype bean creation performance: " + duration + "ms for " +
        ITERATIONS + " iterations");
    assertTrue("Prototype bean creation should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testDependencyInjectionPerformance() {
    // 测试依赖注入性能

    // 注册依赖 Bean
    BeanDefinition dependencyDef = new BeanDefinition();
    dependencyDef.setBeanClass(DependencyBean.class);
    dependencyDef.setBeanName("dependencyBean");
    dependencyDef.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition("dependencyBean", dependencyDef);

    // 注册主 Bean
    BeanDefinition mainDef = new BeanDefinition();
    mainDef.setBeanClass(BeanWithDependency.class);
    mainDef.setBeanName("beanWithDependency");
    mainDef.setScope(Scope.PROTOTYPE);
    beanFactory.registerBeanDefinition("beanWithDependency", mainDef);

    long startTime = System.currentTimeMillis();

    // 多次创建带依赖的 Bean
    for (int i = 0; i < ITERATIONS; i++) {
      BeanWithDependency bean = (BeanWithDependency) beanFactory.getBean("beanWithDependency");
      assertNotNull("Bean should be created", bean);
      assertNotNull("Dependency should be injected", bean.getDependencyBean());
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Dependency injection performance: " + duration + "ms for " +
        ITERATIONS + " iterations");
    assertTrue("Dependency injection should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testComplexDependencyGraphPerformance() {
    // 测试复杂依赖图的性能

    // 注册多个相互依赖的 Bean
    registerComplexBeans();

    long startTime = System.currentTimeMillis();

    // 创建复杂依赖的 Bean
    for (int i = 0; i < ITERATIONS / 2; i++) {
      ComplexBeanA beanA = (ComplexBeanA) beanFactory.getBean("complexBeanA");
      assertNotNull("Complex bean A should be created", beanA);
      assertNotNull("Complex bean A should have dependencies", beanA.getBeanB());
      assertNotNull("Complex bean B should have dependencies", beanA.getBeanB().getBeanC());
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Complex dependency graph performance: " + duration + "ms for " +
        (ITERATIONS / 2) + " iterations");
    assertTrue("Complex dependency resolution should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testConcurrentBeanCreation() throws InterruptedException {
    // 测试并发 Bean 创建性能

    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(SimpleTestBean.class);
    beanDefinition.setBeanName("concurrentTestBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("concurrentTestBean", beanDefinition);

    final int threadCount = 4;
    final int operationsPerThread = ITERATIONS / threadCount;
    Thread[] threads = new Thread[threadCount];
    final long[] threadTimes = new long[threadCount];
    final boolean[] threadResults = new boolean[threadCount];

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          long threadStartTime = System.currentTimeMillis();
          try {
            for (int j = 0; j < operationsPerThread; j++) {
              SimpleTestBean bean = (SimpleTestBean) beanFactory.getBean("concurrentTestBean");
              if (bean == null) {
                threadResults[threadIndex] = false;
                return;
              }
            }
            threadResults[threadIndex] = true;
          } catch (Exception e) {
            threadResults[threadIndex] = false;
            e.printStackTrace();
          }
          threadTimes[threadIndex] = System.currentTimeMillis() - threadStartTime;
        }
      });
    }

    // 启动所有线程
    for (Thread thread : threads) {
      thread.start();
    }

    // 等待所有线程完成
    for (Thread thread : threads) {
      thread.join();
    }

    long totalTime = System.currentTimeMillis() - startTime;

    System.out.println("Concurrent bean creation performance: " + totalTime + "ms total");
    for (int i = 0; i < threadCount; i++) {
      System.out.println("Thread " + i + ": " + threadTimes[i] + "ms, success: " + threadResults[i]);
      assertTrue("Thread " + i + " should complete successfully", threadResults[i]);
    }

    assertTrue("Concurrent bean creation should complete within acceptable time",
        totalTime < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testMemoryUsageDuringBeanCreation() {
    // 测试 Bean 创建过程中的内存使用

    Runtime runtime = Runtime.getRuntime();

    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(SimpleTestBean.class);
    beanDefinition.setBeanName("memoryTestBean");
    beanDefinition.setScope(Scope.PROTOTYPE);

    beanFactory.registerBeanDefinition("memoryTestBean", beanDefinition);

    // 记录初始内存
    System.gc();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    // 创建大量 Bean
    for (int i = 0; i < ITERATIONS * 2; i++) {
      SimpleTestBean bean = (SimpleTestBean) beanFactory.getBean("memoryTestBean");
      assertNotNull("Bean should be created", bean);

      // 定期检查内存使用
      if (i % 200 == 0) {
        long currentMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = currentMemory - initialMemory;

        // 如果内存增长过快，触发垃圾回收
        if (memoryIncrease > 50 * 1024 * 1024) { // 50MB
          System.gc();
          Thread.yield();
        }
      }
    }

    // 最终内存检查
    System.gc();
    long finalMemory = runtime.totalMemory() - runtime.freeMemory();
    long totalMemoryIncrease = finalMemory - initialMemory;

    System.out.println("Memory increase during bean creation: " +
        (totalMemoryIncrease / 1024 / 1024) + "MB");

    // 内存增长应该在合理范围内
    assertTrue("Memory usage should be reasonable",
        totalMemoryIncrease < 100 * 1024 * 1024); // 100MB
  }

  private void registerComplexBeans() {
    // 注册 ComplexBeanC
    BeanDefinition beanCDef = new BeanDefinition();
    beanCDef.setBeanClass(ComplexBeanC.class);
    beanCDef.setBeanName("complexBeanC");
    beanCDef.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition("complexBeanC", beanCDef);

    // 注册 ComplexBeanB
    BeanDefinition beanBDef = new BeanDefinition();
    beanBDef.setBeanClass(ComplexBeanB.class);
    beanBDef.setBeanName("complexBeanB");
    beanBDef.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition("complexBeanB", beanBDef);

    // 注册 ComplexBeanA
    BeanDefinition beanADef = new BeanDefinition();
    beanADef.setBeanClass(ComplexBeanA.class);
    beanADef.setBeanName("complexBeanA");
    beanADef.setScope(Scope.PROTOTYPE);
    beanFactory.registerBeanDefinition("complexBeanA", beanADef);
  }

  // 测试用的简单 Bean
  public static class SimpleTestBean {
    private String value = "test";

    public String getValue() {
      return value;
    }
  }

  // 测试用的依赖 Bean
  @Component
  public static class DependencyBean {
    public String doSomething() {
      return "dependency working";
    }
  }

  // 测试用的带依赖的 Bean
  @Component
  public static class BeanWithDependency {
    @Autowired
    private DependencyBean dependencyBean;

    public DependencyBean getDependencyBean() {
      return dependencyBean;
    }
  }

  // 复杂依赖测试 Bean
  @Component
  public static class ComplexBeanA {
    @Autowired
    private ComplexBeanB beanB;

    public ComplexBeanB getBeanB() {
      return beanB;
    }
  }

  @Component
  public static class ComplexBeanB {
    @Autowired
    private ComplexBeanC beanC;

    public ComplexBeanC getBeanC() {
      return beanC;
    }
  }

  @Component
  public static class ComplexBeanC {
    public String doSomething() {
      return "complex bean C working";
    }
  }
}
