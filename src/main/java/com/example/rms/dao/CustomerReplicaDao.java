package com.example.rms.dao;

import com.example.rms.entity.CustomerReplica;
import com.example.rms.entity.ProcessedEvent;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CustomerReplicaDao {

    @Select
    Optional<ProcessedEvent> findProcessedEvent(String eventId);

    @Insert
    int insertProcessedEvent(ProcessedEvent event);

    @Select
    Optional<CustomerReplica> selectById(String crmCustomerId);

    @Select
    List<CustomerReplica> selectAll();

    @Insert
    int insert(CustomerReplica replica);

    @Update
    int update(CustomerReplica replica);
}