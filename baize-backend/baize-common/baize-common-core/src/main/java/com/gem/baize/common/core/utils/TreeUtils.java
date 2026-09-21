package com.gem.baize.common.core.utils;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形结构构建工具类
 */
public class TreeUtils {

    private TreeUtils() {
    }

    /**
     * 构建树形结构
     *
     * @param list            节点列表
     * @param idExtractor     ID提取器
     * @param parentIdExtractor 父ID提取器
     * @param childrenSetter  子节点设置器
     * @param <T>             节点类型
     * @return 树形结构根节点列表
     */
    public static <T> List<T> buildTree(List<T> list,
                                        Function<T, String> idExtractor,
                                        Function<T, String> parentIdExtractor,
                                        TreeChildrenSetter<T> childrenSetter) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 按parentId分组，O(n)复杂度
        Map<String, List<T>> parentIdMap = list.stream()
                .collect(Collectors.groupingBy(item -> {
                    String parentId = parentIdExtractor.apply(item);
                    return parentId == null ? "" : parentId;
                }));

        // 设置子节点，O(n)复杂度
        for (T item : list) {
            String id = idExtractor.apply(item);
            List<T> children = parentIdMap.getOrDefault(id, Collections.emptyList());
            childrenSetter.setChildren(item, children.isEmpty() ? null : children);
        }

        // 返回根节点（parentId为空或"0"的节点）
        return list.stream()
                .filter(item -> {
                    String parentId = parentIdExtractor.apply(item);
                    return parentId == null || parentId.isEmpty() || "0".equals(parentId);
                })
                .collect(Collectors.toList());
    }

    /**
     * 树形结构子节点设置器函数式接口
     */
    @FunctionalInterface
    public interface TreeChildrenSetter<T> {
        void setChildren(T node, List<T> children);
    }
}
