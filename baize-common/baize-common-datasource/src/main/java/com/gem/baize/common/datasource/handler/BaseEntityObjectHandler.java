package com.gem.baize.common.datasource.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gem.baize.common.core.id.handle.IdGeneratorProcessor;
import com.gem.baize.common.datasource.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

@Slf4j
public class BaseEntityObjectHandler implements MetaObjectHandler {
    private final IdGeneratorProcessor idGeneratorProcessor;

    // 通过构造器注入（如果使用Spring）
    public BaseEntityObjectHandler(IdGeneratorProcessor idGeneratorProcessor) {
        this.idGeneratorProcessor = idGeneratorProcessor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        //TODO 获取当前用户信息
        String userId = "admin";
        // 1. 处理自动生成的ID
        idGeneratorProcessor.process(metaObject.getOriginalObject());

        this.strictInsertFill(metaObject, "createdBy", String.class, userId);
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "status", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "deleted", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "version", Long.class, 1L);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //TODO 获取当前用户信息
        String userId = "admin";
        this.strictInsertFill(metaObject, "modified_by", String.class, userId);
        this.strictUpdateFill(metaObject, "modifiedTime", LocalDateTime::now, LocalDateTime.class);
    }
}
