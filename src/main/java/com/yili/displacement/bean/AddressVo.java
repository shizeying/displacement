package com.yili.displacement.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * @author shizeying
 * @date 2019/11/19 10:55
 * @description
 */
@Data
public class AddressVo implements Serializable {
  private static final long serialVersionUID = 2084886055991558779L;
  private String id;

  private String name;

  private Object wkt;
}
