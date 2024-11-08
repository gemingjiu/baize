package com.baize.system.service;

import java.util.List;

import com.baize.system.domain.SysId;

/**
 * 自增ID生成Service接口
 *
 * @author baize
 * @date 2024-05-13
 */
public interface ISysIdService {
    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    public SysId selectSysIdById(String id);

    /**
     * 查询自增ID生成列表
     *
     * @param sysId 自增ID生成
     * @return 自增ID生成集合
     */
    public List<SysId> selectSysIdList(SysId sysId);

    /**
     * 新增自增ID生成
     *
     * @param sysId 自增ID生成
     * @return 结果
     */
    public int insertSysId(SysId sysId);

    /**
     * 修改自增ID生成
     *
     * @param sysId 自增ID生成
     * @return 结果
     */
    public int updateSysId(SysId sysId);

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的自增ID生成主键集合
     * @return 结果
     */
    public int deleteSysIdByIds(String[] ids);

    /**
     * 删除自增ID生成信息
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    public int deleteSysIdById(String id);

    int updateSysIdStatus(SysId sysId);
}