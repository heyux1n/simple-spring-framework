package com.simplespring.example.aspect;

import com.simplespring.example.aspect.PerformanceAspect;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 性能监控切面测试
 * 验证性能监控切面的基本功能
 */
public class PerformanceAspectTest {

  private PerformanceAspect performanceAspect;

  @Before
  public void setUp() {
    performanceAspect = new PerformanceAspect();
  }

  @Test
  public void testPerformanceAspectCreation() {
    // 测试切面对象创建
    assertNotNull("性能监控切面不应为空", performanceAspect);
  }

  @Test
  public void testStartTimingMethods() {
    // 测试开始计时方法可以正常调用（不抛出异常）
    try {
      performanceAspect.startTimingCreateUser();
      performanceAspect.startTimingAuthenticate();
      performanceAspect.startTimingFindUserById();
      performanceAspect.startTimingFindByUsername();
      performanceAspect.startTimingUpdateUser();
      performanceAspect.startTimingCreateOrder();
      performanceAspect.startTimingConfirmOrder();
      performanceAspect.startTimingProcessOrder();
      performanceAspect.startTimingShipOrder();
      performanceAspect.startTimingCompleteOrder();
      performanceAspect.startTimingCancelOrder();
      performanceAspect.startTimingCalculateStatistics();
      performanceAspect.startTimingFindOrderById();
      performanceAspect.startTimingFindByUserId();
    } catch (Exception e) {
      fail("开始计时方法不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testEndTimingMethods() {
    // 测试结束计时方法可以正常调用（不抛出异常）
    try {
      performanceAspect.endTimingCreateUser();
      performanceAspect.endTimingAuthenticate();
      performanceAspect.endTimingFindUserById();
      performanceAspect.endTimingFindByUsername();
      performanceAspect.endTimingUpdateUser();
      performanceAspect.endTimingCreateOrder();
      performanceAspect.endTimingConfirmOrder();
      performanceAspect.endTimingProcessOrder();
      performanceAspect.endTimingShipOrder();
      performanceAspect.endTimingCompleteOrder();
      performanceAspect.endTimingCancelOrder();
      performanceAspect.endTimingCalculateStatistics();
      performanceAspect.endTimingFindOrderById();
      performanceAspect.endTimingFindByUserId();
    } catch (Exception e) {
      fail("结束计时方法不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testPerformanceStatisticsCollection() {
    // 测试性能统计数据收集

    // 模拟方法执行：开始计时 -> 等待 -> 结束计时
    performanceAspect.startTimingCreateUser();

    try {
      Thread.sleep(10); // 模拟方法执行时间
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    performanceAspect.endTimingCreateUser();

    // 验证统计数据
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("UserService.createUser");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是1", 1, stats.getTotalExecutions());
    assertTrue("总耗时应该大于0", stats.getTotalTime() > 0);
    assertTrue("平均耗时应该大于0", stats.getAverageTime() > 0);
  }

  @Test
  public void testMultipleExecutionsStatistics() {
    // 测试多次执行的统计

    // 执行多次相同方法
    for (int i = 0; i < 3; i++) {
      performanceAspect.startTimingAuthenticate();

      try {
        Thread.sleep(5); // 模拟不同的执行时间
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      performanceAspect.endTimingAuthenticate();
    }

    // 验证统计数据
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("UserService.authenticate");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是3", 3, stats.getTotalExecutions());
    assertTrue("总耗时应该大于0", stats.getTotalTime() > 0);
    assertTrue("平均耗时应该大于0", stats.getAverageTime() > 0);
    assertTrue("最小耗时应该大于等于0", stats.getMinTime() >= 0);
    assertTrue("最大耗时应该大于等于最小耗时", stats.getMaxTime() >= stats.getMinTime());
  }

  @Test
  public void testPerformanceReport() {
    // 测试性能报告生成

    // 执行一些方法以生成统计数据
    performanceAspect.startTimingCreateOrder();
    performanceAspect.endTimingCreateOrder();

    performanceAspect.startTimingFindOrderById();
    performanceAspect.endTimingFindOrderById();

    String report = performanceAspect.getPerformanceReport();
    assertNotNull("性能报告不应为空", report);
    assertTrue("报告应包含标题", report.contains("性能统计报告"));
    assertTrue("报告应包含方法统计信息", report.contains("OrderService.createOrder") || report.contains("OrderService.findById"));
  }

  @Test
  public void testEmptyPerformanceReport() {
    // 测试空的性能报告
    String report = performanceAspect.getPerformanceReport();
    assertNotNull("性能报告不应为空", report);
    assertTrue("空报告应包含标题", report.contains("性能统计报告"));
    assertTrue("空报告应显示无数据信息", report.contains("暂无性能数据"));
  }

  @Test
  public void testClearStatistics() {
    // 测试清空统计数据

    // 先生成一些统计数据
    performanceAspect.startTimingUpdateUser();
    performanceAspect.endTimingUpdateUser();

    PerformanceAspect.PerformanceStatistics statsBefore = performanceAspect
        .getMethodStatistics("UserService.updateUser");
    assertNotNull("清空前应该有统计数据", statsBefore);

    // 清空统计数据
    performanceAspect.clearStatistics();

    PerformanceAspect.PerformanceStatistics statsAfter = performanceAspect
        .getMethodStatistics("UserService.updateUser");
    assertNull("清空后应该没有统计数据", statsAfter);

    String report = performanceAspect.getPerformanceReport();
    assertTrue("清空后的报告应显示无数据", report.contains("暂无性能数据"));
  }

  @Test
  public void testEndTimingWithoutStartTiming() {
    // 测试没有开始计时就结束计时的情况
    try {
      performanceAspect.endTimingFindByUserId(); // 没有对应的开始计时

      // 应该不会抛出异常，也不会产生统计数据
      PerformanceAspect.PerformanceStatistics stats = performanceAspect
          .getMethodStatistics("OrderService.findByUserId");
      assertNull("没有开始计时的方法不应该有统计数据", stats);
    } catch (Exception e) {
      fail("没有开始计时就结束计时不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testPerformanceStatisticsToString() {
    // 测试性能统计对象的字符串表示
    performanceAspect.startTimingCalculateStatistics();

    try {
      Thread.sleep(1); // 确保有执行时间
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    performanceAspect.endTimingCalculateStatistics();

    PerformanceAspect.PerformanceStatistics stats = performanceAspect
        .getMethodStatistics("OrderService.calculateStatistics");
    assertNotNull("应该有统计数据", stats);

    String statsString = stats.toString();
    assertNotNull("统计信息字符串不应为空", statsString);
    assertTrue("应包含方法名", statsString.contains("OrderService.calculateStatistics"));
    assertTrue("应包含调用次数", statsString.contains("调用次数"));
    assertTrue("应包含耗时信息", statsString.contains("耗时"));
  }
}
