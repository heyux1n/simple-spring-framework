package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * HandlerExecutionChain 类的单元测试
 * 
 * 测试处理器执行链的基本功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class HandlerExecutionChainTest {

  private TestController testController;
  private HandlerMethod handlerMethod;

  @Before
  public void setUp() throws NoSuchMethodException {
    testController = new TestController();
    Method method = TestController.class.getDeclaredMethod("testMethod");
    handlerMethod = new HandlerMethod(testController, method);
  }

  @Test
  public void testHandlerExecutionChainCreation() {
    // 测试处理器执行链的创建
    HandlerExecutionChain chain = new HandlerExecutionChain(handlerMethod);

    assertNotNull("执行链不应该为空", chain);
    assertEquals("处理器方法应该正确", handlerMethod, chain.getHandler());
  }

  @Test
  public void testSetHandler() {
    // 测试设置处理器方法
    HandlerExecutionChain chain = new HandlerExecutionChain(handlerMethod);

    TestController anotherController = new TestController();
    Method anotherMethod = TestController.class.getDeclaredMethods()[0];
    HandlerMethod anotherHandlerMethod = new HandlerMethod(anotherController, anotherMethod);

    chain.setHandler(anotherHandlerMethod);

    assertEquals("处理器方法应该被更新", anotherHandlerMethod, chain.getHandler());
  }

  @Test
  public void testToString() {
    // 测试 toString 方法
    HandlerExecutionChain chain = new HandlerExecutionChain(handlerMethod);
    String toString = chain.toString();

    assertNotNull("toString 不应该为空", toString);
    assertTrue("toString 应该包含 HandlerExecutionChain", toString.contains("HandlerExecutionChain"));
    assertTrue("toString 应该包含处理器信息", toString.contains("handler="));
  }

  @Test
  public void testNullHandler() {
    // 测试空处理器的情况
    HandlerExecutionChain chain = new HandlerExecutionChain(null);

    assertNull("处理器应该为空", chain.getHandler());

    // 设置非空处理器
    chain.setHandler(handlerMethod);
    assertEquals("处理器应该被正确设置", handlerMethod, chain.getHandler());
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
