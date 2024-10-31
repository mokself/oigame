package com.msr.oigame.config;

import com.msr.oigame.business.entity.base.SnowflakeIdSupport;
import com.msr.oigame.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

/**
 * 为实现 {@link SnowflakeIdSupport} 接口的实体生成雪花ID主键
 */
@Configuration
public class JdbcSnowflakeIdConvert {

    @Bean
    BeforeConvertCallback<SnowflakeIdSupport> beforeConvertCallback(RelationalMappingContext mappingContext) {
        return entity -> {
            RelationalPersistentEntity<?> persistentEntity = mappingContext.getRequiredPersistentEntity(entity.getClass());
            PersistentPropertyAccessor<SnowflakeIdSupport> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
            RelationalPersistentProperty idProperty = persistentEntity.getRequiredIdProperty();
            Object id = propertyAccessor.getProperty(idProperty);
            if (id == null || id.equals(0L)) {
                propertyAccessor.setProperty(idProperty, IdUtil.getSnowflakeNextId());
            }

            return entity;
        };
    }
}
