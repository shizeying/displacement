package com.yili.displacement.spacetype;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shizeying
 * @date 2019/11/22 21:07
 * @description
 */
@Getter
@Setter
public class LineString {
  private List<List<Double[]>> paths;
  private HashMap<String, Integer> spatialReference;
}
