package com.gem.baize.common.datasource.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gem.baize.common.datasource.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

@Slf4j
public class BaseEntityObjectHandler implements MetaObjectHandler {
    private final IdGeneratorProcessor idGeneratorProcessor;

    // 通过构造器注入（如果使用Spring）
    public BaseEntityObjectHandler(IdGeneratorProcessor idGeneratorProcessor) {
        log.info(">>> MetaObjectHandler 初始化");
        this.idGeneratorProcessor = idGeneratorProcessor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        //TODO 获取当前用户信息
        Long userId = 1L;
        // 1. 处理自动生成的ID
        idGeneratorProcessor.process(metaObject.getOriginalObject());

        this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);

        this.strictInsertFill(metaObject, "modifiedTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "status", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "deleted", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "version", Long.class, 1L);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
