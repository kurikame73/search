//package com.example.demo.ItemSearchEntity;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.lucene.search.TotalHits;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.mockito.ArgumentMatchers.any;
//
//@Slf4j
//class SearchServiceTest {
//
//    @Mock
//    private RestHighLevelClient client;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private SearchService searchService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSearchProducts() throws IOException {
//        // given
//        String query = "Test Item";
//        String category = "Electronics";
//        Double minPrice = 100.0;
//        Double maxPrice = 500.0;
//        String sortBy = "price";
//
//        log.info("SearchResponse, SearchHits, SearchHit를 모킹 준비");
//
//        // 검색 응답(Mock)
//        SearchResponse searchResponse = mock(SearchResponse.class);
//        SearchHits searchHits = mock(SearchHits.class);
//        SearchHit searchHit = mock(SearchHit.class);
//
//        log.info("searchHit에서 JSON 문자열을 반환하도록 모킹");
//
//        // getSourceAsString()의 응답 모킹
//        String mockJsonResponse = "{\"id\":1, \"itemName\":\"Test Item\", \"price\":200.0}";
//        when(searchHit.getSourceAsString()).thenReturn(mockJsonResponse);
//        log.info("Mock된 searchHit sourceAsString: {}", mockJsonResponse);
//
//        log.info("searchHits.getHits()가 빈 배열이 아닌 값을 반환하도록 모킹");
//
//        // searchHits.getHits()가 값이 있는 배열을 반환하도록 설정
//        when(searchHits.getHits()).thenReturn(new SearchHit[]{searchHit});
//        when(searchHits.getTotalHits()).thenReturn(new TotalHits(1, TotalHits.Relation.EQUAL_TO));  // 총 히트 수 모킹
//        when(searchResponse.getHits()).thenReturn(searchHits);
//
//        log.info("Mock된 SearchHits의 총 히트 수: {}", searchHits.getTotalHits());
//
//        log.info("client가 searchResponse를 반환하도록 모킹");
//
//        // client가 모킹된 searchResponse를 반환하도록 설정
//        when(client.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(searchResponse);
//
//        log.info("ObjectMapper가 JSON을 ProductSearchEntity로 매핑하도록 모킹");
//
//        // ObjectMapper가 JSON을 ProductSearchEntity로 매핑하도록 설정
//        ItemSearchEntity product = new ItemSearchEntity();
//        product.setItemName("Test Item");
//        product.setPrice(200.0);
//        when(objectMapper.readValue(anyString(), eq(ItemSearchEntity.class))).thenReturn(product);
//
//        log.info("searchProducts 실행 - query: {}, category: {}, minPrice: {}, maxPrice: {}, sortBy: {}",
//                query, category, minPrice, maxPrice, sortBy);
//
//        // when
//        List<ItemSearchEntity> results = searchService.searchProducts(query, category, minPrice, maxPrice, sortBy);
//
//        log.info("검색 결과 크기: {}", results.size());
//        log.info("검색 결과 데이터: {}", results);
//
//        // then
//        assertEquals(1, results.size()); // 결과가 1개인지 확인
//        assertEquals("Test Item", results.get(0).getItemName());
//        assertEquals(200.0, results.get(0).getPrice());
//
//        // 상호작용 검증
//        verify(client, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));
//        verify(objectMapper, times(1)).readValue(anyString(), eq(ItemSearchEntity.class));
//
//        // SearchRequest 검증을 위한 로그 추가
//        ArgumentCaptor<SearchRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
//        verify(client).search(searchRequestCaptor.capture(), eq(RequestOptions.DEFAULT));
//        SearchRequest capturedRequest = searchRequestCaptor.getValue();
//
//        log.info("캡처된 SearchRequest 인덱스: {}", capturedRequest.indices()[0]);
//        log.info("캡처된 SearchRequest 쿼리: {}", capturedRequest.source().toString());
//
//        log.info("검색 결과 수: {}", results.size());
//        log.info("첫 번째 검색 결과: {}", results.isEmpty() ? "결과 없음" : results.get(0).toString());
//    }
//
//    @Test
//    void testHandleItemChangeEvent() throws IOException {
//        // given
//        ItemChangeEvent event = new ItemChangeEvent();
//        event.setItemId(1L);
//        event.setItemName("Test Item");
//        event.setItemPrice(200);
//        event.setStatus("AVAILABLE");
//        event.setCategoryName("Electronics");
//        event.setBrand("TestBrand");
//
//        // when
//        searchService.handleItemChangeEvent(event);
//
//        // then
//        verify(client, times(1)).index(any(IndexRequest.class), eq(RequestOptions.DEFAULT));
//    }
//}