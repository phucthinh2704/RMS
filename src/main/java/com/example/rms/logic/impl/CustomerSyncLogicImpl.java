package com.example.rms.logic.impl;

import com.example.rms.dao.CustomerReplicaDao;
import com.example.rms.dto.CustomerPayloadDto;
import com.example.rms.entity.CustomerReplica;
import com.example.rms.entity.ProcessedEvent;
import com.example.rms.exception.InvalidPayloadException;
import com.example.rms.logic.CustomerSyncLogic;
import com.example.rms.mapper.CustomerReplicaMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerSyncLogicImpl implements CustomerSyncLogic {

    private final CustomerReplicaDao replicaDao;
    private final CustomerReplicaMapper replicaMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void processCustomerEvent(String eventId, String aggregateId, Long incomingVersion, String eventType,
            String payloadJson) {
        // 1. Kiểm tra Idempotency: Nếu event_id đã xử lý rồi thì bỏ qua ngay
        if (replicaDao.findProcessedEvent(eventId).isPresent()) {
            log.info("Bỏ qua event_id={} vì đã được xử lý trước đó.", eventId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Optional<CustomerReplica> existingOpt = replicaDao.selectById(aggregateId);

        // 2. Kiểm tra Version: Bỏ qua nếu version nhận được cũ hơn hoặc bằng version
        // đang lưu
        if (existingOpt.isPresent()) {
            CustomerReplica existing = existingOpt.get();
            if (incomingVersion <= existing.getAggregateVersion()) {
                log.warn("Bỏ qua event cũ: incomingVersion={} <= currentVersion={} của Customer {}",
                        incomingVersion, existing.getAggregateVersion(), aggregateId);
                replicaDao.insertProcessedEvent(new ProcessedEvent(eventId, now));
                return;
            }
        }

        // 3. Giải mã JSON Payload (Nếu JSON hỏng, ném InvalidPayloadException)
        CustomerPayloadDto payload;
        try {
            payload = objectMapper.readValue(payloadJson, CustomerPayloadDto.class);
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException("Lỗi cấu trúc JSON trong payload của event_id=" + eventId, e);
        }

        // 4. Lưu hoặc Cập nhật bản sao bằng MapStruct
        if (existingOpt.isEmpty()) {
            CustomerReplica newReplica = replicaMapper.toNewReplica(payload, aggregateId, incomingVersion);
            if ("CUSTOMER_DEACTIVATED".equals(eventType)) {
                newReplica.setActive(false);
            }
            replicaDao.insert(newReplica);
        } else {
            CustomerReplica existingReplica = existingOpt.get();
            if ("CUSTOMER_UPSERTED".equals(eventType)) {
                replicaMapper.updateReplicaFromDto(payload, incomingVersion, existingReplica);
            } else if ("CUSTOMER_DEACTIVATED".equals(eventType)) {
                existingReplica.setActive(false);
                existingReplica.setAggregateVersion(incomingVersion);
                existingReplica.setSyncedAt(now);
            }
            replicaDao.update(existingReplica);
        }

        // 5. Đánh dấu event_id đã xử lý xong
        replicaDao.insertProcessedEvent(new ProcessedEvent(eventId, now));
        log.info("Đồng bộ thành công Customer ID={}, version={}, eventType={}", aggregateId, incomingVersion,
                eventType);
    }

    @Override
    public List<CustomerReplica> getAllReplicas() {
        return replicaDao.selectAll();
    }

    @Override
    public CustomerReplica getReplicaById(String crmCustomerId) {
        return replicaDao.selectById(crmCustomerId)
                .orElseThrow(() -> new com.example.rms.exception.ResourceNotFoundException(
                        "Khách hàng có mã CRM ID '" + crmCustomerId
                                + "' chưa tồn tại hoặc chưa được phê duyệt đồng bộ sang RMS."));
    }
}