package com.simplespring.context.testdata;

import com.simplespring.core.annotation.Component;

/**
 * 测试用的组件类
 */
@Component
public class TestComponent {

  public String getMessage() {
    return "Hello from TestComponent";
  }
}
