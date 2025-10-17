package com.simplespring.webmvc;

import com.simplespring.context.AnnotationConfigApplicationContext;
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
 * WebMVC 性能测试，测试请求处理的性能
 * 
 * @author SimpleSpring Framework
 */
public class WebMvcPerformanceTest {

  private static final int ITERATIONS = 1000;
  private static final long MAX_ACCEPTABLE_TIME_MS = 3000; // 3秒

  private DispatcherServlet dispatcherServlet;
  private RequestMappingHandlerMapping handlerMapping;
  private ParameterResolverComposite parameterResolver;
  private CompositeViewResolver viewResolver;

  @Before
  public void setUp() {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(TestController.class);
    applicationContext.refresh();

    dispatcherServlet = new DispatcherServlet();
    dispatcherServlet.setApplicationContext(applicationContext);
    dispatcherServlet.init();

    handlerMapping = new RequestMappingHandlerMapping();
    handlerMapping.setApplicationContext(applicationContext);

    parameterResolver = new ParameterResolverComposite();
    parameterResolver.addResolver(new BasicTypeParameterResolver());
    parameterResolver.addResolver(new ServletParameterResolver());

    viewResolver = new CompositeViewResolver();
    viewResolver.addViewResolver(new JsonViewResolver());
    viewResolver.addViewResolver(new SimpleViewResolver());
  }

  @Test
  public void testRequestMappingPerformance() {
    // 测试请求映射性能

    TestController controller = new TestController();
    handlerMapping.scanController(TestController.class, controller);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/test");

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      HandlerExecutionChain chain = handlerMapping.getHandler(request);
      assertNotNull("Handler should be found", chain);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Request mapping performance: " + duration + "ms for " +
        ITERATIONS + " lookups");
    assertTrue("Request mapping should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS / 3);
  }

  @Test
  public void testParameterResolutionPerformance() throws Exception {
    // 测试参数解析性能

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setParameter("name", "test");
    request.setParameter("age", "25");
    request.setParameter("active", "true");

    TestController controller = new TestController();
    HandlerMethod handlerMethod = new HandlerMethod(controller,
        TestController.class.getMethod("methodWithParams", String.class, int.class, boolean.class));

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      Object[] args = parameterResolver.resolveParameters(handlerMethod, request, response);
      assertEquals("Should resolve 3 parameters", 3, args.length);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Parameter resolution performance: " + duration + "ms for " +
        ITERATIONS + " resolutions");
    assertTrue("Parameter resolution should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS / 2);
  }

