package com.simplespring.webmvc;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.*;

/**
 * JsonViewResolver 类的单元测试
 * 
 * 测试 JSON 视图解析器的功能。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class JsonViewResolverTest {

  private JsonViewResolver resolver;
  private HttpServletRequest request;
  private SimpleViewResolverTest.MockHttpServletResponse response;

  @Before
  public void setUp() {
    resolver = new JsonViewResolver();
    request = new BasicTypeParameterResolverTest.MockHttpServletRequest();
    response = new SimpleViewResolverTest.MockHttpServletResponse();
  }

  @Test
  public void testResolveNullValue() throws Exception {
    // 测试 null 值
    resolver.resolveView(null, request, response);

    assertEquals("null 值应该序列化为 'null'", "null", response.getContent());
    assertTrue("Content-Type 应该是 JSON", response.getContentType().contains("application/json"));
  }

  @Test
  public void testResolveStringValue() throws Exception {
    // 测试字符串值
    resolver.resolveView("hello world", request, response);

    assertEquals("字符串应该被引号包围", "\"hello world\"", response.getContent());
    assertTrue("Content-Type 应该是 JSON", response.getContentType().contains("application/json"));
  }

  @Test
  public void testResolveStringWithSpecialCharacters() throws Exception {
    // 测试包含特殊字符的字符串
    resolver.resolveView("hello \"world\"\ntest\ttab", request, response);

    assertEquals("特殊字符应该被转义", "\"hello \\\"world\\\"\\ntest\\ttab\"", response.getContent());
  }

  @Test
  public void testResolveNumberValues() throws Exception {
    // 测试数字值
    resolver.resolveView(42, request, response);
    assertEquals("整数应该直接输出", "42", response.getContent());

    response = new SimpleViewResolverTest.MockHttpServletResponse();
    resolver.resolveView(3.14, request, response);
    assertEquals("浮点数应该直接输出", "3.14", response.getContent());

    response = new SimpleViewResolverTest.MockHttpServletResponse();
    resolver.resolveView(123L, request, response);
    assertEquals("长整数应该直接输出", "123", response.getContent());
  }

  @Test
  public void testResolveBooleanValue() throws Exception {
    // 测试布尔值
    resolver.resolveView(true, request, response);
    assertEquals("true 应该直接输出", "true", response.getContent());

    response = new SimpleViewResolverTest.MockHttpServletResponse();
    resolver.resolveView(false, request, response);
    assertEquals("false 应该直接输出", "false", response.getContent());
  }

  @Test
  public void testResolveArrayValue() throws Exception {
    // 测试数组值
    String[] array = { "hello", "world", "test" };
    resolver.resolveView(array, request, response);

    assertEquals("数组应该序列化为 JSON 数组", "[\"hello\",\"world\",\"test\"]", response.getContent());
  }

  @Test
  public void testResolveIntegerArray() throws Exception {
    // 测试整数数组
    Integer[] array = { 1, 2, 3, 4, 5 };
    resolver.resolveView(array, request, response);

    assertEquals("整数数组应该正确序列化", "[1,2,3,4,5]", response.getContent());
  }

  @Test
  public void testResolveListValue() throws Exception {
    // 测试 List 值
    List<String> list = new ArrayList<String>();
    list.add("item1");
    list.add("item2");
    list.add("item3");

    resolver.resolveView(list, request, response);

    assertEquals("List 应该序列化为 JSON 数组", "[\"item1\",\"item2\",\"item3\"]", response.getContent());
  }

  @Test
  public void testResolveMapValue() throws Exception {
    // 测试 Map 值
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("name", "John");
    map.put("age", 30);
    map.put("active", true);

    resolver.resolveView(map, request, response);

    String content = response.getContent();
    assertTrue("Map 应该序列化为 JSON 对象", content.startsWith("{") && content.endsWith("}"));
    assertTrue("应该包含 name 字段", content.contains("\"name\":\"John\""));
    assertTrue("应该包含 age 字段", content.contains("\"age\":30"));
    assertTrue("应该包含 active 字段", content.contains("\"active\":true"));
  }

  @Test
  public void testResolveObjectValue() throws Exception {
    // 测试自定义对象值
    TestUser user = new TestUser();
    user.name = "Alice";
    user.age = 25;
    user.active = true;

    resolver.resolveView(user, request, response);

    String content = response.getContent();
    assertTrue("对象应该序列化为 JSON", content.startsWith("{") && content.endsWith("}"));
    assertTrue("应该包含 name 字段", content.contains("\"name\":\"Alice\""));
    assertTrue("应该包含 age 字段", content.contains("\"age\":25"));
    assertTrue("应该包含 active 字段", content.contains("\"active\":true"));
  }

  @Test
  public void testResolveNestedObject() throws Exception {
    // 测试嵌套对象
    TestUser user = new TestUser();
    user.name = "Bob";
    user.age = 35;

    TestAddress address = new TestAddress();
    address.street = "123 Main St";
    address.city = "New York";
    user.address = address;

    resolver.resolveView(user, request, response);

    String content = response.getContent();
    assertTrue("应该包含嵌套对象", content.contains("\"address\":{"));
    assertTrue("应该包含街道信息", content.contains("\"street\":\"123 Main St\""));
    assertTrue("应该包含城市信息", content.contains("\"city\":\"New York\""));
  }

  @Test
  public void testResolveEmptyCollections() throws Exception {
    // 测试空集合
    List<String> emptyList = new ArrayList<String>();
    resolver.resolveView(emptyList, request, response);
    assertEquals("空 List 应该序列化为空数组", "[]", response.getContent());

    response = new SimpleViewResolverTest.MockHttpServletResponse();
    Map<String, Object> emptyMap = new HashMap<String, Object>();
    resolver.resolveView(emptyMap, request, response);
    assertEquals("空 Map 应该序列化为空对象", "{}", response.getContent());
  }

  @Test
  public void testResolveComplexNestedStructure() throws Exception {
    // 测试复杂嵌套结构
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("users", Arrays.asList("Alice", "Bob", "Charlie"));
    data.put("count", 3);
    data.put("success", true);

    Map<String, String> meta = new HashMap<String, String>();
    meta.put("version", "1.0");
    meta.put("timestamp", "2023-01-01");
    data.put("meta", meta);

    resolver.resolveView(data, request, response);

    String content = response.getContent();
    assertTrue("应该是有效的 JSON 对象", content.startsWith("{") && content.endsWith("}"));
    assertTrue("应该包含用户数组", content.contains("\"users\":[\"Alice\",\"Bob\",\"Charlie\"]"));
    assertTrue("应该包含计数", content.contains("\"count\":3"));
    assertTrue("应该包含成功标志", content.contains("\"success\":true"));
    assertTrue("应该包含元数据对象", content.contains("\"meta\":{"));
  }

  /**
   * 测试用的用户类
   */
  static class TestUser {
    public String name;
    public int age;
    public boolean active;
    public TestAddress address;
  }

  /**
   * 测试用的地址类
   */
  static class TestAddress {
    public String street;
    public String city;
  }
}
