package com.yili.displacement.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shizeying
 * @date 2019/11/22 21:15
 * @description
 */
@Getter
@Setter
public class ResponseBean {

  /** 状态码 */
  private int code;

  /** 数量 */
  private Long num;

  /** 操作信息 */
  private String msg;

  /** 返回的结果 */
  private Object results;
}
