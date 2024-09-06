package com.example.demo.ItemSearchEntity;

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
import org.elasticsearch.search.SearchHit;
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

    public List<ItemSearchEntity> searchProducts(String query, String category, Double minPrice, Double maxPrice, String sortBy, Boolean isOnPromotion, String status) {
        List<ItemSearchEntity> products = new ArrayList<>();

        try {
            log.info("Elasticsearch 쿼리 빌드 시작 - query: {}, category: {}, minPrice: {}, maxPrice: {}, sortBy: {}, isOnPromotion: {}, status: {}",
                    query, category, minPrice, maxPrice, sortBy, isOnPromotion, status);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.multiMatchQuery(query, "itemName", "searchKeywords"));

            log.info("기본 검색 조건 설정 완료 - itemName 및 searchKeywords: {}", query);

            if (category != null) {
                boolQuery.filter(QueryBuilders.termQuery("categoryName.keyword", category));
                log.info("카테고리 필터 추가 완료 - category: {}", category);
            }

            if (minPrice != null && maxPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
                log.info("가격 필터 추가 완료 - minPrice: {}, maxPrice: {}", minPrice, maxPrice);
            }

            if (isOnPromotion != null) {
                boolQuery.filter(QueryBuilders.termQuery("isOnPromotion", isOnPromotion));
                log.info("프로모션 필터 추가 완료 - isOnPromotion: {}", isOnPromotion);
            }

            if (status != null) {
                boolQuery.filter(QueryBuilders.termQuery("status.keyword", status));
                log.info("상태 필터 추가 완료 - status: {}", status);
            }

            if (sortBy != null) {
                searchSourceBuilder.sort(sortBy, SortOrder.ASC);
                log.info("정렬 추가 완료 - sortBy: {}", sortBy);
            }

            searchSourceBuilder.query(boolQuery);
            log.info("완성된 쿼리: {}", searchSourceBuilder);

            SearchRequest searchRequest = new SearchRequest("items");
            searchRequest.source(searchSourceBuilder);

            log.info("SearchRequest 생성 완료 - 인덱스: items");

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("Elasticsearch 검색 완료 - 검색 결과 총 히트 수: {}", searchResponse.getHits().getTotalHits().value);

            for (SearchHit hit : searchResponse.getHits().getHits()) {
                log.info("검색 결과 개별 hit: {}", hit.toString());
                String hitSource = hit.getSourceAsString();
                log.info("검색 결과 hitSource: {}", hitSource);
                try {
                    ItemSearchEntity product = objectMapper.readValue(hitSource, ItemSearchEntity.class);
                    log.info("ItemSearchEntity 변환 성공: {}", product);
                    products.add(product);
                } catch (IOException e) {
                    log.error("검색 결과를 ItemSearchEntity로 변환하는 중 오류 발생: {}", e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Elasticsearch 검색 실패", e);
        }

        log.info("검색 결과 리스트 크기: {}", products.size());
        return products;
    }

    public void handleItemChangeEvent(ItemChangeEvent event) throws IOException {
        log.info("Handling Item Change Event: {}", event);

        Function<ItemChangeEvent, IndexRequest> buildIndexRequest = e -> new IndexRequest("items")
                .id(e.getItemId().toString())
                .source("id", e.getItemId(),
                        "itemName", e.getItemName(),
                        "categoryName", e.getCategoryName(),
                        "brand", e.getBrand(),
                        "price", e.getItemPrice()
//                        "stockQuantity", e.g(),
//                        "rating", e.getRating(),
//                        "sales", e.getSales(),
//                        "isOnPromotion", e.getIsOnPromotion(),
//                        "searchKeywords", e.getSearchKeywords(),
//                        "status", e.getStatus(),
//                        "detailImages", e.getDetailImages());
                );

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
