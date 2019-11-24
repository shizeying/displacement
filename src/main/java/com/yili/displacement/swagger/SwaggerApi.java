package com.yili.displacement.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author shizeying
 * @date 2019/11/22 19:53
 * @description
 */
@Controller
public class SwaggerApi {

  /**
   * Swagger ui string.
   *
   * @return the string
   */
  @GetMapping("/")
  public String swaggerUi() {
    return "redirect:/swagger-ui.html";
  }
}
