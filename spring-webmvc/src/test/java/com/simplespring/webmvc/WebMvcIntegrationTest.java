package com.simplespring.webmvc;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Component;
import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * WebMVC 模块集成测试，验证 MVC 组件与其他模块的协作
 * 
 * @author SimpleSpring Framework
 */
public class WebMvcIntegrationTest {

  private AnnotationConfigApplicationContext applicationContext;
  private DispatcherServlet dispatcherServlet;
  private RequestMappingHandlerMapping handlerMapping;

  @Before
  public void setUp() {
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.scan("com.simplespring.webmvc.WebMvcIntegrationTest");
    applicationContext.refresh();

    dispatcherServlet = new DispatcherServlet();
    dispatcherServlet.setApplicationContext(applicationContext);
    dispatcherServlet.init();

    handlerMapping = new RequestMappingHandlerMapping();
    handlerMapping.setApplicationContext(applicationContext);
  }

  @Test
  public void testControllerRegistration() {
    // 测试控制器注册

    TestController controller = applicationContext.getBean(TestController.class);
    assertNotNull("Controller should be registered", controller);

    // 扫描控制器
    handlerMapping.scanController(TestController.class, controller);

    // 验证请求映射被正确注册
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/test");

    HandlerExecutionChain chain = handlerMapping.getHandler(request);
    assertNotNull("Handler should be found", chain);

    HandlerMethod handlerMethod = (HandlerMethod) chain.getHandler();
    assertEquals("Handler should be correct method", "testMethod",
        handlerMethod.getMethod().getName());
  }

  @Test
  public void testParameterResolution() throws Exception {
    // 测试参数解析

    ParameterResolverComposite composite = new ParameterResolverComposite();
    composite.addResolver(new BasicTypeParameterResolver());
    composite.addResolver(new ServletParameterResolver());

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setParameter("name", "test");
    request.setParameter("age", "25");

    TestController controller = new TestController();
    HandlerMethod handlerMethod = new HandlerMethod(controller,
        TestController.class.getMethod("methodWithParams", String.class, int.class));

    Object[] args = composite.resolveParameters(handlerMethod, request, response);

    assertEquals("Should resolve 2 parameters", 2, args.length);
    assertEquals("First parameter should be string", "test", args[0]);
    assertEquals("Second parameter should be integer", 25, args[1]);
  }

  @Test
  public void testViewResolution() {
    // 测试视图解析

    CompositeViewResolver viewResolver = new CompositeViewResolver();
    viewResolver.addViewResolver(new JsonViewResolver());
    viewResolver.addViewResolver(new SimpleViewResolver());

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试 JSON 视图解析
    request.setHeader("Accept", "application/json");

    try {
      viewResolver.resolveView("test result", request, response);
      assertEquals("Content type should be JSON", "application/json;charset=UTF-8",
          response.getContentType());
    } catch (Exception e) {
      fail("View resolution should not throw exception: " + e.getMessage());
    }
  }

  @Test
  public void testCompleteRequestProcessing() throws ServletException, IOException {
    // 测试完整的请求处理流程

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setRequestURI("/test");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String content = response.getContentAsString();
    assertNotNull("Response should have content", content);
    assertTrue("Response should contain expected content", content.contains("test"));
  }

  @Test
  public void testDependencyInjectionInController() throws ServletException, IOException {
    // 测试控制器中的依赖注入

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setRequestURI("/service");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String content = response.getContentAsString();
    assertNotNull("Response should have content", content);
    assertTrue("Response should show service was injected", content.contains("Service"));
  }

  @Test
  public void testPostRequestHandling() throws ServletException, IOException {
    // 测试 POST 请求处理

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setRequestURI("/test");
    request.setParameter("data", "test data");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String content = response.getContentAsString();
    assertTrue("Response should contain posted data", content.contains("test data"));
  }

  @Test
  public void testErrorHandling() throws ServletException, IOException {
    // 测试错误处理

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setRequestURI("/nonexistent");

    dispatcherServlet.service(request, response);

    assertEquals("Should return 404 for non-existent mapping", 404, response.getStatus());
  }

  @Test
  public void testMethodNotAllowed() throws ServletException, IOException {
    // 测试方法不允许的情况

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 尝试用 DELETE 方法访问只支持 GET 的端点
    request.setMethod("DELETE");
    request.setRequestURI("/test");

    dispatcherServlet.service(request, response);

    // 应该返回 405 Method Not Allowed 或 404
    assertTrue("Should return error status",
        response.getStatus() == 404 || response.getStatus() == 405);
  }

  @Test
  public void testMultipleControllers() {
    // 测试多个控制器的处理

    TestController controller1 = applicationContext.getBean(TestController.class);
    AnotherTestController controller2 = applicationContext.getBean(AnotherTestController.class);

    assertNotNull("First controller should be registered", controller1);
    assertNotNull("Second controller should be registered", controller2);

    // 扫描两个控制器
    handlerMapping.scanController(TestController.class, controller1);
    handlerMapping.scanController(AnotherTestController.class, controller2);

    // 验证两个控制器的映射都被注册
    MockHttpServletRequest request1 = new MockHttpServletRequest();
    request1.setMethod("GET");
    request1.setRequestURI("/test");

    MockHttpServletRequest request2 = new MockHttpServletRequest();
    request2.setMethod("GET");
    request2.setRequestURI("/another");

    HandlerExecutionChain chain1 = handlerMapping.getHandler(request1);
    HandlerExecutionChain chain2 = handlerMapping.getHandler(request2);

    assertNotNull("First handler should be found", chain1);
    assertNotNull("Second handler should be found", chain2);
  }

  // 测试用的控制器
  @Controller
  public static class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public String testMethod() {
      return "test result";
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String postMethod(String data) {
      return "posted: " + data;
    }

    @RequestMapping("/service")
    public String serviceMethod() {
      return testService != null ? testService.doSomething() : "no service";
    }

    public String methodWithParams(String name, int age) {
      return "name: " + name + ", age: " + age;
    }
  }

  @Controller
  public static class AnotherTestController {

    @RequestMapping("/another")
    public String anotherMethod() {
      return "another result";
    }
  }

  @Component
  public static class TestService {
    public String doSomething() {
      return "Service working";
    }
  }
}
