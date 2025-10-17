package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

/**
 * ServletParameterResolver 类的单元测试
 * 
 * 测试 Servlet 参数解析器的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ServletParameterResolverTest {

  private ServletParameterResolver resolver;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @Before
  public void setUp() {
    resolver = new ServletParameterResolver();
    request = new BasicTypeParameterResolverTest.MockHttpServletRequest();
    response = new BasicTypeParameterResolverTest.MockHttpServletResponse();
  }

  @Test
  public void testSupportsParameter() {
    // 测试支持的参数类型
    assertTrue("应该支持 HttpServletRequest 类型",
        resolver.supportsParameter(HttpServletRequest.class));
    assertTrue("应该支持 HttpServletResponse 类型",
        resolver.supportsParameter(HttpServletResponse.class));

    // 测试不支持的参数类型
    assertFalse("不应该支持 String 类型", resolver.supportsParameter(String.class));
    assertFalse("不应该支持 Object 类型", resolver.supportsParameter(Object.class));
  }

  @Test
  public void testResolveHttpServletRequest() throws Exception {
    // 测试 HttpServletRequest 参数解析
    Object result = resolver.resolveParameter(HttpServletRequest.class, "request", request, response);

    assertNotNull("HttpServletRequest 参数不应该为空", result);
    assertSame("应该返回相同的 HttpServletRequest 实例", request, result);
    assertTrue("返回值应该是 HttpServletRequest 类型", result instanceof HttpServletRequest);
  }

  @Test
  public void testResolveHttpServletResponse() throws Exception {
    // 测试 HttpServletResponse 参数解析
    Object result = resolver.resolveParameter(HttpServletResponse.class, "response", request, response);

    assertNotNull("HttpServletResponse 参数不应该为空", result);
    assertSame("应该返回相同的 HttpServletResponse 实例", response, result);
    assertTrue("返回值应该是 HttpServletResponse 类型", result instanceof HttpServletResponse);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnsupportedParameterType() throws Exception {
    // 测试不支持的参数类型
    resolver.resolveParameter(String.class, "unsupported", request, response);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAnotherUnsupportedParameterType() throws Exception {
    // 测试另一个不支持的参数类型
    resolver.resolveParameter(Object.class, "unsupported", request, response);
  }
}
