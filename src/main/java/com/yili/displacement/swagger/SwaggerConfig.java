package com.yili.displacement.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * @author shizeying
 * @date 2019/11/22 19:53
 * @description
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  /**
   * Create rest api docket.
   *
   * @return the docket
   */
  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(metaData())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.yili.displacement.controller"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo metaData() {

    return new ApiInfoBuilder()
        .title("API文档")
        .description("API文档")
        .termsOfServiceUrl("")
        .contact(new Contact("api", "", ""))
        .version("1.0")
        .build();
  }
}
