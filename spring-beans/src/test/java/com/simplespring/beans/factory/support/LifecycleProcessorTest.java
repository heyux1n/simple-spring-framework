package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanCreationException;
import com.simplespring.core.annotation.PostConstruct;
import com.simplespring.core.annotation.PreDestroy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * LifecycleProcessor 单元测试
 * 
 * @author SimpleSpring Framework
 */
public class LifecycleProcessorTest {

  private LifecycleProcessor lifecycleProcessor;

  @Before
  public void setUp() {
    lifecycleProcessor = new LifecycleProcessor();
  }

  @Test
  public void testPostProcessBeforeInitialization() {
    TestBean testBean = new TestBean();
    Object result = lifecycleProcessor.postProcessBeforeInitialization(testBean, "testBean");

    assertSame("postProcessBeforeInitialization 应该返回原始对象", testBean, result);
    assertFalse("@PostConstruct 方法不应该在 beforeInitialization 时调用", testBean.isPostConstructCalled());
  }

  @Test
  public void testPostProcessAfterInitialization() {
    TestBean testBean = new TestBean();
    Object result = lifecycleProcessor.postProcessAfterInitialization(testBean, "testBean");

    assertSame("postProcessAfterInitialization 应该返回原始对象", testBean, result);
    assertTrue("@PostConstruct 方法应该在 afterInitialization 时调用", testBean.isPostConstructCalled());
  }

  @Test
  public void testInvokePostConstructMethods() {
    TestBean testBean = new TestBean();
    assertFalse("初始状态下 @PostConstruct 方法未调用", testBean.isPostConstructCalled());

    lifecycleProcessor.invokePostConstructMethods(testBean, "testBean");

    assertTrue("@PostConstruct 方法应该被调用", testBean.isPostConstructCalled());
  }

  @Test
  public void testInvokePreDestroyMethods() {
    TestBean testBean = new TestBean();
    assertFalse("初始状态下 @PreDestroy 方法未调用", testBean.isPreDestroyCalled());

    lifecycleProcessor.invokePreDestroyMethods(testBean, "testBean");

    assertTrue("@PreDestroy 方法应该被调用", testBean.isPreDestroyCalled());
  }

  @Test
  public void testMultiplePostConstructMethods() {
    MultipleLifecycleBean bean = new MultipleLifecycleBean();

    lifecycleProcessor.invokePostConstructMethods(bean, "multipleBean");

    assertTrue("第一个 @PostConstruct 方法应该被调用", bean.isInit1Called());
    assertTrue("第二个 @PostConstruct 方法应该被调用", bean.isInit2Called());
  }

  @Test
  public void testMultiplePreDestroyMethods() {
    MultipleLifecycleBean bean = new MultipleLifecycleBean();

    lifecycleProcessor.invokePreDestroyMethods(bean, "multipleBean");

    assertTrue("第一个 @PreDestroy 方法应该被调用", bean.isDestroy1Called());
    assertTrue("第二个 @PreDestroy 方法应该被调用", bean.isDestroy2Called());
  }

  @Test
  public void testInheritedLifecycleMethods() {
    ChildBean childBean = new ChildBean();

    lifecycleProcessor.invokePostConstructMethods(childBean, "childBean");
    lifecycleProcessor.invokePreDestroyMethods(childBean, "childBean");

    assertTrue("父类的 @PostConstruct 方法应该被调用", childBean.isParentPostConstructCalled());
    assertTrue("子类的 @PostConstruct 方法应该被调用", childBean.isChildPostConstructCalled());
    assertTrue("父类的 @PreDestroy 方法应该被调用", childBean.isParentPreDestroyCalled());
    assertTrue("子类的 @PreDestroy 方法应该被调用", childBean.isChildPreDestroyCalled());
  }

  @Test
  public void testPrivateLifecycleMethods() {
    PrivateMethodBean bean = new PrivateMethodBean();

    lifecycleProcessor.invokePostConstructMethods(bean, "privateBean");
    lifecycleProcessor.invokePreDestroyMethods(bean, "privateBean");

    assertTrue("私有的 @PostConstruct 方法应该被调用", bean.isPostConstructCalled());
    assertTrue("私有的 @PreDestroy 方法应该被调用", bean.isPreDestroyCalled());
  }

  @Test(expected = BeanCreationException.class)
  public void testPostConstructMethodThrowsException() {
    ExceptionBean bean = new ExceptionBean();
    lifecycleProcessor.invokePostConstructMethods(bean, "exceptionBean");
  }

  @Test
  public void testPreDestroyMethodThrowsException() {
    // @PreDestroy 方法抛出异常不应该导致测试失败，只是记录错误
    ExceptionBean bean = new ExceptionBean();

    // 这个调用不应该抛出异常
    lifecycleProcessor.invokePreDestroyMethods(bean, "exceptionBean");
  }

