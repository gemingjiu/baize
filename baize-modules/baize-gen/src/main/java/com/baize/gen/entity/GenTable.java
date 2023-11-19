package com.baize.gen.entity;

import com.baize.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 代码生成业务表;
 * @author : gemingjiu
 * @date : 2023-9-11
 */
@ApiModel(value = "代码生成业务表",description = "")
public class GenTable  extends BaseEntity implements Serializable,Cloneable{
    /** 主键ID */
    @ApiModelProperty(name = "主键ID",notes = "")
    private String id ;
    /** 表名 */
    @ApiModelProperty(name = "表名",notes = "")
    private String tableName ;
    /**
     * 表描述
     */
    @ApiModelProperty(name = "表描述", notes = "")
    private String tableComment;
    /**
     * 关联表名
     */
    @ApiModelProperty(name = "关联表名", notes = "")
    private String relationTable;
    /**
     * 外键名
     */
    @ApiModelProperty(name = "外键名", notes = "")
    private String relationFkName;
    /**
     * 实体类名称
     */
    @ApiModelProperty(name = "实体类名称", notes = "")
    private String className;
    /**
     * 使用的模板
     */
    @ApiModelProperty(name = "使用的模板", notes = "")
    private String tplCategory;

    @ApiModelProperty(name= "Vue版本",notes = "")
    private String vueType;
    /**
     * 显示类型
     */
    @ApiModelProperty(name = "显示类型", notes = "")
    private String viewType;
    /**
     * 包路径
     */
    @ApiModelProperty(name = "包路径", notes = "")
    private String packageName;
    /**
     * 模块名
     */
    @ApiModelProperty(name = "模块名", notes = "")
    private String moduleName;
    /**
     * 业务名
     */
    @ApiModelProperty(name = "业务名", notes = "")
    private String businessName;
    /**
     * 功能名
     */
    @ApiModelProperty(name = "功能名",notes = "")
    private String functionName ;
    /** 作者 */
    @ApiModelProperty(name = "作者",notes = "")
    private String author ;
    /** 生成方式;0:gzip 1:自定义 */
    @ApiModelProperty(name = "生成方式",notes = "0:gzip 1:自定义")
    private String genType ;
    /** 生成路径 */
    @ApiModelProperty(name = "生成路径", notes = "")
    private String genPath ;
    /** 主键信息 */
    private GenTableColumn pkColumn;

    /** 子表信息 */
    private GenTable relationGenTable;

    /** 表列信息 */
    @Valid
    private List<GenTableColumn> columns;

    /** 其他生成选项 */
    @ApiModelProperty(name = "其他生成选项",notes = "")
    private String options ;
    /** 树编码字段 */
    private String treeCode;

    /** 树父编码字段 */
    private String treeParentCode;

    /** 树名称字段 */
    private String treeName;

    /** 上级菜单ID字段 */
    private String parentMenuId;

    /** 上级菜单名称字段 */
    private String parentMenuName;

    /** 主键ID */
    public String getId(){
        return this.id;
    }
    /** 主键ID */
    public void setId(String id){
        this.id=id;
    }
    /** 表名 */
    public String getTableName(){
        return this.tableName;
    }
    /** 表名 */
    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    /**
     * 表描述
     */
    public String getTableComment() {
        return this.tableComment;
    }

    /**
     * 表描述
     */
    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    /**
     * 关联表名
     */
    public String getRelationTable() {
        return this.relationTable;
    }

    /**
     * 关联表名
     */
    public void setRelationTable(String relationTable) {
        this.relationTable = relationTable;
    }
    /** 外键名 */
    public String getRelationFkName(){
        return this.relationFkName;
    }
    /** 外键名 */
    public void setRelationFkName(String relationFkName){
        this.relationFkName=relationFkName;
    }
    /** 实体类名称 */
    public String getClassName(){
        return this.className;
    }
    /** 实体类名称 */
    public void setClassName(String className){
        this.className=className;
    }
    /** 使用的模板 */
    public String getTplCategory(){
        return this.tplCategory;
    }
    /** 使用的模板 */
    public void setTplCategory(String tplCategory){
        this.tplCategory=tplCategory;
    }


    public String getVueType() {
        return vueType;
    }

    public void setVueType(String vueType) {
        this.vueType = vueType;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    /** 包路径 */
    public String getPackageName(){
        return this.packageName;
    }
    /** 包路径 */
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
    /** 模块名 */
    public String getModuleName(){
        return this.moduleName;
    }
    /** 模块名 */
    public void setModuleName(String moduleName){
        this.moduleName=moduleName;
    }
    /** 业务名 */
    public String getBusinessName(){
        return this.businessName;
    }
    /** 业务名 */
    public void setBusinessName(String businessName){
        this.businessName = businessName;
    }
    /** 功能名 */
    public String getFunctionName(){
        return this.functionName;
    }
    /** 功能名 */
    public void setFunctionName(String functionName){
        this.functionName=functionName;
    }
    /** 作者 */
    public String getAuthor(){
        return this.author;
    }
    /** 作者 */
    public void setAuthor(String author){
        this.author=author;
    }
    /** 生成方式;0:gzip 1:自定义 */
    public String getGenType(){
        return this.genType;
    }
    /** 生成方式;0:gzip 1:自定义 */
    public void setGenType(String genType){
        this.genType=genType;
    }
    /** 生成路径 */
    public String getGenPath(){
        return this.genPath;
    }
    /** 生成路径 */
    public void setGenPath(String genPath){
        this.genPath=genPath;
    }
    /** 其他生成选项 */
    public String getOptions() {
        return this.options;
    }

    /**
     * 其他生成选项
     */
    public void setOptions(String options) {
        this.options = options;
    }

    public GenTableColumn getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(GenTableColumn pkColumn) {
        this.pkColumn = pkColumn;
    }

    public GenTable getRelationGenTable() {
        return relationGenTable;
    }

    public void setRelationGenTable(GenTable relationGenTable) {
        this.relationGenTable = relationGenTable;
    }

    public List<GenTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<GenTableColumn> columns) {
        this.columns = columns;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getTreeParentCode() {
        return treeParentCode;
    }

    public void setTreeParentCode(String treeParentCode) {
        this.treeParentCode = treeParentCode;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public String getParentMenuName() {
        return parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }
}