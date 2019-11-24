package com.yili.displacement.utils;

import com.vividsolutions.jts.io.ParseException;
import com.yili.displacement.bean.AddressBean;
import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.bean.WktBean;
import com.yili.displacement.enumeration.FileTypeEnum;
import com.yili.displacement.spacetype.PointBean;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author shizeying
 * @date 2019/11/19 10:55
 * @description
 */
@Slf4j
public class AddressVoUtil {

  private static AddressVoUtil instance;
  /**
   * 获取工具类实例
   *
   * @return
   */
  public static AddressVoUtil getInstance() {
    if (instance == null) {
      instance = new AddressVoUtil();
    }
    return instance;
  }

  public List<AddressBean> getAddressBeans(List<AddressVo> addressTemps) {
    List<AddressBean> beans = new ArrayList<>();
    addressTemps.forEach(
        address -> {
          AddressBean bean = new AddressBean();
          bean.setId(address.getId());
          bean.setName(address.getName());
          beans.add(bean);
        });
    return beans;
  }

  public List<WktBean> getWktBeans(List<AddressVo> addressTemps) {
    List<WktBean> beans = new ArrayList<>();
    addressTemps.forEach(
        address -> {
          WktBean bean = new WktBean();
          bean.setId(address.getId());
          bean.setWkt(WKTUtil.wkt2json(address.getWkt().toString()));
          beans.add(bean);
        });
    return beans;
  }

  /**
   * 解析空间shp获取所有字段对象
   *
   * @param path
   * @param charSet
   * @return
   * @throws Exception
   */
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("WMI_WRONG_MAP_ITERATOR")
  public List<AddressVo> getAddressBeans(String path, String charSet) throws Exception {
    ShpUtil shp = new ShpUtil();
    Map<String, List<String>> shpFiles = shp.getShpFiles(path);
    List<AddressVo> list = new ArrayList<>();
    for (String shpFileName : shpFiles.keySet()) {
      switch (FileTypeEnum.getColor(shpFileName)) {
        case POI:
          List<AddressVo> addressVoBeansPoi =
              _getAddressPointList(shpFiles.get(shpFileName), charSet);
          for (AddressVo addressVo : addressVoBeansPoi) {
            if (addressVo.getId() == null) {
              System.err.println("POI为null");
            }
            list.add(addressVo);
          }
          break;
        case ROAD:
          List<AddressVo> addressVoBeansRoad =
              _getAddressLineList(shpFiles.get(shpFileName), charSet);
          for (AddressVo addressVoBean : addressVoBeansRoad) {
            if (addressVoBean.getId() == null) {
              System.err.println("ROAD为null");
            }
            list.add(addressVoBean);
          }
          break;
        case BUILD:
          List<AddressVo> addressVoBeans =
              _getAddressPolygonList(shpFiles.get(shpFileName), charSet);
          addressVoBeans.forEach(
              addressBean -> {
                if (addressBean.getId() == null) {
                  System.err.println("BUILD为null");
                }
                list.add(addressBean);
              });
          break;
        default:
          break;
      }
    }
    log.info("getAddressBeans--------------" + list.size());
    return list;
  }

  private List<AddressVo> _getAddressPolygonList(List<String> polygonStrFile, String charSet)
      throws IOException, ParseException {
    System.out.println("polygonline开始啦-------------");
    List<AddressVo> lisPol = new ArrayList<>();
    for (String newPath : polygonStrFile) {
      File file = new File(newPath);
      FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
      ((ShapefileDataStore) dataStore).setCharset(Charset.forName(charSet));
      SimpleFeatureSource featureSource = dataStore.getFeatureSource();
      SimpleFeatureCollection features = featureSource.getFeatures();
      SimpleFeatureIterator iterator = features.features();
      while (iterator.hasNext()) {
        SimpleFeature next = iterator.next();
        String nameTemp = (String) next.getAttribute("FNAME");
        String name = StringUtils.contains(nameTemp, "/") ? nameTemp.replaceAll("/", "") : nameTemp;
        Object wkt;
        try {
          wkt = next.getDefaultGeometry().toString();
        } catch (NullPointerException e) {
          continue;
        }
        AddressVo bean = new AddressVo();
        if (StringUtils.isNotBlank(name)) {
          bean.setId(UUID.randomUUID().toString().replace("-", ""));
          bean.setName(name);
          bean.setWkt(wkt);
          lisPol.add(bean);
        }
      }

      iterator.close();
      features.features();
      dataStore.dispose();
    }
    log.info("_getAddressPolygonList-------" + lisPol.size());
    return lisPol;
  }

