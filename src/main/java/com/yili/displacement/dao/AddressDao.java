package com.yili.displacement.dao;

import com.yili.displacement.bean.AddressBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author shizeying
 * @date 2019/11/19 10:55
 * @description
 */
public interface AddressDao extends ElasticsearchRepository<AddressBean, String> {}
