package com.example.demo.ItemSearchEntity;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
@Slf4j
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient client() {
        log.info("%%%%%%%%%%%%%%%%ElasticsearchConfig%%%%%%%%%%%%%%%%%%");
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }
}
