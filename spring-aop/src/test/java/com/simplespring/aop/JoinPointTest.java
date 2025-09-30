package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * JoinPoint 接口测试
 * 
 * @author SimpleSpring
 */
public class JoinPointTest {

  @Test
  public void testJoinPointType() {
    // 测试 JoinPointType 枚举
    JoinPoint.JoinPointType[] types = JoinPoint.JoinPointType.values();
    assertEquals("应该有4种连接点类型", 4, types.length);

    assertEquals("METHOD_EXECUTION", JoinPoint.JoinPointType.METHOD_EXECUTION.name());
    assertEquals("METHOD_CALL", JoinPoint.JoinPointType.METHOD_CALL.name());
    assertEquals("FIELD_ACCESS", JoinPoint.JoinPointType.FIELD_ACCESS.name());
    assertEquals("FIELD_SET", JoinPoint.JoinPointType.FIELD_SET.name());
  }

  @Test
  public void testJoinPointTypeValueOf() {
    // 测试 valueOf 方法
    assertEquals(JoinPoint.JoinPointType.METHOD_EXECUTION,
        JoinPoint.JoinPointType.valueOf("METHOD_EXECUTION"));
    assertEquals(JoinPoint.JoinPointType.METHOD_CALL,
        JoinPoint.JoinPointType.valueOf("METHOD_CALL"));
    assertEquals(JoinPoint.JoinPointType.FIELD_ACCESS,
        JoinPoint.JoinPointType.valueOf("FIELD_ACCESS"));
    assertEquals(JoinPoint.JoinPointType.FIELD_SET,
        JoinPoint.JoinPointType.valueOf("FIELD_SET"));
  }
}
