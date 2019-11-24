package com.yili.displacement.spacetype;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shizeying
 * @date 2019/11/22 21:09
 * @description Polygon 和 MULTIPOLYGON
 */
@Setter
@Getter
public class Polygon {
  private List<List<Double[]>> rings;
  private HashMap<String, Integer> spatialReference;
}
