package com.yili.displacement.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.StringUtils;

/**
 * @author shizeying
 * @date 2019/11/19 10:55
 * @description
 */
@Configuration
@EnableElasticsearchRepositories
@Slf4j
public class MyEsConfig {
  @Autowired Environment env;
  private Client esClient;

  @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
  @Bean
  public TransportClient client() {
    String clusterName = env.getProperty("spring.data.elasticsearch.cluster-name");
    String clusterNodes = env.getProperty("spring.data.elasticsearch.cluster-nodes");
    String IP = (clusterNodes.split(":"))[0];
    String port = (clusterNodes.split(":"))[1];
    TransportAddress node;
    TransportClient client = null;
    try {
      node = new TransportAddress(InetAddress.getByName(IP), Integer.valueOf(port));
      // 通过setting对象指定集群配置信息, 配置的集群名
      Settings setting =
          Settings.builder()
              // 设置集群名称
              .put("cluster.name", clusterName)
              // 忽略集群名字验证
              .put("client.transport.ignore_cluster_name", true)
              .build();
      client = new PreBuiltTransportClient(setting);
      client.addTransportAddress(node);
    } catch (UnknownHostException e) {
      log.error("elasticsearch 连接失败 !");
    }

    return client;
  }
  /** 避免TransportClient每次使用创建和释放 */
  public Client esTemplate() {
    if (StringUtils.isEmpty(esClient) || StringUtils.isEmpty(esClient.admin())) {
      esClient = client();
      return esClient;
    }
    return esClient;
  }
}
