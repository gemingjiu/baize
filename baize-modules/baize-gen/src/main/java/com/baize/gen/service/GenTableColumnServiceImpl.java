package com.baize.gen.service;

import com.baize.common.core.utils.ConvertUtils;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.gen.entity.GenTableColumn;
import com.baize.gen.mapper.GenTableColumnMapper;
import com.baize.gen.util.GenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gemj
 * @since 2023/12/08 00:44
 */
@Service
public class GenTableColumnServiceImpl implements IGenTableColumnService {
    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(String tableId) {
        return genTableColumnMapper.selectGenTableColumnListByTableId(tableId);
    }

    /**
     * 新增业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int insertGenTableColumn(GenTableColumn genTableColumn) {
        GenUtils.initColumnField(genTableColumn);
        return genTableColumnMapper.insertGenTableColumn(genTableColumn);
    }



    /**
     * 修改业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int updateGenTableColumn(GenTableColumn genTableColumn) {
        genTableColumn.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
        return genTableColumnMapper.updateGenTableColumn(genTableColumn);
    }

    /**
     * 删除业务字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteGenTableColumnByIds(String[] ids) {
        return genTableColumnMapper.deleteGenTableColumnByIds(ids);
    }
}
