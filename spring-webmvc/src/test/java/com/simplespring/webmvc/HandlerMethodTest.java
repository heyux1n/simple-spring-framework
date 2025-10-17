package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * HandlerMethod 类的单元测试
 * 
 * 测试处理器方法的创建、调用和参数匹配功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class HandlerMethodTest {

  private TestController testController;

  @Before
  public void setUp() {
    testController = new TestController();
  }

  @Test
  public void testHandlerMethodCreation() throws NoSuchMethodException {
    // 测试处理器方法的创建
    Method method = TestController.class.getDeclaredMethod("simpleMethod");
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    assertEquals("控制器应该正确", testController, handlerMethod.getController());
    assertEquals("方法应该正确", method, handlerMethod.getMethod());
    assertEquals("方法名应该正确", "simpleMethod", handlerMethod.getMethodName());
    assertEquals("控制器名应该正确", "TestController", handlerMethod.getControllerName());
    assertEquals("返回类型应该正确", String.class, handlerMethod.getReturnType());
    assertEquals("参数类型数组长度应该为 0", 0, handlerMethod.getParameterTypes().length);
  }

  @Test
  public void testMethodInvocation() throws Exception {
    // 测试方法调用
    Method method = TestController.class.getDeclaredMethod("simpleMethod");
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    Object result = handlerMethod.invoke();
    assertEquals("方法调用结果应该正确", "simple", result);
  }

  @Test
  public void testMethodInvocationWithParameters() throws Exception {
    // 测试带参数的方法调用
    Method method = TestController.class.getDeclaredMethod("methodWithParameters", String.class, int.class);
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    Object result = handlerMethod.invoke("test", 123);
    assertEquals("方法调用结果应该正确", "test-123", result);
  }

  @Test
  public void testParameterTypeMatching() throws NoSuchMethodException {
    // 测试参数类型匹配
    Method method = TestController.class.getDeclaredMethod("methodWithParameters", String.class, int.class);
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    assertTrue("应该匹配正确的参数类型", handlerMethod.isParameterTypeMatch(0, String.class));
    assertTrue("应该匹配正确的参数类型", handlerMethod.isParameterTypeMatch(1, int.class));
    assertTrue("应该匹配正确的参数类型", handlerMethod.isParameterTypeMatch(1, Integer.class));

    assertFalse("不应该匹配错误的参数类型", handlerMethod.isParameterTypeMatch(0, int.class));
    assertFalse("不应该匹配超出范围的参数位置", handlerMethod.isParameterTypeMatch(2, String.class));
    assertFalse("不应该匹配负数参数位置", handlerMethod.isParameterTypeMatch(-1, String.class));
  }

  @Test
  public void testCanHandle() throws NoSuchMethodException {
    // 测试参数数量匹配
    Method noParamMethod = TestController.class.getDeclaredMethod("simpleMethod");
    HandlerMethod noParamHandler = new HandlerMethod(testController, noParamMethod);

    Method twoParamMethod = TestController.class.getDeclaredMethod("methodWithParameters", String.class, int.class);
    HandlerMethod twoParamHandler = new HandlerMethod(testController, twoParamMethod);

    assertTrue("无参方法应该能处理 0 个参数", noParamHandler.canHandle(0));
    assertFalse("无参方法不应该能处理 1 个参数", noParamHandler.canHandle(1));

    assertTrue("双参方法应该能处理 2 个参数", twoParamHandler.canHandle(2));
    assertFalse("双参方法不应该能处理 1 个参数", twoParamHandler.canHandle(1));
    assertFalse("双参方法不应该能处理 3 个参数", twoParamHandler.canHandle(3));
  }

  @Test
  public void testMethodSignature() throws NoSuchMethodException {
    // 测试方法签名生成
    Method method = TestController.class.getDeclaredMethod("methodWithParameters", String.class, int.class);
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    String signature = handlerMethod.getMethodSignature();
    assertEquals("方法签名应该正确", "TestController.methodWithParameters(String, int)", signature);
  }

  @Test
  public void testInheritanceParameterMatching() throws NoSuchMethodException {
    // 测试继承关系的参数匹配
    Method method = TestController.class.getDeclaredMethod("methodWithObjectParameter", Object.class);
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    assertTrue("应该匹配 Object 类型", handlerMethod.isParameterTypeMatch(0, Object.class));
    assertTrue("应该匹配 String 类型（继承自 Object）", handlerMethod.isParameterTypeMatch(0, String.class));
    assertTrue("应该匹配 Integer 类型（继承自 Object）", handlerMethod.isParameterTypeMatch(0, Integer.class));
  }

  @Test(expected = RuntimeException.class)
  public void testInvocationException() throws Exception {
    // 测试方法调用异常
    Method method = TestController.class.getDeclaredMethod("methodWithException");
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    handlerMethod.invoke(); // 应该抛出 RuntimeException
  }

  @Test
  public void testEqualsAndHashCode() throws NoSuchMethodException {
    // 测试 equals 和 hashCode 方法
    Method method = TestController.class.getDeclaredMethod("simpleMethod");
    HandlerMethod handlerMethod1 = new HandlerMethod(testController, method);
    HandlerMethod handlerMethod2 = new HandlerMethod(testController, method);

    TestController anotherController = new TestController();
    HandlerMethod handlerMethod3 = new HandlerMethod(anotherController, method);

    assertEquals("相同控制器和方法的处理器方法应该相等", handlerMethod1, handlerMethod2);
    assertNotEquals("不同控制器的处理器方法不应该相等", handlerMethod1, handlerMethod3);

    assertEquals("相同的处理器方法应该有相同的 hashCode",
        handlerMethod1.hashCode(), handlerMethod2.hashCode());
  }

  @Test
  public void testToString() throws NoSuchMethodException {
    // 测试 toString 方法
    Method method = TestController.class.getDeclaredMethod("methodWithParameters", String.class, int.class);
    HandlerMethod handlerMethod = new HandlerMethod(testController, method);

    String toString = handlerMethod.toString();
    assertTrue("toString 应该包含控制器名", toString.contains("TestController"));
    assertTrue("toString 应该包含方法签名", toString.contains("methodWithParameters"));
    assertTrue("toString 应该包含返回类型", toString.contains("String"));
  }

  /**
   * 测试用的控制器类
   */
  static class TestController {

    public String simpleMethod() {
      return "simple";
    }

    public String methodWithParameters(String name, int value) {
      return name + "-" + value;
    }

    public String methodWithObjectParameter(Object obj) {
      return obj.toString();
    }

    public void methodWithException() {
      throw new RuntimeException("测试异常");
    }
  }
}
