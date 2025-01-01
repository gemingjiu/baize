package com.baize.system.domain;

import com.baize.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 通知公告表 sys_notice
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class SysNotice extends BaseEntity {
    private static final Long serialVersionUID = 1L;
    /**
     * 公告ID
     */
    private String id;
    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;
}
