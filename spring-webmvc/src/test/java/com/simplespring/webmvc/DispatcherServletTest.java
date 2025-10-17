package com.simplespring.webmvc;

import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * DispatcherServlet 类的集成测试
 * 
 * 测试完整的请求处理流程，验证 DispatcherServlet 的核心功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class DispatcherServletTest {

  private DispatcherServlet dispatcherServlet;

  @Before
  public void setUp() {
    dispatcherServlet = new DispatcherServlet();

    // 注册测试控制器
    TestController controller = new TestController();
    dispatcherServlet.registerController(TestController.class, controller);
  }

  @Test
  public void testGetRequest() throws ServletException, IOException {
    // 测试 GET 请求处理
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/users");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertEquals("响应内容应该正确", "user list", response.getContent());
    assertTrue("Content-Type 应该包含 text/plain",
        response.getContentType().contains("text/plain"));
  }

  @Test
  public void testPostRequest() throws ServletException, IOException {
    // 测试 POST 请求处理
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");
    request.setRequestURI("/users");
    request.setParameter("param0", "newUser");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doPost(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertEquals("响应内容应该正确", "created user: newUser", response.getContent());
  }

  @Test
  public void testRequestWithParameters() throws ServletException, IOException {
    // 测试带参数的请求处理
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/users/details");
    request.setParameter("param0", "123");
    request.setParameter("param1", "true");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertEquals("响应内容应该正确", "user details: 123, active: true", response.getContent());
  }

  @Test
  public void testServletParameterInjection() throws ServletException, IOException {
    // 测试 Servlet 参数注入
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/servlet-info");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertTrue("响应内容应该包含请求信息", response.getContent().contains("GET"));
    assertTrue("响应内容应该包含路径信息", response.getContent().contains("/servlet-info"));
  }

  @Test
  public void testNotFoundHandler() throws ServletException, IOException {
    // 测试找不到处理器的情况
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/nonexistent");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 404", 404, response.getStatus());
    assertTrue("响应内容应该包含 404 信息", response.getContent().contains("404 Not Found"));
    assertTrue("响应内容应该包含请求路径", response.getContent().contains("/nonexistent"));
  }

  @Test
  public void testMethodNotAllowed() throws ServletException, IOException {
    // 测试方法不匹配的情况
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("DELETE"); // TestController 没有 DELETE 映射
    request.setRequestURI("/users");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doDelete(request, response);

    assertEquals("响应状态应该是 404", 404, response.getStatus());
    assertTrue("响应内容应该包含 404 信息", response.getContent().contains("404 Not Found"));
  }

  @Test
  public void testExceptionHandling() throws ServletException, IOException {
    // 测试异常处理
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/error");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 500", 500, response.getStatus());
    assertTrue("响应内容应该包含 500 信息", response.getContent().contains("500 Internal Server Error"));
  }

  @Test
  public void testJsonResponse() throws ServletException, IOException {
    // 测试 JSON 响应
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/json");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertTrue("Content-Type 应该是 JSON", response.getContentType().contains("application/json"));
    assertEquals("响应内容应该是 JSON", "{\"message\":\"hello\"}", response.getContent());
  }

  @Test
  public void testContextPathHandling() throws ServletException, IOException {
    // 测试上下文路径处理
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/myapp/users");
    request.setContextPath("/myapp");

    MockHttpServletResponse response = new MockHttpServletResponse();

    dispatcherServlet.doGet(request, response);

    assertEquals("响应状态应该是 200", 200, response.getStatus());
    assertEquals("响应内容应该正确", "user list", response.getContent());
  }

  /**
   * 测试用的控制器类
   */
  @Controller
  static class TestController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers() {
      return "user list";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name) {
      return "created user: " + name;
    }

    @RequestMapping("/users/details")
    public String getUserDetails(int id, boolean active) {
      return "user details: " + id + ", active: " + active;
    }

    @RequestMapping("/servlet-info")
    public String getServletInfo(HttpServletRequest request, HttpServletResponse response) {
      return "Method: " + request.getMethod() + ", Path: " + request.getRequestURI();
    }

    @RequestMapping("/error")
    public String throwError() {
      throw new RuntimeException("Test exception");
    }

    @RequestMapping("/json")
    public String getJson() {
      return "{\"message\":\"hello\"}";
    }
  }

  /**
   * 扩展的 Mock HttpServletRequest，支持更多功能
   */
  static class MockHttpServletRequest extends BasicTypeParameterResolverTest.MockHttpServletRequest {
    private String method = "GET";
    private String requestURI = "/";
    private String contextPath = "";

    public void setMethod(String method) {
      this.method = method;
    }

    @Override
    public String getMethod() {
      return method;
    }

    public void setRequestURI(String requestURI) {
      this.requestURI = requestURI;
    }

    @Override
    public String getRequestURI() {
      return requestURI;
    }

    public void setContextPath(String contextPath) {
      this.contextPath = contextPath;
    }

    @Override
    public String getContextPath() {
      return contextPath;
    }
  }

  /**
   * 扩展的 Mock HttpServletResponse，支持状态码和内容捕获
   */
  static class MockHttpServletResponse extends BasicTypeParameterResolverTest.MockHttpServletResponse {
    private int status = 200;
    private String contentType = "text/plain";
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);

    @Override
    public void setStatus(int sc) {
      this.status = sc;
    }

    @Override
    public int getStatus() {
      return status;
    }

    @Override
    public void setContentType(String type) {
      this.contentType = type;
    }

    @Override
    public String getContentType() {
      return contentType;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      return printWriter;
    }

    public String getContent() {
      printWriter.flush();
      return stringWriter.toString();
    }
  }
}
