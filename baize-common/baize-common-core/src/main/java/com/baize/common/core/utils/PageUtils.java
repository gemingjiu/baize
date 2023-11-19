package com.baize.common.core.utils;

import com.baize.common.core.domain.PageData;
import com.baize.common.core.domain.TableBuilder;
import com.baize.common.core.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
/**
 * @author gemj
 * @since 2023/12/08 14:42
 */
public class PageUtils {
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        PageData pageData = TableBuilder.buildPageRequest();
        Integer pageNum = pageData.getPageNum();
        Integer pageSize = pageData.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageData.getOrderBy());
        Boolean reasonable = pageData.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
