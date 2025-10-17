package com.simplespring.beans.factory.support;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DependencyInjectionException 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class DependencyInjectionExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "依赖注入失败";
    DependencyInjectionException exception = new DependencyInjectionException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "依赖注入失败";
    Throwable cause = new RuntimeException("原因异常");
    DependencyInjectionException exception = new DependencyInjectionException(message, cause);

    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  public void testConstructorWithCause() {
    Throwable cause = new IllegalArgumentException("参数错误");
    DependencyInjectionException exception = new DependencyInjectionException(cause);

    assertEquals(cause, exception.getCause());
    assertTrue(exception.getMessage().contains(cause.toString()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    DependencyInjectionException exception = new DependencyInjectionException((String) null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithNullCause() {
    String message = "依赖注入失败";
    DependencyInjectionException exception = new DependencyInjectionException(message, null);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithNullCauseOnly() {
    DependencyInjectionException exception = new DependencyInjectionException((Throwable) null);

    assertNull(exception.getCause());
    assertTrue(exception.getMessage().contains("null"));
  }
}
