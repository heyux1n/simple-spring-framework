package com.simplespring.example.aspect;

import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 性能监控切面
 * 演示方法执行时间统计功能，监控业务方法的性能
 */
@Aspect
@Component
public class PerformanceAspect {

  private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

  // 使用ThreadLocal存储每个线程的方法执行开始时间
  private final ThreadLocal<Map<String, Long>> methodStartTimes = new ThreadLocal<Map<String, Long>>() {
    @Override
    protected Map<String, Long> initialValue() {
      return new ConcurrentHashMap<String, Long>();
    }
  };

  // 性能统计信息存储
  private final Map<String, PerformanceStatistics> performanceStats = new ConcurrentHashMap<String, PerformanceStatistics>();

  public PerformanceAspect() {
    logger.info("PerformanceAspect 初始化完成");
  }

  /**
   * 前置通知 - 记录方法开始执行时间
   * 拦截用户服务的关键方法
   */
  @Before("com.simplespring.example.service.impl.UserServiceImpl.createUser")
  public void startTimingCreateUser() {
    startTiming("UserService.createUser");
  }

  @Before("com.simplespring.example.service.impl.UserServiceImpl.authenticate")
  public void startTimingAuthenticate() {
    startTiming("UserService.authenticate");
  }

  @Before("com.simplespring.example.service.impl.UserServiceImpl.findById")
  public void startTimingFindUserById() {
    startTiming("UserService.findById");
  }

  @Before("com.simplespring.example.service.impl.UserServiceImpl.findByUsername")
  public void startTimingFindByUsername() {
    startTiming("UserService.findByUsername");
  }

  @Before("com.simplespring.example.service.impl.UserServiceImpl.updateUser")
  public void startTimingUpdateUser() {
    startTiming("UserService.updateUser");
  }

  /**
   * 前置通知 - 记录方法开始执行时间
   * 拦截订单服务的关键方法
   */
  @Before("com.simplespring.example.service.OrderService.createOrder")
  public void startTimingCreateOrder() {
    startTiming("OrderService.createOrder");
  }

  @Before("com.simplespring.example.service.OrderService.confirmOrder")
  public void startTimingConfirmOrder() {
    startTiming("OrderService.confirmOrder");
  }

  @Before("com.simplespring.example.service.OrderService.processOrder")
  public void startTimingProcessOrder() {
    startTiming("OrderService.processOrder");
  }

  @Before("com.simplespring.example.service.OrderService.shipOrder")
  public void startTimingShipOrder() {
    startTiming("OrderService.shipOrder");
  }

  @Before("com.simplespring.example.service.OrderService.completeOrder")
  public void startTimingCompleteOrder() {
    startTiming("OrderService.completeOrder");
  }

  @Before("com.simplespring.example.service.OrderService.cancelOrder")
  public void startTimingCancelOrder() {
    startTiming("OrderService.cancelOrder");
  }

  @Before("com.simplespring.example.service.OrderService.calculateStatistics")
  public void startTimingCalculateStatistics() {
    startTiming("OrderService.calculateStatistics");
  }

  @Before("com.simplespring.example.service.OrderService.findById")
  public void startTimingFindOrderById() {
    startTiming("OrderService.findById");
  }

  @Before("com.simplespring.example.service.OrderService.findByUserId")
  public void startTimingFindByUserId() {
    startTiming("OrderService.findByUserId");
  }

  /**
   * 后置通知 - 计算方法执行时间
   * 拦截用户服务的关键方法
   */
  @After("com.simplespring.example.service.impl.UserServiceImpl.createUser")
  public void endTimingCreateUser() {
    endTiming("UserService.createUser");
  }

  @After("com.simplespring.example.service.impl.UserServiceImpl.authenticate")
  public void endTimingAuthenticate() {
    endTiming("UserService.authenticate");
  }

  @After("com.simplespring.example.service.impl.UserServiceImpl.findById")
  public void endTimingFindUserById() {
    endTiming("UserService.findById");
  }

  @After("com.simplespring.example.service.impl.UserServiceImpl.findByUsername")
  public void endTimingFindByUsername() {
    endTiming("UserService.findByUsername");
  }

  @After("com.simplespring.example.service.impl.UserServiceImpl.updateUser")
  public void endTimingUpdateUser() {
    endTiming("UserService.updateUser");
  }

  /**
   * 后置通知 - 计算方法执行时间
   * 拦截订单服务的关键方法
   */
  @After("com.simplespring.example.service.OrderService.createOrder")
  public void endTimingCreateOrder() {
    endTiming("OrderService.createOrder");
  }

  @After("com.simplespring.example.service.OrderService.confirmOrder")
  public void endTimingConfirmOrder() {
    endTiming("OrderService.confirmOrder");
  }

