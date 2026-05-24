package com.gem.baize.api.system.notice.domain.dto;

import lombok.Data;

@Data
public class SysNoticeDto {
    private String id;
    private String tenantId;
    private String noticeTitle;
    private String noticeContent;
    private String noticeType;
    private String status;
    private String remark;
}
