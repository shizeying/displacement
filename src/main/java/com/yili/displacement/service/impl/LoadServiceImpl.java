package com.yili.displacement.service.impl;

import com.yili.displacement.bean.AddressBean;
import com.yili.displacement.bean.AddressVo;
import com.yili.displacement.bean.WktBean;
import com.yili.displacement.dao.AddressDao;
import com.yili.displacement.service.LoadService;
import com.yili.displacement.utils.AddressVoUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * @author shizeying
 * @date 2019/11/20 18:48
 * @description
 */
@Service
public class LoadServiceImpl implements LoadService {
  @Autowired private ElasticsearchTemplate esTemplate;
  @Autowired private AddressDao dao;

  @Override
  public Long putAddressList(List<AddressVo> addressTemps) {
    assert addressTemps != null;
    int counter = 0;

    // 判断index 是否存在
    if (!esTemplate.indexExists(AddressBean.class)) {
      esTemplate.createIndex(AddressBean.class);
      esTemplate.putMapping(AddressBean.class);
    }

    List<IndexQuery> queries = new ArrayList<>();
    List<AddressBean> beans = AddressVoUtil.getInstance().getAddressBeans(addressTemps);

    if (beans != null && beans.size() > 0) {
      for (AddressBean bean : beans) {
        IndexQuery indexQuery =
            new IndexQueryBuilder()
                .withIndexName("address")
                .withType("address")
                .withId(bean.getId())
                .withObject(bean)
                .build();
        queries.add(indexQuery);

        // 分批提交索引
        if (counter % 2000 == 0) {
          esTemplate.bulkIndex(queries);
          queries.clear();
          System.out.println("bulkIndex counter : " + counter);
        }
        counter++;
      }
    }
    // 不足批的索引最后不要忘记提交
    if (queries.size() > 0) {
      esTemplate.bulkIndex(queries);
      counter += queries.size();
    }
    esTemplate.refresh(AddressBean.class);
    System.err.println("bulkIndex  counter : " + counter);
    return Long.valueOf(counter);
  }

  @Override
  public Long putWktList(List<AddressVo> addressTemps, long counter) {
    assert addressTemps != null;
    int counterWkt = 0;
    // 判断index 是否存在
    if (!esTemplate.indexExists(WktBean.class)) {
      esTemplate.createIndex(WktBean.class);
      esTemplate.putMapping(WktBean.class);
    }

    List<IndexQuery> queries = new ArrayList<>();

    List<WktBean> beans = AddressVoUtil.getInstance().getWktBeans(addressTemps);

    if (beans != null && beans.size() > 0) {
      for (WktBean bean : beans) {
        IndexQuery indexQuery =
            new IndexQueryBuilder()
                .withIndexName("wkt")
                .withType("wkt")
                .withId(bean.getId())
                .withObject(bean)
                .build();
        queries.add(indexQuery);

        // 分批提交索引
        if (counterWkt % 2000 == 0) {
          esTemplate.bulkIndex(queries);
          queries.clear();
          System.out.println("bulkIndex  counterWkt : " + counterWkt);
        }
        counterWkt++;
      }
    }
    // 不足批的索引最后不要忘记提交
    if (queries.size() > 0) {
      esTemplate.bulkIndex(queries);
      counterWkt += queries.size();
    }
    esTemplate.refresh(WktBean.class);
    System.err.println("bulkIndex  counterWkt : " + counterWkt);
    if (counter == counterWkt) {
      return Long.valueOf(counterWkt);
    }

    return (long) -1;
  }

  @Override
  public List<AddressVo> getAddressList(String path, String charSet) {
    assert path != null;
    assert charSet != null;
    List<AddressVo> beans = null;
    try {
      beans = AddressVoUtil.getInstance().getAddressBeans(path, charSet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return beans;
  }
}
