package com.simplespring.example.aspect;

import com.simplespring.example.aspect.LoggingAspect;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 日志切面测试
 * 验证日志切面的基本功能
 */
public class LoggingAspectTest {

  private LoggingAspect loggingAspect;

  @Before
  public void setUp() {
    loggingAspect = new LoggingAspect();
  }

  @Test
  public void testLoggingAspectCreation() {
    // 测试切面对象创建
    assertNotNull("日志切面不应为空", loggingAspect);
  }

  @Test
  public void testBeforeAdviceMethods() {
    // 测试前置通知方法可以正常调用（不抛出异常）
    try {
      loggingAspect.logBeforeUserService();
      loggingAspect.logBeforeOrderService();
      loggingAspect.logBeforeCreateOrder();
      loggingAspect.logBeforeAuthenticate();
      loggingAspect.logBeforeConfirmOrder();
      loggingAspect.logBeforeProcessOrder();
      loggingAspect.logBeforeShipOrder();
      loggingAspect.logBeforeCompleteOrder();
      loggingAspect.logBeforeCancelOrder();
    } catch (Exception e) {
      fail("前置通知方法不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testAfterAdviceMethods() {
    // 测试后置通知方法可以正常调用（不抛出异常）
    try {
      loggingAspect.logAfterUserService();
      loggingAspect.logAfterOrderService();
      loggingAspect.logAfterCreateOrder();
      loggingAspect.logAfterAuthenticate();
      loggingAspect.logAfterConfirmOrder();
      loggingAspect.logAfterProcessOrder();
      loggingAspect.logAfterShipOrder();
      loggingAspect.logAfterCompleteOrder();
      loggingAspect.logAfterCancelOrder();
    } catch (Exception e) {
      fail("后置通知方法不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testAfterReturningAdviceMethods() {
    // 测试返回后通知方法可以正常调用（不抛出异常）
    try {
      // 测试有返回值的情况
      Object mockResult = "测试结果";
      loggingAspect.logAfterReturningCreateUser(mockResult);
      loggingAspect.logAfterReturningCreateOrder(mockResult);
      loggingAspect.logAfterReturningAuthenticate(mockResult);
      loggingAspect.logAfterReturningFindUserById(mockResult);
      loggingAspect.logAfterReturningFindOrderById(mockResult);
      loggingAspect.logAfterReturningCalculateStatistics(mockResult);

      // 测试返回值为null的情况
      loggingAspect.logAfterReturningCreateUser(null);
      loggingAspect.logAfterReturningCreateOrder(null);
      loggingAspect.logAfterReturningAuthenticate(null);
      loggingAspect.logAfterReturningFindUserById(null);
      loggingAspect.logAfterReturningFindOrderById(null);
      loggingAspect.logAfterReturningCalculateStatistics(null);
    } catch (Exception e) {
      fail("返回后通知方法不应抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testAfterReturningWithDifferentResultTypes() {
    // 测试不同类型的返回值
    try {
      // 字符串类型
      loggingAspect.logAfterReturningCreateUser("字符串结果");

      // 数字类型
      loggingAspect.logAfterReturningCreateOrder(123);

      // 布尔类型
      loggingAspect.logAfterReturningAuthenticate(true);

      // 对象类型
      Object complexObject = new Object() {
        @Override
        public String toString() {
          return "复杂对象结果";
        }
      };
      loggingAspect.logAfterReturningFindUserById(complexObject);
    } catch (Exception e) {
      fail("处理不同类型返回值时不应抛出异常: " + e.getMessage());
    }
  }
}
