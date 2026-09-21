package com.gem.baize.system.job.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 定时任务调用工具：解析 invokeTarget 并执行
 * 支持两种目标格式：
 *   1. Spring Bean：beanName.methodName()，如 sysConfigService.refreshCache()
 *   2. 全类名反射：com.xxx.ClassName.methodName()，如 com.gem.baize.system.job.DemoTask.run()
 * 仅支持无参或单个基础类型参数的方法
 */
@Slf4j
@Component
public class SysJobInvokeUtils {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 执行目标任务
     */
    public void invokeMethod(String invokeTarget) throws Exception {
        if (StringUtils.isBlank(invokeTarget)) {
            throw new IllegalArgumentException("调用目标字符串不能为空");
        }
        String target = invokeTarget.trim();
        // 提取方法参数（若有）
        String methodName;
        String[] args = new String[0];
        int parenIndex = target.indexOf('(');
        if (parenIndex > 0 && target.endsWith(")")) {
            methodName = target.substring(0, parenIndex);
            String argStr = target.substring(parenIndex + 1, target.length() - 1).trim();
            if (StringUtils.isNotBlank(argStr)) {
                args = argStr.split(",");
            }
        } else {
            methodName = target;
        }

        int lastDot = methodName.lastIndexOf('.');
        if (lastDot < 0) {
            throw new IllegalArgumentException("调用目标格式错误，应为 beanName.method() 或 全类名.method()");
        }
        String classNameOrBean = methodName.substring(0, lastDot);
        String method = methodName.substring(lastDot + 1);

        Object bean = resolveBean(classNameOrBean);
        invoke(bean, method, args);
    }

    /**
     * 解析目标：优先按 Spring Bean 名称（首字母小写类名），否则按全类名反射实例化
     */
    private Object resolveBean(String classNameOrBean) throws Exception {
        // 尝试 Spring Bean（beanName 首字母小写）
        String beanName = uncapitalize(classNameOrBean.substring(classNameOrBean.lastIndexOf('.') + 1));
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName);
        }
        // 尝试按全类名实例化
        Class<?> clazz = Class.forName(classNameOrBean);
        try {
            // 优先从 Spring 容器获取（可能已注册）
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return clazz.getDeclaredConstructor().newInstance();
        }
    }

    private void invoke(Object bean, String methodName, String[] args) throws Exception {
        Class<?> clazz = bean.getClass();
        Method method = findMethod(clazz, methodName, args.length);
        method.setAccessible(true);
        Object[] params = convertArgs(method, args);
        method.invoke(bean, params);
        log.info("[job] 任务方法执行成功: {}.{}()", clazz.getName(), methodName);
    }

    private Method findMethod(Class<?> clazz, String methodName, int argCount) throws NoSuchMethodException {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == argCount) {
                return m;
            }
        }
        throw new NoSuchMethodException("未找到方法: " + clazz.getName() + "." + methodName + "(" + argCount + " 个参数)");
    }

    private Object[] convertArgs(Method method, String[] args) {
        Class<?>[] types = method.getParameterTypes();
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = convertValue(args[i].trim(), types[i]);
        }
        return result;
    }

    private Object convertValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        return value;
    }

    private String uncapitalize(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase(Locale.ROOT) + str.substring(1);
    }
}
