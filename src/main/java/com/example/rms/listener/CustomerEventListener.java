package com.example.rms.listener;

import com.example.rms.exception.InvalidPayloadException;
import com.example.rms.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerSyncService syncService;

    @RabbitListener(queues = "rms.customer.sync.queue")
    public void onCustomerEvent(
            Message message,
            @Header("event_id") String eventId,
            @Header("aggregate_id") String aggregateId,
            @Header("aggregate_version") Long aggregateVersion,
            @Header("event_type") String eventType) {

        String payloadJson = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            syncService.syncCustomer(eventId, aggregateId, aggregateVersion, eventType, payloadJson);
        } catch (InvalidPayloadException ex) {
            log.error("Dữ liệu Payload không hợp lệ, từ chối message event_id={}", eventId, ex);
            // Ném ngoại lệ này để báo RabbitMQ loại bỏ message lỗi, không đẩy ngược lại vào hàng đợi
            throw new AmqpRejectAndDontRequeueException(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Lỗi tạm thời khi đồng bộ event_id={}, hệ thống sẽ thử lại.", eventId, ex);
            // Với các lỗi tạm thời (như DB RMS bị nghẽn), ném lỗi ra để RabbitMQ tự động gửi lại sau
            throw ex;
        }
    }
}