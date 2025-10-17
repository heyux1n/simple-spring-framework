package com.simplespring.core;

import com.simplespring.core.convert.DefaultTypeConverter;
import com.simplespring.core.util.ClassUtils;
import com.simplespring.core.util.ReflectionUtils;
import com.simplespring.core.util.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 核心组件性能测试
 * 
 * @author SimpleSpring Framework
 */
public class PerformanceTest {

  private static final int ITERATIONS = 10000;
  private static final long MAX_ACCEPTABLE_TIME_MS = 1000; // 1秒

  @Test
  public void testStringUtilsPerformance() {
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      String test = "com.example.TestClass" + i;

      // 测试常用方法的性能
      StringUtils.hasText(test);
      StringUtils.isEmpty(test);
      StringUtils.capitalize(test);
      StringUtils.uncapitalize(test);
      StringUtils.cleanPath(test);
      StringUtils.getShortName(test);
      StringUtils.tokenizeToStringArray(test, ".");
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("StringUtils performance test: " + duration + "ms for " + ITERATIONS + " iterations");
    assertTrue("StringUtils operations should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testClassUtilsPerformance() throws ClassNotFoundException {
    long startTime = System.currentTimeMillis();

    Class<?>[] testClasses = {
        String.class, Integer.class, Boolean.class, Double.class,
        Object.class, Class.class, Thread.class, System.class
    };

    for (int i = 0; i < ITERATIONS; i++) {
      Class<?> testClass = testClasses[i % testClasses.length];

      // 测试常用方法的性能
      ClassUtils.getShortName(testClass);
      ClassUtils.isPrimitiveType(testClass);
      ClassUtils.isPrimitiveWrapper(testClass);
      ClassUtils.isAssignable(testClass, Object.class);
      ClassUtils.resolvePrimitiveWrapper(testClass);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("ClassUtils performance test: " + duration + "ms for " + ITERATIONS + " iterations");
    assertTrue("ClassUtils operations should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testReflectionUtilsPerformance() {
    long startTime = System.currentTimeMillis();

    Class<?> testClass = TestPerformanceClass.class;

    for (int i = 0; i < ITERATIONS / 10; i++) { // 减少迭代次数，因为反射操作较慢
      // 测试字段查找性能
      Field field = ReflectionUtils.findField(testClass, "testField");
      if (field != null) {
        ReflectionUtils.makeAccessible(field);
      }

      // 测试方法查找性能
      Method method = ReflectionUtils.findMethod(testClass, "testMethod");
      if (method != null) {
        ReflectionUtils.makeAccessible(method);
      }

      // 测试获取所有字段和方法
      ReflectionUtils.getAllFields(testClass);
      ReflectionUtils.getAllMethods(testClass);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("ReflectionUtils performance test: " + duration + "ms for " + (ITERATIONS / 10) + " iterations");
    assertTrue("ReflectionUtils operations should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testTypeConverterPerformance() {
    long startTime = System.currentTimeMillis();

    DefaultTypeConverter converter = new DefaultTypeConverter();

    for (int i = 0; i < ITERATIONS; i++) {
      // 测试常见类型转换的性能
      converter.convertIfNecessary("123", Integer.class);
      converter.convertIfNecessary("true", Boolean.class);
      converter.convertIfNecessary("123.45", Double.class);
      converter.convertIfNecessary(123, String.class);
      converter.convertIfNecessary(true, String.class);

      // 测试类型检查性能
      converter.canConvert(String.class, Integer.class);
      converter.canConvert(Integer.class, String.class);
      converter.canConvert(int.class, Integer.class);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("TypeConverter performance test: " + duration + "ms for " + ITERATIONS + " iterations");
    assertTrue("TypeConverter operations should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testMemoryEfficiency() {
    Runtime runtime = Runtime.getRuntime();

    // 记录初始内存使用
    System.gc();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    // 执行大量操作
    for (int i = 0; i < ITERATIONS; i++) {
      String test = "test" + i;
      StringUtils.hasText(test);
      ClassUtils.getShortName(String.class);

      if (i % 1000 == 0) {
        // 定期检查内存使用
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

    System.out.println("Memory increase: " + (totalMemoryIncrease / 1024 / 1024) + "MB");

    // 内存增长应该在合理范围内
    assertTrue("Memory usage should be reasonable",
        totalMemoryIncrease < 100 * 1024 * 1024); // 100MB
  }

  @Test
  public void testConcurrentPerformance() throws InterruptedException {
    final int threadCount = 4;
    final int operationsPerThread = ITERATIONS / threadCount;
    Thread[] threads = new Thread[threadCount];
    final long[] threadTimes = new long[threadCount];

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          long threadStartTime = System.currentTimeMillis();

          for (int j = 0; j < operationsPerThread; j++) {
            String test = "test" + threadIndex + "_" + j;
            StringUtils.hasText(test);
            ClassUtils.getShortName(String.class);

            DefaultTypeConverter converter = new DefaultTypeConverter();
            converter.convertIfNecessary("123", Integer.class);
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

    System.out.println("Concurrent performance test: " + totalTime + "ms total");
    for (int i = 0; i < threadCount; i++) {
      System.out.println("Thread " + i + ": " + threadTimes[i] + "ms");
    }

    assertTrue("Concurrent operations should complete within acceptable time",
        totalTime < MAX_ACCEPTABLE_TIME_MS * 2);
  }

  // 测试用的性能测试类
  public static class TestPerformanceClass {
    private String testField = "test";
    public int publicField = 123;
    protected boolean protectedField = true;

    private String testMethod() {
      return "test";
    }

    public void publicMethod() {
      // Empty method for testing
    }

    protected void protectedMethod(String param) {
      // Empty method for testing
    }
  }
}
