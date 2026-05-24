package com.gem.baize.system.notice.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.notice.entity.SysNotice;
import com.gem.baize.system.notice.mapper.SysNoticeMapper;
import com.gem.baize.system.notice.service.SysNoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SysNoticeServiceImpl.class)
class SysNoticeServiceImplTest {

    @MockBean
    private SysNoticeMapper sysNoticeMapper;

    @MockBean
    private SysNoticeConvert sysNoticeConvert;

    @Autowired
    private SysNoticeService sysNoticeService;

    @Test
    void shouldReturnNoticeWhenIdExists() {
        // Given
        String id = "1";
        SysNotice notice = new SysNotice();
        notice.setId(id);
        notice.setNoticeTitle("测试公告");
        notice.setNoticeContent("测试内容");

        SysNoticeDto dto = new SysNoticeDto();
        dto.setId(id);
        dto.setNoticeTitle("测试公告");

        when(sysNoticeMapper.selectById(id)).thenReturn(notice);
        when(sysNoticeConvert.toDto(notice)).thenReturn(dto);

        // When
        SysNoticeDto result = sysNoticeService.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNoticeTitle()).isEqualTo("测试公告");
    }

    @Test
    void shouldThrowExceptionWhenNoticeNotFound() {
        // Given
        String id = "999";
        when(sysNoticeMapper.selectById(id)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> sysNoticeService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("公告不存在");
    }

    @Test
    void shouldCreateNoticeSuccessfully() {
        // Given
        SysNoticeDto dto = new SysNoticeDto();
        dto.setNoticeTitle("新公告");
        dto.setNoticeContent("新内容");

        SysNotice entity = new SysNotice();
        entity.setNoticeTitle("新公告");

        when(sysNoticeConvert.toEntity(dto)).thenReturn(entity);
        when(sysNoticeMapper.insert(any(SysNotice.class))).thenReturn(1);

        // When
        sysNoticeService.create(dto);

        // Then
        verify(sysNoticeMapper, times(1)).insert(any(SysNotice.class));
    }

    @Test
    void shouldReturnPagedNotices() {
        // Given
        Page<SysNotice> page = new Page<>(1, 10);
        SysNoticeDto dto = new SysNoticeDto();
        dto.setNoticeTitle("测试");
        dto.setTenantId("1");

        Page<SysNoticeDto> expectedPage = new Page<>(1, 10);
        when(sysNoticeConvert.toDtoPage(any())).thenReturn(expectedPage);

        // When
        Page<SysNoticeDto> result = sysNoticeService.page(page, dto);

        // Then
        assertThat(result).isNotNull();
    }
}
