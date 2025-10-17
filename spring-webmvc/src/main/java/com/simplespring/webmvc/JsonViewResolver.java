package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * JSON 视图解析器
 * 
 * JsonViewResolver 专门处理 JSON 格式的响应，支持：
 * 1. 基本数据类型的 JSON 序列化
 * 2. 对象的简单 JSON 序列化
 * 3. 集合和数组的 JSON 序列化
 * 4. Map 的 JSON 序列化
 * 
 * 注意：这是一个简化的 JSON 序列化实现，仅用于学习目的。
 * 在生产环境中应该使用成熟的 JSON 库如 Jackson 或 Gson。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class JsonViewResolver implements ViewResolver {

  @Override
  public void resolveView(Object returnValue, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 设置 JSON 响应头
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    // 序列化为 JSON
    String jsonContent = toJson(returnValue);

    // 写入响应
    PrintWriter writer = response.getWriter();
    writer.print(jsonContent);
    writer.flush();
  }

  /**
   * 将对象转换为 JSON 字符串
   * 
   * @param obj 要转换的对象
   * @return JSON 字符串
   */
  private String toJson(Object obj) {
    if (obj == null) {
      return "null";
    }

    // 基本类型处理
    if (obj instanceof String) {
      return "\"" + escapeJson(obj.toString()) + "\"";
    } else if (obj instanceof Number || obj instanceof Boolean) {
      return obj.toString();
    }

    // 数组处理
    if (obj.getClass().isArray()) {
      return arrayToJson((Object[]) obj);
    }

    // 集合处理
    if (obj instanceof Collection) {
      return collectionToJson((Collection<?>) obj);
    }

    // Map 处理
    if (obj instanceof Map) {
      return mapToJson((Map<?, ?>) obj);
    }

    // 对象处理
    return objectToJson(obj);
  }

  /**
   * 数组转 JSON
   * 
   * @param array 数组对象
   * @return JSON 字符串
   */
  private String arrayToJson(Object[] array) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");

    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(toJson(array[i]));
    }

    sb.append("]");
    return sb.toString();
  }

  /**
   * 集合转 JSON
   * 
   * @param collection 集合对象
   * @return JSON 字符串
   */
  private String collectionToJson(Collection<?> collection) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");

    boolean first = true;
    for (Object item : collection) {
      if (!first) {
        sb.append(",");
      }
      sb.append(toJson(item));
      first = false;
    }

    sb.append("]");
    return sb.toString();
  }

  /**
   * Map 转 JSON
   * 
   * @param map Map 对象
   * @return JSON 字符串
   */
  private String mapToJson(Map<?, ?> map) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");

    boolean first = true;
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      if (!first) {
        sb.append(",");
      }

      sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\"");
      sb.append(":");
      sb.append(toJson(entry.getValue()));

      first = false;
    }

    sb.append("}");
    return sb.toString();
  }

  /**
   * 对象转 JSON
   * 
   * @param obj 对象
   * @return JSON 字符串
   */
  private String objectToJson(Object obj) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");

    Class<?> clazz = obj.getClass();
    Field[] fields = clazz.getDeclaredFields();

    boolean first = true;
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        Object value = field.get(obj);

        if (!first) {
          sb.append(",");
        }

        sb.append("\"").append(field.getName()).append("\"");
        sb.append(":");
        sb.append(toJson(value));

        first = false;
      } catch (IllegalAccessException e) {
        // 忽略无法访问的字段
      }
    }

    sb.append("}");
    return sb.toString();
  }

  /**
   * 转义 JSON 字符串中的特殊字符
   * 
   * @param str 原始字符串
   * @return 转义后的字符串
   */
  private String escapeJson(String str) {
    if (str == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        default:
          sb.append(c);
          break;
      }
    }
    return sb.toString();
  }
}
