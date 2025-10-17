package com.simplespring.aop;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.context.AspectProcessor;
import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.AfterReturning;
import com.simplespring.core.annotation.Component;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * AOP 模块集成测试，验证 AOP 与其他模块的协作
 * 
 * @author SimpleSpring Framework
 */
public class AopIntegrationTest {

  private AnnotationConfigApplicationContext applicationContext;
  private AspectProcessor aspectProcessor;

  @Before
  public void setUp() {
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.scan("com.simplespring.aop.AopIntegrationTest");
    applicationContext.refresh();

    aspectProcessor = applicationContext.getBean(AspectProcessor.class);
  }

  @Test
  public void testAspectRegistration() {
    // 测试切面注册

    TestAspect aspect = applicationContext.getBean(TestAspect.class);
    assertNotNull("Aspect should be registered", aspect);

    // 验证切面被 AspectProcessor 识别
    aspectProcessor.registerAspect(aspect);

    // 验证切面定义被正确创建
    assertTrue("Aspect should be registered with processor",
        aspectProcessor.getAspectDefinitions().size() > 0);
  }

  @Test
  public void testProxyCreation() {
    // 测试代理创建

    TestService originalService = new TestService();

    // 创建代理
    Object proxy = aspectProcessor.createProxy(originalService, TestService.class);

    assertNotNull("Proxy should be created", proxy);
    assertTrue("Proxy should implement target interface or extend target class",
        proxy instanceof TestService);
    assertNotSame("Proxy should be different from original", originalService, proxy);
  }

  @Test
  public void testAdviceExecution() throws Exception {
    // 测试通知执行

    TestAspect aspect = new TestAspect();
    TestService service = new TestService();

    // 创建切面定义
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
    AdviceDefinition beforeAdvice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.doSomething(..))", aspect);

    Method afterMethod = TestAspect.class.getMethod("afterAdvice");
    AdviceDefinition afterAdvice = new AdviceDefinition(afterMethod, AdviceType.AFTER,
        "execution(* *.doSomething(..))", aspect);

    AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
    aspectDef.addAdvice(beforeAdvice);
    aspectDef.addAdvice(afterAdvice);

    // 创建代理工厂并添加切面
    ProxyFactory proxyFactory = new ProxyFactory(service);
    proxyFactory.addAspectDefinition(aspectDef);

    TestService proxy = (TestService) proxyFactory.createProxy();

    // 执行方法，验证通知被调用
    String result = proxy.doSomething("test");

