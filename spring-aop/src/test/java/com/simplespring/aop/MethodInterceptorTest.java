package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * MethodInterceptor 接口测试
 * 
 * @author SimpleSpring
 */
public class MethodInterceptorTest {

  @Test
  public void testMethodInterceptorInterface() {
    // 测试 MethodInterceptor 接口的基本功能
    MethodInterceptor interceptor = new TestMethodInterceptor();

    assertNotNull("拦截器不应该为 null", interceptor);
    assertTrue("应该是 MethodInterceptor 的实例", interceptor instanceof MethodInterceptor);
  }

  @Test
  public void testMethodInterceptorExecution() throws Throwable {
    // 测试方法拦截器的执行
    TestMethodInterceptor interceptor = new TestMethodInterceptor();
    TestService service = new TestService();

    try {
      java.lang.reflect.Method method = TestService.class.getMethod("testMethod", String.class);
      MethodInvocation invocation = new MethodInvocation(service, method, new Object[] { "test" });

      Object result = interceptor.intercept(invocation);

      assertTrue("拦截器应该被调用", interceptor.intercepted);
      assertEquals("应该返回正确的结果", "intercepted: result: test", result);
    } catch (Exception e) {
      fail("不应该抛出异常: " + e.getMessage());
    }
  }

  // 测试用的方法拦截器
  private static class TestMethodInterceptor implements MethodInterceptor {
    public boolean intercepted = false;

    @Override
    public Object intercept(MethodInvocation invocation) throws Throwable {
      intercepted = true;
      Object result = invocation.proceed();
      return "intercepted: " + result;
    }
  }

  // 测试用的服务类
  public static class TestService {
    public String testMethod(String input) {
      return "result: " + input;
    }
  }
}
