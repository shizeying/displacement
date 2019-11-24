package com.yili.displacement.spacetype;

import lombok.Data;
/**
 * @author shizeying
 * @date 2019/11/21 07:21
 * @description POINT
 */
@Data
public class PointBean {
  /** 经度 */
  private Double lat;
  /** 纬度 */
  private Double lng;

  public PointBean(Double lat, Double lng) {
    super();
    this.lat = lat;
    this.lng = lng;
  }

  public PointBean() {
    super();
  }
}