    assertEquals("Method should return correct result", "TestService: test", result);
    assertTrue("Before advice should be called", aspect.beforeCalled);
    assertTrue("After advice should be called", aspect.afterCalled);
  }

  @Test
  public void testPointcutMatching() {
    // 测试切点匹配

    PointcutExpressionParser parser = new PointcutExpressionParser();
    PointcutMatcher matcher = parser.parse("execution(* *.doSomething(..))");

    assertNotNull("Matcher should be created", matcher);

    try {
      Method method = TestService.class.getMethod("doSomething", String.class);
      assertTrue("Method should match pointcut", matcher.matches(method));

      Method otherMethod = TestService.class.getMethod("otherMethod");
      assertFalse("Other method should not match pointcut", matcher.matches(otherMethod));
    } catch (NoSuchMethodException e) {
      fail("Test methods should exist");
    }
  }

  @Test
  public void testMultipleAspects() throws Exception {
    // 测试多个切面

    TestAspect aspect1 = new TestAspect();
    AnotherTestAspect aspect2 = new AnotherTestAspect();
    TestService service = new TestService();

    // 创建第一个切面定义
    Method beforeMethod1 = TestAspect.class.getMethod("beforeAdvice");
    AdviceDefinition advice1 = new AdviceDefinition(beforeMethod1, AdviceType.BEFORE,
        "execution(* *.doSomething(..))", aspect1);
    AspectDefinition aspectDef1 = new AspectDefinition(aspect1, TestAspect.class);
    aspectDef1.addAdvice(advice1);

    // 创建第二个切面定义
    Method beforeMethod2 = AnotherTestAspect.class.getMethod("anotherBeforeAdvice");
    AdviceDefinition advice2 = new AdviceDefinition(beforeMethod2, AdviceType.BEFORE,
        "execution(* *.doSomething(..))", aspect2);
    AspectDefinition aspectDef2 = new AspectDefinition(aspect2, AnotherTestAspect.class);
    aspectDef2.addAdvice(advice2);

    // 创建代理工厂并添加两个切面
    ProxyFactory proxyFactory = new ProxyFactory(service);
    proxyFactory.addAspectDefinition(aspectDef1);
    proxyFactory.addAspectDefinition(aspectDef2);

    TestService proxy = (TestService) proxyFactory.createProxy();

    // 执行方法，验证两个切面的通知都被调用
    proxy.doSomething("test");

    assertTrue("First aspect should be called", aspect1.beforeCalled);
    assertTrue("Second aspect should be called", aspect2.anotherBeforeCalled);
  }

  @Test
  public void testAfterReturningAdvice() throws Exception {
    // 测试返回后通知

    TestAspect aspect = new TestAspect();
    TestService service = new TestService();

    Method afterReturningMethod = TestAspect.class.getMethod("afterReturningAdvice", Object.class);
    AdviceDefinition advice = new AdviceDefinition(afterReturningMethod, AdviceType.AFTER_RETURNING,
        "execution(* *.doSomething(..))", aspect);
    advice.setReturningParameter("result");

    AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
    aspectDef.addAdvice(advice);

    ProxyFactory proxyFactory = new ProxyFactory(service);
    proxyFactory.addAspectDefinition(aspectDef);

    TestService proxy = (TestService) proxyFactory.createProxy();

    String result = proxy.doSomething("test");

    assertEquals("Method should return correct result", "TestService: test", result);
    assertTrue("AfterReturning advice should be called", aspect.afterReturningCalled);
    assertEquals("AfterReturning advice should receive return value",
        "TestService: test", aspect.returnValue);
  }

  @Test
  public void testJoinPointInformation() throws Exception {
    // 测试连接点信息

    TestService service = new TestService();
    Method method = TestService.class.getMethod("doSomething", String.class);
    Object[] args = { "test" };

    MethodInvocation invocation = new MethodInvocation(service, method, args);

    assertEquals("Target should be correct", service, invocation.getTarget());
    assertEquals("Method should be correct", method, invocation.getMethod());
    assertArrayEquals("Arguments should be correct", args, invocation.getArgs());
    assertTrue("Signature should contain method name",
        invocation.getSignature().contains("doSomething"));
  }

  @Test
  public void testAdviceExecutor() throws Exception {
    // 测试通知执行器

    TestAspect aspect = new TestAspect();
    Method adviceMethod = TestAspect.class.getMethod("beforeAdvice");

    AdviceExecutor executor = new AdviceExecutor();

    TestService service = new TestService();
    Method targetMethod = TestService.class.getMethod("doSomething", String.class);
    MethodInvocation invocation = new MethodInvocation(service, targetMethod, new Object[] { "test" });

    // 执行前置通知
    executor.executeBefore(adviceMethod, aspect, invocation);

    assertTrue("Before advice should be executed", aspect.beforeCalled);
  }

  @Test
  public void testProxyWithInterface() {
    // 测试接口代理

    TestServiceInterface service = new TestServiceImpl();

    ProxyFactory proxyFactory = new ProxyFactory(service);
    Object proxy = proxyFactory.createProxy();

    assertNotNull("Proxy should be created", proxy);
    assertTrue("Proxy should implement interface", proxy instanceof TestServiceInterface);

    TestServiceInterface proxyService = (TestServiceInterface) proxy;
    String result = proxyService.doSomething("test");
    assertEquals("Proxy should work correctly", "TestServiceImpl: test", result);
  }

  // 测试用的服务类
  public static class TestService {
    public String doSomething(String input) {
      return "TestService: " + input;
    }

    public String otherMethod() {
      return "other";
    }
  }

  // 测试用的接口
  public interface TestServiceInterface {
    String doSomething(String input);
  }

  // 测试用的接口实现
  public static class TestServiceImpl implements TestServiceInterface {
    @Override
    public String doSomething(String input) {
      return "TestServiceImpl: " + input;
    }
  }

  // 测试用的切面
  @Aspect
  @Component
  public static class TestAspect {
    public boolean beforeCalled = false;
    public boolean afterCalled = false;
    public boolean afterReturningCalled = false;
    public Object returnValue;

    @Before("execution(* *.doSomething(..))")
    public void beforeAdvice() {
      beforeCalled = true;
    }

    @After("execution(* *.doSomething(..))")
    public void afterAdvice() {
      afterCalled = true;
    }

    @AfterReturning(value = "execution(* *.doSomething(..))", returning = "result")
    public void afterReturningAdvice(Object result) {
      afterReturningCalled = true;
      returnValue = result;
    }
  }

  // 另一个测试用的切面
  @Aspect
  @Component
  public static class AnotherTestAspect {
    public boolean anotherBeforeCalled = false;

    @Before("execution(* *.doSomething(..))")
    public void anotherBeforeAdvice() {
      anotherBeforeCalled = true;
    }
  }
}
