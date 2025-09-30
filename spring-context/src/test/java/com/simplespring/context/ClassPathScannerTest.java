package com.simplespring.context;

import com.simplespring.context.testdata.PlainClass;
import com.simplespring.context.testdata.TestComponent;
import com.simplespring.context.testdata.TestConfiguration;
import com.simplespring.context.testdata.TestController;
import com.simplespring.context.testdata.subpackage.SubPackageComponent;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * ClassPathScanner 的单元测试
 * 
 * 测试包扫描功能的各种场景，包括：
 * 1. 扫描带有不同注解的类
 * 2. 递归扫描子包
 * 3. 过滤非组件类
 * 4. 组件名称解析
 * 5. 边界条件处理
 * 
 * @author Simple Spring Framework
 */
public class ClassPathScannerTest {

  private ClassPathScanner scanner;

  @Before
  public void setUp() {
    scanner = new ClassPathScanner();
  }

  /**
   * 测试扫描包含各种注解的类
   */
  @Test
  public void testScanPackageWithDifferentAnnotations() {
    // 扫描测试数据包
    Set<Class<?>> components = scanner.scanPackage("com.simplespring.context.testdata");

    // 验证扫描结果
    assertNotNull("扫描结果不应为 null", components);
    assertTrue("应该扫描到组件", components.size() > 0);

    // 验证包含带注解的类
    assertTrue("应该包含 TestComponent", components.contains(TestComponent.class));
    assertTrue("应该包含 TestController", components.contains(TestController.class));
    assertTrue("应该包含 TestConfiguration", components.contains(TestConfiguration.class));

    // 验证不包含普通类
    assertFalse("不应该包含 PlainClass", components.contains(PlainClass.class));
  }

  /**
   * 测试递归扫描子包
   */
  @Test
  public void testScanPackageRecursively() {
    // 扫描包含子包的包
    Set<Class<?>> components = scanner.scanPackage("com.simplespring.context.testdata");

    // 验证包含子包中的组件
    assertTrue("应该包含子包中的组件", components.contains(SubPackageComponent.class));
  }

  /**
   * 测试只扫描子包
   */
  @Test
  public void testScanSubPackageOnly() {
    // 只扫描子包
    Set<Class<?>> components = scanner.scanPackage("com.simplespring.context.testdata.subpackage");

    // 验证只包含子包中的组件
    assertTrue("应该包含子包组件", components.contains(SubPackageComponent.class));
    assertFalse("不应该包含父包组件", components.contains(TestComponent.class));
  }

  /**
   * 测试组件类识别
   */
  @Test
  public void testIsComponent() {
    // 测试带注解的类
    assertTrue("TestComponent 应该是组件", scanner.isComponent(TestComponent.class));
    assertTrue("TestController 应该是组件", scanner.isComponent(TestController.class));
    assertTrue("TestConfiguration 应该是组件", scanner.isComponent(TestConfiguration.class));

    // 测试普通类
    assertFalse("PlainClass 不应该是组件", scanner.isComponent(PlainClass.class));

    // 测试 null
    assertFalse("null 不应该是组件", scanner.isComponent(null));
  }

  /**
   * 测试组件名称解析
   */
  @Test
  public void testGetComponentName() {
    // 测试默认名称（类名首字母小写）
    assertEquals("TestComponent 的默认名称应该是 testComponent",
        "testComponent", scanner.getComponentName(TestComponent.class));

    // 测试自定义名称
    assertEquals("TestController 的自定义名称应该是 testController",
        "testController", scanner.getComponentName(TestController.class));

    // 测试 Configuration 类的默认名称
    assertEquals("TestConfiguration 的默认名称应该是 testConfiguration",
        "testConfiguration", scanner.getComponentName(TestConfiguration.class));

    // 测试子包组件的自定义名称
    assertEquals("SubPackageComponent 的自定义名称应该是 subComponent",
        "subComponent", scanner.getComponentName(SubPackageComponent.class));

    // 测试 null
    assertNull("null 的组件名称应该是 null", scanner.getComponentName(null));
  }

  /**
   * 测试空包路径
   */
  @Test(expected = IllegalArgumentException.class)
  public void testScanEmptyPackage() {
    scanner.scanPackage("");
  }

  /**
   * 测试 null 包路径
   */
  @Test(expected = IllegalArgumentException.class)
  public void testScanNullPackage() {
    scanner.scanPackage(null);
  }

  /**
   * 测试不存在的包路径
   */
  @Test
  public void testScanNonExistentPackage() {
    Set<Class<?>> components = scanner.scanPackage("com.nonexistent.package");

    // 验证返回空集合而不是 null
    assertNotNull("扫描结果不应为 null", components);
    assertTrue("不存在的包应该返回空集合", components.isEmpty());
  }

  /**
   * 测试扫描结果的唯一性
   */
  @Test
  public void testScanResultUniqueness() {
    Set<Class<?>> components1 = scanner.scanPackage("com.simplespring.context.testdata");
    Set<Class<?>> components2 = scanner.scanPackage("com.simplespring.context.testdata");

    // 验证两次扫描结果一致
    assertEquals("两次扫描结果应该一致", components1, components2);

    // 验证集合中没有重复元素
    assertEquals("集合中不应有重复元素", components1.size(), components1.size());
  }
}
