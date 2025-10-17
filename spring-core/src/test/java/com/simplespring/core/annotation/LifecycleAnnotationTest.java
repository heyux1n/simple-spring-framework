package com.simplespring.core.annotation;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * 生命周期注解测试
 * 验证 @PostConstruct 和 @PreDestroy 注解的基本功能
 * 
 * @author SimpleSpring Framework
 */
public class LifecycleAnnotationTest {

  @Test
  public void testPostConstructAnnotation() throws NoSuchMethodException {
    Method initMethod = TestBean.class.getMethod("init");

    assertTrue("init 方法应该有 @PostConstruct 注解",
        initMethod.isAnnotationPresent(PostConstruct.class));

    PostConstruct annotation = initMethod.getAnnotation(PostConstruct.class);
    assertNotNull("@PostConstruct 注解不应该为空", annotation);
  }

  @Test
  public void testPreDestroyAnnotation() throws NoSuchMethodException {
    Method destroyMethod = TestBean.class.getMethod("destroy");

    assertTrue("destroy 方法应该有 @PreDestroy 注解",
        destroyMethod.isAnnotationPresent(PreDestroy.class));

    PreDestroy annotation = destroyMethod.getAnnotation(PreDestroy.class);
    assertNotNull("@PreDestroy 注解不应该为空", annotation);
  }

  @Test
  public void testAnnotationRetention() {
    // 验证注解的保留策略是 RUNTIME
    assertEquals("@PostConstruct 注解应该在运行时可用",
        java.lang.annotation.RetentionPolicy.RUNTIME,
        PostConstruct.class.getAnnotation(java.lang.annotation.Retention.class).value());

    assertEquals("@PreDestroy 注解应该在运行时可用",
        java.lang.annotation.RetentionPolicy.RUNTIME,
        PreDestroy.class.getAnnotation(java.lang.annotation.Retention.class).value());
  }

  @Test
  public void testAnnotationTarget() {
    // 验证注解的目标是 METHOD
    java.lang.annotation.Target postConstructTarget = PostConstruct.class
        .getAnnotation(java.lang.annotation.Target.class);
    assertEquals("@PostConstruct 注解应该只能用于方法",
        1, postConstructTarget.value().length);
    assertEquals("@PostConstruct 注解应该只能用于方法",
        java.lang.annotation.ElementType.METHOD, postConstructTarget.value()[0]);

    java.lang.annotation.Target preDestroyTarget = PreDestroy.class.getAnnotation(java.lang.annotation.Target.class);
    assertEquals("@PreDestroy 注解应该只能用于方法",
        1, preDestroyTarget.value().length);
    assertEquals("@PreDestroy 注解应该只能用于方法",
        java.lang.annotation.ElementType.METHOD, preDestroyTarget.value()[0]);
  }

  @Test
  public void testMultipleAnnotationsOnSameClass() throws NoSuchMethodException {
    Method[] methods = MultipleAnnotationBean.class.getDeclaredMethods();

    int postConstructCount = 0;
    int preDestroyCount = 0;

    for (Method method : methods) {
      if (method.isAnnotationPresent(PostConstruct.class)) {
        postConstructCount++;
      }
      if (method.isAnnotationPresent(PreDestroy.class)) {
        preDestroyCount++;
      }
    }

    assertEquals("应该有2个 @PostConstruct 方法", 2, postConstructCount);
    assertEquals("应该有2个 @PreDestroy 方法", 2, preDestroyCount);
  }

  @Test
  public void testInheritedAnnotations() throws NoSuchMethodException {
    // 验证子类可以继承父类的注解方法
    Method parentInit = ParentBean.class.getDeclaredMethod("parentInit");
    Method childInit = ChildBean.class.getDeclaredMethod("childInit");

    assertTrue("父类方法应该有 @PostConstruct 注解",
        parentInit.isAnnotationPresent(PostConstruct.class));
    assertTrue("子类方法应该有 @PostConstruct 注解",
        childInit.isAnnotationPresent(PostConstruct.class));
  }

  @Test
  public void testPrivateMethodAnnotation() throws NoSuchMethodException {
    Method privateInit = PrivateMethodBean.class.getDeclaredMethod("privateInit");

    assertTrue("私有方法应该可以有 @PostConstruct 注解",
        privateInit.isAnnotationPresent(PostConstruct.class));
  }

  // 测试用的 Bean 类

  public static class TestBean {
    @PostConstruct
    public void init() {
      // 初始化方法
    }

    @PreDestroy
    public void destroy() {
      // 销毁方法
    }

    public void normalMethod() {
      // 普通方法，没有注解
    }
  }

  public static class MultipleAnnotationBean {
    @PostConstruct
    public void init1() {
      // 第一个初始化方法
    }

    @PostConstruct
    public void init2() {
      // 第二个初始化方法
    }

    @PreDestroy
    public void destroy1() {
      // 第一个销毁方法
    }

    @PreDestroy
    public void destroy2() {
      // 第二个销毁方法
    }
  }

  public static class ParentBean {
    @PostConstruct
    protected void parentInit() {
      // 父类初始化方法
    }

    @PreDestroy
    protected void parentDestroy() {
      // 父类销毁方法
    }
  }

  public static class ChildBean extends ParentBean {
    @PostConstruct
    public void childInit() {
      // 子类初始化方法
    }

    @PreDestroy
    public void childDestroy() {
      // 子类销毁方法
    }
  }

  public static class PrivateMethodBean {
    @PostConstruct
    private void privateInit() {
      // 私有初始化方法
    }

    @PreDestroy
    private void privateDestroy() {
      // 私有销毁方法
    }
  }
}
