package com.example.rms.entity;

import lombok.Data;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;
import java.time.LocalDateTime;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "processed_event")
public class ProcessedEvent {

    @Id
    public String eventId;

    public LocalDateTime processedAt;

    // 1. Constructor rỗng bắt buộc cho Doma 2
    public ProcessedEvent() {
    }

    // 2. Constructor 2 tham số để gọi nhanh trong CustomerSyncLogicImpl
    public ProcessedEvent(String eventId, LocalDateTime processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }
}