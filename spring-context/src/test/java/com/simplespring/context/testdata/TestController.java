package com.simplespring.context.testdata;

import com.simplespring.core.annotation.Controller;

/**
 * 测试用的控制器类
 */
@Controller("testController")
public class TestController {

  public String handleRequest() {
    return "Hello from TestController";
  }
}
