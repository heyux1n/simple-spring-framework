package com.simplespring.webmvc;

import com.simplespring.core.annotation.RequestMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * RequestMappingInfo 类的单元测试
 * 
 * 测试请求映射信息的创建、路径匹配和方法匹配功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class RequestMappingInfoTest {

  private TestController testController;
  private HandlerMethod handlerMethod;

  @Before
  public void setUp() throws NoSuchMethodException {
    testController = new TestController();
    Method method = TestController.class.getDeclaredMethod("testMethod");
    handlerMethod = new HandlerMethod(testController, method);
  }

  @Test
  public void testExactPathMatching() {
    // 测试精确路径匹配
    RequestMappingInfo mappingInfo = new RequestMappingInfo("/users", RequestMethod.GET, handlerMethod);

    assertTrue("应该匹配精确路径", mappingInfo.matches("/users", RequestMethod.GET));
    assertFalse("不应该匹配不同路径", mappingInfo.matches("/orders", RequestMethod.GET));
    assertFalse("不应该匹配子路径", mappingInfo.matches("/users/123", RequestMethod.GET));
  }

  @Test
  public void testWildcardPathMatching() {
    // 测试单级通配符匹配
    RequestMappingInfo mappingInfo = new RequestMappingInfo("/users/*", RequestMethod.GET, handlerMethod);

    assertTrue("应该匹配单级子路径", mappingInfo.matches("/users/123", RequestMethod.GET));
    assertTrue("应该匹配单级子路径", mappingInfo.matches("/users/abc", RequestMethod.GET));
    assertFalse("不应该匹配多级子路径", mappingInfo.matches("/users/123/orders", RequestMethod.GET));
    assertFalse("不应该匹配基础路径", mappingInfo.matches("/users", RequestMethod.GET));
    assertFalse("不应该匹配不同基础路径", mappingInfo.matches("/orders/123", RequestMethod.GET));
  }

  @Test
  public void testMultiLevelWildcardMatching() {
    // 测试多级通配符匹配
    RequestMappingInfo mappingInfo = new RequestMappingInfo("/api/**", RequestMethod.GET, handlerMethod);

    assertTrue("应该匹配单级子路径", mappingInfo.matches("/api/users", RequestMethod.GET));
    assertTrue("应该匹配多级子路径", mappingInfo.matches("/api/users/123", RequestMethod.GET));
    assertTrue("应该匹配深层子路径", mappingInfo.matches("/api/users/123/orders/456", RequestMethod.GET));
    assertFalse("不应该匹配基础路径", mappingInfo.matches("/api", RequestMethod.GET));
    assertFalse("不应该匹配不同基础路径", mappingInfo.matches("/web/users", RequestMethod.GET));
  }

  @Test
  public void testHttpMethodMatching() {
    // 测试 HTTP 方法匹配
    RequestMappingInfo getMappingInfo = new RequestMappingInfo("/users", RequestMethod.GET, handlerMethod);
    RequestMappingInfo postMappingInfo = new RequestMappingInfo("/users", RequestMethod.POST, handlerMethod);

    assertTrue("GET 映射应该匹配 GET 请求", getMappingInfo.matches("/users", RequestMethod.GET));
    assertFalse("GET 映射不应该匹配 POST 请求", getMappingInfo.matches("/users", RequestMethod.POST));

    assertTrue("POST 映射应该匹配 POST 请求", postMappingInfo.matches("/users", RequestMethod.POST));
    assertFalse("POST 映射不应该匹配 GET 请求", postMappingInfo.matches("/users", RequestMethod.GET));
  }

  @Test
  public void testNullPathHandling() {
    // 测试空路径处理
    RequestMappingInfo mappingInfo = new RequestMappingInfo(null, RequestMethod.GET, handlerMethod);

    assertFalse("空路径不应该匹配任何请求", mappingInfo.matches("/users", RequestMethod.GET));
    assertFalse("空路径不应该匹配空请求", mappingInfo.matches(null, RequestMethod.GET));
  }

  @Test
  public void testGetters() {
    // 测试 getter 方法
    String path = "/users";
    RequestMethod method = RequestMethod.POST;
    RequestMappingInfo mappingInfo = new RequestMappingInfo(path, method, handlerMethod);

    assertEquals("路径应该正确", path, mappingInfo.getPath());
    assertEquals("方法应该正确", method, mappingInfo.getMethod());
    assertEquals("处理器方法应该正确", handlerMethod, mappingInfo.getHandlerMethod());
  }

  @Test
  public void testEqualsAndHashCode() {
    // 测试 equals 和 hashCode 方法
    RequestMappingInfo mappingInfo1 = new RequestMappingInfo("/users", RequestMethod.GET, handlerMethod);
    RequestMappingInfo mappingInfo2 = new RequestMappingInfo("/users", RequestMethod.GET, handlerMethod);
    RequestMappingInfo mappingInfo3 = new RequestMappingInfo("/orders", RequestMethod.GET, handlerMethod);

    assertEquals("相同的映射信息应该相等", mappingInfo1, mappingInfo2);
    assertNotEquals("不同的映射信息不应该相等", mappingInfo1, mappingInfo3);

    assertEquals("相同的映射信息应该有相同的 hashCode",
        mappingInfo1.hashCode(), mappingInfo2.hashCode());
  }

  @Test
  public void testToString() {
    // 测试 toString 方法
    RequestMappingInfo mappingInfo = new RequestMappingInfo("/users", RequestMethod.GET, handlerMethod);
    String toString = mappingInfo.toString();

    assertTrue("toString 应该包含路径", toString.contains("/users"));
    assertTrue("toString 应该包含方法", toString.contains("GET"));
    assertTrue("toString 应该包含处理器方法", toString.contains("HandlerMethod"));
  }

  /**
   * 测试用的控制器类
   */
  static class TestController {
    public String testMethod() {
      return "test";
    }
  }
}
