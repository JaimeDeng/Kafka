package com.jaime.products.service;


import com.jaime.products.rest.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws Exception {

        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId, productRestModel.getTitle(), productRestModel.getPrice(),
                productRestModel.getQuantity());

        // 使用kafkaTemplate.send(topic名稱, key值, data)來傳送資料進入topic
        /**
         * 非同步執行方式
         * 1. 使用CompletableFuture定義一個回傳結果為SendResult的物件, 接取kafkaTemplate.send()執行結果
         * 2. 使用future.whenComplete()來接收執行完畢後的Result
         * 額外提示: future.join()可以將執行行為變為同步, 但因為已經使用future, 但又變成同步容易迷惑開發者, 因此不建議使用該方式
         */
//        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
//
//        future.whenComplete((result, exception) -> {
//
//            if(exception != null) {
//                LOGGER.error("****** Failed to send message: {}", exception.getMessage());
//            } else {
//                LOGGER.info("****** Message sent successfully: {}", result.getRecordMetadata());
//            }
//
//        });

        /**
         * 同步執行方式
         * 使用get()來獲取SendResult
         */
        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();

        LOGGER.info("Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("Offset: {}", result.getRecordMetadata().offset());

        LOGGER.info("****** Returning product id...");

        return productId;
    }

}
