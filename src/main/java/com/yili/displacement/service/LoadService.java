package com.yili.displacement.service;

import com.yili.displacement.bean.AddressVo;
import java.util.List;

/**
 * @author shizeying
 * @date 2019/11/20 18:47
 * @description :保存
 */
public interface LoadService {

  /**
   * 批量增加address
   *
   * @param addressTemps
   * @return
   */
  public Long putAddressList(List<AddressVo> addressTemps);

  /**
   * 批量增加wkt
   *
   * @param addressTemps
   * @param counter
   * @return
   * @throws Exception
   */
  public Long putWktList(List<AddressVo> addressTemps, long counter) throws Exception;

  /**
   * 获取全部数据
   *
   * @param path
   * @param charSet
   * @return
   */
  public List<AddressVo> getAddressList(String path, String charSet);
}
