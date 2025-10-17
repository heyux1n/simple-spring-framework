package com.simplespring.webmvc;

import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import com.simplespring.core.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于注解的请求映射处理器映射实现
 * 
 * RequestMappingHandlerMapping 通过扫描控制器类中的 @RequestMapping 注解
 * 来构建请求映射表，支持类级别和方法级别的路径映射。
 * 
 * 主要功能：
 * 1. 扫描带有 @Controller 注解的类
 * 2. 解析类和方法上的 @RequestMapping 注解
 * 3. 构建完整的请求路径映射
 * 4. 提供快速的请求路由查找
 * 
 * 路径组合规则：
 * - 如果类和方法都有 @RequestMapping，则组合两个路径
 * - 如果只有方法有 @RequestMapping，则直接使用方法路径
 * - 路径组合时会自动处理斜杠分隔符
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class RequestMappingHandlerMapping implements HandlerMapping {

  /**
   * 请求映射缓存，key 为 "path:method" 格式
   */
  private final ConcurrentMap<String, RequestMappingInfo> mappingCache = new ConcurrentHashMap<String, RequestMappingInfo>();

  /**
   * 所有映射信息列表
   */
  private final List<RequestMappingInfo> allMappings = new ArrayList<RequestMappingInfo>();

  @Override
  public HandlerExecutionChain getHandler(String requestPath, RequestMethod requestMethod) {
    String key = createMappingKey(requestPath, requestMethod);
    RequestMappingInfo mappingInfo = mappingCache.get(key);

    if (mappingInfo != null) {
      return new HandlerExecutionChain(mappingInfo.getHandlerMethod());
    }

    // 如果直接匹配失败，尝试模式匹配
    for (RequestMappingInfo mapping : allMappings) {
      if (mapping.matches(requestPath, requestMethod)) {
        return new HandlerExecutionChain(mapping.getHandlerMethod());
      }
    }

    return null;
  }

  @Override
  public void registerMapping(RequestMappingInfo mappingInfo) {
    String key = createMappingKey(mappingInfo.getPath(), mappingInfo.getMethod());
    mappingCache.put(key, mappingInfo);
    allMappings.add(mappingInfo);
  }

  @Override
  public RequestMappingInfo[] getAllMappings() {
    return allMappings.toArray(new RequestMappingInfo[allMappings.size()]);
  }

  @Override
  public boolean hasMapping(String requestPath, RequestMethod requestMethod) {
    return getHandler(requestPath, requestMethod) != null;
  }

  /**
   * 扫描控制器类并注册请求映射
   * 
   * @param controllerClass    控制器类
   * @param controllerInstance 控制器实例
   */
  public void scanController(Class<?> controllerClass, Object controllerInstance) {
    if (!controllerClass.isAnnotationPresent(Controller.class)) {
      return;
    }

    // 获取类级别的 @RequestMapping 注解
    RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
    String classPath = classMapping != null ? classMapping.value() : "";

    // 扫描所有方法
    Method[] methods = controllerClass.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(RequestMapping.class)) {
        registerHandlerMethod(controllerInstance, method, classPath);
      }
    }
  }

  /**
   * 注册处理器方法
   * 
   * @param controllerInstance 控制器实例
   * @param method             处理器方法
   * @param classPath          类级别的路径
   */
  private void registerHandlerMethod(Object controllerInstance, Method method, String classPath) {
    RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
    if (methodMapping == null) {
      return;
    }

    // 组合完整路径
    String fullPath = combinePaths(classPath, methodMapping.value());
    RequestMethod requestMethod = methodMapping.method();

    // 创建处理器方法和映射信息
    HandlerMethod handlerMethod = new HandlerMethod(controllerInstance, method);
    RequestMappingInfo mappingInfo = new RequestMappingInfo(fullPath, requestMethod, handlerMethod);

    // 注册映射
    registerMapping(mappingInfo);
  }

  /**
   * 组合路径
   * 
   * @param classPath  类级别路径
   * @param methodPath 方法级别路径
   * @return 组合后的完整路径
   */
  private String combinePaths(String classPath, String methodPath) {
    if (!StringUtils.hasText(classPath)) {
      return normalizePath(methodPath);
    }

    if (!StringUtils.hasText(methodPath)) {
      return normalizePath(classPath);
    }

    // 确保路径以 / 开头
    String normalizedClassPath = normalizePath(classPath);
    String normalizedMethodPath = normalizePath(methodPath);

    // 组合路径，避免重复的斜杠
    if (normalizedClassPath.endsWith("/") && normalizedMethodPath.startsWith("/")) {
      return normalizedClassPath + normalizedMethodPath.substring(1);
    } else if (!normalizedClassPath.endsWith("/") && !normalizedMethodPath.startsWith("/")) {
      return normalizedClassPath + "/" + normalizedMethodPath;
    } else {
      return normalizedClassPath + normalizedMethodPath;
    }
  }

  /**
   * 规范化路径，确保以 / 开头
   * 
   * @param path 原始路径
   * @return 规范化后的路径
   */
  private String normalizePath(String path) {
    if (!StringUtils.hasText(path)) {
      return "/";
    }

    if (!path.startsWith("/")) {
      return "/" + path;
    }

    return path;
  }

  /**
   * 创建映射缓存的键
   * 
   * @param path   请求路径
   * @param method HTTP 方法
   * @return 缓存键
   */
  private String createMappingKey(String path, RequestMethod method) {
    return path + ":" + method;
  }

  /**
   * 获取映射数量
   * 
   * @return 映射数量
   */
  public int getMappingCount() {
    return allMappings.size();
  }

  /**
   * 清空所有映射
   */
  public void clearMappings() {
    mappingCache.clear();
    allMappings.clear();
  }
}
