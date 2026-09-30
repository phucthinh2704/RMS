package com.example.rms.service.impl;

import com.example.rms.entity.CustomerReplica;
import com.example.rms.logic.CustomerSyncLogic;
import com.example.rms.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerSyncServiceImpl implements CustomerSyncService {

    private final CustomerSyncLogic customerSyncLogic;

    @Override
    @Transactional // Ghi đè readOnly = false để cho phép INSERT/UPDATE vào customer_replica và
                   // processed_event
    public void syncCustomer(String eventId, String aggregateId, Long incomingVersion, String eventType,
            String payloadJson) {
        customerSyncLogic.processCustomerEvent(eventId, aggregateId, incomingVersion, eventType, payloadJson);
    }

    @Override
    public List<CustomerReplica> getAllReplicas() {
        return customerSyncLogic.getAllReplicas();
    }

    @Override
    public CustomerReplica getReplicaById(String crmCustomerId) {
        return customerSyncLogic.getReplicaById(crmCustomerId);
    }
}