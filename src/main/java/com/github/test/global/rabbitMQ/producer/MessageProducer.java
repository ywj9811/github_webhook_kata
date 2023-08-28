package com.github.test.global.rabbitMQ.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.test.domain.github.dto.GithubPRRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageProducer {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Queue로 메시지를 발행
     *
     * @param prRequest 발행할 메시지의 DTO 객체
     */
    public void sendMessage(GithubPRRequest prRequest, String userId) throws JsonProcessingException {
        log.info("message sent: {}", prRequest.toString());
        prRequest.insertUser(userId);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, objectMapper.writeValueAsString(prRequest));
    }
}
