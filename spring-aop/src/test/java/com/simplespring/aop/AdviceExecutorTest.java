package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * AdviceExecutor 类测试
 * 
 * @author SimpleSpring
 */
public class AdviceExecutorTest {

  private TestAspect testAspect;
  private TestService testService;
  private MethodInvocation methodInvocation;
  private Method testMethod;

  @Before
  public void setUp() throws Exception {
    testAspect = new TestAspect();
    testService = new TestService();
    testMethod = TestService.class.getMethod("testMethod", String.class);
    methodInvocation = new MethodInvocation(testService, testMethod, new Object[] { "test" });
  }

  @Test
  public void testExecuteBefore() throws Throwable {
    // 测试前置通知执行
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", testAspect);

    AdviceExecutor.executeBefore(advice, methodInvocation);

    assertTrue("前置通知应该被执行", testAspect.beforeCalled);
    assertEquals("应该传递正确的连接点", methodInvocation, testAspect.lastJoinPoint);
  }

  @Test
  public void testExecuteAfter() throws Throwable {
    // 测试后置通知执行
    Method afterMethod = TestAspect.class.getMethod("afterAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(afterMethod, AdviceType.AFTER,
        "execution(* *.*(..))", testAspect);

    AdviceExecutor.executeAfter(advice, methodInvocation);

    assertTrue("后置通知应该被执行", testAspect.afterCalled);
    assertEquals("应该传递正确的连接点", methodInvocation, testAspect.lastJoinPoint);
  }

  @Test
  public void testExecuteAfterReturning() throws Throwable {
    // 测试返回后通知执行
    Method afterReturningMethod = TestAspect.class.getMethod("afterReturningAdvice", JoinPoint.class, Object.class);
    AdviceDefinition advice = new AdviceDefinition(afterReturningMethod, AdviceType.AFTER_RETURNING,
        "execution(* *.*(..))", testAspect);

    Object returnValue = "test result";
    AdviceExecutor.executeAfterReturning(advice, methodInvocation, returnValue);

    assertTrue("返回后通知应该被执行", testAspect.afterReturningCalled);
    assertEquals("应该传递正确的连接点", methodInvocation, testAspect.lastJoinPoint);
    assertEquals("应该传递正确的返回值", returnValue, testAspect.lastReturnValue);
  }

  @Test
  public void testExecuteAfterThrowing() throws Throwable {
    // 测试异常通知执行
    Method afterThrowingMethod = TestAspect.class.getMethod("afterThrowingAdvice", JoinPoint.class, Throwable.class);
    AdviceDefinition advice = new AdviceDefinition(afterThrowingMethod, AdviceType.AFTER_THROWING,
        "execution(* *.*(..))", testAspect);

    Exception exception = new RuntimeException("test exception");
    AdviceExecutor.executeAfterThrowing(advice, methodInvocation, exception);

    assertTrue("异常通知应该被执行", testAspect.afterThrowingCalled);
    assertEquals("应该传递正确的连接点", methodInvocation, testAspect.lastJoinPoint);
    assertEquals("应该传递正确的异常", exception, testAspect.lastException);
  }

  @Test
  public void testExecuteAround() throws Throwable {
    // 测试环绕通知执行
    Method aroundMethod = TestAspect.class.getMethod("aroundAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(aroundMethod, AdviceType.AROUND,
        "execution(* *.*(..))", testAspect);

    Object result = AdviceExecutor.executeAround(advice, methodInvocation);

    assertTrue("环绕通知应该被执行", testAspect.aroundCalled);
    assertEquals("应该传递正确的连接点", methodInvocation, testAspect.lastJoinPoint);
    assertEquals("应该返回通知方法的结果", "around result", result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecuteBeforeWithWrongType() throws Throwable {
    // 测试错误的通知类型
    Method afterMethod = TestAspect.class.getMethod("afterAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(afterMethod, AdviceType.AFTER,
        "execution(* *.*(..))", testAspect);

    AdviceExecutor.executeBefore(advice, methodInvocation);
  }

  @Test
  public void testMatches() throws Exception {
    // 测试切点匹配
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.test*(..))", testAspect);

    assertTrue("应该匹配 testMethod", AdviceExecutor.matches(advice, methodInvocation));

    // 测试不匹配的情况
    AdviceDefinition noMatchAdvice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.save*(..))", testAspect);

    assertFalse("不应该匹配 testMethod", AdviceExecutor.matches(noMatchAdvice, methodInvocation));
  }

  @Test
  public void testMatchesWithInvalidExpression() throws Exception {
    // 测试无效的切点表达式
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice", JoinPoint.class);
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "invalid expression", testAspect);

    assertFalse("无效表达式应该返回 false", AdviceExecutor.matches(advice, methodInvocation));
  }

  @Test
  public void testAdviceWithNoParameters() throws Throwable {
    // 测试无参数的通知方法
    Method noParamMethod = TestAspect.class.getMethod("noParamAdvice");
    AdviceDefinition advice = new AdviceDefinition(noParamMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", testAspect);

    AdviceExecutor.executeBefore(advice, methodInvocation);

    assertTrue("无参数通知应该被执行", testAspect.noParamCalled);
  }

  // 测试用的切面类
  public static class TestAspect {
    public boolean beforeCalled = false;
    public boolean afterCalled = false;
    public boolean afterReturningCalled = false;
    public boolean afterThrowingCalled = false;
    public boolean aroundCalled = false;
    public boolean noParamCalled = false;

    public JoinPoint lastJoinPoint;
    public Object lastReturnValue;
    public Throwable lastException;

    public void beforeAdvice(JoinPoint joinPoint) {
      beforeCalled = true;
      lastJoinPoint = joinPoint;
    }

    public void afterAdvice(JoinPoint joinPoint) {
      afterCalled = true;
      lastJoinPoint = joinPoint;
    }

    public void afterReturningAdvice(JoinPoint joinPoint, Object returnValue) {
      afterReturningCalled = true;
      lastJoinPoint = joinPoint;
      lastReturnValue = returnValue;
    }

    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
      afterThrowingCalled = true;
      lastJoinPoint = joinPoint;
      lastException = exception;
    }

    public Object aroundAdvice(JoinPoint joinPoint) {
      aroundCalled = true;
      lastJoinPoint = joinPoint;
      return "around result";
    }

    public void noParamAdvice() {
      noParamCalled = true;
    }
  }

  // 测试用的服务类
  public static class TestService {
    public String testMethod(String input) {
      return "result: " + input;
    }
  }
}
