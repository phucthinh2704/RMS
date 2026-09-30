package com.example.rms.controller;

import com.example.rms.entity.CustomerReplica;
import com.example.rms.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rms/customers")
@RequiredArgsConstructor
public class CustomerReplicaController {

    private final CustomerSyncService customerSyncService;

    @GetMapping
    public ResponseEntity<List<CustomerReplica>> getAllSyncedCustomers() {
        return ResponseEntity.ok(customerSyncService.getAllReplicas());
    }

    @GetMapping("/{crmCustomerId}")
    public ResponseEntity<CustomerReplica> getSyncedCustomerById(@PathVariable String crmCustomerId) {
        return ResponseEntity.ok(customerSyncService.getReplicaById(crmCustomerId));
    }
}