package com.simplespring.beans.factory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * BeanCreationException 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class BeanCreationExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "测试异常消息";
    BeanCreationException exception = new BeanCreationException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getBeanName());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithBeanNameAndMessage() {
    String beanName = "testBean";
    String message = "测试异常消息";
    BeanCreationException exception = new BeanCreationException(beanName, message);

    assertEquals(beanName, exception.getBeanName());
    assertTrue(exception.getMessage().contains(beanName));
    assertTrue(exception.getMessage().contains(message));
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithBeanNameMessageAndCause() {
    String beanName = "testBean";
    String message = "测试异常消息";
    Throwable cause = new RuntimeException("原因异常");
    BeanCreationException exception = new BeanCreationException(beanName, message, cause);

    assertEquals(beanName, exception.getBeanName());
    assertTrue(exception.getMessage().contains(beanName));
    assertTrue(exception.getMessage().contains(message));
    assertEquals(cause, exception.getCause());
  }

  @Test
  public void testConstructorWithBeanNameAndCause() {
    String beanName = "testBean";
    Throwable cause = new RuntimeException("原因异常");
    BeanCreationException exception = new BeanCreationException(beanName, cause);

    assertEquals(beanName, exception.getBeanName());
    assertTrue(exception.getMessage().contains(beanName));
    assertEquals(cause, exception.getCause());
  }

  @Test
  public void testConstructorWithNullBeanName() {
    String message = "测试异常消息";
    BeanCreationException exception = new BeanCreationException(null, message);

    assertNull(exception.getBeanName());
    assertTrue(exception.getMessage().contains("null"));
    assertTrue(exception.getMessage().contains(message));
  }

  @Test
  public void testConstructorWithEmptyBeanName() {
    String beanName = "";
    String message = "测试异常消息";
    BeanCreationException exception = new BeanCreationException(beanName, message);

    assertEquals(beanName, exception.getBeanName());
    assertTrue(exception.getMessage().contains("''"));
    assertTrue(exception.getMessage().contains(message));
  }
}
