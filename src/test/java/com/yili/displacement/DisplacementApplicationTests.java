package com.yili.displacement;

import com.alibaba.fastjson.JSON;
import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.config.MyEsConfig;
import com.yili.displacement.dao.AddressDao;
import com.yili.displacement.dao.WktDao;
import com.yili.displacement.service.LoadService;
import com.yili.displacement.service.SearchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("UUF_UNUSED_FIELD")
@SpringBootTest
@RunWith(SpringRunner.class)
class DisplacementApplicationTests {
  // private static Logger logger = LoggerFactory.getLogger(MatchAddressController.class);
  @Autowired Environment env;
  @Autowired private MyEsConfig config;
  @Autowired private SearchService searchService;
  @Autowired private LoadService loadService;
  private Client client;
  @Autowired private AddressDao addressDao;
  @Autowired private WktDao wktDao;
  @Autowired private ElasticsearchTemplate esTemplate;
  @Autowired private WebApplicationContext wac;
  @Autowired private ElasticsearchTemplate elasticsearchTemplate;

  @Before // 在测试开始前初始化工作
  public void setup() {}

  @Test
  public void searchHinghlight() {
    List<AddressVo> zhang = searchService.search("wuhouqu", "");

    for (AddressVo addressVo : zhang) {
      System.out.println(JSON.toJSON(addressVo));
    }
  }

  @Test
  public void bulkIndex() throws Exception {
    String path = "/home/shizeying/IdeaProjects/yili_displacement/src/main/resources/shp/";
    String charset = "GBK";
    List<AddressVo> addressVoList = loadService.getAddressList(path, charset);
    Long counter = loadService.putAddressList(addressVoList);
    Long aLong = loadService.putWktList(addressVoList, counter);
    System.err.println("最后结果----------------" + aLong);
  }

  @Test
  public void ById() {
    System.out.println(
        JSON.toJSON(wktDao.findById("77e24568ae7b4469ad13944eaee84418").get().getWkt()));
  }

  @Test
  public void setSearchService() {
    String query = "武侯";
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
      System.out.println(hit.getHighlightFields());
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
          entity.setWkt(wktDao.findById(entityMap.get("id").toString()).get().getWkt());
        }
      }
      list.add(entity);
    }
    list.forEach(
        addressVo -> {
          System.out.println(JSON.toJSON(addressVo));
        });
  }
}
