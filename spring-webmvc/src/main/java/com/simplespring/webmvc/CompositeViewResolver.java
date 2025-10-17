package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合视图解析器
 * 
 * CompositeViewResolver 管理多个视图解析器，并根据返回值类型或请求特征
 * 选择合适的视图解析器进行处理。
 * 
 * 解析策略：
 * 1. 检查请求头 Accept 是否包含 application/json
 * 2. 检查返回值是否为复杂对象（需要 JSON 序列化）
 * 3. 默认使用简单视图解析器
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class CompositeViewResolver implements ViewResolver {

  /**
   * 视图解析器列表
   */
  private final List<ViewResolver> resolvers = new ArrayList<ViewResolver>();

  /**
   * JSON 视图解析器
   */
  private final JsonViewResolver jsonViewResolver = new JsonViewResolver();

  /**
   * 简单视图解析器
   */
  private final SimpleViewResolver simpleViewResolver = new SimpleViewResolver();

  /**
   * 构造函数，初始化默认解析器
   */
  public CompositeViewResolver() {
    resolvers.add(jsonViewResolver);
    resolvers.add(simpleViewResolver);
  }

  /**
   * 添加视图解析器
   * 
   * @param resolver 视图解析器
   */
  public void addResolver(ViewResolver resolver) {
    if (resolver != null) {
      resolvers.add(resolver);
    }
  }

  @Override
  public void resolveView(Object returnValue, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    ViewResolver selectedResolver = selectResolver(returnValue, request);
    selectedResolver.resolveView(returnValue, request, response);
  }

  /**
   * 选择合适的视图解析器
   * 
   * @param returnValue 返回值
   * @param request     HTTP 请求
   * @return 选中的视图解析器
   */
  private ViewResolver selectResolver(Object returnValue, HttpServletRequest request) {

    // 检查请求头是否要求 JSON 响应
    String acceptHeader = request.getHeader("Accept");
    if (acceptHeader != null && acceptHeader.contains("application/json")) {
      return jsonViewResolver;
    }

    // 检查返回值类型是否需要 JSON 序列化
    if (needsJsonSerialization(returnValue)) {
      return jsonViewResolver;
    }

    // 默认使用简单视图解析器
    return simpleViewResolver;
  }

  /**
   * 判断返回值是否需要 JSON 序列化
   * 
   * @param returnValue 返回值
   * @return 如果需要 JSON 序列化返回 true，否则返回 false
   */
  private boolean needsJsonSerialization(Object returnValue) {
    if (returnValue == null) {
      return false;
    }

    Class<?> returnType = returnValue.getClass();

    // 基本类型和字符串不需要 JSON 序列化
    if (returnType == String.class ||
        returnType.isPrimitive() ||
        Number.class.isAssignableFrom(returnType) ||
        Boolean.class.isAssignableFrom(returnType)) {

      // 但是如果字符串看起来像 JSON，则使用 JSON 解析器
      if (returnType == String.class) {
        String str = returnValue.toString().trim();
        return (str.startsWith("{") && str.endsWith("}")) ||
            (str.startsWith("[") && str.endsWith("]"));
      }

      return false;
    }

    // 复杂对象、数组、集合、Map 需要 JSON 序列化
    return true;
  }

  /**
   * 获取解析器数量
   * 
   * @return 解析器数量
   */
  public int getResolverCount() {
    return resolvers.size();
  }
}
