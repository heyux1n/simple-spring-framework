package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * PointcutExpressionParser 类测试
 * 
 * @author SimpleSpring
 */
public class PointcutExpressionParserTest {

  @Test
  public void testParseWithinExpression() {
    // 测试解析 within 表达式
    String expression = "within(com.example.service.*)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());
  }

  @Test
  public void testParseAnnotationExpression() {
    // 测试解析 @annotation 表达式
    String expression = "@annotation(com.example.MyAnnotation)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());
  }

  @Test
  public void testParseExpressionWithWhitespace() {
    // 测试带空白字符的表达式
    String expression = "  within(com.example.service.*)  ";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseNullExpression() {
    // 测试 null 表达式
    PointcutExpressionParser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEmptyExpression() {
    // 测试空表达式
    PointcutExpressionParser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWhitespaceOnlyExpression() {
    // 测试只有空白字符的表达式
    PointcutExpressionParser.parse("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseUnsupportedExpression() {
    // 测试不支持的表达式格式
    PointcutExpressionParser.parse("unsupported(expression)");
  }

  @Test
  public void testSimpleExecutionExpression() {
    // 测试简单的 execution 表达式
    String expression = "execution(* *.save(..))";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertNotNull("解析结果不应该为 null", matcher);
    assertEquals("应该返回原始表达式", expression, matcher.getExpression());
  }

  @Test
  public void testAnnotationMatchingWithNonExistentAnnotation() {
    // 测试不存在的注解
    String expression = "@annotation(com.nonexistent.Annotation)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    // 不存在的注解应该不匹配任何方法
    assertFalse("不存在的注解不应该匹配任何类",
        matcher.matches(TestUserService.class));
  }

  @Test
  public void testWildcardMatching() throws Exception {
    // 测试通配符匹配
    String expression = "execution(* *.save*(..))";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    // 创建测试类来验证匹配
    TestUserService testService = new TestUserService();
    Method saveMethod = TestUserService.class.getMethod("saveUser");
    Method saveDataMethod = TestUserService.class.getMethod("saveData");
    Method findMethod = TestUserService.class.getMethod("findUser");

    assertTrue("应该匹配 saveUser 方法",
        matcher.matches(saveMethod, TestUserService.class));
    assertTrue("应该匹配 saveData 方法",
        matcher.matches(saveDataMethod, TestUserService.class));
    assertFalse("不应该匹配 findUser 方法",
        matcher.matches(findMethod, TestUserService.class));
  }

  // 测试用的服务类
  public static class TestUserService {
    public void saveUser() {
    }

    public void saveData() {
    }

    public void findUser() {
    }
  }
}
