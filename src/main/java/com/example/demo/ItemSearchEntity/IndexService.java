package com.example.demo.ItemSearchEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class IndexService {

    private final RestHighLevelClient client;

    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    public void createIndex() throws IOException {
        log.info("Starting index creation...");

        CreateIndexRequest request = new CreateIndexRequest("items");
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        log.info("Index settings defined");

        client.indices().create(request, RequestOptions.DEFAULT);
        log.info("Index creation request sent to Elasticsearch");
    }
}

