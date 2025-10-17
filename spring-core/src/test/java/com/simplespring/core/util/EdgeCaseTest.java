package com.simplespring.core.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 边界条件和异常场景的综合测试
 * 
 * @author SimpleSpring Framework
 */
public class EdgeCaseTest {

  @Test
  public void testStringUtilsEdgeCases() {
    // 测试极长字符串
    StringBuilder longString = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longString.append("a");
    }
    String veryLongString = longString.toString();

    assertTrue(StringUtils.hasText(veryLongString));
    assertEquals("a", StringUtils.getShortName(veryLongString));

    // 测试包含特殊字符的字符串
    String specialChars = "测试中文/\\:*?\"<>|";
    assertTrue(StringUtils.hasText(specialChars));
    assertEquals("测试中文/:*?\"<>|", StringUtils.cleanPath(specialChars));

    // 测试Unicode字符
    String unicode = "Hello\u0000World\uFFFF";
    assertTrue(StringUtils.hasText(unicode));
    assertEquals("Hello\u0000World\uFFFF", StringUtils.capitalize(unicode));
  }

  @Test
  public void testClassUtilsEdgeCases() throws ClassNotFoundException {
    // 测试数组类型
    Class<?> arrayClass = ClassUtils.forName("[Ljava.lang.String;", null);
    assertEquals(String[].class, arrayClass);
    assertEquals("String[]", ClassUtils.getShortName(arrayClass));

    // 测试基本类型数组
    Class<?> intArrayClass = ClassUtils.forName("[I", null);
    assertEquals(int[].class, intArrayClass);
    assertEquals("int[]", ClassUtils.getShortName(intArrayClass));

    // 测试内部类
    String innerClassName = EdgeCaseTest.class.getName() + "$TestInnerClass";
    try {
      ClassUtils.forName(innerClassName, null);
      fail("Should throw ClassNotFoundException");
    } catch (ClassNotFoundException e) {
      // Expected
    }

    // 测试泛型类型的兼容性
    assertTrue(ClassUtils.isAssignable(String.class, Object.class));
    assertFalse(ClassUtils.isAssignable(Object.class, String.class));
  }

  @Test
  public void testReflectionUtilsEdgeCases() {
    // 测试继承链中的字段查找
    Field field = ReflectionUtils.findField(ChildClass.class, "parentField");
    assertNotNull(field);
    assertEquals("parentField", field.getName());

    // 测试重写方法的查找
    Method method = ReflectionUtils.findMethod(ChildClass.class, "overriddenMethod");
    assertNotNull(method);
    assertEquals(ChildClass.class, method.getDeclaringClass());

    // 测试静态字段和方法
    Field staticField = ReflectionUtils.findField(TestStaticClass.class, "staticField");
    assertNotNull(staticField);
    assertTrue(java.lang.reflect.Modifier.isStatic(staticField.getModifiers()));

    Method staticMethod = ReflectionUtils.findMethod(TestStaticClass.class, "staticMethod");
    assertNotNull(staticMethod);
    assertTrue(java.lang.reflect.Modifier.isStatic(staticMethod.getModifiers()));
  }

  @Test
  public void testConcurrentAccess() throws InterruptedException {
    // 测试多线程并发访问工具类的安全性
    final int threadCount = 10;
    final int operationsPerThread = 100;
    Thread[] threads = new Thread[threadCount];
    final boolean[] results = new boolean[threadCount];

    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            for (int j = 0; j < operationsPerThread; j++) {
              // 测试 StringUtils 的线程安全性
              assertTrue(StringUtils.hasText("test" + j));
              assertEquals("test" + j, StringUtils.capitalize("test" + j));

              // 测试 ClassUtils 的线程安全性
              assertTrue(ClassUtils.isAssignable(String.class, Object.class));
              assertEquals("String", ClassUtils.getShortName(String.class));

              // 测试 ReflectionUtils 的线程安全性
              Field field = ReflectionUtils.findField(String.class, "value");
              if (field != null) {
                ReflectionUtils.makeAccessible(field);
              }
            }
            results[threadIndex] = true;
          } catch (Exception e) {
            results[threadIndex] = false;
            e.printStackTrace();
          }
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

    // 验证所有线程都成功完成
    for (boolean result : results) {
      assertTrue("Thread should complete successfully", result);
    }
  }

  @Test
  public void testMemoryUsage() {
    // 测试大量对象创建不会导致内存泄漏
    Runtime runtime = Runtime.getRuntime();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    // 创建大量字符串操作
    for (int i = 0; i < 10000; i++) {
      String test = "test" + i;
      StringUtils.hasText(test);
      StringUtils.capitalize(test);
      StringUtils.uncapitalize(test);
      StringUtils.cleanPath(test);
    }

    // 强制垃圾回收
    System.gc();
    Thread.yield();

    long finalMemory = runtime.totalMemory() - runtime.freeMemory();
    long memoryIncrease = finalMemory - initialMemory;

    // 内存增长应该在合理范围内（小于10MB）
    assertTrue("Memory increase should be reasonable: " + memoryIncrease,
        memoryIncrease < 10 * 1024 * 1024);
  }

  // 测试用的父类
  public static class ParentClass {
    protected String parentField = "parent";

    public String overriddenMethod() {
      return "parent";
    }

    public String parentMethod() {
      return "parent";
    }
  }

  // 测试用的子类
  public static class ChildClass extends ParentClass {
    private String childField = "child";

    @Override
    public String overriddenMethod() {
      return "child";
    }

    public String childMethod() {
      return "child";
    }
  }

  // 测试用的静态类
  public static class TestStaticClass {
    public static String staticField = "static";

    public static String staticMethod() {
      return "static";
    }

    public String instanceMethod() {
      return "instance";
    }
  }
}
