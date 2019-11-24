package com.yili.displacement.service;

import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.spacetype.PointBean;
import java.util.List;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.index.query.DisMaxQueryBuilder;

/**
 * @author shizeying
 * @date 2019/11/21 13:43
 * @description
 */
public interface SearchService {

  /**
   * 中文、拼音混合搜索
   *
   * @param content
   * @return
   */
  DisMaxQueryBuilder structureQuery(String content);

  public List<AddressVo> search(String name, String director);
  /**
   * 拼接搜索条件
   *
   * @param name
   * @param director
   * @return
   */
  List<AddressVo> searchHinghlight(String name, String director);

  /**
   * 高亮查询内容, query的值查询两个字段name, director。当然了你可以配置查询更多个字段或者你可以改成你所需查询的字段
   *
   * @param query
   * @return
   */
  public List<AddressVo> search(String query);

  /**
   * 调用 ES 获取 IK 分词后结果
   *
   * @param content
   * @param analyzerType
   * @return
   */
  public List<AnalyzeResponse.AnalyzeToken> getAnalyzeResult(String content, String analyzerType);

  /**
   * 查询距离
   *
   * @param from
   * @param to
   * @return
   */
  String searchDistance(PointBean from, PointBean to);
}
