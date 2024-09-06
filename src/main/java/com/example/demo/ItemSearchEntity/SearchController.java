package com.example.demo.ItemSearchEntity;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final RestHighLevelClient restHighLevelClient;


    private final SearchService searchService;

    @GetMapping("/search")
    public List<ItemSearchEntity> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean isOnPromotion,
            @RequestParam(required = false) String status) {
        log.info("Search query: {}, category: {}, minPrice: {}, maxPrice: {}, sortBy: {}, isOnPromotion: {}, status: {}",
                query, category, minPrice, maxPrice, sortBy, isOnPromotion, status);

        return searchService.searchProducts(query, category, minPrice, maxPrice, sortBy, isOnPromotion, status);
    }

    @PostMapping("/migrate")
    public ResponseEntity<String> migrateItemsToElasticsearch(@RequestBody List<ItemSearchEntity> items) {
        log.info("########################");

        try {
            BulkRequest bulkRequest = new BulkRequest();

            for (ItemSearchEntity item : items) {
                IndexRequest indexRequest = new IndexRequest("items")
                        .id(item.getId().toString())
                        .source(Map.of(
                                "itemName", item.getItemName(),
                                "categoryName", item.getCategoryName(),
                                "brand", item.getBrand(),
                                "price", item.getPrice(),
                                "stockQuantity", item.getStockQuantity(),
                                "sales", item.getSales(),
                                "isOnPromotion", item.getIsOnPromotion(),
                                "searchKeywords", item.getSearchKeywords(),
                                "status", item.getStatus(),
                                "detailImages", item.getDetailImages()
                        ));
                bulkRequest.add(indexRequest);
            }

            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("Failed to index some items: {}", bulkResponse.buildFailureMessage());
                return ResponseEntity.status(500).body("Failed to migrate some items to Elasticsearch");
            }

        } catch (Exception e) {
            log.error("Error occurred while migrating items", e);
            return ResponseEntity.status(500).body("Failed to migrate items to Elasticsearch");
        }

        return ResponseEntity.ok("Items successfully migrated to Elasticsearch");
    }
}
