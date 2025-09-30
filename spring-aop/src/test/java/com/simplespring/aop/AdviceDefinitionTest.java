package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * AdviceDefinition 类测试
 * 
 * @author SimpleSpring
 */
public class AdviceDefinitionTest {

  private Method testMethod;
  private Object aspectInstance;

  @Before
  public void setUp() throws Exception {
    // 准备测试数据
    testMethod = this.getClass().getDeclaredMethod("testMethod");
    aspectInstance = new Object();
  }

  @Test
  public void testDefaultConstructor() {
    // 测试默认构造函数
    AdviceDefinition advice = new AdviceDefinition();

    assertNull("默认构造函数应该设置 adviceMethod 为 null", advice.getAdviceMethod());
    assertNull("默认构造函数应该设置 type 为 null", advice.getType());
    assertNull("默认构造函数应该设置 pointcutExpression 为 null", advice.getPointcutExpression());
    assertNull("默认构造函数应该设置 aspectInstance 为 null", advice.getAspectInstance());
  }

  @Test
  public void testParameterizedConstructor() {
    // 测试带参数的构造函数
    String pointcutExpression = "execution(* com.example.*.*(..))";
    AdviceDefinition advice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        pointcutExpression, aspectInstance);

    assertEquals("构造函数应该正确设置 adviceMethod", testMethod, advice.getAdviceMethod());
    assertEquals("构造函数应该正确设置 type", AdviceType.BEFORE, advice.getType());
    assertEquals("构造函数应该正确设置 pointcutExpression", pointcutExpression, advice.getPointcutExpression());
    assertEquals("构造函数应该正确设置 aspectInstance", aspectInstance, advice.getAspectInstance());
  }

  @Test
  public void testSettersAndGetters() {
    // 测试 setter 和 getter 方法
    AdviceDefinition advice = new AdviceDefinition();

    // 测试 adviceMethod
    advice.setAdviceMethod(testMethod);
    assertEquals(testMethod, advice.getAdviceMethod());

    // 测试 type
    advice.setType(AdviceType.AFTER);
    assertEquals(AdviceType.AFTER, advice.getType());

    // 测试 pointcutExpression
    String expression = "execution(* *.save(..))";
    advice.setPointcutExpression(expression);
    assertEquals(expression, advice.getPointcutExpression());

    // 测试 returningParameter
    String returning = "result";
    advice.setReturningParameter(returning);
    assertEquals(returning, advice.getReturningParameter());

    // 测试 throwingParameter
    String throwing = "exception";
    advice.setThrowingParameter(throwing);
    assertEquals(throwing, advice.getThrowingParameter());

    // 测试 aspectInstance
    advice.setAspectInstance(aspectInstance);
    assertEquals(aspectInstance, advice.getAspectInstance());
  }

  @Test
  public void testToString() {
    // 测试 toString 方法
    String pointcutExpression = "execution(* com.example.*.*(..))";
    AdviceDefinition advice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        pointcutExpression, aspectInstance);

    String result = advice.toString();

    assertNotNull("toString 不应该返回 null", result);
    assertTrue("toString 应该包含通知类型", result.contains("BEFORE"));
    assertTrue("toString 应该包含切点表达式", result.contains(pointcutExpression));
    assertTrue("toString 应该包含方法名", result.contains("testMethod"));
  }

  @Test
  public void testToStringWithNullMethod() {
    // 测试当 adviceMethod 为 null 时的 toString 方法
    AdviceDefinition advice = new AdviceDefinition();
    advice.setType(AdviceType.AFTER);
    advice.setPointcutExpression("execution(* *.*(..))");

    String result = advice.toString();

    assertNotNull("toString 不应该返回 null", result);
    assertTrue("toString 应该包含 'null' 当方法为空时", result.contains("null"));
  }

  /**
   * 测试用的方法
   */
  private void testMethod() {
    // 用于测试的空方法
  }
}
