package com.yili.displacement.controller;

import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.bean.ResponseBean;
import com.yili.displacement.service.LoadService;
import com.yili.displacement.service.SearchService;
import com.yili.displacement.spacetype.PointBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author shizeying
 * @date 2019/11/20 18:48
 * @description
 */
@Api(value = "controller", tags = "restApi")
@RestController
public class MatchAddressController {
  @Autowired private SearchService searchService;
  @Autowired private LoadService loadService;

  /**
   * 增量数据
   *
   * @param path
   * @param charSet
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "增量数据", response = ResponseBean.class)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "path", value = "增量数据", required = true, dataType = "string"),
    @ApiImplicitParam(name = "path", value = "增量数据", required = true, dataType = "string")
  })
  @RequestMapping(value = "put", method = RequestMethod.POST)
  public ResponseBean putList(
      @RequestParam(value = "path", required = true) String path,
      @RequestParam(value = "charSet", required = true) String charSet)
      throws Exception {
    ResponseBean responseBean = new ResponseBean();
    List<AddressVo> addressList = loadService.getAddressList(path, charSet);
    Long count = loadService.putAddressList(addressList);
    Long aLong = loadService.putWktList(addressList, count);
    if (aLong == -1) {
      responseBean.setCode(500);
      responseBean.setMsg("增量数据没有更新完成");
      responseBean.setResults(aLong);
      responseBean.setNum(aLong);
      return responseBean;
    }
    responseBean.setCode(200);
    responseBean.setMsg("增量成功");
    responseBean.setResults(aLong);
    responseBean.setNum(aLong);
    return responseBean;
  }

  @ApiOperation(value = "查询", response = ResponseBean.class)
  @ApiImplicitParam(name = "查询", required = true, dataType = "string")
  @RequestMapping(value = "getLikeList", method = RequestMethod.POST)
  public ResponseBean getLikeList(@RequestParam(value = "query", required = true) String query) {
    ResponseBean responseBean = new ResponseBean();
    List<AddressVo> beans = searchService.search(query);
    responseBean.setResults(beans);
    responseBean.setNum((long) beans.size());
    responseBean.setCode(200);
    return responseBean;
  }

  @ApiImplicitParam(name = "to", required = true, dataTypeClass = Map.class)
  @RequestMapping(value = "getTwoPointDis", method = RequestMethod.POST)
  public ResponseBean getTwoPoint(@RequestBody Map<String, PointBean> map) {
    ResponseBean responseBean = new ResponseBean();
    String distance = searchService.searchDistance(map.get("from"), map.get("to"));
    responseBean.setCode(200);
    if (StringUtils.isNumeric(distance)) {
      responseBean.setResults(Double.valueOf(distance));
      return responseBean;
    }
    responseBean.setResults(distance);
    return responseBean;
  }
}
