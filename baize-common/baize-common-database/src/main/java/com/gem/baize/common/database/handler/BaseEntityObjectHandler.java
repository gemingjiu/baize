package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gem.baize.common.core.id.handle.IdGeneratorProcessor;
import com.gem.baize.common.database.enums.StatusEnum;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

@Slf4j
public class BaseEntityObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 取当前用户信息
        String userId = RequestContextHolder.getUserId();

        this.strictInsertFill(metaObject, "createdBy", String.class, userId);
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "status", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "deleted", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "version", Long.class, 1L);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 取当前用户信息
        String userId = RequestContextHolder.getUserId();

        this.strictInsertFill(metaObject, "modifiedBy", String.class, userId);
        this.strictUpdateFill(metaObject, "modifiedTime", LocalDateTime::now, LocalDateTime.class);
    }
}
