package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * BasicTypeParameterResolver 类的单元测试
 * 
 * 测试基本类型参数解析器的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class BasicTypeParameterResolverTest {

  private BasicTypeParameterResolver resolver;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Before
  public void setUp() {
    resolver = new BasicTypeParameterResolver();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testSupportsParameter() {
    // 测试支持的参数类型
    assertTrue("应该支持 String 类型", resolver.supportsParameter(String.class));
    assertTrue("应该支持 int 类型", resolver.supportsParameter(int.class));
    assertTrue("应该支持 Integer 类型", resolver.supportsParameter(Integer.class));
    assertTrue("应该支持 long 类型", resolver.supportsParameter(long.class));
    assertTrue("应该支持 Long 类型", resolver.supportsParameter(Long.class));
    assertTrue("应该支持 boolean 类型", resolver.supportsParameter(boolean.class));
    assertTrue("应该支持 Boolean 类型", resolver.supportsParameter(Boolean.class));
    assertTrue("应该支持 double 类型", resolver.supportsParameter(double.class));
    assertTrue("应该支持 Double 类型", resolver.supportsParameter(Double.class));
    assertTrue("应该支持 float 类型", resolver.supportsParameter(float.class));
    assertTrue("应该支持 Float 类型", resolver.supportsParameter(Float.class));

    // 测试不支持的参数类型
    assertFalse("不应该支持 Object 类型", resolver.supportsParameter(Object.class));
    assertFalse("不应该支持自定义类型", resolver.supportsParameter(BasicTypeParameterResolverTest.class));
  }

  @Test
  public void testResolveStringParameter() throws Exception {
    // 测试 String 参数解析
    request.setParameter("name", "test");

    Object result = resolver.resolveParameter(String.class, "name", request, response);
    assertEquals("String 参数应该正确解析", "test", result);
  }

  @Test
  public void testResolveIntParameter() throws Exception {
    // 测试 int 参数解析
    request.setParameter("age", "25");

    Object result = resolver.resolveParameter(int.class, "age", request, response);
    assertEquals("int 参数应该正确解析", 25, result);

    // 测试 Integer 包装类型
    Object integerResult = resolver.resolveParameter(Integer.class, "age", request, response);
    assertEquals("Integer 参数应该正确解析", Integer.valueOf(25), integerResult);
  }

  @Test
  public void testResolveLongParameter() throws Exception {
    // 测试 long 参数解析
    request.setParameter("id", "123456789");

    Object result = resolver.resolveParameter(long.class, "id", request, response);
    assertEquals("long 参数应该正确解析", 123456789L, result);

    // 测试 Long 包装类型
    Object longResult = resolver.resolveParameter(Long.class, "id", request, response);
    assertEquals("Long 参数应该正确解析", Long.valueOf(123456789L), longResult);
  }

  @Test
  public void testResolveBooleanParameter() throws Exception {
    // 测试 boolean 参数解析
    request.setParameter("active", "true");

    Object result = resolver.resolveParameter(boolean.class, "active", request, response);
    assertEquals("boolean 参数应该正确解析", true, result);

    // 测试 Boolean 包装类型
    Object booleanResult = resolver.resolveParameter(Boolean.class, "active", request, response);
    assertEquals("Boolean 参数应该正确解析", Boolean.TRUE, booleanResult);

    // 测试 false 值
    request.setParameter("inactive", "false");
    Object falseResult = resolver.resolveParameter(boolean.class, "inactive", request, response);
    assertEquals("boolean false 参数应该正确解析", false, falseResult);
  }

  @Test
  public void testResolveDoubleParameter() throws Exception {
    // 测试 double 参数解析
    request.setParameter("price", "99.99");

    Object result = resolver.resolveParameter(double.class, "price", request, response);
    assertEquals("double 参数应该正确解析", 99.99, (Double) result, 0.001);

    // 测试 Double 包装类型
    Object doubleResult = resolver.resolveParameter(Double.class, "price", request, response);
    assertEquals("Double 参数应该正确解析", Double.valueOf(99.99), doubleResult);
  }

  @Test
  public void testResolveFloatParameter() throws Exception {
    // 测试 float 参数解析
    request.setParameter("rate", "3.14");

    Object result = resolver.resolveParameter(float.class, "rate", request, response);
    assertEquals("float 参数应该正确解析", 3.14f, (Float) result, 0.001f);

    // 测试 Float 包装类型
    Object floatResult = resolver.resolveParameter(Float.class, "rate", request, response);
    assertEquals("Float 参数应该正确解析", Float.valueOf(3.14f), floatResult);
  }

  @Test
  public void testResolveNullParameter() throws Exception {
    // 测试空参数值
    Object stringResult = resolver.resolveParameter(String.class, "name", request, response);
    assertNull("String 空参数应该返回 null", stringResult);

    Object intResult = resolver.resolveParameter(int.class, "age", request, response);
    assertEquals("int 空参数应该返回默认值 0", 0, intResult);

    Object integerResult = resolver.resolveParameter(Integer.class, "age", request, response);
    assertNull("Integer 空参数应该返回 null", integerResult);

    Object booleanResult = resolver.resolveParameter(boolean.class, "active", request, response);
    assertEquals("boolean 空参数应该返回默认值 false", false, booleanResult);
  }

  @Test
  public void testResolveEmptyParameter() throws Exception {
    // 测试空字符串参数值
    request.setParameter("name", "");

    Object stringResult = resolver.resolveParameter(String.class, "name", request, response);
    assertNull("String 空字符串参数应该返回 null", stringResult);

    request.setParameter("age", "  ");
    Object intResult = resolver.resolveParameter(int.class, "age", request, response);
    assertEquals("int 空白字符串参数应该返回默认值 0", 0, intResult);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntParameter() throws Exception {
    // 测试无效的 int 参数
    request.setParameter("age", "invalid");

    resolver.resolveParameter(int.class, "age", request, response);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDoubleParameter() throws Exception {
    // 测试无效的 double 参数
    request.setParameter("price", "not-a-number");

    resolver.resolveParameter(double.class, "price", request, response);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnsupportedParameterType() throws Exception {
    // 测试不支持的参数类型
    resolver.resolveParameter(Object.class, "obj", request, response);
  }

  /**
   * 简单的 HttpServletRequest 模拟实现
   */
  static class MockHttpServletRequest implements HttpServletRequest {
    private Map<String, String> parameters = new HashMap<String, String>();

    public void setParameter(String name, String value) {
      parameters.put(name, value);
    }

    @Override
    public String getParameter(String name) {
      return parameters.get(name);
    }

    // 其他方法的空实现（为了简化测试）
    @Override
    public String getAuthType() {
      return null;
    }

    @Override
    public javax.servlet.http.Cookie[] getCookies() {
      return new javax.servlet.http.Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
      return 0;
    }

    @Override
    public String getHeader(String name) {
      return null;
    }

    @Override
    public java.util.Enumeration<String> getHeaders(String name) {
      return null;
    }

    @Override
    public java.util.Enumeration<String> getHeaderNames() {
      return null;
    }

    @Override
    public int getIntHeader(String name) {
      return 0;
    }

    @Override
    public String getMethod() {
      return null;
    }

    @Override
    public String getPathInfo() {
      return null;
    }

    @Override
    public String getPathTranslated() {
      return null;
    }

    @Override
    public String getContextPath() {
      return null;
    }

    @Override
    public String getQueryString() {
      return null;
    }

    @Override
    public String getRemoteUser() {
      return null;
    }

    @Override
    public boolean isUserInRole(String role) {
      return false;
    }

    @Override
    public java.security.Principal getUserPrincipal() {
      return null;
    }

    @Override
    public String getRequestedSessionId() {
      return null;
    }

    @Override
    public String getRequestURI() {
      return null;
    }

    @Override
    public StringBuffer getRequestURL() {
      return null;
    }

    @Override
    public String getServletPath() {
      return null;
    }

    @Override
    public javax.servlet.http.HttpSession getSession(boolean create) {
      return null;
    }

    @Override
    public javax.servlet.http.HttpSession getSession() {
      return null;
    }

    @Override
    public String changeSessionId() {
      return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
      return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    @Override
    public boolean authenticate(javax.servlet.http.HttpServletResponse response) {
      return false;
    }

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void logout() {
    }

    @Override
    public java.util.Collection<javax.servlet.http.Part> getParts() {
      return null;
    }

    @Override
    public javax.servlet.http.Part getPart(String name) {
      return null;
    }

    @Override
    public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
      return null;
    }

    @Override
    public Object getAttribute(String name) {
      return null;
    }

    @Override
    public java.util.Enumeration<String> getAttributeNames() {
      return null;
    }

    @Override
    public String getCharacterEncoding() {
      return null;
    }

    @Override
    public void setCharacterEncoding(String env) {
    }

    @Override
    public int getContentLength() {
      return 0;
    }

    @Override
    public long getContentLengthLong() {
      return 0;
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public javax.servlet.ServletInputStream getInputStream() {
      return null;
    }

    @Override
    public String[] getParameterValues(String name) {
      return new String[0];
    }

    @Override
    public java.util.Map<String, String[]> getParameterMap() {
      return null;
    }

    @Override
    public java.util.Enumeration<String> getParameterNames() {
      return null;
    }

    @Override
    public String getProtocol() {
      return null;
    }

    @Override
    public String getScheme() {
      return null;
    }

    @Override
    public String getServerName() {
      return null;
    }

    @Override
    public int getServerPort() {
      return 0;
    }

    @Override
    public java.io.BufferedReader getReader() {
      return null;
    }

    @Override
    public String getRemoteAddr() {
      return null;
    }

    @Override
    public String getRemoteHost() {
      return null;
    }

    @Override
    public void setAttribute(String name, Object o) {
    }

    @Override
    public void removeAttribute(String name) {
    }

    @Override
    public java.util.Locale getLocale() {
      return null;
    }

    @Override
    public java.util.Enumeration<java.util.Locale> getLocales() {
      return null;
    }

    @Override
    public boolean isSecure() {
      return false;
    }

    @Override
    public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
      return null;
    }

    @Override
    public String getRealPath(String path) {
      return null;
    }

    @Override
    public int getRemotePort() {
      return 0;
    }

    @Override
    public String getLocalName() {
      return null;
    }

    @Override
    public String getLocalAddr() {
      return null;
    }

    @Override
    public int getLocalPort() {
      return 0;
    }

    @Override
    public javax.servlet.ServletContext getServletContext() {
      return null;
    }

    @Override
    public javax.servlet.AsyncContext startAsync() {
      return null;
    }

    @Override
    public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest servletRequest,
        javax.servlet.ServletResponse servletResponse) {
      return null;
    }

    @Override
    public boolean isAsyncStarted() {
      return false;
    }

    @Override
    public boolean isAsyncSupported() {
      return false;
    }

    @Override
    public javax.servlet.AsyncContext getAsyncContext() {
      return null;
    }

    @Override
    public javax.servlet.DispatcherType getDispatcherType() {
      return null;
    }
  }

  /**
   * 简单的 HttpServletResponse 模拟实现
   */
  static class MockHttpServletResponse implements HttpServletResponse {
    // 空实现（为了简化测试）
    @Override
    public void addCookie(javax.servlet.http.Cookie cookie) {
    }

    @Override
    public boolean containsHeader(String name) {
      return false;
    }

    @Override
    public String encodeURL(String url) {
      return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
      return null;
    }

    @Override
    public String encodeUrl(String url) {
      return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
      return null;
    }

    @Override
    public void sendError(int sc, String msg) {
    }

    @Override
    public void sendError(int sc) {
    }

    @Override
    public void sendRedirect(String location) {
    }

    @Override
    public void setDateHeader(String name, long date) {
    }

    @Override
    public void addDateHeader(String name, long date) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public void setIntHeader(String name, int value) {
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void setStatus(int sc) {
    }

    @Override
    public void setStatus(int sc, String sm) {
    }

    @Override
    public int getStatus() {
      return 0;
    }

    @Override
    public String getHeader(String name) {
      return null;
    }

    @Override
    public java.util.Collection<String> getHeaders(String name) {
      return null;
    }

    @Override
    public java.util.Collection<String> getHeaderNames() {
      return null;
    }

    @Override
    public String getCharacterEncoding() {
      return null;
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public javax.servlet.ServletOutputStream getOutputStream() {
      return null;
    }

    @Override
    public java.io.PrintWriter getWriter() {
      return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
    }

    @Override
    public void setContentLength(int len) {
    }

    @Override
    public void setContentLengthLong(long len) {
    }

    @Override
    public void setContentType(String type) {
    }

    @Override
    public void setBufferSize(int size) {
    }

    @Override
    public int getBufferSize() {
      return 0;
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public boolean isCommitted() {
      return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public void setLocale(java.util.Locale loc) {
    }

    @Override
    public java.util.Locale getLocale() {
      return null;
    }
  }
}
