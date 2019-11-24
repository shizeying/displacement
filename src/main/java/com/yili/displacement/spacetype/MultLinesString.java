package com.yili.displacement.spacetype;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shizeying
 * @date 2019/11/22 21:08
 * @description
 */
@Setter
@Getter
public class MultLinesString {
  private List<List<Double[]>> rings;
  private HashMap<String, Integer> spatialReference;
}
