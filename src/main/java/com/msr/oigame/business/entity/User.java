package com.msr.oigame.business.entity;

import com.msr.oigame.business.entity.base.SnowflakeIdSupport;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table
public class User implements SnowflakeIdSupport {
    @Id
    private Long id;

    private String key;

    private String lastLoginIp;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
