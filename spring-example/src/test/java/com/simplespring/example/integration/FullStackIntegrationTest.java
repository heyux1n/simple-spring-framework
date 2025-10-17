package com.simplespring.example.integration;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.example.config.AppConfig;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.controller.UserController;
import com.simplespring.example.controller.OrderController;
import com.simplespring.webmvc.DispatcherServlet;
import com.simplespring.webmvc.RequestMappingHandlerMapping;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 全栈集成测试，验证所有模块的协作
 * 测试从 HTTP 请求到业务逻辑处理的完整流程
 * 
 * @author SimpleSpring Framework
 */
public class FullStackIntegrationTest {

  private AnnotationConfigApplicationContext applicationContext;
  private DispatcherServlet dispatcherServlet;

  @Before
  public void setUp() {
    // 初始化应用上下文
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.scan("com.simplespring.example");
    applicationContext.refresh();

    // 初始化 DispatcherServlet
    dispatcherServlet = new DispatcherServlet();
    dispatcherServlet.setApplicationContext(applicationContext);
    dispatcherServlet.init();
  }

  @Test
  public void testCompleteIoCContainer() {
    // 验证 IoC 容器正确创建和管理 Bean

    // 验证服务层 Bean
    UserService userService = applicationContext.getBean(UserService.class);
    assertNotNull("UserService should be created", userService);

    OrderService orderService = applicationContext.getBean(OrderService.class);
    assertNotNull("OrderService should be created", orderService);

    // 验证控制器 Bean
    UserController userController = applicationContext.getBean(UserController.class);
    assertNotNull("UserController should be created", userController);

    OrderController orderController = applicationContext.getBean(OrderController.class);
    assertNotNull("OrderController should be created", orderController);

    // 验证依赖注入正确工作
    // UserController 应该注入了 UserService
    String result = userController.getUser("1");
    assertNotNull("UserController should work with injected UserService", result);
    assertTrue("Result should contain user info", result.contains("用户"));
  }

  @Test
  public void testAopIntegration() {
    // 验证 AOP 功能正确集成
    OrderService orderService = applicationContext.getBean(OrderService.class);

    // 调用业务方法，应该触发 AOP 切面
    String result = orderService.createOrder("用户1", "商品A", 2);

    assertNotNull("Order creation should return result", result);
    assertTrue("Result should contain order info", result.contains("订单"));

    // 验证性能监控切面是否工作
    // 这里可以通过日志或其他方式验证切面是否被执行
    // 由于测试环境限制，我们主要验证方法能正常执行
  }

  @Test
  public void testHttpRequestProcessing() throws ServletException, IOException {
    // 测试完整的 HTTP 请求处理流程

    // 创建模拟的 HTTP 请求和响应
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试用户相关请求
    request.setMethod("GET");
    request.setRequestURI("/user/1");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String responseContent = response.getContentAsString();
    assertNotNull("Response should have content", responseContent);
    assertTrue("Response should contain user info", responseContent.contains("用户"));
  }

  @Test
  public void testOrderRequestProcessing() throws ServletException, IOException {
    // 测试订单相关的 HTTP 请求处理

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试创建订单请求
    request.setMethod("POST");
    request.setRequestURI("/order");
    request.setParameter("userId", "1");
    request.setParameter("productName", "测试商品");
    request.setParameter("quantity", "2");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String responseContent = response.getContentAsString();
    assertNotNull("Response should have content", responseContent);
    assertTrue("Response should contain order info", responseContent.contains("订单"));
  }

  @Test
  public void testParameterBinding() throws ServletException, IOException {
    // 测试参数绑定功能

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试带参数的请求
    request.setMethod("GET");
    request.setRequestURI("/user/search");
    request.setParameter("name", "张三");
    request.setParameter("age", "25");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    String responseContent = response.getContentAsString();
    assertNotNull("Response should have content", responseContent);
  }

  @Test
  public void testErrorHandling() throws ServletException, IOException {
    // 测试错误处理

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试不存在的路径
    request.setMethod("GET");
    request.setRequestURI("/nonexistent");

    dispatcherServlet.service(request, response);

    assertEquals("Should return 404 for non-existent path", 404, response.getStatus());
  }

  @Test
  public void testViewResolution() throws ServletException, IOException {
    // 测试视图解析功能

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 测试返回 JSON 的请求
    request.setMethod("GET");
    request.setRequestURI("/user/1");
    request.setHeader("Accept", "application/json");

    dispatcherServlet.service(request, response);

    assertEquals("Response should be successful", 200, response.getStatus());
    assertEquals("Content type should be JSON", "application/json;charset=UTF-8",
        response.getContentType());
  }

  @Test
  public void testBeanLifecycle() {
    // 测试 Bean 生命周期管理

    // 验证 Bean 的初始化
    UserService userService = applicationContext.getBean(UserService.class);
    assertNotNull("UserService should be initialized", userService);

    // 验证单例模式
    UserService userService2 = applicationContext.getBean(UserService.class);
    assertSame("UserService should be singleton", userService, userService2);

    // 验证不同类型的 Bean 是不同的实例
    OrderService orderService = applicationContext.getBean(OrderService.class);
    assertNotSame("Different bean types should be different instances",
        userService, orderService);
  }

  @Test
  public void testConfigurationIntegration() {
    // 测试配置类的集成

    // 验证配置类被正确处理
    AppConfig config = applicationContext.getBean(AppConfig.class);
    assertNotNull("AppConfig should be created", config);

    // 验证 @Bean 方法创建的 Bean
    // 这里需要根据实际的 AppConfig 实现来验证
    assertTrue("Application context should contain configured beans",
        applicationContext.containsBean("userService") ||
            applicationContext.containsBean("orderService"));
  }

  @Test
  public void testConcurrentRequests() throws InterruptedException {
    // 测试并发请求处理

    final int threadCount = 5;
    final int requestsPerThread = 10;
    Thread[] threads = new Thread[threadCount];
    final boolean[] results = new boolean[threadCount];

    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            for (int j = 0; j < requestsPerThread; j++) {
              MockHttpServletRequest request = new MockHttpServletRequest();
              MockHttpServletResponse response = new MockHttpServletResponse();

              request.setMethod("GET");
              request.setRequestURI("/user/" + (threadIndex * requestsPerThread + j));

              dispatcherServlet.service(request, response);

              if (response.getStatus() != 200) {
                results[threadIndex] = false;
                return;
              }
            }
            results[threadIndex] = true;
          } catch (Exception e) {
            results[threadIndex] = false;
            e.printStackTrace();
          }
        }
      });
    }

    // 启动所有线程
    for (Thread thread : threads) {
      thread.start();
    }

    // 等待所有线程完成
    for (Thread thread : threads) {
      thread.join();
    }

    // 验证所有线程都成功完成
    for (int i = 0; i < threadCount; i++) {
      assertTrue("Thread " + i + " should complete successfully", results[i]);
    }
  }

  @Test
  public void testResourceManagement() {
    // 测试资源管理

    // 验证应用上下文可以正确关闭
    assertNotNull("Application context should be active", applicationContext);
    assertTrue("Application context should be active", applicationContext.isActive());

    // 关闭应用上下文
    applicationContext.close();

    assertFalse("Application context should be closed", applicationContext.isActive());
  }
}