  @After("com.simplespring.example.service.OrderService.processOrder")
  public void endTimingProcessOrder() {
    endTiming("OrderService.processOrder");
  }

  @After("com.simplespring.example.service.OrderService.shipOrder")
  public void endTimingShipOrder() {
    endTiming("OrderService.shipOrder");
  }

  @After("com.simplespring.example.service.OrderService.completeOrder")
  public void endTimingCompleteOrder() {
    endTiming("OrderService.completeOrder");
  }

  @After("com.simplespring.example.service.OrderService.cancelOrder")
  public void endTimingCancelOrder() {
    endTiming("OrderService.cancelOrder");
  }

  @After("com.simplespring.example.service.OrderService.calculateStatistics")
  public void endTimingCalculateStatistics() {
    endTiming("OrderService.calculateStatistics");
  }

  @After("com.simplespring.example.service.OrderService.findById")
  public void endTimingFindOrderById() {
    endTiming("OrderService.findById");
  }

  @After("com.simplespring.example.service.OrderService.findByUserId")
  public void endTimingFindByUserId() {
    endTiming("OrderService.findByUserId");
  }

  /**
   * 开始计时
   */
  private void startTiming(String methodName) {
    long startTime = System.currentTimeMillis();
    methodStartTimes.get().put(methodName, startTime);
    logger.debug("⏱️ [性能监控] {} 开始执行", methodName);
  }

  /**
   * 结束计时并记录性能数据
   */
  private void endTiming(String methodName) {
    Long startTime = methodStartTimes.get().remove(methodName);
    if (startTime != null) {
      long endTime = System.currentTimeMillis();
      long executionTime = endTime - startTime;

      // 更新性能统计
      updatePerformanceStatistics(methodName, executionTime);

      // 记录执行时间
      if (executionTime > 100) {
        logger.warn("⏱️ [性能监控] {} 执行完成，耗时: {}ms (较慢)", methodName, executionTime);
      } else if (executionTime > 50) {
        logger.info("⏱️ [性能监控] {} 执行完成，耗时: {}ms", methodName, executionTime);
      } else {
        logger.debug("⏱️ [性能监控] {} 执行完成，耗时: {}ms", methodName, executionTime);
      }
    }
  }

  /**
   * 更新性能统计信息
   */
  private void updatePerformanceStatistics(String methodName, long executionTime) {
    PerformanceStatistics stats = performanceStats.get(methodName);
    if (stats == null) {
      stats = new PerformanceStatistics(methodName);
      performanceStats.put(methodName, stats);
    }
    stats.addExecution(executionTime);
  }

  /**
   * 获取性能统计报告
   */
  public String getPerformanceReport() {
    StringBuilder report = new StringBuilder();
    report.append("\n=== 性能统计报告 ===\n");

    if (performanceStats.isEmpty()) {
      report.append("暂无性能数据\n");
    } else {
      for (PerformanceStatistics stats : performanceStats.values()) {
        report.append(stats.toString()).append("\n");
      }
    }

    report.append("==================\n");
    return report.toString();
  }

  /**
   * 清空性能统计数据
   */
  public void clearStatistics() {
    performanceStats.clear();
    logger.info("性能统计数据已清空");
  }

  /**
   * 获取指定方法的性能统计
   */
  public PerformanceStatistics getMethodStatistics(String methodName) {
    return performanceStats.get(methodName);
  }

  /**
   * 性能统计信息内部类
   */
  public static class PerformanceStatistics {
    private final String methodName;
    private long totalExecutions;
    private long totalTime;
    private long minTime;
    private long maxTime;
    private double averageTime;

    public PerformanceStatistics(String methodName) {
      this.methodName = methodName;
      this.totalExecutions = 0;
      this.totalTime = 0;
      this.minTime = Long.MAX_VALUE;
      this.maxTime = 0;
      this.averageTime = 0.0;
    }

    public synchronized void addExecution(long executionTime) {
      totalExecutions++;
      totalTime += executionTime;

      if (executionTime < minTime) {
        minTime = executionTime;
      }

      if (executionTime > maxTime) {
        maxTime = executionTime;
      }

      averageTime = (double) totalTime / totalExecutions;
    }

    // Getters
    public String getMethodName() {
      return methodName;
    }

    public long getTotalExecutions() {
      return totalExecutions;
    }

    public long getTotalTime() {
      return totalTime;
    }

    public long getMinTime() {
      return minTime == Long.MAX_VALUE ? 0 : minTime;
    }

    public long getMaxTime() {
      return maxTime;
    }

    public double getAverageTime() {
      return averageTime;
    }

    @Override
    public String toString() {
      return String.format(
          "方法: %s | 调用次数: %d | 总耗时: %dms | 平均耗时: %.2fms | 最短: %dms | 最长: %dms",
          methodName, totalExecutions, totalTime, averageTime, getMinTime(), maxTime);
    }
  }
}
