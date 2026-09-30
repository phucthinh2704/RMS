package com.example.rms.service;

import com.example.rms.entity.CustomerReplica;
import java.util.List;

public interface CustomerSyncService {
    void syncCustomer(String eventId, String aggregateId, Long incomingVersion, String eventType, String payloadJson);
    List<CustomerReplica> getAllReplicas();
    CustomerReplica getReplicaById(String crmCustomerId);
}