  @Test(expected = IllegalStateException.class)
  public void testPostConstructMethodWithParameters() {
    InvalidPostConstructBean bean = new InvalidPostConstructBean();
    lifecycleProcessor.invokePostConstructMethods(bean, "invalidBean");
  }

  @Test(expected = IllegalStateException.class)
  public void testStaticPostConstructMethod() {
    StaticMethodBean bean = new StaticMethodBean();
    lifecycleProcessor.invokePostConstructMethods(bean, "staticBean");
  }

  @Test
  public void testHasPostConstructMethods() {
    assertTrue("TestBean 应该有 @PostConstruct 方法",
        lifecycleProcessor.hasPostConstructMethods(TestBean.class));
    assertFalse("Object 不应该有 @PostConstruct 方法",
        lifecycleProcessor.hasPostConstructMethods(Object.class));
  }

  @Test
  public void testHasPreDestroyMethods() {
    assertTrue("TestBean 应该有 @PreDestroy 方法",
        lifecycleProcessor.hasPreDestroyMethods(TestBean.class));
    assertFalse("Object 不应该有 @PreDestroy 方法",
        lifecycleProcessor.hasPreDestroyMethods(Object.class));
  }

  @Test
  public void testClearCache() {
    // 先调用方法以填充缓存
    lifecycleProcessor.hasPostConstructMethods(TestBean.class);
    lifecycleProcessor.hasPreDestroyMethods(TestBean.class);

    // 清除缓存
    lifecycleProcessor.clearCache();

    // 缓存应该被清空，但功能仍然正常
    assertTrue("清除缓存后功能应该仍然正常",
        lifecycleProcessor.hasPostConstructMethods(TestBean.class));
  }

  // 测试用的 Bean 类

  public static class TestBean {
    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    public void init() {
      postConstructCalled = true;
    }

    @PreDestroy
    public void destroy() {
      preDestroyCalled = true;
    }

    public boolean isPostConstructCalled() {
      return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
      return preDestroyCalled;
    }
  }

  public static class MultipleLifecycleBean {
    private boolean init1Called = false;
    private boolean init2Called = false;
    private boolean destroy1Called = false;
    private boolean destroy2Called = false;

    @PostConstruct
    public void init1() {
      init1Called = true;
    }

    @PostConstruct
    public void init2() {
      init2Called = true;
    }

    @PreDestroy
    public void destroy1() {
      destroy1Called = true;
    }

    @PreDestroy
    public void destroy2() {
      destroy2Called = true;
    }

    public boolean isInit1Called() {
      return init1Called;
    }

    public boolean isInit2Called() {
      return init2Called;
    }

    public boolean isDestroy1Called() {
      return destroy1Called;
    }

    public boolean isDestroy2Called() {
      return destroy2Called;
    }
  }

  public static class ParentBean {
    private boolean parentPostConstructCalled = false;
    private boolean parentPreDestroyCalled = false;

    @PostConstruct
    protected void parentInit() {
      parentPostConstructCalled = true;
    }

    @PreDestroy
    protected void parentDestroy() {
      parentPreDestroyCalled = true;
    }

    public boolean isParentPostConstructCalled() {
      return parentPostConstructCalled;
    }

    public boolean isParentPreDestroyCalled() {
      return parentPreDestroyCalled;
    }
  }

  public static class ChildBean extends ParentBean {
    private boolean childPostConstructCalled = false;
    private boolean childPreDestroyCalled = false;

    @PostConstruct
    public void childInit() {
      childPostConstructCalled = true;
    }

    @PreDestroy
    public void childDestroy() {
      childPreDestroyCalled = true;
    }

    public boolean isChildPostConstructCalled() {
      return childPostConstructCalled;
    }

    public boolean isChildPreDestroyCalled() {
      return childPreDestroyCalled;
    }
  }

  public static class PrivateMethodBean {
    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    private void init() {
      postConstructCalled = true;
    }

    @PreDestroy
    private void destroy() {
      preDestroyCalled = true;
    }

    public boolean isPostConstructCalled() {
      return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
      return preDestroyCalled;
    }
  }

  public static class ExceptionBean {
    @PostConstruct
    public void init() {
      throw new RuntimeException("PostConstruct 方法异常");
    }

    @PreDestroy
    public void destroy() {
      throw new RuntimeException("PreDestroy 方法异常");
    }
  }

  public static class InvalidPostConstructBean {
    @PostConstruct
    public void init(String param) {
      // 无效：@PostConstruct 方法不能有参数
    }
  }

  public static class StaticMethodBean {
    @PostConstruct
    public static void init() {
      // 无效：@PostConstruct 方法不能是静态方法
    }
  }
}
