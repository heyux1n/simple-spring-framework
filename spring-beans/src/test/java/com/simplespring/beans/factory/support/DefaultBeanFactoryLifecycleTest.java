package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.BeanPostProcessor;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.PostConstruct;
import com.simplespring.core.annotation.PreDestroy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DefaultBeanFactory 生命周期功能集成测试
 * 
 * @author SimpleSpring Framework
 */
public class DefaultBeanFactoryLifecycleTest {

  private DefaultBeanFactory beanFactory;

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
  }

  @Test
  public void testBeanLifecycleWithPostConstruct() {
    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(LifecycleTestBean.class);
    beanDefinition.setBeanName("lifecycleBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

    // 获取 Bean 实例
    LifecycleTestBean bean = beanFactory.getBean("lifecycleBean", LifecycleTestBean.class);

    assertNotNull("Bean 实例不应该为空", bean);
    assertTrue("@PostConstruct 方法应该被调用", bean.isPostConstructCalled());
    assertFalse("@PreDestroy 方法不应该被调用", bean.isPreDestroyCalled());
  }

  @Test
  public void testBeanDestroyWithPreDestroy() {
    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(LifecycleTestBean.class);
    beanDefinition.setBeanName("lifecycleBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

    // 获取 Bean 实例
    LifecycleTestBean bean = beanFactory.getBean("lifecycleBean", LifecycleTestBean.class);
    assertTrue("@PostConstruct 方法应该被调用", bean.isPostConstructCalled());

    // 销毁 Bean
    beanFactory.destroyBean("lifecycleBean");
    assertTrue("@PreDestroy 方法应该被调用", bean.isPreDestroyCalled());
  }

  @Test
  public void testPrototypeBeanLifecycle() {
    // 注册原型 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(LifecycleTestBean.class);
    beanDefinition.setBeanName("prototypeBean");
    beanDefinition.setScope(Scope.PROTOTYPE);

    beanFactory.registerBeanDefinition("prototypeBean", beanDefinition);

    // 获取两个不同的实例
    LifecycleTestBean bean1 = beanFactory.getBean("prototypeBean", LifecycleTestBean.class);
    LifecycleTestBean bean2 = beanFactory.getBean("prototypeBean", LifecycleTestBean.class);

    assertNotSame("原型 Bean 应该创建不同的实例", bean1, bean2);
    assertTrue("第一个实例的 @PostConstruct 方法应该被调用", bean1.isPostConstructCalled());
    assertTrue("第二个实例的 @PostConstruct 方法应该被调用", bean2.isPostConstructCalled());
  }

  @Test
  public void testCustomBeanPostProcessor() {
    // 添加自定义 BeanPostProcessor
    CustomBeanPostProcessor customProcessor = new CustomBeanPostProcessor();
    beanFactory.addBeanPostProcessor(customProcessor);

    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(LifecycleTestBean.class);
    beanDefinition.setBeanName("testBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("testBean", beanDefinition);

    // 获取 Bean 实例
    LifecycleTestBean bean = beanFactory.getBean("testBean", LifecycleTestBean.class);

    assertTrue("自定义 BeanPostProcessor 的 before 方法应该被调用",
        customProcessor.isBeforeInitializationCalled());
    assertTrue("自定义 BeanPostProcessor 的 after 方法应该被调用",
        customProcessor.isAfterInitializationCalled());
    assertTrue("@PostConstruct 方法应该被调用", bean.isPostConstructCalled());
  }

  @Test
  public void testBeanPostProcessorOrder() {
    // 添加多个 BeanPostProcessor
    OrderTestProcessor processor1 = new OrderTestProcessor("processor1");
    OrderTestProcessor processor2 = new OrderTestProcessor("processor2");

    beanFactory.addBeanPostProcessor(processor1);
    beanFactory.addBeanPostProcessor(processor2);

    // 注册 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(LifecycleTestBean.class);
    beanDefinition.setBeanName("orderTestBean");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("orderTestBean", beanDefinition);

    // 获取 Bean 实例
    LifecycleTestBean bean = beanFactory.getBean("orderTestBean", LifecycleTestBean.class);

    // 验证处理器调用顺序
    assertTrue("第一个处理器应该被调用", processor1.isCalled());
    assertTrue("第二个处理器应该被调用", processor2.isCalled());
    assertTrue("@PostConstruct 方法应该被调用", bean.isPostConstructCalled());
  }

  @Test
  public void testDestroyAllSingletons() {
    // 注册多个单例 Bean
    for (int i = 1; i <= 3; i++) {
      BeanDefinition beanDefinition = new BeanDefinition();
      beanDefinition.setBeanClass(LifecycleTestBean.class);
      beanDefinition.setBeanName("bean" + i);
      beanDefinition.setScope(Scope.SINGLETON);
      beanFactory.registerBeanDefinition("bean" + i, beanDefinition);
    }

    // 获取所有 Bean 实例
    LifecycleTestBean bean1 = beanFactory.getBean("bean1", LifecycleTestBean.class);
    LifecycleTestBean bean2 = beanFactory.getBean("bean2", LifecycleTestBean.class);
    LifecycleTestBean bean3 = beanFactory.getBean("bean3", LifecycleTestBean.class);

    // 验证 @PostConstruct 被调用
    assertTrue("Bean1 的 @PostConstruct 应该被调用", bean1.isPostConstructCalled());
    assertTrue("Bean2 的 @PostConstruct 应该被调用", bean2.isPostConstructCalled());
    assertTrue("Bean3 的 @PostConstruct 应该被调用", bean3.isPostConstructCalled());

    // 销毁所有单例
    beanFactory.destroySingletons();

    // 验证 @PreDestroy 被调用
    assertTrue("Bean1 的 @PreDestroy 应该被调用", bean1.isPreDestroyCalled());
    assertTrue("Bean2 的 @PreDestroy 应该被调用", bean2.isPreDestroyCalled());
    assertTrue("Bean3 的 @PreDestroy 应该被调用", bean3.isPreDestroyCalled());
  }

  @Test
  public void testBeanPostProcessorManagement() {
    CustomBeanPostProcessor processor1 = new CustomBeanPostProcessor();
    CustomBeanPostProcessor processor2 = new CustomBeanPostProcessor();

    // 添加处理器
    beanFactory.addBeanPostProcessor(processor1);
    beanFactory.addBeanPostProcessor(processor2);

    assertEquals("应该有3个处理器（包括默认的生命周期处理器）",
        3, beanFactory.getBeanPostProcessors().size());

    // 移除处理器
    beanFactory.removeBeanPostProcessor(processor1);
    assertEquals("移除后应该有2个处理器",
        2, beanFactory.getBeanPostProcessors().size());

    // 重复添加同一个处理器
    beanFactory.addBeanPostProcessor(processor2);
    assertEquals("重复添加不应该增加处理器数量",
        2, beanFactory.getBeanPostProcessors().size());
  }

  @Test
  public void testLifecycleProcessorAccess() {
    LifecycleProcessor lifecycleProcessor = beanFactory.getLifecycleProcessor();
    assertNotNull("生命周期处理器不应该为空", lifecycleProcessor);

    // 验证生命周期处理器功能
    LifecycleTestBean testBean = new LifecycleTestBean();
    lifecycleProcessor.invokePostConstructMethods(testBean, "testBean");
    assertTrue("生命周期处理器应该能够调用 @PostConstruct 方法",
        testBean.isPostConstructCalled());
  }

  // 测试用的 Bean 类

  public static class LifecycleTestBean {
    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    public void init() {
      postConstructCalled = true;
    }

    @PreDestroy
    public void cleanup() {
      preDestroyCalled = true;
    }

    public boolean isPostConstructCalled() {
      return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
      return preDestroyCalled;
    }
  }

  public static class CustomBeanPostProcessor implements BeanPostProcessor {
    private boolean beforeInitializationCalled = false;
    private boolean afterInitializationCalled = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
      beforeInitializationCalled = true;
      return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
      afterInitializationCalled = true;
      return bean;
    }

    public boolean isBeforeInitializationCalled() {
      return beforeInitializationCalled;
    }

    public boolean isAfterInitializationCalled() {
      return afterInitializationCalled;
    }
  }

  public static class OrderTestProcessor implements BeanPostProcessor {
    private final String name;
    private boolean called = false;

    public OrderTestProcessor(String name) {
      this.name = name;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
      return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
      called = true;
      return bean;
    }

    public boolean isCalled() {
      return called;
    }

    public String getName() {
      return name;
    }
  }
}
