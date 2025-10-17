package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * AOP 性能测试，测试代理创建和方法拦截的性能开销
 * 
 * @author SimpleSpring Framework
 */
public class AopPerformanceTest {

  private static final int ITERATIONS = 10000;
  private static final long MAX_ACCEPTABLE_TIME_MS = 2000; // 2秒
  private static final double MAX_ACCEPTABLE_OVERHEAD = 3.0; // 最大3倍性能开销

  private TestService originalService;
  private TestService proxiedService;
  private TestAspect aspect;

  @Before
  public void setUp() throws Exception {
    originalService = new TestService();
    aspect = new TestAspect();

    // 创建代理服务
    proxiedService = createProxiedService();
  }

  @Test
  public void testProxyCreationPerformance() {
    // 测试代理创建性能

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS / 10; i++) {
      ProxyFactory proxyFactory = new ProxyFactory(new TestService());

      // 添加切面
      try {
        Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
        AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
            "execution(* *.doSomething(..))", aspect);
        AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
        aspectDef.addAdvice(advice);
        proxyFactory.addAspectDefinition(aspectDef);

        Object proxy = proxyFactory.createProxy();
        assertNotNull("Proxy should be created", proxy);
      } catch (Exception e) {
        fail("Proxy creation should not fail: " + e.getMessage());
      }
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Proxy creation performance: " + duration + "ms for " +
        (ITERATIONS / 10) + " iterations");
    assertTrue("Proxy creation should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testMethodInvocationOverhead() {
    // 测试方法调用开销

    // 测试原始方法调用性能
    long originalStartTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      String result = originalService.doSomething("test" + i);
      assertNotNull("Result should not be null", result);
    }

    long originalEndTime = System.currentTimeMillis();
    long originalDuration = originalEndTime - originalStartTime;

    // 测试代理方法调用性能
    long proxiedStartTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      String result = proxiedService.doSomething("test" + i);
      assertNotNull("Result should not be null", result);
    }

    long proxiedEndTime = System.currentTimeMillis();
    long proxiedDuration = proxiedEndTime - proxiedStartTime;

    System.out.println("Original method calls: " + originalDuration + "ms");
    System.out.println("Proxied method calls: " + proxiedDuration + "ms");

    double overhead = (double) proxiedDuration / originalDuration;
    System.out.println("Performance overhead: " + String.format("%.2f", overhead) + "x");

    assertTrue("Proxied calls should complete within acceptable time",
        proxiedDuration < MAX_ACCEPTABLE_TIME_MS);
    assertTrue("Performance overhead should be acceptable",
        overhead < MAX_ACCEPTABLE_OVERHEAD);
  }

  @Test
  public void testPointcutMatchingPerformance() {
    // 测试切点匹配性能

    PointcutExpressionParser parser = new PointcutExpressionParser();
    PointcutMatcher matcher = parser.parse("execution(* *.doSomething(..))");

    Method[] methods;
    try {
      methods = new Method[] {
          TestService.class.getMethod("doSomething", String.class),
          TestService.class.getMethod("otherMethod"),
          TestService.class.getMethod("anotherMethod", int.class)
      };
    } catch (NoSuchMethodException e) {
      fail("Test methods should exist");
      return;
    }

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      for (Method method : methods) {
        boolean matches = matcher.matches(method);
        // 验证匹配结果的正确性
        if (method.getName().equals("doSomething")) {
          assertTrue("doSomething should match", matches);
        } else {
          assertFalse("Other methods should not match", matches);
        }
      }
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Pointcut matching performance: " + duration + "ms for " +
        (ITERATIONS * methods.length) + " matches");
    assertTrue("Pointcut matching should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testAdviceExecutionPerformance() throws Exception {
    // 测试通知执行性能

    TestAspect testAspect = new TestAspect();
    Method adviceMethod = TestAspect.class.getMethod("beforeAdvice");
    AdviceExecutor executor = new AdviceExecutor();

    TestService service = new TestService();
    Method targetMethod = TestService.class.getMethod("doSomething", String.class);

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      MethodInvocation invocation = new MethodInvocation(service, targetMethod,
          new Object[] { "test" + i });
      executor.executeBefore(adviceMethod, testAspect, invocation);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Advice execution performance: " + duration + "ms for " +
        ITERATIONS + " executions");
    assertTrue("Advice execution should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
    assertTrue("All advice executions should be called", testAspect.beforeCallCount >= ITERATIONS);
  }

  @Test
  public void testMultipleAspectsPerformance() throws Exception {
    // 测试多个切面的性能影响

    TestService service = new TestService();
    ProxyFactory proxyFactory = new ProxyFactory(service);

    // 添加多个切面
    for (int i = 0; i < 5; i++) {
      TestAspect aspect = new TestAspect();
      Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
      AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
          "execution(* *.doSomething(..))", aspect);
      AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
      aspectDef.addAdvice(advice);
      proxyFactory.addAspectDefinition(aspectDef);
    }

    TestService multiAspectProxy = (TestService) proxyFactory.createProxy();

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS / 2; i++) {
      String result = multiAspectProxy.doSomething("test" + i);
      assertNotNull("Result should not be null", result);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Multiple aspects performance: " + duration + "ms for " +
        (ITERATIONS / 2) + " calls with 5 aspects");
    assertTrue("Multiple aspects should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testConcurrentProxyAccess() throws InterruptedException {
    // 测试并发代理访问性能

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
              String result = proxiedService.doSomething("test" + threadIndex + "_" + j);
              if (result == null || !result.contains("test")) {
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

    System.out.println("Concurrent proxy access performance: " + totalTime + "ms total");
    for (int i = 0; i < threadCount; i++) {
      System.out.println("Thread " + i + ": " + threadTimes[i] + "ms, success: " + threadResults[i]);
      assertTrue("Thread " + i + " should complete successfully", threadResults[i]);
    }

    assertTrue("Concurrent proxy access should complete within acceptable time",
        totalTime < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testMemoryUsageWithProxies() {
    // 测试代理的内存使用

    Runtime runtime = Runtime.getRuntime();

    // 记录初始内存
    System.gc();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    // 创建大量代理
    Object[] proxies = new Object[ITERATIONS / 10];
    for (int i = 0; i < proxies.length; i++) {
      try {
        ProxyFactory proxyFactory = new ProxyFactory(new TestService());
        Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
        AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
            "execution(* *.doSomething(..))", aspect);
        AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
        aspectDef.addAdvice(advice);
        proxyFactory.addAspectDefinition(aspectDef);

        proxies[i] = proxyFactory.createProxy();
      } catch (Exception e) {
        fail("Proxy creation should not fail: " + e.getMessage());
      }

      // 定期检查内存使用
      if (i % 100 == 0) {
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

    System.out.println("Memory increase with " + proxies.length + " proxies: " +
        (totalMemoryIncrease / 1024 / 1024) + "MB");

    // 内存增长应该在合理范围内
    assertTrue("Memory usage should be reasonable",
        totalMemoryIncrease < 100 * 1024 * 1024); // 100MB
  }

  private TestService createProxiedService() throws Exception {
    ProxyFactory proxyFactory = new ProxyFactory(originalService);

    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.doSomething(..))", aspect);
    AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
    aspectDef.addAdvice(advice);
    proxyFactory.addAspectDefinition(aspectDef);

    return (TestService) proxyFactory.createProxy();
  }

  // 测试用的服务类
  public static class TestService {
    public String doSomething(String input) {
      return "TestService: " + input;
    }

    public String otherMethod() {
      return "other";
    }

    public String anotherMethod(int value) {
      return "another: " + value;
    }
  }

  // 测试用的切面
  public static class TestAspect {
    public int beforeCallCount = 0;

    public void beforeAdvice() {
      beforeCallCount++;
    }
  }
}
