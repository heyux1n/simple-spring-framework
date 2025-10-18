package com.simplespring.core.convert;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TypeMismatchException 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class TypeMismatchExceptionTest {

  @Test
  public void testConstructorWithNullValueAndType() {
    Object value = null;
    Class<?> requiredType = String.class;
    TypeMismatchException exception = new TypeMismatchException(value, requiredType);

    assertNull(exception.getValue());
    assertEquals(requiredType, exception.getRequiredType());
    assertNull(exception.getCause());
    assertTrue(exception.getMessage().contains("null"));
    assertTrue(exception.getMessage().contains(requiredType.getName()));
  }

  @Test
  public void testConstructorWithValueAndRequiredType() {
    Object value = "123abc";
    Class<?> requiredType = Integer.class;
    TypeMismatchException exception = new TypeMismatchException(value, requiredType);

    assertEquals(value, exception.getValue());
    assertEquals(requiredType, exception.getRequiredType());
    assertTrue(exception.getMessage().contains(value.toString()));
    assertTrue(exception.getMessage().contains(requiredType.getName()));
  }

  @Test
  public void testConstructorWithValueRequiredTypeAndCause() {
    Object value = "invalid";
    Class<?> requiredType = Integer.class;
    Throwable cause = new NumberFormatException("For input string: \"invalid\"");
    TypeMismatchException exception = new TypeMismatchException(value, requiredType, cause);

    assertEquals(value, exception.getValue());
    assertEquals(requiredType, exception.getRequiredType());
    assertEquals(cause, exception.getCause());
    assertTrue(exception.getMessage().contains(value.toString()));
    assertTrue(exception.getMessage().contains(requiredType.getName()));
  }

  @Test
  public void testConstructorWithNullValue() {
    Class<?> requiredType = Integer.class;
    TypeMismatchException exception = new TypeMismatchException(null, requiredType);

    assertNull(exception.getValue());
    assertEquals(requiredType, exception.getRequiredType());
    assertTrue(exception.getMessage().contains("null"));
    assertTrue(exception.getMessage().contains(requiredType.getName()));
  }

  @Test
  public void testConstructorWithNullRequiredType() {
    Object value = "test";
    TypeMismatchException exception = new TypeMismatchException(value, null);

    assertEquals(value, exception.getValue());
    assertNull(exception.getRequiredType());
    assertTrue(exception.getMessage().contains(value.toString()));
    assertTrue(exception.getMessage().contains("null"));
  }

  @Test
  public void testConstructorWithComplexValue() {
    Object value = new java.util.Date();
    Class<?> requiredType = String.class;
    TypeMismatchException exception = new TypeMismatchException(value, requiredType);

    assertEquals(value, exception.getValue());
    assertEquals(requiredType, exception.getRequiredType());
    assertTrue(exception.getMessage().contains(value.toString()));
    assertTrue(exception.getMessage().contains(requiredType.getName()));
  }

  @Test
  public void testExceptionMessage() {
    Object value = "test";
    Class<?> requiredType = Integer.class;
    TypeMismatchException exception = new TypeMismatchException(value, requiredType);

    String message = exception.getMessage();
    assertTrue("Message should contain value type", message.contains("String"));
    assertTrue("Message should contain required type", message.contains("Integer"));
    assertTrue("Message should contain conversion failure info", message.contains("Failed to convert"));
  }
}
