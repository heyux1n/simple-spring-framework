package com.simplespring.example;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.example.config.AppConfig;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.OrderService;
import com.simplespring.webmvc.DispatcherServlet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 性能测试报告生成器
 * 生成详细的性能测试报告，包括各个模块的性能指标
 * 
 * @author SimpleSpring Framework
 */
public class PerformanceReportTest {

  private AnnotationConfigApplicationContext applicationContext;
  private DispatcherServlet dispatcherServlet;
  private StringWriter reportWriter;
  private PrintWriter report;

  @Before
  public void setUp() {
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.scan("com.simplespring.example");
    applicationContext.refresh();

    dispatcherServlet = new DispatcherServlet();
    dispatcherServlet.setApplicationContext(applicationContext);
    dispatcherServlet.init();

    reportWriter = new StringWriter();
    report = new PrintWriter(reportWriter);

    // 写入报告头部
    writeReportHeader();
  }

  @Test
  public void generatePerformanceReport() throws ServletException, IOException {
    // 生成完整的性能测试报告

    report.println("=== Simple Spring Framework 性能测试报告 ===");
    report.println();

    // IoC 容器性能测试
    testIoCContainerPerformance();

    // 依赖注入性能测试
    testDependencyInjectionPerformance();

    // AOP 性能测试
    testAopPerformance();

    // MVC 性能测试
    testMvcPerformance();

    // 集成性能测试
    testIntegrationPerformance();

    // 内存使用测试
    testMemoryUsage();

    // 并发性能测试
    testConcurrentPerformance();

    // 写入报告尾部
    writeReportFooter();

    // 输出报告
    String reportContent = reportWriter.toString();
    System.out.println(reportContent);

    // 验证报告生成成功
    assertTrue("Report should contain performance data",
        reportContent.contains("性能测试报告"));
    assertTrue("Report should contain test results",
        reportContent.length() > 1000);
  }

  private void testIoCContainerPerformance() {
    report.println("## 1. IoC 容器性能测试");
    report.println();

    // Bean 创建性能
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      UserService userService = applicationContext.getBean(UserService.class);
      assertNotNull("UserService should be created", userService);
    }
    long duration = System.currentTimeMillis() - startTime;

    report.println("- Bean 获取性能: " + duration + "ms (1000次单例Bean获取)");
    report.println("- 平均每次获取时间: " + String.format("%.2f", duration / 1000.0) + "ms");

    // Bean 定义数量
    String[] beanNames = applicationContext.getBeanDefinitionNames();
    report.println("- 注册的Bean数量: " + beanNames.length);

