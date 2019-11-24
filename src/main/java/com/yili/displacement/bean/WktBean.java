package com.yili.displacement.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 * @author shizeying
 * @date 2019/11/22 21:45
 * @description
 */
@Mapping(mappingPath = "/json/wkt-mapping.json")
@Document(
    indexName = "wkt",
    type = "wkt",
    shards = 5,
    replicas = 0,
    refreshInterval = "1s",
    indexStoreType = "fs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WktBean {
  @Id private String id;

  @Field(index = false, type = FieldType.Object, store = true)
  private Wkt wkt;

  @Getter
  @Setter
  public static class Wkt {
    private String type;
    private Object coordinates;
  }
}
