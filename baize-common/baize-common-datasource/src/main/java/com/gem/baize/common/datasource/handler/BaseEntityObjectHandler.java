package com.gem.baize.common.datasource.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gem.baize.common.datasource.enums.StatusEnum;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseEntityObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //TODO 获取当前用户信息
        String userName = "admin";
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "modifiedTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "status", StatusEnum.ENABLED::getValue, String.class);
        this.strictInsertFill(metaObject, "deleted", StatusEnum.ENABLED::getValue, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);

    }
}
