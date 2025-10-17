package com.simplespring.beans.factory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * CircularDependencyException 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "检测到循环依赖";
    CircularDependencyException exception = new CircularDependencyException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "检测到循环依赖";
    Throwable cause = new RuntimeException("原因异常");
    CircularDependencyException exception = new CircularDependencyException(message, cause);

    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  public void testConstructorWithNullMessage() {
    CircularDependencyException exception = new CircularDependencyException(null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    String message = "";
    CircularDependencyException exception = new CircularDependencyException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithNullCause() {
    String message = "检测到循环依赖";
    CircularDependencyException exception = new CircularDependencyException(message, null);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }
}
