package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet 参数解析器
 * 
 * 负责解析 Servlet 相关的参数类型，包括：
 * - HttpServletRequest
 * - HttpServletResponse
 * 
 * 这些参数直接从方法调用上下文中获取，不需要从请求参数中解析。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ServletParameterResolver implements ParameterResolver {

  @Override
  public boolean supportsParameter(Class<?> parameterType) {
    return parameterType == HttpServletRequest.class ||
        parameterType == HttpServletResponse.class;
  }

  @Override
  public Object resolveParameter(Class<?> parameterType, String parameterName,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    if (parameterType == HttpServletRequest.class) {
      return request;
    } else if (parameterType == HttpServletResponse.class) {
      return response;
    }

    throw new IllegalArgumentException("不支持的 Servlet 参数类型: " + parameterType);
  }
}
