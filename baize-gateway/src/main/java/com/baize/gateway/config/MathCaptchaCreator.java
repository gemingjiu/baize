package com.baize.gateway.config;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * 数学表达式验证码生成器
 *
 * @gemj
 */
public class MathCaptchaCreator extends DefaultTextCreator {
    private static final String[] NUMBERS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    @Override
    public String getText() {
        // 计算式的结果
        int result = 0;

        Random random = new Random();

        int x = random.nextInt(10);

        int y = random.nextInt(10);

        StringBuilder text = new StringBuilder();

        // 随机运算符 取0 1 2
        int operatorRandom = random.nextInt(3);

        switch (operatorRandom) {
            case 0:
                result = x * y;
                text.append(NUMBERS[x]);
                text.append("*");
                text.append(NUMBERS[y]);
                break;
            case 1:
                // 除法失败情况较多，失败就改为+
                if (y != 0 && x % y == 0) {
                    result = x / y;
                    text.append(NUMBERS[x]);
                    text.append("/");
                    text.append(NUMBERS[y]);
                } else {
                    result = x + y;
                    text.append(NUMBERS[x]);
                    text.append("+");
                    text.append(NUMBERS[y]);
                }
                break;
            case 2:
                if (x >= y) {
                    result = x - y;
                    text.append(NUMBERS[x]);
                    text.append("-");
                    text.append(NUMBERS[y]);
                } else {
                    result = y - x;
                    text.append(NUMBERS[y]);
                    text.append("-");
                    text.append(NUMBERS[x]);
                }
                break;
            default:
                result = x + y;
                text.append(NUMBERS[x]);
                text.append("+");
                text.append(NUMBERS[y]);
        }

        // 返回运算表达式和结果，用@符号分割
        text.append("=?@" + result);

        return text.toString();
    }
}
