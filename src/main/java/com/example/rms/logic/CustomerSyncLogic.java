package com.example.rms.logic;

import com.example.rms.entity.CustomerReplica;
import java.util.List;

public interface CustomerSyncLogic {
    void processCustomerEvent(String eventId, String aggregateId, Long incomingVersion, String eventType, String payloadJson);
    List<CustomerReplica> getAllReplicas();
    CustomerReplica getReplicaById(String crmCustomerId);
}