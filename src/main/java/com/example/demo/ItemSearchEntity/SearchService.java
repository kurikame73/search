package com.example.demo.ItemSearchEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper; // ObjectMapper를 주입받거나 생성

    public List<ProductSearchEntity> searchProducts(String query, String category, Double minPrice, Double maxPrice, String sortBy) {
        List<ProductSearchEntity> products = new ArrayList<>();

        try {
            // Elasticsearch 쿼리 빌드
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 기본 검색 조건 설정 (상품명 검색)
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("itemName", query));

            // 카테고리 필터 추가
            if (category != null) {
                boolQuery.filter(QueryBuilders.termQuery("categoryName.keyword", category));
            }

            // 가격 필터 추가
            if (minPrice != null && maxPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
            }

            // 정렬 추가
            if (sortBy != null) {
                searchSourceBuilder.sort(sortBy, SortOrder.ASC);
            }

            searchSourceBuilder.query(boolQuery);

            // Search Request 생성
            SearchRequest searchRequest = new SearchRequest("items");
            searchRequest.source(searchSourceBuilder);

            // Elasticsearch 검색 실행
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 검색 결과를 ProductSearchEntity로 매핑
            searchResponse.getHits().forEach(hit -> {
                try {
                    // JSON을 ProductSearchEntity로 변환
                    ProductSearchEntity product = objectMapper.readValue(hit.getSourceAsString(), ProductSearchEntity.class);
                    products.add(product);
                } catch (IOException e) {
                    log.error("Error converting search hit to ProductSearchEntity", e);
                }
            });

        } catch (IOException e) {
            log.error("Elasticsearch search failed", e);
        }

        return products;
    }

    public void handleItemChangeEvent(ItemChangeEvent event) throws IOException {
        log.info("Handling Item Change Event: {}", event);

        // IndexRequest 생성하는 함수
        Function<ItemChangeEvent, IndexRequest> buildIndexRequest = e -> new IndexRequest("items")
                .id(e.getItemId().toString())
                .source("itemName", e.getItemName(),
                        "itemPrice", e.getItemPrice(),
                        "status", e.getStatus(),
                        "categoryName", e.getCategoryName(),
                        "brand", e.getBrand());

        Consumer<IndexRequest> indexItem = request -> {
            try {
                client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Optional.ofNullable(event)
                .map(buildIndexRequest)
                .ifPresent(indexItem);
    }
}
