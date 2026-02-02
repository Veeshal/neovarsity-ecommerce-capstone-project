package com.capstone.ecommerce.notification.config;

import com.capstone.ecommerce.notification.model.OrderPlacedEvent;
import com.capstone.ecommerce.notification.model.UserRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private void setCommonProperties(Map<String, Object> configs) {
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "com.capstone.ecommerce.notification.model");
    }

    // ======================== Order Placed Event Consumer =========================

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> orderEventConsumerFactory(KafkaProperties properties) {
        var configs = properties.buildConsumerProperties();
        setCommonProperties(configs);
        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(OrderPlacedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> orderKafkaListenerFactory(
            ConsumerFactory<String, OrderPlacedEvent> orderEventConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>();
        factory.setConsumerFactory(orderEventConsumerFactory);
        return factory;
    }

    // =========================== User Event Consumer ==========================


    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> userEventConsumerFactory(KafkaProperties properties) {
        var configs = properties.buildConsumerProperties();
        setCommonProperties(configs);
        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(UserRegisteredEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> userKafkaListenerFactory(
            ConsumerFactory<String, UserRegisteredEvent> userEventConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>();
        factory.setConsumerFactory(userEventConsumerFactory);
        return factory;
    }


}
