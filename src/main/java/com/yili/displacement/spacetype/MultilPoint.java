package com.yili.displacement.spacetype;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shizeying
 * @date 2019/11/22 21:05
 * @description MultiIPointObject
 */
@Getter
@Setter
public class MultilPoint {
  private List<Double[]> points;
  private HashMap<String, Integer> spatialReference;
}
