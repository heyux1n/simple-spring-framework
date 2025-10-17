package com.simplespring.webmvc;

import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * RequestMappingHandlerMapping 类的单元测试
 * 
 * 测试基于注解的处理器映射功能，包括控制器扫描、映射注册和请求路由。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class RequestMappingHandlerMappingTest {

  private RequestMappingHandlerMapping handlerMapping;

  @Before
  public void setUp() {
    handlerMapping = new RequestMappingHandlerMapping();
  }

  @Test
  public void testScanControllerWithMethodLevelMapping() {
    // 测试扫描只有方法级别映射的控制器
    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    // 验证映射是否正确注册
    assertTrue("应该存在 /users GET 映射",
        handlerMapping.hasMapping("/users", RequestMethod.GET));
    assertTrue("应该存在 /users POST 映射",
        handlerMapping.hasMapping("/users", RequestMethod.POST));

    assertEquals("应该注册 2 个映射", 2, handlerMapping.getMappingCount());
  }

  @Test
  public void testScanControllerWithClassAndMethodMapping() {
    // 测试扫描有类级别和方法级别映射的控制器
    ApiController controller = new ApiController();
    handlerMapping.scanController(ApiController.class, controller);

    // 验证路径组合是否正确
    assertTrue("应该存在 /api/users GET 映射",
        handlerMapping.hasMapping("/api/users", RequestMethod.GET));
    assertTrue("应该存在 /api/users POST 映射",
        handlerMapping.hasMapping("/api/users", RequestMethod.POST));
    assertTrue("应该存在 /api/orders GET 映射",
        handlerMapping.hasMapping("/api/orders", RequestMethod.GET));

    assertEquals("应该注册 3 个映射", 3, handlerMapping.getMappingCount());
  }

  @Test
  public void testGetHandler() {
    // 测试获取处理器
    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    HandlerExecutionChain chain = handlerMapping.getHandler("/users", RequestMethod.GET);
    assertNotNull("应该找到处理器", chain);
    assertNotNull("处理器方法不应该为空", chain.getHandler());
    assertEquals("方法名应该正确", "getUsers", chain.getHandler().getMethodName());

    HandlerExecutionChain postChain = handlerMapping.getHandler("/users", RequestMethod.POST);
    assertNotNull("应该找到 POST 处理器", postChain);
    assertEquals("方法名应该正确", "createUser", postChain.getHandler().getMethodName());
  }

  @Test
  public void testGetHandlerNotFound() {
    // 测试找不到处理器的情况
    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    HandlerExecutionChain chain = handlerMapping.getHandler("/nonexistent", RequestMethod.GET);
    assertNull("不存在的路径应该返回 null", chain);

    HandlerExecutionChain wrongMethodChain = handlerMapping.getHandler("/users", RequestMethod.DELETE);
    assertNull("错误的 HTTP 方法应该返回 null", wrongMethodChain);
  }

  @Test
  public void testWildcardMapping() {
    // 测试通配符映射
    WildcardController controller = new WildcardController();
    handlerMapping.scanController(WildcardController.class, controller);

    // 测试单级通配符
    assertTrue("应该匹配单级通配符",
        handlerMapping.hasMapping("/files/123", RequestMethod.GET));
    assertFalse("不应该匹配多级路径",
        handlerMapping.hasMapping("/files/123/content", RequestMethod.GET));

    // 测试多级通配符
    assertTrue("应该匹配多级通配符",
        handlerMapping.hasMapping("/api/v1/users", RequestMethod.GET));
    assertTrue("应该匹配深层路径",
        handlerMapping.hasMapping("/api/v1/users/123/orders", RequestMethod.GET));
  }

  @Test
  public void testRegisterMappingDirectly() {
    // 测试直接注册映射
    TestController controller = new TestController();
    HandlerMethod handlerMethod = new HandlerMethod(controller,
        TestController.class.getDeclaredMethods()[0]);
    RequestMappingInfo mappingInfo = new RequestMappingInfo("/direct", RequestMethod.GET, handlerMethod);

    handlerMapping.registerMapping(mappingInfo);

    assertTrue("应该存在直接注册的映射",
        handlerMapping.hasMapping("/direct", RequestMethod.GET));
    assertEquals("映射数量应该为 1", 1, handlerMapping.getMappingCount());
  }

  @Test
  public void testGetAllMappings() {
    // 测试获取所有映射
    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    RequestMappingInfo[] mappings = handlerMapping.getAllMappings();
    assertEquals("应该有 2 个映射", 2, mappings.length);

    // 验证映射信息
    boolean hasGetMapping = false;
    boolean hasPostMapping = false;

    for (RequestMappingInfo mapping : mappings) {
      if ("/users".equals(mapping.getPath())) {
        if (RequestMethod.GET == mapping.getMethod()) {
          hasGetMapping = true;
        } else if (RequestMethod.POST == mapping.getMethod()) {
          hasPostMapping = true;
        }
      }
    }

    assertTrue("应该包含 GET 映射", hasGetMapping);
    assertTrue("应该包含 POST 映射", hasPostMapping);
  }

  @Test
  public void testClearMappings() {
    // 测试清空映射
    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    assertEquals("扫描后应该有映射", 2, handlerMapping.getMappingCount());

    handlerMapping.clearMappings();

    assertEquals("清空后应该没有映射", 0, handlerMapping.getMappingCount());
    assertFalse("清空后不应该存在映射",
        handlerMapping.hasMapping("/users", RequestMethod.GET));
  }

  @Test
  public void testNonControllerClass() {
    // 测试扫描非控制器类
    NonControllerClass nonController = new NonControllerClass();
    handlerMapping.scanController(NonControllerClass.class, nonController);

    assertEquals("非控制器类不应该注册映射", 0, handlerMapping.getMappingCount());
  }

  @Test
  public void testPathNormalization() {
    // 测试路径规范化
    PathTestController controller = new PathTestController();
    handlerMapping.scanController(PathTestController.class, controller);

    // 验证路径是否正确规范化
    assertTrue("应该正确处理路径组合",
        handlerMapping.hasMapping("/api/test", RequestMethod.GET));
    assertTrue("应该正确处理无斜杠的路径",
        handlerMapping.hasMapping("/api/noSlash", RequestMethod.GET));
  }

  /**
   * 测试用的控制器类 - 只有方法级别映射
   */
  @Controller
  static class TestController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers() {
      return "users";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser() {
      return "created";
    }
  }

  /**
   * 测试用的控制器类 - 有类级别和方法级别映射
   */
  @Controller
  @RequestMapping("/api")
  static class ApiController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers() {
      return "api-users";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser() {
      return "api-created";
    }

    @RequestMapping("/orders")
    public String getOrders() {
      return "api-orders";
    }
  }

  /**
   * 测试用的控制器类 - 通配符映射
   */
  @Controller
  static class WildcardController {

    @RequestMapping("/files/*")
    public String getFile() {
      return "file";
    }

    @RequestMapping("/api/**")
    public String getApiResource() {
      return "api-resource";
    }
  }

  /**
   * 测试用的控制器类 - 路径规范化测试
   */
  @Controller
  @RequestMapping("api") // 没有前导斜杠
  static class PathTestController {

    @RequestMapping("/test") // 有前导斜杠
    public String test() {
      return "test";
    }

    @RequestMapping("noSlash") // 没有前导斜杠
    public String noSlash() {
      return "noSlash";
    }
  }

  /**
   * 非控制器类
   */
  static class NonControllerClass {

    @RequestMapping("/should-not-work")
    public String shouldNotWork() {
      return "error";
    }
  }
}
