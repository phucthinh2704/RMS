package com.example.rms.mapper;

import com.example.rms.dto.CustomerPayloadDto;
import com.example.rms.entity.CustomerReplica;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerReplicaMapper {

    @Mapping(target = "crmCustomerId", source = "aggregateId")
    @Mapping(target = "aggregateVersion", source = "incomingVersion")
    @Mapping(target = "syncedAt", expression = "java(java.time.LocalDateTime.now())")
    CustomerReplica toNewReplica(CustomerPayloadDto dto, String aggregateId, Long incomingVersion);

    @Mapping(target = "crmCustomerId", ignore = true)
    @Mapping(target = "aggregateVersion", source = "incomingVersion")
    @Mapping(target = "syncedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateReplicaFromDto(CustomerPayloadDto dto, Long incomingVersion, @MappingTarget CustomerReplica replica);
}