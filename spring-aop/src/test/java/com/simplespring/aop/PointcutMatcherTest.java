package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * PointcutMatcher 接口测试
 * 
 * @author SimpleSpring
 */
public class PointcutMatcherTest {

  @Test
  public void testWithinExpression() throws Exception {
    // 测试 within 表达式
    String expression = "within(com.simplespring.aop.*)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertEquals("应该返回原始表达式", expression, matcher.getExpression());

    assertTrue("应该匹配 com.simplespring.aop.UserService",
        matcher.matches(UserService.class));
    assertFalse("不应该匹配 java.lang.String",
        matcher.matches(String.class));

    // 对于 within 表达式，方法匹配应该和类匹配结果一致
    Method method = UserService.class.getMethod("save", Object.class);
    assertTrue("within 表达式的方法匹配应该和类匹配一致",
        matcher.matches(method, UserService.class));
  }

  @Test
  public void testExecutionExpressionWithMethod() throws Exception {
    // 测试带方法名的 execution 表达式
    String expression = "execution(* *.save(..))";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    Method saveMethod = UserService.class.getMethod("save", Object.class);
    Method findMethod = UserService.class.getMethod("find", String.class);

    assertTrue("应该匹配 save 方法",
        matcher.matches(saveMethod, UserService.class));
    assertFalse("不应该匹配 find 方法",
        matcher.matches(findMethod, UserService.class));
  }

  @Test
  public void testAnnotationExpression() throws Exception {
    // 测试 @annotation 表达式
    String expression = "@annotation(com.simplespring.aop.PointcutMatcherTest$TestAnnotation)";
    PointcutMatcher matcher = PointcutExpressionParser.parse(expression);

    assertEquals("应该返回原始表达式", expression, matcher.getExpression());

    Method annotatedMethod = TestClass.class.getMethod("annotatedMethod");
    Method normalMethod = TestClass.class.getMethod("normalMethod");

    assertTrue("应该匹配带注解的方法",
        matcher.matches(annotatedMethod, TestClass.class));
    assertFalse("不应该匹配普通方法",
        matcher.matches(normalMethod, TestClass.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidExpression() {
    // 测试无效的表达式
    PointcutExpressionParser.parse("invalid expression");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyExpression() {
    // 测试空表达式
    PointcutExpressionParser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullExpression() {
    // 测试 null 表达式
    PointcutExpressionParser.parse(null);
  }

  @Test
  public void testInvalidExecutionExpression() {
    // 测试无效的 execution 表达式
    try {
      PointcutExpressionParser.parse("execution(invalid)");
      fail("应该抛出异常");
    } catch (IllegalArgumentException e) {
      assertTrue("异常信息应该包含格式错误提示",
          e.getMessage().contains("格式不正确") || e.getMessage().contains("缺少"));
    }
  }

  // 测试用的注解
  @Target({ ElementType.METHOD, ElementType.TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  public @interface TestAnnotation {
  }

  // 测试用的类
  public static class TestClass {
    @TestAnnotation
    public void annotatedMethod() {
    }

    public void normalMethod() {
    }
  }

  // 测试用的服务类
  public static class UserService {
    public void save(Object obj) {
    }

    public Object find(String id) {
      return null;
    }
  }

  public static class OrderService {
    public void save(Object obj) {
    }
  }

  public static class UserDaoService {
    public void save(Object obj) {
    }
  }
}