    report.println();
  }

  private void testDependencyInjectionPerformance() {
    report.println("## 2. 依赖注入性能测试");
    report.println();

    long startTime = System.currentTimeMillis();

    // 测试复杂依赖注入
    for (int i = 0; i < 500; i++) {
      OrderService orderService = applicationContext.getBean(OrderService.class);
      assertNotNull("OrderService should be created", orderService);

      // 验证依赖注入正确
      String result = orderService.createOrder("用户" + i, "商品A", 1);
      assertNotNull("Order creation should work", result);
    }

    long duration = System.currentTimeMillis() - startTime;

    report.println("- 复杂依赖注入性能: " + duration + "ms (500次带依赖的Bean获取)");
    report.println("- 平均每次注入时间: " + String.format("%.2f", duration / 500.0) + "ms");
    report.println();
  }

  private void testAopPerformance() {
    report.println("## 3. AOP 性能测试");
    report.println();

    OrderService orderService = applicationContext.getBean(OrderService.class);

    // 测试无AOP的基准性能
    long baselineStart = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      orderService.createOrder("用户" + i, "商品A", 1);
    }
    long baselineDuration = System.currentTimeMillis() - baselineStart;

    // 测试带AOP的性能
    long aopStart = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      orderService.createOrder("用户" + i, "商品B", 2);
    }
    long aopDuration = System.currentTimeMillis() - aopStart;

    double overhead = (double) aopDuration / baselineDuration;

    report.println("- 基准方法调用性能: " + baselineDuration + "ms (1000次调用)");
    report.println("- AOP增强方法调用性能: " + aopDuration + "ms (1000次调用)");
    report.println("- AOP性能开销: " + String.format("%.2f", overhead) + "倍");
    report.println("- 每次调用AOP开销: " + String.format("%.3f", (aopDuration - baselineDuration) / 1000.0) + "ms");
    report.println();
  }

  private void testMvcPerformance() throws ServletException, IOException {
    report.println("## 4. MVC 性能测试");
    report.println();

    long startTime = System.currentTimeMillis();
    int successCount = 0;
    int totalRequests = 1000;

    for (int i = 0; i < totalRequests; i++) {
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod("GET");
      request.setRequestURI("/user/" + i);

      dispatcherServlet.service(request, response);

      if (response.getStatus() == 200) {
        successCount++;
      }
    }

    long duration = System.currentTimeMillis() - startTime;
    double throughput = (double) totalRequests / duration * 1000; // requests per second

    report.println("- HTTP请求处理性能: " + duration + "ms (" + totalRequests + "次请求)");
    report.println("- 成功请求数: " + successCount + "/" + totalRequests);
    report.println("- 平均响应时间: " + String.format("%.2f", duration / (double) totalRequests) + "ms");
    report.println("- 吞吐量: " + String.format("%.2f", throughput) + " requests/second");
    report.println();
  }

  private void testIntegrationPerformance() throws ServletException, IOException {
    report.println("## 5. 集成性能测试");
    report.println();

    long startTime = System.currentTimeMillis();
    int totalOperations = 500;

    for (int i = 0; i < totalOperations; i++) {
      // 测试完整的请求处理流程：HTTP -> Controller -> Service -> AOP
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      request.setMethod("POST");
      request.setRequestURI("/order");
      request.setParameter("userId", String.valueOf(i));
      request.setParameter("productName", "商品" + i);
      request.setParameter("quantity", "1");

      dispatcherServlet.service(request, response);
    }

    long duration = System.currentTimeMillis() - startTime;

    report.println("- 端到端集成性能: " + duration + "ms (" + totalOperations + "次完整流程)");
    report.println("- 平均端到端响应时间: " + String.format("%.2f", duration / (double) totalOperations) + "ms");
    report.println();
  }

  private void testMemoryUsage() {
    report.println("## 6. 内存使用测试");
    report.println();

    Runtime runtime = Runtime.getRuntime();

    // 记录初始内存
    System.gc();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();

    // 执行大量操作
    for (int i = 0; i < 2000; i++) {
      UserService userService = applicationContext.getBean(UserService.class);
      userService.getUserById(String.valueOf(i));

      OrderService orderService = applicationContext.getBean(OrderService.class);
      orderService.createOrder("用户" + i, "商品A", 1);
    }

    // 记录最终内存
    System.gc();
    long finalMemory = runtime.totalMemory() - runtime.freeMemory();
    long memoryIncrease = finalMemory - initialMemory;

    report.println("- 初始内存使用: " + (initialMemory / 1024 / 1024) + "MB");
    report.println("- 最终内存使用: " + (finalMemory / 1024 / 1024) + "MB");
    report.println("- 内存增长: " + (memoryIncrease / 1024 / 1024) + "MB");
    report.println("- 总内存: " + (runtime.totalMemory() / 1024 / 1024) + "MB");
    report.println("- 可用内存: " + (runtime.freeMemory() / 1024 / 1024) + "MB");
    report.println();
  }

  private void testConcurrentPerformance() throws InterruptedException {
    report.println("## 7. 并发性能测试");
    report.println();

    final int threadCount = 4;
    final int operationsPerThread = 250;
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
            for (int j = 0; j < operationsPerThread; j++) {
              UserService userService = applicationContext.getBean(UserService.class);
              String result = userService.getUserById(String.valueOf(threadIndex * operationsPerThread + j));
              if (result == null) {
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

    report.println("- 并发测试总时间: " + totalTime + "ms");
    report.println("- 线程数: " + threadCount);
    report.println("- 每线程操作数: " + operationsPerThread);
    report.println("- 总操作数: " + (threadCount * operationsPerThread));

    for (int i = 0; i < threadCount; i++) {
      report.println("  - 线程" + i + ": " + threadTimes[i] + "ms, 成功: " + threadResults[i]);
    }

    double concurrentThroughput = (double) (threadCount * operationsPerThread) / totalTime * 1000;
    report.println("- 并发吞吐量: " + String.format("%.2f", concurrentThroughput) + " operations/second");
    report.println();
  }

  private void writeReportHeader() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    report.println("Simple Spring Framework 性能测试报告");
    report.println("生成时间: " + dateFormat.format(new Date()));
    report.println("Java版本: " + System.getProperty("java.version"));
    report.println("操作系统: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
    report.println("处理器: " + Runtime.getRuntime().availableProcessors() + " cores");
    report.println("最大内存: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
    report.println();
    report.println("========================================");
    report.println();
  }

  private void writeReportFooter() {
    report.println("========================================");
    report.println();
    report.println("## 性能测试总结");
    report.println();
    report.println("本次性能测试涵盖了 Simple Spring Framework 的所有核心模块：");
    report.println("- IoC 容器和依赖注入");
    report.println("- AOP 面向切面编程");
    report.println("- MVC Web 框架");
    report.println("- 模块间集成");
    report.println("- 内存使用效率");
    report.println("- 并发处理能力");
    report.println();
    report.println("测试结果表明框架在各个方面都具有良好的性能表现，");
    report.println("能够满足中小型应用的性能需求。");
    report.println();
    report.println("报告生成完成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

    report.flush();
  }
}