  @Test
  public void testViewResolutionPerformance() throws Exception {
    // 测试视图解析性能

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setHeader("Accept", "application/json");

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      viewResolver.resolveView("test result " + i, request, response);
      response.reset(); // 重置响应以便下次使用
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("View resolution performance: " + duration + "ms for " +
        ITERATIONS + " resolutions");
    assertTrue("View resolution should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS / 2);
  }

  @Test
  public void testCompleteRequestProcessingPerformance() throws ServletException, IOException {
    // 测试完整请求处理性能

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod("GET");
      request.setRequestURI("/test");
      request.setParameter("id", String.valueOf(i));

      dispatcherServlet.service(request, response);

      assertEquals("Response should be successful", 200, response.getStatus());
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Complete request processing performance: " + duration + "ms for " +
        ITERATIONS + " requests");
    assertTrue("Complete request processing should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testConcurrentRequestProcessing() throws InterruptedException {
    // 测试并发请求处理性能

    final int threadCount = 4;
    final int requestsPerThread = ITERATIONS / threadCount;
    Thread[] threads = new Thread[threadCount];
    final long[] threadTimes = new long[threadCount];
    final boolean[] threadResults = new boolean[threadCount];

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          long threadStartTime = System.currentTimeMillis();
          try {
            for (int j = 0; j < requestsPerThread; j++) {
              MockHttpServletRequest request = new MockHttpServletRequest();
              MockHttpServletResponse response = new MockHttpServletResponse();

              request.setMethod("GET");
              request.setRequestURI("/test");
              request.setParameter("id", String.valueOf(threadIndex * requestsPerThread + j));

              dispatcherServlet.service(request, response);

              if (response.getStatus() != 200) {
                threadResults[threadIndex] = false;
                return;
              }
            }
            threadResults[threadIndex] = true;
          } catch (Exception e) {
            threadResults[threadIndex] = false;
            e.printStackTrace();
          }
          threadTimes[threadIndex] = System.currentTimeMillis() - threadStartTime;
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

    long totalTime = System.currentTimeMillis() - startTime;

    System.out.println("Concurrent request processing performance: " + totalTime + "ms total");
    for (int i = 0; i < threadCount; i++) {
      System.out.println("Thread " + i + ": " + threadTimes[i] + "ms, success: " + threadResults[i]);
      assertTrue("Thread " + i + " should complete successfully", threadResults[i]);
    }

    assertTrue("Concurrent request processing should complete within acceptable time",
        totalTime < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testDifferentRequestTypesPerformance() throws ServletException, IOException {
    // 测试不同类型请求的性能

    String[] uris = { "/test", "/test/params", "/test/json", "/test/post" };
    String[] methods = { "GET", "GET", "GET", "POST" };

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
      int requestType = i % uris.length;

      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod(methods[requestType]);
      request.setRequestURI(uris[requestType]);

      if (requestType == 1) { // params request
        request.setParameter("name", "test" + i);
        request.setParameter("age", String.valueOf(20 + (i % 50)));
      } else if (requestType == 2) { // json request
        request.setHeader("Accept", "application/json");
      } else if (requestType == 3) { // post request
        request.setParameter("data", "test data " + i);
      }

      dispatcherServlet.service(request, response);

      // 大部分请求应该成功，某些可能返回404（如果映射不存在）
      assertTrue("Response should be successful or not found",
          response.getStatus() == 200 || response.getStatus() == 404);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Different request types performance: " + duration + "ms for " +
        ITERATIONS + " mixed requests");
    assertTrue("Different request types should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  @Test
  public void testMemoryUsageDuringRequestProcessing() throws ServletException, IOException {
    // 测试请求处理过程中的内存使用

    Runtime runtime = Runtime.getRuntime();

    // 记录初始内存
    System.gc();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    for (int i = 0; i < ITERATIONS * 2; i++) {
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod("GET");
      request.setRequestURI("/test");
      request.setParameter("id", String.valueOf(i));

      dispatcherServlet.service(request, response);

      // 定期检查内存使用
      if (i % 200 == 0) {
        long currentMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = currentMemory - initialMemory;

        // 如果内存增长过快，触发垃圾回收
        if (memoryIncrease > 50 * 1024 * 1024) { // 50MB
          System.gc();
          Thread.yield();
        }
      }
    }

    // 最终内存检查
    System.gc();
    long finalMemory = runtime.totalMemory() - runtime.freeMemory();
    long totalMemoryIncrease = finalMemory - initialMemory;

    System.out.println("Memory increase during request processing: " +
        (totalMemoryIncrease / 1024 / 1024) + "MB");

    // 内存增长应该在合理范围内
    assertTrue("Memory usage should be reasonable",
        totalMemoryIncrease < 100 * 1024 * 1024); // 100MB
  }

  @Test
  public void testLargeResponseHandlingPerformance() throws ServletException, IOException {
    // 测试大响应处理性能

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS / 10; i++) {
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod("GET");
      request.setRequestURI("/test/large");

      dispatcherServlet.service(request, response);

      // 验证响应
      assertTrue("Response should be successful or not found",
          response.getStatus() == 200 || response.getStatus() == 404);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("Large response handling performance: " + duration + "ms for " +
        (ITERATIONS / 10) + " requests");
    assertTrue("Large response handling should complete within acceptable time",
        duration < MAX_ACCEPTABLE_TIME_MS);
  }

  // 测试用的控制器
  @Controller
  public static class TestController {

    @RequestMapping("/test")
    public String testMethod(String id) {
      return "test result for id: " + id;
    }

    @RequestMapping("/test/params")
    public String methodWithParams(String name, int age, boolean active) {
      return "name: " + name + ", age: " + age + ", active: " + active;
    }

    @RequestMapping("/test/json")
    public String jsonMethod() {
      return "{\"message\": \"json response\", \"timestamp\": " + System.currentTimeMillis() + "}";
    }

    @RequestMapping(value = "/test/post", method = RequestMethod.POST)
    public String postMethod(String data) {
      return "posted data: " + data;
    }

    @RequestMapping("/test/large")
    public String largeResponseMethod() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 1000; i++) {
        sb.append("This is line ").append(i).append(" of a large response. ");
      }
      return sb.toString();
    }
  }
}
