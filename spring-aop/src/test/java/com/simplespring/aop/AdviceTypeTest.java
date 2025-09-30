package com.simplespring.aop;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * AdviceType 枚举测试类
 * 
 * @author SimpleSpring
 */
public class AdviceTypeTest {

  @Test
  public void testAdviceTypeValues() {
    // 测试所有通知类型是否正确定义
    AdviceType[] types = AdviceType.values();
    assertEquals("应该有5种通知类型", 5, types.length);

    // 验证每种类型都存在
    assertTrue("应该包含 BEFORE 类型", containsType(types, AdviceType.BEFORE));
    assertTrue("应该包含 AFTER 类型", containsType(types, AdviceType.AFTER));
    assertTrue("应该包含 AFTER_RETURNING 类型", containsType(types, AdviceType.AFTER_RETURNING));
    assertTrue("应该包含 AFTER_THROWING 类型", containsType(types, AdviceType.AFTER_THROWING));
    assertTrue("应该包含 AROUND 类型", containsType(types, AdviceType.AROUND));
  }

  @Test
  public void testAdviceTypeValueOf() {
    // 测试 valueOf 方法
    assertEquals(AdviceType.BEFORE, AdviceType.valueOf("BEFORE"));
    assertEquals(AdviceType.AFTER, AdviceType.valueOf("AFTER"));
    assertEquals(AdviceType.AFTER_RETURNING, AdviceType.valueOf("AFTER_RETURNING"));
    assertEquals(AdviceType.AFTER_THROWING, AdviceType.valueOf("AFTER_THROWING"));
    assertEquals(AdviceType.AROUND, AdviceType.valueOf("AROUND"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAdviceTypeValueOfInvalid() {
    // 测试无效的通知类型
    AdviceType.valueOf("INVALID_TYPE");
  }

  @Test
  public void testAdviceTypeName() {
    // 测试 name() 方法
    assertEquals("BEFORE", AdviceType.BEFORE.name());
    assertEquals("AFTER", AdviceType.AFTER.name());
    assertEquals("AFTER_RETURNING", AdviceType.AFTER_RETURNING.name());
    assertEquals("AFTER_THROWING", AdviceType.AFTER_THROWING.name());
    assertEquals("AROUND", AdviceType.AROUND.name());
  }

  /**
   * 辅助方法：检查数组中是否包含指定的通知类型
   */
  private boolean containsType(AdviceType[] types, AdviceType target) {
    for (AdviceType type : types) {
      if (type == target) {
        return true;
      }
    }
    return false;
  }
}
