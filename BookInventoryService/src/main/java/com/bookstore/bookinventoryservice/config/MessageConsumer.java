package com.bookstore.bookinventoryservice.config;

import com.bookstore.bookinventoryservice.dtos.BorrowAndReturnEvent;
import com.bookstore.bookinventoryservice.dtos.PublishEvent;
import com.bookstore.bookinventoryservice.service.InventoryService;
import com.bookstore.bookinventoryservice.service.InventoryTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.bookstore.bookinventoryservice.config.KafkaTopics.BOOK_PUBLISHED;
import static com.bookstore.bookinventoryservice.config.KafkaTopics.USER_BORROWED_BOOK;
import static com.bookstore.bookinventoryservice.config.KafkaTopics.USER_RETURNED_BOOK;

@Component
public class MessageConsumer {

    private ObjectMapper objectMapper;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @KafkaListener(topics = BOOK_PUBLISHED, groupId = "my-group-id")
    public void listenToPublishedBookMessage(String message) {
        logger.info("Message received for book published: {}", message);
        PublishEvent publishEvent = objectMapper.convertValue(message, PublishEvent.class);
        inventoryService.handleBookPublishedEvent(publishEvent);
    }

    @KafkaListener(topics = USER_BORROWED_BOOK, groupId = "inventory-group")
    public void listenToBookBorrowed(String message) {
        logger.info("Received Kafka message for user book action: {}", message);
        try {
            BorrowAndReturnEvent event = objectMapper.readValue(message, BorrowAndReturnEvent.class);
            inventoryTransactionService.handleBorrowAndReturnEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process borrow/return event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = USER_RETURNED_BOOK, groupId = "my-group-id")
    public void listenToBookReturned(String message) {
        logger.info("Message received for book returned: {}", message);
        try {
            BorrowAndReturnEvent event = objectMapper.readValue(message, BorrowAndReturnEvent.class);
            inventoryTransactionService.handleBorrowAndReturnEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process borrow/return event: {}", e.getMessage(), e);
        }
    }
}
