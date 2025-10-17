package com.simplespring.beans.factory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * NoSuchBeanDefinitionException 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class NoSuchBeanDefinitionExceptionTest {

  @Test
  public void testConstructorWithBeanName() {
    String beanName = "testBean";
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException(beanName);

    assertEquals(beanName, exception.getBeanName());
    assertNull(exception.getBeanType());
    assertTrue(exception.getMessage().contains(beanName));
  }

  @Test
  public void testConstructorWithBeanType() {
    Class<?> beanType = String.class;
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException(beanType);

    assertNull(exception.getBeanName());
    assertEquals(beanType, exception.getBeanType());
    assertTrue(exception.getMessage().contains(beanType.getName()));
  }

  @Test
  public void testConstructorWithBeanNameAndMessage() {
    String beanName = "testBean";
    String message = "自定义异常消息";
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException(beanName, message);

    assertEquals(beanName, exception.getBeanName());
    assertNull(exception.getBeanType());
    assertTrue(exception.getMessage().contains(message));
  }

  @Test
  public void testConstructorWithBeanTypeAndMessage() {
    Class<?> beanType = String.class;
    String message = "自定义异常消息";
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException(beanType, message);

    assertNull(exception.getBeanName());
    assertEquals(beanType, exception.getBeanType());
    assertTrue(exception.getMessage().contains(message));
  }

  @Test
  public void testConstructorWithNullBeanName() {
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException((String) null);

    assertNull(exception.getBeanName());
    assertNull(exception.getBeanType());
    assertTrue(exception.getMessage().contains("null"));
  }

  @Test
  public void testConstructorWithNullBeanType() {
    NoSuchBeanDefinitionException exception = new NoSuchBeanDefinitionException((Class<?>) null);

    assertNull(exception.getBeanName());
    assertNull(exception.getBeanType());
    assertTrue(exception.getMessage().contains("null"));
  }
}
