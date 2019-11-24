package com.yili.displacement.utils;

import com.alibaba.fastjson.JSONObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.yili.displacement.bean.WktBean.Wkt;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;

/**
 * @author shizeying
 * @date 2019/11/21 07:30
 * @description wkt操作
 */
@Slf4j
public class WKTUtil {

  private static final WKTReader wktReader = new WKTReader(JTSFactoryFinder.getGeometryFactory());

  /**
   * wkt格式的geometry转成json格式
   *
   * @param wktJson
   * @return
   */
  public static Wkt wkt2json(String wktJson) {
    String jsonWkt = null;
    try {
      Geometry geometry = wktReader.read(wktJson);
      StringWriter writer = new StringWriter();
      GeometryJSON g = new GeometryJSON();
      g.write(geometry, writer);
      jsonWkt = writer.toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    Wkt wkt = JSONObject.parseObject(jsonWkt, Wkt.class);
    return wkt;
  }

  @Test
  public void setWktReader() {
    String wktJson =
        "MULTIPOLYGON (((103.99658278871422 30.640481548310223, 103.9967252020378 30.640602359857393, 103.99707605271784 30.64027427515085, 103.99694079740011 30.640146769958328, 103.99658278871422 30.640481548310223)))";

    Wkt wkt = wkt2json(wktJson);
    System.out.println(wkt.getCoordinates());
  }

  /**
   * json格式转wkt格式
   *
   * @param geoJson
   * @return
   */
  public static Point json2wkt(String geoJson) throws ParseException {
    assert geoJson != null;
    String wkt = null;
    GeometryJSON gjson = new GeometryJSON();
    Reader reader = new StringReader(geoJson);
    try {
      Geometry geometry = gjson.read(reader);
      wkt = geometry.toText();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return getCenterPoint(wkt);
  }

  private static Point getCenterPoint(String wkt) throws ParseException {
    assert wkt != null;
    Geometry geometry = wktReader.read(wkt);
    Point centroid = geometry.getCentroid();
    return centroid;
  }
}
