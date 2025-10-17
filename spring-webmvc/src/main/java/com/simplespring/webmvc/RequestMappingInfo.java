package com.simplespring.webmvc;

import com.simplespring.core.annotation.RequestMethod;

/**
 * 请求映射信息类，存储请求映射的详细信息
 * 
 * 该类封装了一个 HTTP 请求映射的所有相关信息，包括：
 * - 请求路径
 * - HTTP 方法
 * - 处理器方法信息
 * - 控制器实例
 * 
 * 用于在请求路由过程中匹配合适的处理器方法。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class RequestMappingInfo {

  /**
   * 请求路径
   */
  private String path;

  /**
   * HTTP 方法
   */
  private RequestMethod method;

  /**
   * 处理器方法信息
   */
  private HandlerMethod handlerMethod;

  /**
   * 构造函数
   * 
   * @param path          请求路径
   * @param method        HTTP 方法
   * @param handlerMethod 处理器方法信息
   */
  public RequestMappingInfo(String path, RequestMethod method, HandlerMethod handlerMethod) {
    this.path = path;
    this.method = method;
    this.handlerMethod = handlerMethod;
  }

  /**
   * 检查当前映射信息是否匹配指定的请求路径和方法
   * 
   * @param requestPath   请求路径
   * @param requestMethod HTTP 方法
   * @return 如果匹配返回 true，否则返回 false
   */
  public boolean matches(String requestPath, RequestMethod requestMethod) {
    return matchesPath(requestPath) && matchesMethod(requestMethod);
  }

  /**
   * 检查路径是否匹配
   * 
   * 支持以下匹配规则：
   * 1. 精确匹配：/users/123
   * 2. 通配符匹配：/users/* 匹配 /users/123
   * 3. 多级通配符：/users/** 匹配 /users/123/orders
   * 
   * @param requestPath 请求路径
   * @return 如果路径匹配返回 true，否则返回 false
   */
  private boolean matchesPath(String requestPath) {
    if (path == null || requestPath == null) {
      return false;
    }

    // 精确匹配
    if (path.equals(requestPath)) {
      return true;
    }

    // 通配符匹配
    if (path.endsWith("/*")) {
      String basePath = path.substring(0, path.length() - 2);
      return requestPath.startsWith(basePath + "/") &&
          requestPath.indexOf('/', basePath.length() + 1) == -1;
    }

    // 多级通配符匹配
    if (path.endsWith("/**")) {
      String basePath = path.substring(0, path.length() - 3);
      return requestPath.startsWith(basePath + "/");
    }

    return false;
  }

  /**
   * 检查 HTTP 方法是否匹配
   * 
   * @param requestMethod 请求的 HTTP 方法
   * @return 如果方法匹配返回 true，否则返回 false
   */
  private boolean matchesMethod(RequestMethod requestMethod) {
    return method == requestMethod;
  }

  // Getter 方法

  public String getPath() {
    return path;
  }

  public RequestMethod getMethod() {
    return method;
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public String toString() {
    return "RequestMappingInfo{" +
        "path='" + path + '\'' +
        ", method=" + method +
        ", handlerMethod=" + handlerMethod +
        '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    RequestMappingInfo that = (RequestMappingInfo) obj;

    if (path != null ? !path.equals(that.path) : that.path != null)
      return false;
    if (method != that.method)
      return false;
    return handlerMethod != null ? handlerMethod.equals(that.handlerMethod) : that.handlerMethod == null;
  }

  @Override
  public int hashCode() {
    int result = path != null ? path.hashCode() : 0;
    result = 31 * result + (method != null ? method.hashCode() : 0);
    result = 31 * result + (handlerMethod != null ? handlerMethod.hashCode() : 0);
    return result;
  }
}
