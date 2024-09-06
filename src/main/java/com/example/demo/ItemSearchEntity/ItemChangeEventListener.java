package com.example.demo.ItemSearchEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemChangeEventListener {

    private SearchService searchService;

    @KafkaListener(topics = "item-events", groupId = "search-group")
    public void listenItemEvents(ItemChangeEvent event) throws IOException {
        log.info("Event Received: {}", event);
        searchService.handleItemChangeEvent(event);
    }
}
