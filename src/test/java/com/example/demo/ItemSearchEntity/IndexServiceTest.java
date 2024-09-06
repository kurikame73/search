package com.example.demo.ItemSearchEntity;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class IndexServiceTest {

    @Mock
    private RestHighLevelClient client;

    @Mock
    private IndicesClient indicesClient;

    @InjectMocks
    private IndexService indexService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        log.info("Setting up mocks...");

        // client가 null이 아닌지 확인하는 추가 디버깅 코드
        if (client == null) {
            log.error("client is null in setUp");
        } else {
            log.info("client is initialized");
        }

        when(client.indices()).thenReturn(indicesClient); // client.indices()가 제대로 동작하도록 설정
        log.info("Mock setup for client.indices() completed");
    }

    @Test
    void testCreateIndex() throws IOException {
        log.info("Starting test for createIndex method");

        // Given
        CreateIndexResponse mockResponse = mock(CreateIndexResponse.class);
        when(indicesClient.create(any(CreateIndexRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(mockResponse);

        log.info("Mock setup for indicesClient.create() completed");

        // 실제 CreateIndexRequest 데이터 로그 남기기
        doAnswer(invocation -> {
            CreateIndexRequest request = invocation.getArgument(0);
            log.info("CreateIndexRequest Details: Index Name - {}, Settings - {}",
                    request.index(), request.settings().toString());
            return mockResponse;
        }).when(indicesClient).create(any(CreateIndexRequest.class), eq(RequestOptions.DEFAULT));

        // When
        indexService.createIndex();

        // Then
        log.info("Verifying interactions...");
        verify(client).indices();
        verify(indicesClient).create(any(CreateIndexRequest.class), eq(RequestOptions.DEFAULT));

        log.info("Test for createIndex completed successfully");
    }
}