  private List<AddressVo> _getAddressLineList(List<String> lineStrFile, String charSet)
      throws IOException, ParseException {
    System.out.println("ROAD开始啦-------------");
    List<AddressVo> list = new ArrayList<>();
    for (String newPath : lineStrFile) {
      File file = new File(newPath);
      FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
      ((ShapefileDataStore) dataStore).setCharset(Charset.forName(charSet));
      SimpleFeatureSource featureSource = dataStore.getFeatureSource();
      SimpleFeatureCollection features = featureSource.getFeatures();
      SimpleFeatureIterator iterator = features.features();
      while (iterator.hasNext()) {
        SimpleFeature next = iterator.next();
        String name = (String) next.getAttribute("name");
        String wkt;
        try {
          wkt = next.getDefaultGeometry().toString();
        } catch (NullPointerException e) {
          continue;
        }
        AddressVo bean = new AddressVo();
        if (StringUtils.isNotBlank(name)) {
          bean.setId(UUID.randomUUID().toString().replace("-", ""));
          bean.setName(name);
          bean.setWkt(wkt);
          list.add(bean);
        }
      }
      iterator.close();
      features.features();
      dataStore.dispose();
    }
    log.info("_getAddressLineList-------" + list.size());

    return list;
  }

  private List<AddressVo> _getAddressPointList(List<String> pointStrFile, String charSet)
      throws IOException, ParseException {
    System.out.println("POI开始-------------");
    List<AddressVo> list = new ArrayList<>();
    for (String newPath : pointStrFile) {
      File file = new File(newPath);
      FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
      ((ShapefileDataStore) dataStore).setCharset(Charset.forName(charSet));
      SimpleFeatureCollection features = dataStore.getFeatureSource().getFeatures();
      SimpleFeatureIterator iterator = features.features();

      while (iterator.hasNext()) {
        SimpleFeature next = iterator.next();
        String name = (String) next.getAttribute("name");

        Object wkt;
        try {
          wkt = next.getDefaultGeometry().toString();
        } catch (NullPointerException e) {
          continue;
        }
        AddressVo bean = new AddressVo();
        if (StringUtils.isNotBlank(name)) {
          bean.setId(UUID.randomUUID().toString().replace("-", ""));
          bean.setName(name);
          bean.setWkt(wkt);
          list.add(bean);
        }
      }
      iterator.close();
      features.features();
      dataStore.dispose();
    }
    log.info("_getAddressPointList-------" + list.size());
    return list;
  }

  public PointBean toCenPoint(String wktJson) {
    assert wktJson != null;
    PointBean pointBean = new PointBean();
    try {
      pointBean.setLat(WKTUtil.json2wkt(wktJson).getX());
      pointBean.setLng(WKTUtil.json2wkt(wktJson).getY());
    } catch (ParseException e) {
      log.error("转换错误", e.getMessage());
    }
    return pointBean;
  }

  public String toDistance(PointBean from, PointBean to) {
    Double distance = GeoToolsUtils.getDistance(from, to);
    if (distance < 110) {
      String direction =
          GeoToolsUtils.getDirection(from.getLat(), from.getLng(), to.getLat(), to.getLng());
      return direction;
    }
    return String.valueOf(distance);
  }
}
