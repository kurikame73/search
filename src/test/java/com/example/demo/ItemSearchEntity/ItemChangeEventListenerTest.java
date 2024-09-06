//package com.example.demo.ItemSearchEntity;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//
//import java.io.IOException;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = "item-events", brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
//public class ItemChangeEventListenerTest {
//
//    private static final Logger log = LoggerFactory.getLogger(ItemChangeEventListenerTest.class);
//    @Autowired
//    private KafkaTemplate<String, ItemChangeEvent> kafkaTemplate;
//
//    @Mock
//    private SearchService searchService;
//
//    @InjectMocks
//    private ItemChangeEventListener itemChangeEventListener;
//
//    @Test
//    void testListenItemEvents() throws IOException {
//        // given
//        ItemChangeEvent event = new ItemChangeEvent(
//                123L,           // itemId
//                "Test Item",    // itemName
//                5000,           // itemPrice
//                "UPDATE",       // status
//                "Electronics",  // categoryName
//                "Test Brand"    // brand
//        );
//
//        log.info("Sending event: {}", event);
//
//        // when
//        itemChangeEventListener.listenItemEvents(event);
//
//        // then
//        verify(searchService, times(1)).handleItemChangeEvent(event);
//        log.info("Verified that SearchService handled the event");
//    }
//}