package com.simplespring.example.aspect;

import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.AfterReturning;
import com.simplespring.core.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录切面
 * 演示方法执行日志记录功能，拦截业务方法的调用
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  public LoggingAspect() {
    logger.info("LoggingAspect 初始化完成");
  }

  /**
   * 前置通知 - 在方法执行前记录日志
   * 拦截所有 UserService 的方法
   */
  @Before("com.simplespring.example.service.impl.UserServiceImpl.*")
  public void logBeforeUserService() {
    logger.info("=== [用户服务] 方法执行前 ===");
  }

  /**
   * 前置通知 - 在方法执行前记录日志
   * 拦截所有 OrderService 的方法
   */
  @Before("com.simplespring.example.service.OrderService.*")
  public void logBeforeOrderService() {
    logger.info("=== [订单服务] 方法执行前 ===");
  }

  /**
   * 前置通知 - 拦截创建订单方法
   */
  @Before("com.simplespring.example.service.OrderService.createOrder")
  public void logBeforeCreateOrder() {
    logger.info(">>> 开始创建订单操作");
  }

  /**
   * 前置通知 - 拦截用户认证方法
   */
  @Before("com.simplespring.example.service.impl.UserServiceImpl.authenticate")
  public void logBeforeAuthenticate() {
    logger.info(">>> 开始用户认证操作");
  }

  /**
   * 前置通知 - 拦截订单状态变更方法
   */
  @Before("com.simplespring.example.service.OrderService.confirmOrder")
  public void logBeforeConfirmOrder() {
    logger.info(">>> 开始确认订单操作");
  }

  @Before("com.simplespring.example.service.OrderService.processOrder")
  public void logBeforeProcessOrder() {
    logger.info(">>> 开始处理订单操作");
  }

  @Before("com.simplespring.example.service.OrderService.shipOrder")
  public void logBeforeShipOrder() {
    logger.info(">>> 开始发货订单操作");
  }

  @Before("com.simplespring.example.service.OrderService.completeOrder")
  public void logBeforeCompleteOrder() {
    logger.info(">>> 开始完成订单操作");
  }

  @Before("com.simplespring.example.service.OrderService.cancelOrder")
  public void logBeforeCancelOrder() {
    logger.info(">>> 开始取消订单操作");
  }

  /**
   * 后置通知 - 在方法执行后记录日志
   * 拦截所有 UserService 的方法
   */
  @After("com.simplespring.example.service.impl.UserServiceImpl.*")
  public void logAfterUserService() {
    logger.info("=== [用户服务] 方法执行后 ===");
  }

  /**
   * 后置通知 - 在方法执行后记录日志
   * 拦截所有 OrderService 的方法
   */
  @After("com.simplespring.example.service.OrderService.*")
  public void logAfterOrderService() {
    logger.info("=== [订单服务] 方法执行后 ===");
  }

  /**
   * 后置通知 - 拦截创建订单方法
   */
  @After("com.simplespring.example.service.OrderService.createOrder")
  public void logAfterCreateOrder() {
    logger.info("<<< 创建订单操作完成");
  }

  /**
   * 后置通知 - 拦截用户认证方法
   */
  @After("com.simplespring.example.service.impl.UserServiceImpl.authenticate")
  public void logAfterAuthenticate() {
    logger.info("<<< 用户认证操作完成");
  }

  /**
   * 后置通知 - 拦截订单状态变更方法
   */
  @After("com.simplespring.example.service.OrderService.confirmOrder")
  public void logAfterConfirmOrder() {
    logger.info("<<< 确认订单操作完成");
  }

  @After("com.simplespring.example.service.OrderService.processOrder")
  public void logAfterProcessOrder() {
    logger.info("<<< 处理订单操作完成");
  }

  @After("com.simplespring.example.service.OrderService.shipOrder")
  public void logAfterShipOrder() {
    logger.info("<<< 发货订单操作完成");
  }

  @After("com.simplespring.example.service.OrderService.completeOrder")
  public void logAfterCompleteOrder() {
    logger.info("<<< 完成订单操作完成");
  }

  @After("com.simplespring.example.service.OrderService.cancelOrder")
  public void logAfterCancelOrder() {
    logger.info("<<< 取消订单操作完成");
  }

  /**
   * 返回后通知 - 在方法正常返回后记录日志和返回值
   * 拦截创建用户方法
   */
  @AfterReturning(value = "com.simplespring.example.service.impl.UserServiceImpl.createUser", returning = "result")
  public void logAfterReturningCreateUser(Object result) {
    if (result != null) {
      logger.info("<<< 用户创建成功，返回结果: {}", result.toString());
    } else {
      logger.warn("<<< 用户创建返回null");
    }
  }

  /**
   * 返回后通知 - 在方法正常返回后记录日志和返回值
   * 拦截创建订单方法
   */
  @AfterReturning(value = "com.simplespring.example.service.OrderService.createOrder", returning = "result")
  public void logAfterReturningCreateOrder(Object result) {
    if (result != null) {
      logger.info("<<< 订单创建成功，返回结果: {}", result.toString());
    } else {
      logger.warn("<<< 订单创建返回null");
    }
  }

  /**
   * 返回后通知 - 在方法正常返回后记录日志和返回值
   * 拦截用户认证方法
   */
  @AfterReturning(value = "com.simplespring.example.service.impl.UserServiceImpl.authenticate", returning = "result")
  public void logAfterReturningAuthenticate(Object result) {
    if (result != null) {
      logger.info("<<< 用户认证成功，用户: {}", result.toString());
    } else {
      logger.warn("<<< 用户认证失败");
    }
  }

  /**
   * 返回后通知 - 拦截查找用户方法
   */
  @AfterReturning(value = "com.simplespring.example.service.impl.UserServiceImpl.findById", returning = "result")
  public void logAfterReturningFindUserById(Object result) {
    if (result != null) {
      logger.debug("<<< 根据ID查找用户成功: {}", result.toString());
    } else {
      logger.debug("<<< 根据ID查找用户未找到");
    }
  }

  /**
   * 返回后通知 - 拦截查找订单方法
   */
  @AfterReturning(value = "com.simplespring.example.service.OrderService.findById", returning = "result")
  public void logAfterReturningFindOrderById(Object result) {
    if (result != null) {
      logger.debug("<<< 根据ID查找订单成功: {}", result.toString());
    } else {
      logger.debug("<<< 根据ID查找订单未找到");
    }
  }

  /**
   * 返回后通知 - 拦截订单统计方法
   */
  @AfterReturning(value = "com.simplespring.example.service.OrderService.calculateStatistics", returning = "result")
  public void logAfterReturningCalculateStatistics(Object result) {
    if (result != null) {
      logger.info("<<< 订单统计计算完成: {}", result.toString());
    } else {
      logger.warn("<<< 订单统计计算返回null");
    }
  }
}
