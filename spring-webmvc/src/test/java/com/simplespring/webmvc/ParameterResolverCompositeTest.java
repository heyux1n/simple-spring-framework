package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * ParameterResolverComposite 类的单元测试
 * 
 * 测试参数解析器组合类的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ParameterResolverCompositeTest {

  private ParameterResolverComposite composite;
  private BasicTypeParameterResolverTest.MockHttpServletRequest request;
  private BasicTypeParameterResolverTest.MockHttpServletResponse response;

  @Before
  public void setUp() {
    composite = new ParameterResolverComposite();
    request = new BasicTypeParameterResolverTest.MockHttpServletRequest();
    response = new BasicTypeParameterResolverTest.MockHttpServletResponse();
  }

  @Test
  public void testDefaultResolvers() {
    // 测试默认解析器的数量
    assertEquals("应该有 2 个默认解析器", 2, composite.getResolverCount());

    // 测试默认解析器支持的类型
    assertTrue("应该支持 HttpServletRequest", composite.supportsParameter(HttpServletRequest.class));
    assertTrue("应该支持 HttpServletResponse", composite.supportsParameter(HttpServletResponse.class));
    assertTrue("应该支持 String", composite.supportsParameter(String.class));
    assertTrue("应该支持 int", composite.supportsParameter(int.class));
    assertTrue("应该支持 Integer", composite.supportsParameter(Integer.class));

    assertFalse("不应该支持 Object", composite.supportsParameter(Object.class));
  }

  @Test
  public void testAddResolver() {
    // 测试添加自定义解析器
    ParameterResolver customResolver = new ParameterResolver() {
      @Override
      public boolean supportsParameter(Class<?> parameterType) {
        return parameterType == Object.class;
      }

      @Override
      public Object resolveParameter(Class<?> parameterType, String parameterName,
          HttpServletRequest request, HttpServletResponse response) {
        return new Object();
      }
    };

    composite.addResolver(customResolver);

    assertEquals("添加解析器后数量应该增加", 3, composite.getResolverCount());
    assertTrue("应该支持自定义解析器的类型", composite.supportsParameter(Object.class));
  }

  @Test
  public void testAddNullResolver() {
    // 测试添加空解析器
    int originalCount = composite.getResolverCount();
    composite.addResolver(null);

    assertEquals("添加空解析器后数量不应该变化", originalCount, composite.getResolverCount());
  }

  @Test
  public void testResolveParametersNoParams() throws Exception {
    // 测试解析无参数方法
    TestController controller = new TestController();
    Method method = TestController.class.getDeclaredMethod("noParamMethod");
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Object[] args = composite.resolveParameters(handlerMethod, request, response);

    assertNotNull("参数数组不应该为空", args);
    assertEquals("无参数方法应该返回空数组", 0, args.length);
  }

  @Test
  public void testResolveParametersWithBasicTypes() throws Exception {
    // 测试解析基本类型参数
    request.setParameter("param0", "test");
    request.setParameter("param1", "123");

    TestController controller = new TestController();
    Method method = TestController.class.getDeclaredMethod("basicTypeMethod", String.class, int.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Object[] args = composite.resolveParameters(handlerMethod, request, response);

    assertNotNull("参数数组不应该为空", args);
    assertEquals("应该有 2 个参数", 2, args.length);
    assertEquals("第一个参数应该是 String", "test", args[0]);
    assertEquals("第二个参数应该是 int", 123, args[1]);
  }

  @Test
  public void testResolveParametersWithServletTypes() throws Exception {
    // 测试解析 Servlet 类型参数
    TestController controller = new TestController();
    Method method = TestController.class.getDeclaredMethod("servletMethod",
        HttpServletRequest.class, HttpServletResponse.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Object[] args = composite.resolveParameters(handlerMethod, request, response);

    assertNotNull("参数数组不应该为空", args);
    assertEquals("应该有 2 个参数", 2, args.length);
    assertSame("第一个参数应该是 HttpServletRequest", request, args[0]);
    assertSame("第二个参数应该是 HttpServletResponse", response, args[1]);
  }

  @Test
  public void testResolveParametersMixed() throws Exception {
    // 测试解析混合类型参数
    request.setParameter("param0", "hello");

    TestController controller = new TestController();
    Method method = TestController.class.getDeclaredMethod("mixedMethod",
        String.class, HttpServletRequest.class, boolean.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    request.setParameter("param2", "true");

    Object[] args = composite.resolveParameters(handlerMethod, request, response);

    assertNotNull("参数数组不应该为空", args);
    assertEquals("应该有 3 个参数", 3, args.length);
    assertEquals("第一个参数应该是 String", "hello", args[0]);
    assertSame("第二个参数应该是 HttpServletRequest", request, args[1]);
    assertEquals("第三个参数应该是 boolean", true, args[2]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testResolveParametersUnsupportedType() throws Exception {
    // 测试解析不支持的参数类型
    TestController controller = new TestController();
    Method method = TestController.class.getDeclaredMethod("unsupportedMethod", Object.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    composite.resolveParameters(handlerMethod, request, response);
  }

  /**
   * 测试用的控制器类
   */
  static class TestController {

    public void noParamMethod() {
    }

    public void basicTypeMethod(String name, int age) {
    }

    public void servletMethod(HttpServletRequest request, HttpServletResponse response) {
    }

    public void mixedMethod(String name, HttpServletRequest request, boolean active) {
    }

    public void unsupportedMethod(Object obj) {
    }
  }
}
