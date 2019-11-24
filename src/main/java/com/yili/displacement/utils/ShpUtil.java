package com.yili.displacement.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author shizeying
 * @date 2019/11/21 07:30
 * @description shp转换
 */
public class ShpUtil {

  /**
   * 获取全部shp文件地址
   *
   * @param path
   * @return
   */
  public Map<String, List<String>> getShpFiles(String path) {

    File file = new File(path);
    File[] listFiles = file.listFiles();
    Map<String, List<String>> map = new HashMap<>();

    List<String> listPaths = new ArrayList<>();
    for (File f : listFiles) {
      if (f.getName().length() < 5) {
        continue;
      }
      String type = f.getName().substring(f.getName().length() - 4, f.getName().length());
      if (type.equals(".shp")) {
        listPaths.add(f.getPath());
        String temp = getType().get(f.getPath().replace(".shp", "").replace(path, ""));
        map.put(temp, listPaths);
      }
    }

    return map;
  }

  /**
   * 获取文件类型
   *
   * @return
   */
  private Map<String, String> getType() {
    Map<String, String> map = new HashMap<>();
    map.put("0604_road", "道路数据");
    map.put("标注建筑物", "标注建筑物");
    map.put("POI", "poi");
    return map;
  }
}
