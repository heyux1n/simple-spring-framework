package com.simplespring.beans.factory.support;

import com.simplespring.core.annotation.PostConstruct;
import com.simplespring.core.annotation.PreDestroy;

/**
 * 简单的生命周期测试
 * 用于验证基本功能是否正常工作
 * 
 * @author SimpleSpring Framework
 */
public class SimpleLifecycleTest {

  public static void main(String[] args) {
    System.out.println("开始生命周期测试...");

    // 测试生命周期处理器
    LifecycleProcessor processor = new LifecycleProcessor();
    TestBean testBean = new TestBean();

    System.out.println("调用前: PostConstruct = " + testBean.isPostConstructCalled());
    System.out.println("调用前: PreDestroy = " + testBean.isPreDestroyCalled());

    // 调用 PostConstruct 方法
    processor.invokePostConstructMethods(testBean, "testBean");
    System.out.println("调用 PostConstruct 后: " + testBean.isPostConstructCalled());

    // 调用 PreDestroy 方法
    processor.invokePreDestroyMethods(testBean, "testBean");
    System.out.println("调用 PreDestroy 后: " + testBean.isPreDestroyCalled());

    // 测试 BeanPostProcessor 接口
    Object result1 = processor.postProcessBeforeInitialization(testBean, "testBean");
    Object result2 = processor.postProcessAfterInitialization(new TestBean(), "testBean2");

    System.out.println("BeanPostProcessor 测试完成");
    System.out.println("生命周期测试完成！");
  }

  public static class TestBean {
    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    public void init() {
      postConstructCalled = true;
      System.out.println("@PostConstruct 方法被调用");
    }

    @PreDestroy
    public void destroy() {
      preDestroyCalled = true;
      System.out.println("@PreDestroy 方法被调用");
    }

    public boolean isPostConstructCalled() {
      return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
      return preDestroyCalled;
    }
  }
}
