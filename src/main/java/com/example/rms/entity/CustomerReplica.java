package com.example.rms.entity;

import lombok.Data;
import org.seasar.doma.*;
import org.seasar.doma.jdbc.entity.NamingType;
import java.time.LocalDateTime;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "customer_replica")
public class CustomerReplica {
    @Id
    public String crmCustomerId;
    public String name;
    public String email;
    public String phone;
    public Boolean active;
    public Long aggregateVersion;
    public LocalDateTime syncedAt;
}