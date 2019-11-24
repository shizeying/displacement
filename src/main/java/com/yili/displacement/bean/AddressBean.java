package com.yili.displacement.bean;

import java.io.Serializable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author shizeying
 * @date 2019/11/19 10:55
 * @description
 */
@Setting(settingPath = "/json/address-setting.json")
@Mapping(mappingPath = "/json/address-mapping.json")
@Document(
    indexName = "address",
    type = "address",
    shards = 5,
    replicas = 0,
    refreshInterval = "1s",
    indexStoreType = "fs")
@Data
public class AddressBean implements Serializable {
  private static final long serialVersionUID = 2084886055991558779L;
  @Id private String id;

  @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
  private String name;
}
