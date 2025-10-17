package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * CompositeViewResolver 类的单元测试
 * 
 * 测试组合视图解析器的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class CompositeViewResolverTest {

  private CompositeViewResolver resolver;
  private MockHttpServletRequest request;
  private SimpleViewResolverTest.MockHttpServletResponse response;

  @Before
  public void setUp() {
    resolver = new CompositeViewResolver();
    request = new MockHttpServletRequest();
    response = new SimpleViewResolverTest.MockHttpServletResponse();
  }

  @Test
  public void testDefaultResolvers() {
    // 测试默认解析器数量
    assertEquals("应该有 2 个默认解析器", 2, resolver.getResolverCount());
  }

  @Test
  public void testAddResolver() {
    // 测试添加自定义解析器
    ViewResolver customResolver = new ViewResolver() {
      @Override
      public void resolveView(Object returnValue, HttpServletRequest request,
          javax.servlet.http.HttpServletResponse response) throws Exception {
        // 自定义实现
      }
    };

    resolver.addResolver(customResolver);
    assertEquals("添加解析器后数量应该增加", 3, resolver.getResolverCount());
  }

  @Test
  public void testAddNullResolver() {
    // 测试添加空解析器
    int originalCount = resolver.getResolverCount();
    resolver.addResolver(null);
    assertEquals("添加空解析器后数量不应该变化", originalCount, resolver.getResolverCount());
  }

  @Test
  public void testResolveStringValue() throws Exception {
    // 测试字符串值解析（应该使用简单解析器）
    resolver.resolveView("Hello World", request, response);

    assertEquals("内容应该正确", "Hello World", response.getContent());
    assertTrue("应该使用简单解析器", response.getContentType().contains("text/plain"));
  }

  @Test
  public void testResolveJsonStringValue() throws Exception {
    // 测试 JSON 字符串值解析（应该使用 JSON 解析器）
    String jsonString = "{\"message\":\"hello\"}";
    resolver.resolveView(jsonString, request, response);

    assertEquals("内容应该正确", jsonString, response.getContent());
    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
  }

  @Test
  public void testResolveObjectValue() throws Exception {
    // 测试对象值解析（应该使用 JSON 解析器）
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("name", "John");
    data.put("age", 30);

    resolver.resolveView(data, request, response);

    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
    String content = response.getContent();
    assertTrue("应该是 JSON 格式", content.startsWith("{") && content.endsWith("}"));
    assertTrue("应该包含数据", content.contains("\"name\":\"John\""));
  }

  @Test
  public void testResolveWithJsonAcceptHeader() throws Exception {
    // 测试带有 JSON Accept 头的请求
    request.setHeader("Accept", "application/json");

    resolver.resolveView("simple text", request, response);

    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
    assertEquals("内容应该被 JSON 序列化", "\"simple text\"", response.getContent());
  }

  @Test
  public void testResolveWithMixedAcceptHeader() throws Exception {
    // 测试带有混合 Accept 头的请求
    request.setHeader("Accept", "text/html,application/json,*/*");

    resolver.resolveView("test content", request, response);

    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
    assertEquals("内容应该被 JSON 序列化", "\"test content\"", response.getContent());
  }

  @Test
  public void testResolveNumberValue() throws Exception {
    // 测试数字值解析（应该使用简单解析器）
    resolver.resolveView(42, request, response);

    assertEquals("内容应该正确", "42", response.getContent());
    assertTrue("应该使用简单解析器", response.getContentType().contains("text/plain"));
  }

  @Test
  public void testResolveBooleanValue() throws Exception {
    // 测试布尔值解析（应该使用简单解析器）
    resolver.resolveView(true, request, response);

    assertEquals("内容应该正确", "true", response.getContent());
    assertTrue("应该使用简单解析器", response.getContentType().contains("text/plain"));
  }

  @Test
  public void testResolveNullValue() throws Exception {
    // 测试 null 值解析
    resolver.resolveView(null, request, response);

    // null 值不会写入任何内容，所以内容应该为空
    assertEquals("null 值不应该写入内容", "", response.getContent());
  }

  @Test
  public void testResolveArrayValue() throws Exception {
    // 测试数组值解析（应该使用 JSON 解析器）
    String[] array = { "item1", "item2", "item3" };
    resolver.resolveView(array, request, response);

    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
    assertEquals("内容应该是 JSON 数组", "[\"item1\",\"item2\",\"item3\"]", response.getContent());
  }

  @Test
  public void testJsonAcceptHeaderPriority() throws Exception {
    // 测试 JSON Accept 头的优先级
    request.setHeader("Accept", "application/json");

    // 即使是简单的数字，也应该使用 JSON 解析器
    resolver.resolveView(123, request, response);

    assertTrue("应该使用 JSON 解析器", response.getContentType().contains("application/json"));
    assertEquals("内容应该是 JSON 格式", "123", response.getContent());
  }

  /**
   * 扩展的 Mock HttpServletRequest，支持设置请求头
   */
  static class MockHttpServletRequest extends BasicTypeParameterResolverTest.MockHttpServletRequest {
    private Map<String, String> headers = new HashMap<String, String>();

    public void setHeader(String name, String value) {
      headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      return headers.get(name);
    }
  }
}
