package com.yili.displacement.utils;

import com.yili.displacement.spacetype.PointBean;
import org.springframework.util.Assert;

/**
 * @author shizeying
 * @date 2019/11/21 07:30
 * @description 角度计算
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("FE_FLOATING_POINT_EQUALITY")
public class GeoToolsUtils {
  /** 地球半径 */
  private static final float EARTH_RADIUS = 6378137;
  /**
   * 根据中心点获取box
   *
   * @return
   */
  public static Double[] getBoxByCoordinate(Double longitude, Double latitude, Double raidusMile) {
    Assert.notNull(longitude);
    Assert.notNull(latitude);

    /** 获取每度弧长 */
    Double degree = (24901 * 1609) / 360.0;

    Double mpdLng =
        Double.parseDouble((degree * Math.cos(latitude * (Math.PI / 180)) + "").replace("-", ""));
    Double dpmLng = 1 / mpdLng;
    Double radiusLng = dpmLng * raidusMile;
    // 获取最小经度
    Double minLat = longitude - radiusLng;
    // 获取最大经度
    Double maxLat = longitude + radiusLng;

    Double dpmLat = 1 / degree;
    Double radiusLat = dpmLat * raidusMile;
    // 获取最小纬度
    Double minLng = latitude - radiusLat;
    // 获取最大纬度
    Double maxLng = latitude + radiusLat;
    System.out.println(minLat + ": " + maxLat + ": " + minLng + ": " + maxLng);
    return null;
  }

  /**
   * 获取距离
   *
   * @param from 开始点
   * @param to 结束点
   * @return
   */
  public static Double getDistance(PointBean from, PointBean to) {
    if (from == null
        || to == null
        || from.getLat() == null
        || from.getLng() == null
        || to.getLat() == null
        || to.getLng() == null) {
      return Double.NaN;
    } else {
      double lat1 = from.getLat() * Math.PI / 180.0;
      double lat2 = to.getLat() * Math.PI / 180.0;
      double a = lat1 - lat2;
      double b = (from.getLng() - to.getLng()) * Math.PI / 180.0;

      double sa2, sb2;
      sa2 = Math.sin(a / 2.0);
      sb2 = Math.sin(b / 2.0);
      return 2
          * EARTH_RADIUS
          * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
    }
  }

  public static double getAngle(double lat1, double lng1, double lat2, double lng2) {
    double x1 = lng1;
    double y1 = lat1;
    double x2 = lng2;
    double y2 = lat2;
    double pi = Math.PI;
    double w1 = y1 / 180 * pi;
    double j1 = x1 / 180 * pi;
    double w2 = y2 / 180 * pi;
    double j2 = x2 / 180 * pi;
    double ret;
    if (j1 == j2) {
      // 北半球的情况，南半球忽略
      if (w1 > w2) return 270;
      else if (w1 < w2) return 90;
      // 位置完全相同
      else return -1;
    }
    ret =
        4 * Math.pow(Math.sin((w1 - w2) / 2), 2)
            - Math.pow(Math.sin((j1 - j2) / 2) * (Math.cos(w1) - Math.cos(w2)), 2);
    ret = Math.sqrt(ret);
    double temp = (Math.sin(Math.abs(j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2)));
    ret = ret / temp;
    ret = Math.atan(ret) / pi * 180;
    // 1为参考点坐标
    if (j1 > j2) {
      if (w1 > w2) ret += 180;
      else ret = 180 - ret;
    } else if (w1 > w2) ret = 360 - ret;
    return ret;
  }

  /**
   * @param lat1 纬度1,参考点
   * @param lng1 经度1,参考点
   * @param lat2 纬度2
   * @param lng2 经度2
   * @return 方向
   */
  public static String getDirection(double lat1, double lng1, double lat2, double lng2) {
    double jiaodu = getAngle(lat1, lng1, lat2, lng2);
    if ((jiaodu <= 10) || (jiaodu > 350)) return "正东方";
    if ((jiaodu > 10) && (jiaodu <= 80)) return "东北方";
    if ((jiaodu > 80) && (jiaodu <= 100)) return "正北方";
    if ((jiaodu > 100) && (jiaodu <= 170)) return "西北方";
    if ((jiaodu > 170) && (jiaodu <= 190)) return "正西方";
    if ((jiaodu > 190) && (jiaodu <= 260)) return "西南方";
    if ((jiaodu > 260) && (jiaodu <= 280)) return "正南方";
    if ((jiaodu > 280) && (jiaodu <= 350)) return "东南方";
    return "";
  }
}
