package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * MethodInvocation 类测试
 * 
 * @author SimpleSpring
 */
public class MethodInvocationTest {

  private TestService testService;
  private Method testMethod;
  private Object[] testArgs;

  @Before
  public void setUp() throws Exception {
    testService = new TestService();
    testMethod = TestService.class.getMethod("testMethod", String.class, int.class);
    testArgs = new Object[] { "test", 123 };
  }

  @Test
  public void testConstructor() {
    // 测试构造函数
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    assertEquals("应该正确设置目标对象", testService, invocation.getTarget());
    assertEquals("应该正确设置目标方法", testMethod, invocation.getMethod());
    assertArrayEquals("应该正确设置方法参数", testArgs, invocation.getArgs());
    assertEquals("应该正确设置目标类", TestService.class, invocation.getTargetClass());
    assertEquals("应该设置连接点类型为方法执行",
        JoinPoint.JoinPointType.METHOD_EXECUTION, invocation.getJoinPointType());
  }

  @Test
  public void testConstructorWithTargetClass() {
    // 测试带目标类的构造函数
    Class<?> customTargetClass = Object.class;
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs, customTargetClass);

    assertEquals("应该使用指定的目标类", customTargetClass, invocation.getTargetClass());
  }

  @Test
  public void testConstructorWithNullArgs() {
    // 测试 null 参数
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, null);

    assertNotNull("参数数组不应该为 null", invocation.getArgs());
    assertEquals("参数数组应该为空", 0, invocation.getArgs().length);
  }

  @Test
  public void testGetArgsReturnsClone() {
    // 测试 getArgs 返回副本
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    Object[] args1 = invocation.getArgs();
    Object[] args2 = invocation.getArgs();

    assertNotSame("应该返回不同的数组实例", args1, args2);
    assertArrayEquals("数组内容应该相同", args1, args2);

    // 修改返回的数组不应该影响原始数据
    args1[0] = "modified";
    assertNotEquals("修改返回的数组不应该影响原始数据", args1[0], invocation.getArgs()[0]);
  }

  @Test
  public void testGetSignature() {
    // 测试方法签名生成
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    String signature = invocation.getSignature();

    assertNotNull("签名不应该为 null", signature);
    assertTrue("签名应该包含返回类型", signature.contains("String"));
    assertTrue("签名应该包含类名", signature.contains("TestService"));
    assertTrue("签名应该包含方法名", signature.contains("testMethod"));
    assertTrue("签名应该包含参数类型", signature.contains("String"));
    assertTrue("签名应该包含参数类型", signature.contains("int"));
  }

  @Test
  public void testReturnValueAndException() {
    // 测试返回值和异常的设置和获取
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    // 测试返回值
    Object returnValue = "test result";
    invocation.setReturnValue(returnValue);
    assertEquals("应该正确设置和获取返回值", returnValue, invocation.getReturnValue());

    // 测试异常
    Exception exception = new RuntimeException("test exception");
    invocation.setException(exception);
    assertEquals("应该正确设置和获取异常", exception, invocation.getException());
  }

  @Test
  public void testProceed() throws Throwable {
    // 测试方法执行
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    Object result = invocation.proceed();

    assertEquals("应该返回正确的结果", "test-123", result);
    assertEquals("应该设置返回值", result, invocation.getReturnValue());
    assertNull("不应该有异常", invocation.getException());
  }

  @Test
  public void testProceedWithException() throws Throwable {
    // 测试方法执行抛出异常
    Method exceptionMethod = TestService.class.getMethod("throwException");
    MethodInvocation invocation = new MethodInvocation(testService, exceptionMethod, new Object[0]);

    try {
      invocation.proceed();
      fail("应该抛出异常");
    } catch (RuntimeException e) {
      assertEquals("应该抛出正确的异常", "test exception", e.getMessage());
      assertEquals("应该设置异常", e, invocation.getException());
    }
  }

  @Test
  public void testToString() {
    // 测试 toString 方法
    MethodInvocation invocation = new MethodInvocation(testService, testMethod, testArgs);

    String result = invocation.toString();

    assertNotNull("toString 不应该返回 null", result);
    assertTrue("toString 应该包含类名", result.contains("MethodInvocation"));
    assertTrue("toString 应该包含签名信息", result.contains("signature"));
    assertTrue("toString 应该包含参数数量", result.contains("argsCount=2"));
  }

  // 测试用的服务类
  public static class TestService {
    public String testMethod(String str, int num) {
      return str + "-" + num;
    }

    public void throwException() {
      throw new RuntimeException("test exception");
    }
  }
}
