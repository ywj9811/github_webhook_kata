package com.github.test.global.rabbitMQ.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.test.domain.github.dto.GithubPRRequest;
import com.github.test.domain.pr.PRRepository;
import com.github.test.domain.pr.entity.PR;
import com.github.test.domain.pr.utils.WebhookToPR;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageConsumer {
    private final ObjectMapper objectMapper;
    private final WebhookToPR webhookToPR;
    private final PRRepository prRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void receive(String request) throws JsonProcessingException {
        GithubPRRequest prRequest = objectMapper.readValue(request, GithubPRRequest.class);
        PR pr = webhookToPR.fromWebhook(prRequest);
        prRepository.save(pr);
        log.info("pr 저장");
        log.info("receive : {}", prRequest.toString());
    }
}
