package com.simplespring.aop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AspectDefinition 类测试
 * 
 * @author SimpleSpring
 */
public class AspectDefinitionTest {

  private Object aspectInstance;
  private Class<?> aspectClass;
  private Method testMethod;

  @Before
  public void setUp() throws Exception {
    aspectInstance = new TestAspect();
    aspectClass = TestAspect.class;
    testMethod = this.getClass().getDeclaredMethod("testMethod");
  }

  @Test
  public void testDefaultConstructor() {
    // 测试默认构造函数
    AspectDefinition aspect = new AspectDefinition();

    assertNull("默认构造函数应该设置 aspectInstance 为 null", aspect.getAspectInstance());
    assertNull("默认构造函数应该设置 aspectClass 为 null", aspect.getAspectClass());
    assertNotNull("默认构造函数应该初始化 advices 列表", aspect.getAdvices());
    assertTrue("默认构造函数应该创建空的 advices 列表", aspect.getAdvices().isEmpty());
    assertEquals("默认优先级应该是最低", Integer.MAX_VALUE, aspect.getOrder());
  }

  @Test
  public void testParameterizedConstructor() {
    // 测试带参数的构造函数
    AspectDefinition aspect = new AspectDefinition(aspectInstance, aspectClass);

    assertEquals("构造函数应该正确设置 aspectInstance", aspectInstance, aspect.getAspectInstance());
    assertEquals("构造函数应该正确设置 aspectClass", aspectClass, aspect.getAspectClass());
    assertEquals("构造函数应该设置 aspectName", "TestAspect", aspect.getAspectName());
    assertNotNull("构造函数应该初始化 advices 列表", aspect.getAdvices());
    assertTrue("构造函数应该创建空的 advices 列表", aspect.getAdvices().isEmpty());
  }

  @Test
  public void testAddAdvice() {
    // 测试添加通知定义
    AspectDefinition aspect = new AspectDefinition();
    AdviceDefinition advice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", aspectInstance);

    aspect.addAdvice(advice);

    assertEquals("添加通知后列表大小应该为1", 1, aspect.getAdvices().size());
    assertTrue("列表应该包含添加的通知", aspect.getAdvices().contains(advice));
    assertTrue("应该有通知定义", aspect.hasAdvices());
  }

  @Test
  public void testAddNullAdvice() {
    // 测试添加 null 通知定义
    AspectDefinition aspect = new AspectDefinition();

    aspect.addAdvice(null);

    assertEquals("添加 null 通知后列表大小应该为0", 0, aspect.getAdvices().size());
    assertFalse("不应该有通知定义", aspect.hasAdvices());
  }

  @Test
  public void testRemoveAdvice() {
    // 测试移除通知定义
    AspectDefinition aspect = new AspectDefinition();
    AdviceDefinition advice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", aspectInstance);

    aspect.addAdvice(advice);
    assertEquals("添加通知后列表大小应该为1", 1, aspect.getAdvices().size());

    aspect.removeAdvice(advice);
    assertEquals("移除通知后列表大小应该为0", 0, aspect.getAdvices().size());
    assertFalse("不应该有通知定义", aspect.hasAdvices());
  }

  @Test
  public void testGetAdvicesByType() {
    // 测试按类型获取通知定义
    AspectDefinition aspect = new AspectDefinition();

    AdviceDefinition beforeAdvice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", aspectInstance);
    AdviceDefinition afterAdvice = new AdviceDefinition(testMethod, AdviceType.AFTER,
        "execution(* *.*(..))", aspectInstance);
    AdviceDefinition anotherBeforeAdvice = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        "execution(* *.save(..))", aspectInstance);

    aspect.addAdvice(beforeAdvice);
    aspect.addAdvice(afterAdvice);
    aspect.addAdvice(anotherBeforeAdvice);

    List<AdviceDefinition> beforeAdvices = aspect.getAdvicesByType(AdviceType.BEFORE);
    List<AdviceDefinition> afterAdvices = aspect.getAdvicesByType(AdviceType.AFTER);
    List<AdviceDefinition> aroundAdvices = aspect.getAdvicesByType(AdviceType.AROUND);

    assertEquals("应该有2个 BEFORE 通知", 2, beforeAdvices.size());
    assertEquals("应该有1个 AFTER 通知", 1, afterAdvices.size());
    assertEquals("应该有0个 AROUND 通知", 0, aroundAdvices.size());

    assertTrue("BEFORE 通知列表应该包含第一个 BEFORE 通知", beforeAdvices.contains(beforeAdvice));
    assertTrue("BEFORE 通知列表应该包含第二个 BEFORE 通知", beforeAdvices.contains(anotherBeforeAdvice));
    assertTrue("AFTER 通知列表应该包含 AFTER 通知", afterAdvices.contains(afterAdvice));
  }

  @Test
  public void testSetAspectClass() {
    // 测试设置切面类
    AspectDefinition aspect = new AspectDefinition();

    aspect.setAspectClass(aspectClass);

    assertEquals("应该正确设置切面类", aspectClass, aspect.getAspectClass());
    assertEquals("应该自动设置切面名称", "TestAspect", aspect.getAspectName());
  }

  @Test
  public void testSetAdvices() {
    // 测试设置通知列表
    AspectDefinition aspect = new AspectDefinition();

    AdviceDefinition advice1 = new AdviceDefinition(testMethod, AdviceType.BEFORE,
        "execution(* *.*(..))", aspectInstance);
    AdviceDefinition advice2 = new AdviceDefinition(testMethod, AdviceType.AFTER,
        "execution(* *.*(..))", aspectInstance);

    List<AdviceDefinition> advices = new java.util.ArrayList<AdviceDefinition>();
    advices.add(advice1);
    advices.add(advice2);

    aspect.setAdvices(advices);

    assertEquals("应该正确设置通知列表大小", 2, aspect.getAdvices().size());
    assertTrue("应该包含第一个通知", aspect.getAdvices().contains(advice1));
    assertTrue("应该包含第二个通知", aspect.getAdvices().contains(advice2));

    // 测试修改原列表不会影响切面定义中的列表
    advices.clear();
    assertEquals("修改原列表不应该影响切面定义中的列表", 2, aspect.getAdvices().size());
  }

  @Test
  public void testSetNullAdvices() {
    // 测试设置 null 通知列表
    AspectDefinition aspect = new AspectDefinition();

    aspect.setAdvices(null);

    assertNotNull("设置 null 后应该创建空列表", aspect.getAdvices());
    assertTrue("设置 null 后应该是空列表", aspect.getAdvices().isEmpty());
  }

  @Test
  public void testOrder() {
    // 测试优先级设置
    AspectDefinition aspect = new AspectDefinition();

    aspect.setOrder(100);
    assertEquals("应该正确设置优先级", 100, aspect.getOrder());
  }

  @Test
  public void testToString() {
    // 测试 toString 方法
    AspectDefinition aspect = new AspectDefinition(aspectInstance, aspectClass);
    aspect.setOrder(100);

    String result = aspect.toString();

    assertNotNull("toString 不应该返回 null", result);
    assertTrue("toString 应该包含切面名称", result.contains("TestAspect"));
    assertTrue("toString 应该包含类名", result.contains("TestAspect"));
    assertTrue("toString 应该包含通知数量", result.contains("advicesCount=0"));
    assertTrue("toString 应该包含优先级", result.contains("order=100"));
  }

  /**
   * 测试用的方法
   */
  private void testMethod() {
    // 用于测试的空方法
  }

  /**
   * 测试用的切面类
   */
  private static class TestAspect {
    // 测试用的空切面类
  }
}
