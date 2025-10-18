package com.simplespring.webmvc;

import com.simplespring.context.ApplicationContext;
import com.simplespring.core.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 分发器 Servlet，Spring MVC 框架的核心组件
 * 
 * DispatcherServlet 负责处理所有的 HTTP 请求，并将请求分发到相应的控制器方法。
 * 它是 Spring MVC 的前端控制器，实现了以下功能：
 * 
 * 1. 接收 HTTP 请求
 * 2. 根据请求路径和方法查找匹配的处理器
 * 3. 解析方法参数并调用处理器方法
 * 4. 处理方法返回值并生成响应
 * 5. 异常处理和错误响应
 * 
 * 请求处理流程：
 * 1. doDispatch() 方法接收请求
 * 2. HandlerMapping 查找匹配的处理器
 * 3. ParameterResolver 解析方法参数
 * 4. 调用控制器方法
 * 5. ViewResolver 处理返回值
 * 6. 生成 HTTP 响应
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class DispatcherServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * 应用上下文
   */
  private ApplicationContext applicationContext;

  /**
   * 处理器映射器
   */
  private HandlerMapping handlerMapping;

  /**
   * 参数解析器组合
   */
  private ParameterResolverComposite parameterResolver;

  /**
   * 视图解析器
   */
  private ViewResolver viewResolver;

  /**
   * 构造函数
   */
  public DispatcherServlet() {
    this.handlerMapping = new RequestMappingHandlerMapping();
    this.parameterResolver = new ParameterResolverComposite();
    this.viewResolver = new SimpleViewResolver();
  }

  /**
   * 构造函数，允许注入自定义组件
   * 
   * @param handlerMapping    处理器映射器
   * @param parameterResolver 参数解析器
   * @param viewResolver      视图解析器
   */
  public DispatcherServlet(HandlerMapping handlerMapping,
      ParameterResolverComposite parameterResolver,
      ViewResolver viewResolver) {
    this.handlerMapping = handlerMapping != null ? handlerMapping : new RequestMappingHandlerMapping();
    this.parameterResolver = parameterResolver != null ? parameterResolver : new ParameterResolverComposite();
    this.viewResolver = viewResolver != null ? viewResolver : new SimpleViewResolver();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doDispatch(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doDispatch(request, response);
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doDispatch(request, response);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doDispatch(request, response);
  }

  /**
   * 核心请求分发方法
   * 
   * @param request  HTTP 请求对象
   * @param response HTTP 响应对象
   * @throws ServletException 如果 Servlet 处理失败
   * @throws IOException      如果 I/O 操作失败
   */
  protected void doDispatch(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HandlerExecutionChain executionChain = null;
    Exception dispatchException = null;

    try {
      // 1. 获取请求路径和方法
      String requestPath = getRequestPath(request);
      RequestMethod requestMethod = getRequestMethod(request);

      // 2. 查找匹配的处理器
      executionChain = handlerMapping.getHandler(requestPath, requestMethod);

      if (executionChain == null) {
        handleNoHandlerFound(request, response, requestPath, requestMethod);
        return;
      }

      // 3. 解析方法参数
      HandlerMethod handlerMethod = executionChain.getHandler();
      Object[] args = parameterResolver.resolveParameters(handlerMethod, request, response);

      // 4. 调用处理器方法
      Object result = handlerMethod.invoke(args);

      // 5. 处理返回值
      handleReturnValue(result, request, response);

    } catch (Exception ex) {
      dispatchException = ex;
    }

    // 6. 处理异常
    if (dispatchException != null) {
      handleException(request, response, executionChain, dispatchException);
    }
  }

  /**
   * 获取请求路径
   * 
   * @param request HTTP 请求对象
   * @return 请求路径
   */
  private String getRequestPath(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String contextPath = request.getContextPath();

    if (contextPath != null && requestURI.startsWith(contextPath)) {
      return requestURI.substring(contextPath.length());
    }

    return requestURI;
  }

  /**
   * 获取请求方法
   * 
   * @param request HTTP 请求对象
   * @return 请求方法
   */
  private RequestMethod getRequestMethod(HttpServletRequest request) {
    String method = request.getMethod();

    if ("GET".equalsIgnoreCase(method)) {
      return RequestMethod.GET;
    } else if ("POST".equalsIgnoreCase(method)) {
      return RequestMethod.POST;
    } else if ("PUT".equalsIgnoreCase(method)) {
      return RequestMethod.PUT;
    } else if ("DELETE".equalsIgnoreCase(method)) {
      return RequestMethod.DELETE;
    } else if ("PATCH".equalsIgnoreCase(method)) {
      return RequestMethod.PATCH;
    } else if ("HEAD".equalsIgnoreCase(method)) {
      return RequestMethod.HEAD;
    } else if ("OPTIONS".equalsIgnoreCase(method)) {
      return RequestMethod.OPTIONS;
    }

    // 默认返回 GET
    return RequestMethod.GET;
  }

  /**
   * 处理找不到处理器的情况
   * 
   * @param request       HTTP 请求对象
   * @param response      HTTP 响应对象
   * @param requestPath   请求路径
   * @param requestMethod 请求方法
   * @throws IOException 如果 I/O 操作失败
   */
  private void handleNoHandlerFound(HttpServletRequest request, HttpServletResponse response,
      String requestPath, RequestMethod requestMethod) throws IOException {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    response.setContentType("text/plain; charset=UTF-8");

    PrintWriter writer = response.getWriter();
    writer.println("404 Not Found");
    writer.println("No handler found for " + requestMethod + " " + requestPath);
    writer.flush();
  }

  /**
   * 处理方法返回值
   * 
   * @param returnValue 方法返回值
   * @param request     HTTP 请求对象
   * @param response    HTTP 响应对象
   * @throws Exception 如果处理失败
   */
  private void handleReturnValue(Object returnValue, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if (viewResolver != null) {
      viewResolver.resolveView(returnValue, request, response);
    } else {
      // 如果没有视图解析器，直接输出返回值
      response.setContentType("text/plain; charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.print(returnValue != null ? returnValue.toString() : "");
      writer.flush();
    }
  }

  /**
   * 处理异常
   * 
   * @param request        HTTP 请求对象
   * @param response       HTTP 响应对象
   * @param executionChain 处理器执行链
   * @param exception      异常对象
   * @throws IOException 如果 I/O 操作失败
   */
  private void handleException(HttpServletRequest request, HttpServletResponse response,
      HandlerExecutionChain executionChain, Exception exception) throws IOException {

    // 记录异常日志
    System.err.println("Request processing failed: " + exception.getMessage());
    exception.printStackTrace();

    // 返回 500 错误响应
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    response.setContentType("text/plain; charset=UTF-8");

    PrintWriter writer = response.getWriter();
    writer.println("500 Internal Server Error");
    writer.println("Error processing request: " + exception.getMessage());
    writer.flush();
  }

  /**
   * 注册控制器
   * 
   * @param controllerClass    控制器类
   * @param controllerInstance 控制器实例
   */
  public void registerController(Class<?> controllerClass, Object controllerInstance) {
    if (handlerMapping instanceof RequestMappingHandlerMapping) {
      ((RequestMappingHandlerMapping) handlerMapping).scanController(controllerClass, controllerInstance);
    }
  }

  // Getter 和 Setter 方法

  public HandlerMapping getHandlerMapping() {
    return handlerMapping;
  }

  public void setHandlerMapping(HandlerMapping handlerMapping) {
    this.handlerMapping = handlerMapping;
  }

  public ParameterResolverComposite getParameterResolver() {
    return parameterResolver;
  }

  public void setParameterResolver(ParameterResolverComposite parameterResolver) {
    this.parameterResolver = parameterResolver;
  }

  public ViewResolver getViewResolver() {
    return viewResolver;
  }

  public void setViewResolver(ViewResolver viewResolver) {
    this.viewResolver = viewResolver;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
