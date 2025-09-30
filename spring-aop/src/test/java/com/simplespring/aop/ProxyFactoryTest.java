package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * ProxyFactory 类测试
 * 
 * @author SimpleSpring
 */
public class ProxyFactoryTest {

  private TestService testService;
  private TestServiceInterface testServiceInterface;
  private ProxyFactory proxyFactory;

  @Before
  public void setUp() {
    testService = new TestService();
    testServiceInterface = new TestServiceImpl();
  }

  @Test
  public void testDefaultConstructor() {
    // 测试默认构造函数
    ProxyFactory factory = new ProxyFactory();

    assertNull("默认构造函数应该设置 target 为 null", factory.getTarget());
    assertNull("默认构造函数应该设置 targetClass 为 null", factory.getTargetClass());
    assertNotNull("默认构造函数应该初始化拦截器列表", factory.getInterceptors());
    assertTrue("默认构造函数应该创建空的拦截器列表", factory.getInterceptors().isEmpty());
  }

  @Test
  public void testConstructorWithTarget() {
    // 测试带目标对象的构造函数
    ProxyFactory factory = new ProxyFactory(testServiceInterface);

    assertEquals("构造函数应该正确设置目标对象", testServiceInterface, factory.getTarget());
    assertEquals("构造函数应该正确设置目标类", TestServiceImpl.class, factory.getTargetClass());
  }

  @Test
  public void testConstructorWithTargetAndClass() {
    // 测试带目标对象和类的构造函数
    ProxyFactory factory = new ProxyFactory(testServiceInterface, TestServiceInterface.class);

    assertEquals("构造函数应该正确设置目标对象", testServiceInterface, factory.getTarget());
    assertEquals("构造函数应该正确设置目标类", TestServiceInterface.class, factory.getTargetClass());
  }

  @Test
  public void testCreateJdkProxy() throws Exception {
    // 测试创建 JDK 动态代理
    ProxyFactory factory = new ProxyFactory(testServiceInterface);

    // 添加一个简单的拦截器
    factory.addInterceptor(new TestMethodInterceptor());

    Object proxy = factory.createProxy();

    assertNotNull("代理对象不应该为 null", proxy);
    assertTrue("代理对象应该实现目标接口", proxy instanceof TestServiceInterface);
    assertFalse("代理对象不应该是原始对象", proxy == testServiceInterface);

    // 测试代理方法调用
    TestServiceInterface proxyService = (TestServiceInterface) proxy;
    String result = proxyService.doSomething("test");

    assertEquals("代理应该正确执行方法", "intercepted: TestServiceImpl: test", result);
  }

  @Test(expected = IllegalStateException.class)
  public void testCreateProxyWithNullTarget() {
    // 测试 null 目标对象
    ProxyFactory factory = new ProxyFactory();
    factory.createProxy();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCreateCglibProxyNotSupported() {
    // 测试 CGLIB 代理（暂未实现）
    ProxyFactory factory = new ProxyFactory(testService); // TestService 没有实现接口
    factory.createProxy();
  }

  @Test
  public void testForceCglibProxy() {
    // 测试强制使用 CGLIB 代理
    ProxyFactory factory = new ProxyFactory(testServiceInterface);
    factory.setForceCglib(true);

    try {
      factory.createProxy();
      fail("应该抛出 UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue("异常信息应该包含 CGLIB 相关信息",
          e.getMessage().contains("CGLIB"));
    }
  }

  @Test
  public void testAddInterceptor() {
    // 测试添加拦截器
    ProxyFactory factory = new ProxyFactory();
    TestMethodInterceptor interceptor = new TestMethodInterceptor();

    factory.addInterceptor(interceptor);

    assertEquals("应该添加一个拦截器", 1, factory.getInterceptors().size());
    assertTrue("应该包含添加的拦截器", factory.getInterceptors().contains(interceptor));
  }

  @Test
  public void testAddNullInterceptor() {
    // 测试添加 null 拦截器
    ProxyFactory factory = new ProxyFactory();

    factory.addInterceptor(null);

    assertEquals("不应该添加 null 拦截器", 0, factory.getInterceptors().size());
  }

  @Test
  public void testAddAspectDefinition() throws Exception {
    // 测试添加切面定义
    ProxyFactory factory = new ProxyFactory();

    TestAspect aspect = new TestAspect();
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", aspect);
    AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
    aspectDef.addAdvice(advice);

    factory.addAspectDefinition(aspectDef);

    assertEquals("应该添加一个切面定义", 1, factory.getAspectDefinitions().size());
    assertTrue("应该包含添加的切面定义", factory.getAspectDefinitions().contains(aspectDef));
  }

  @Test
  public void testProxyWithAspect() throws Exception {
    // 测试带切面的代理
    ProxyFactory factory = new ProxyFactory(testServiceInterface);

    // 创建切面
    TestAspect aspect = new TestAspect();
    Method beforeMethod = TestAspect.class.getMethod("beforeAdvice");
    AdviceDefinition advice = new AdviceDefinition(beforeMethod, AdviceType.BEFORE,
        "execution(* *.doSomething(..))", aspect);
    AspectDefinition aspectDef = new AspectDefinition(aspect, TestAspect.class);
    aspectDef.addAdvice(advice);

    factory.addAspectDefinition(aspectDef);

    Object proxy = factory.createProxy();
    TestServiceInterface proxyService = (TestServiceInterface) proxy;

    String result = proxyService.doSomething("test");

    assertTrue("前置通知应该被执行", aspect.beforeCalled);
    assertEquals("应该返回正确的结果", "TestServiceImpl: test", result);
  }

  @Test
  public void testSetTarget() {
    // 测试设置目标对象
    ProxyFactory factory = new ProxyFactory();

    factory.setTarget(testServiceInterface);

    assertEquals("应该正确设置目标对象", testServiceInterface, factory.getTarget());
    assertEquals("应该自动设置目标类", TestServiceImpl.class, factory.getTargetClass());
  }

  // 测试用的接口
  public interface TestServiceInterface {
    String doSomething(String input);
  }

  // 测试用的实现类
  public static class TestServiceImpl implements TestServiceInterface {
    @Override
    public String doSomething(String input) {
      return "TestServiceImpl: " + input;
    }
  }

  // 测试用的无接口类
  public static class TestService {
    public String doSomething(String input) {
      return "TestService: " + input;
    }
  }

  // 测试用的方法拦截器
  private static class TestMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(MethodInvocation invocation) throws Throwable {
      Object result = invocation.proceed();
      return "intercepted: " + result;
    }
  }

  // 测试用的切面
  public static class TestAspect {
    public boolean beforeCalled = false;

    public void beforeAdvice() {
      beforeCalled = true;
    }
  }
}
