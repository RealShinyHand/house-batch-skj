package com.fastcapus.housebatchskj.core.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class LawdDto {
    private Long lawdId;
    private String lawdCd;
    private String lawdDong;
    private Boolean exist;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
