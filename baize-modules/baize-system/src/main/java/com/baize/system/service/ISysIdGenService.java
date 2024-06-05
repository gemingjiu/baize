package com.baize.system.service;

import java.util.List;

import com.baize.system.domain.SysIdGen;

/**
 * 自增ID生成Service接口
 *
 * @author baize
 * @date 2024-05-13
 */
public interface ISysIdGenService {
    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    public SysIdGen selectSysIdGenById(String id);

    /**
     * 查询自增ID生成列表
     *
     * @param sysIdGen 自增ID生成
     * @return 自增ID生成集合
     */
    public List<SysIdGen> selectSysIdGenList(SysIdGen sysIdGen);

    /**
     * 新增自增ID生成
     *
     * @param sysIdGen 自增ID生成
     * @return 结果
     */
    public int insertSysIdGen(SysIdGen sysIdGen);

    /**
     * 修改自增ID生成
     *
     * @param sysIdGen 自增ID生成
     * @return 结果
     */
    public int updateSysIdGen(SysIdGen sysIdGen);

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的自增ID生成主键集合
     * @return 结果
     */
    public int deleteSysIdGenByIds(String[] ids);

    /**
     * 删除自增ID生成信息
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    public int deleteSysIdGenById(String id);

    int updateSysIdGenStatus(SysIdGen sysIdGen);
}