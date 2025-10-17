package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * SimpleViewResolver 类的单元测试
 * 
 * 测试简单视图解析器的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class SimpleViewResolverTest {

  private SimpleViewResolver resolver;
  private HttpServletRequest request;
  private MockHttpServletResponse response;

  @Before
  public void setUp() {
    resolver = new SimpleViewResolver();
    request = new BasicTypeParameterResolverTest.MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testResolveStringView() throws Exception {
    // 测试字符串视图解析
    String returnValue = "Hello World";

    resolver.resolveView(returnValue, request, response);

    assertEquals("内容应该正确", "Hello World", response.getContent());
    assertTrue("Content-Type 应该是 text/plain", response.getContentType().contains("text/plain"));
    assertEquals("字符编码应该是 UTF-8", "UTF-8", response.getCharacterEncoding());
  }

  @Test
  public void testResolveJsonView() throws Exception {
    // 测试 JSON 字符串视图解析
    String jsonReturn = "{\"message\":\"hello\",\"status\":\"ok\"}";

    resolver.resolveView(jsonReturn, request, response);

    assertEquals("内容应该正确", jsonReturn, response.getContent());
    assertTrue("Content-Type 应该是 application/json", response.getContentType().contains("application/json"));
  }

  @Test
  public void testResolveJsonArrayView() throws Exception {
    // 测试 JSON 数组视图解析
    String jsonArray = "[{\"id\":1,\"name\":\"user1\"},{\"id\":2,\"name\":\"user2\"}]";

    resolver.resolveView(jsonArray, request, response);

    assertEquals("内容应该正确", jsonArray, response.getContent());
    assertTrue("Content-Type 应该是 application/json", response.getContentType().contains("application/json"));
  }

  @Test
  public void testResolveHtmlView() throws Exception {
    // 测试 HTML 视图解析
    String htmlReturn = "<html><body><h1>Hello</h1></body></html>";

    resolver.resolveView(htmlReturn, request, response);

    assertEquals("内容应该正确", htmlReturn, response.getContent());
    assertTrue("Content-Type 应该是 text/html", response.getContentType().contains("text/html"));
  }

  @Test
  public void testResolveNullView() throws Exception {
    // 测试 null 返回值
    resolver.resolveView(null, request, response);

    assertEquals("null 返回值不应该写入任何内容", "", response.getContent());
  }

  @Test
  public void testResolveObjectView() throws Exception {
    // 测试对象视图解析
    Integer number = 42;

    resolver.resolveView(number, request, response);

    assertEquals("内容应该是对象的字符串表示", "42", response.getContent());
    assertTrue("Content-Type 应该是 text/plain", response.getContentType().contains("text/plain"));
  }

  @Test
  public void testContentTypeDetection() throws Exception {
    // 测试各种内容类型的检测

    // 测试普通文本
    resolver.resolveView("plain text", request, response);
    assertTrue("普通文本应该是 text/plain", response.getContentType().contains("text/plain"));

    // 重置响应
    response = new MockHttpServletResponse();

    // 测试 JSON 对象
    resolver.resolveView("{\"key\":\"value\"}", request, response);
    assertTrue("JSON 对象应该是 application/json", response.getContentType().contains("application/json"));

    // 重置响应
    response = new MockHttpServletResponse();

    // 测试 JSON 数组
    resolver.resolveView("[1,2,3]", request, response);
    assertTrue("JSON 数组应该是 application/json", response.getContentType().contains("application/json"));

    // 重置响应
    response = new MockHttpServletResponse();

    // 测试 HTML
    resolver.resolveView("<div>content</div>", request, response);
    assertTrue("HTML 应该是 text/html", response.getContentType().contains("text/html"));
  }

  @Test
  public void testWhitespaceHandling() throws Exception {
    // 测试空白字符处理
    String jsonWithWhitespace = "  { \"message\" : \"hello\" }  ";

    resolver.resolveView(jsonWithWhitespace, request, response);

    assertEquals("内容应该保持原样", jsonWithWhitespace, response.getContent());
    assertTrue("应该识别为 JSON", response.getContentType().contains("application/json"));
  }

  /**
   * Mock HttpServletResponse 实现，用于测试
   */
  static class MockHttpServletResponse extends BasicTypeParameterResolverTest.MockHttpServletResponse {
    private String contentType = "text/plain";
    private String characterEncoding = "UTF-8";
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);

    @Override
    public void setContentType(String type) {
      this.contentType = type;
    }

    @Override
    public String getContentType() {
      return contentType;
    }

    @Override
    public void setCharacterEncoding(String charset) {
      this.characterEncoding = charset;
    }

    @Override
    public String getCharacterEncoding() {
      return characterEncoding;
    }

    @Override
    public PrintWriter getWriter() {
      return printWriter;
    }

    public String getContent() {
      printWriter.flush();
      return stringWriter.toString();
    }
  }
}
