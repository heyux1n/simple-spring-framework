package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 简单的切点测试
 * 
 * @author SimpleSpring
 */
public class SimplePointcutTest {

  @Test
  public void testSimpleWithinExpression() {
    // 测试简单的 within 表达式
    String expression = "within(com.simplespring.aop.*)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());

    // 测试类匹配
    assertTrue("应该匹配同包下的类",
        matcher.matches(SimplePointcutTest.class));
    assertFalse("不应该匹配其他包的类",
        matcher.matches(String.class));
  }

  @Test
  public void testSimpleExecutionExpression() {
    // 测试简单的 execution 表达式
    String expression = "execution(* *.test*(..))";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidExpression() {
    // 测试无效表达式
    PointcutExpressionParser.parse("invalid");
  }
}
