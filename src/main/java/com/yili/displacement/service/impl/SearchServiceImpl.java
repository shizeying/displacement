package com.yili.displacement.service.impl;

import com.alibaba.fastjson.JSON;
import com.yili.displacement.bean.AddressBean;
import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.bean.WktBean.Wkt;
import com.yili.displacement.config.MyEsConfig;
import com.yili.displacement.dao.AddressDao;
import com.yili.displacement.dao.WktDao;
import com.yili.displacement.service.SearchService;
import com.yili.displacement.spacetype.PointBean;
import com.yili.displacement.utils.AddressVoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author shizeying
 * @date 2019/11/21 14:01
 * @description
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
  @Autowired private AddressDao dao;

  @Autowired private MyEsConfig config;
  @Autowired private WktDao wktDao;
  /**
   * 中文、拼音混合搜索
   *
   * @param content the content
   * @return dis max query builder
   */
  @Override
  public DisMaxQueryBuilder structureQuery(String content) {
    // 使用dis_max直接取多个query中，分数最高的那一个query的分数即可
    DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
    // boost 设置权重,只搜索匹配name和disrector字段
    QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
    QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
    disMaxQueryBuilder.add(ikNameQuery);
    disMaxQueryBuilder.add(pinyinNameQuery);
    return disMaxQueryBuilder;
  }

  /**
   * 拼接搜索条件
   *
   * @param name the name
   * @param director the director
   * @return list
   */
  @Override
  public List<AddressVo> searchHinghlight(String name, String director) {
    List<AddressVo> list = search(name);

    return list;
  }

  /**
   * 拼接搜索条件
   *
   * @param name the name
   * @param director the director
   * @return list
   */
  @Override
  public List<AddressVo> search(String name, String director) {
    // 使用中文拼音混合搜索，取分数最高的，具体评分规则可参照：
    //  https://blog.csdn.net/paditang/article/details/79098830
    SearchQuery searchQuery =
        new NativeSearchQueryBuilder().withQuery(structureQuery(name)).build();
    List<AddressBean> list = dao.search(searchQuery).getContent();
    List<AddressVo> addressVoList = new ArrayList<>();
    list.forEach(
        addressBean -> {
          AddressVo addressVo = new AddressVo();
          addressVo.setId(addressBean.getId());
          addressVo.setName(addressBean.getName());
          addressVo.setWkt(getWkt(wktDao.findById(addressBean.getId()).get().getWkt()));
          addressVoList.add(addressVo);
        });
    return addressVoList;
  }

  /**
   * 高亮查询内容, query的值查询两个字段name。当然了你可以配置查询更多个字段或者你可以改成你所需查询的字段
   *
   * @param query the query
   * @return List<AddressBean> list
   */
  @Override
  public List<AddressVo> search(String query) {
    Client client = config.esTemplate();
    List<AddressVo> list = new ArrayList<>();
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    // 高亮显示规则
    highlightBuilder.preTags("<span style='color:green'>");
    highlightBuilder.postTags("</span>");
    // 指定高亮字段
    highlightBuilder.field("name");
    highlightBuilder.field("name.pinyin");
    String[] fileds = {"name", "name.pinyin"};
    QueryBuilder matchQuery = QueryBuilders.multiMatchQuery(query, fileds);
    // 搜索数据
    SearchResponse response =
        client
            .prepareSearch("address")
            .setQuery(matchQuery)
            .highlighter(highlightBuilder)
            .execute()
            .actionGet();

    SearchHits searchHits = response.getHits();
    System.out.println("记录数-->" + searchHits.getTotalHits());

    for (SearchHit hit : searchHits) {
      AddressVo entity = new AddressVo();
      Map<String, Object> entityMap = hit.getSourceAsMap();
      // 高亮字段
      if (!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
        Text[] text = hit.getHighlightFields().get("name").getFragments();
        entity.setName(text[0].toString());
      }
      if (!StringUtils.isEmpty(hit.getHighlightFields().get("name.pinyin"))) {
        Text[] text = hit.getHighlightFields().get("name.pinyin").getFragments();
        entity.setName(text[0].toString());
      }
      // map to object
      if (!CollectionUtils.isEmpty(entityMap)) {
        if (!StringUtils.isEmpty(entityMap.get("id"))) {
          entity.setId(entityMap.get("id").toString());
          entity.setWkt(getWkt(wktDao.findById(entityMap.get("id").toString()).get().getWkt()));
        }
      }
      list.add(entity);
    }
    return list;
  }

  private PointBean getWkt(Wkt wkt) {
    assert wkt != null;
    String toJSON = JSON.toJSON(wkt).toString();
    return AddressVoUtil.getInstance().toCenPoint(toJSON);
  }

  @Override
  public List<AnalyzeResponse.AnalyzeToken> getAnalyzeResult(String content, String analyzerType) {
    AnalyzeRequest analyzeRequest =
        new AnalyzeRequest("name")
            .text(content)
            .analyzer(Optional.ofNullable(analyzerType).orElse("ik_max_word"));
    List<AnalyzeResponse.AnalyzeToken> tokens =
        config.esTemplate().admin().indices().analyze(analyzeRequest).actionGet().getTokens();
    return tokens;
  }

  /**
   * 查询距离
   *
   * @param from
   * @param to
   * @return
   */
  @Override
  public String searchDistance(PointBean from, PointBean to) {
    String distance = AddressVoUtil.getInstance().toDistance(from, to);
    return distance;
  }
}
