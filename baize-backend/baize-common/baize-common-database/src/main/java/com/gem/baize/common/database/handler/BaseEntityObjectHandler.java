package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gem.baize.common.database.enums.StatusEnum;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

@Slf4j
public class BaseEntityObjectHandler implements MetaObjectHandler {
    /** 平台级租户ID：租户上下文缺失时的兜底值，避免 tenant_id NOT NULL 违约 */
    private static final String PLATFORM_TENANT = "0";

    @Override
    public void insertFill(MetaObject metaObject) {
        // 取当前用户信息
        String userId = RequestContextHolder.getUserId();
        String tenantId = RequestContextHolder.getTenantId();
        // strictInsertFill 仅在字段为 null 时填充；tenant_id 为 NOT NULL，空值必须兜底为平台租户
        if (StringUtils.isBlank(tenantId)) {
            tenantId = PLATFORM_TENANT;
        }

        this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
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

        // modifiedBy 是更新字段，必须用 strictUpdateFill；strictInsertFill 在 update 时不会写入
        this.strictUpdateFill(metaObject, "modifiedBy", String.class, userId);
        this.strictUpdateFill(metaObject, "modifiedTime", LocalDateTime::now, LocalDateTime.class);

        // status/deleted/version 由 DTO 直接携带时，updateById 的 SET 子句会引用这些字段；
        // 若前端表单未提交（或提交了 null），MyBatis-Plus 会把它们写成 NULL → 违反 NOT NULL。
        // 因此在更新时也兜底填充一次（strictUpdateFill 仅填 null 字段，不覆盖显式值）。
        // 注意：这里的兜底只能是「已删除」语义的合法值（deleted 必须回到未删除 '0'），
        // 而不能把 deleted 也兜底成 status 的 ENABLED——两者取值恰好都是 '0'，但语义不同。
        this.strictUpdateFill(metaObject, "status", StatusEnum.ENABLED::getValue, String.class);
        this.strictUpdateFill(metaObject, "deleted", StatusEnum.ENABLED::getValue, String.class);
        this.strictUpdateFill(metaObject, "version", Long.class, 1L);
    }
}
