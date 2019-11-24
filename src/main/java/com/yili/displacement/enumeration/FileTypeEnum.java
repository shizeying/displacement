package com.yili.displacement.enumeration;

import lombok.Getter;

/**
 * @author shizeying
 * @date 2019/11/20 14:34
 * @description
 */
public enum FileTypeEnum {
  ROAD("道路数据"),
  POI("POI"),
  BUILD("标注建筑物");

  @Getter private String _type;

  FileTypeEnum(String type) {
    _type = type;
  }

  /**
   * @param type
   * @return
   */
  public static FileTypeEnum getColor(String type) {
    switch (type) {
      case "道路数据":
        return FileTypeEnum.ROAD;
      case "标注建筑物":
        return FileTypeEnum.BUILD;
      default:
        return FileTypeEnum.POI;
    }
  }
}
