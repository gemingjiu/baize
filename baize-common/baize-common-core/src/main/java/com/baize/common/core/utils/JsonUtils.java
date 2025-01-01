package com.baize.common.core.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 用Jackson代替Fastjson
 *
 * @author gemj
 * @since 2024/04/06 10:55
 */


public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    // 时间日期格式
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // 以静态代码块初始化
    static {
        // 对象的所有字段全部列入序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 所有的日期格式都统一为以下的格式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        // 忽略 在json字符串中存在，但在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T parseObject(String jsonString, Class<T> object) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, object);
        } catch (JsonProcessingException e) {
            logger.error("JsonString转为自定义对象失败：{}", e.getMessage());
        }
        return t;
    }

    public static <T> T parseObject(File file, Class<T> object) {
        T t = null;
        try {
            t = objectMapper.readValue(file, object);
        } catch (IOException e) {
            logger.error("从文件中读取json字符串转为自定义对象失败：{}", e.getMessage());
        }
        return t;
    }

    public static <T> T parseObject(String jsonString, JavaType javaType) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.error("从文件中读取json字符串转为自定义对象失败：{}", e.getMessage());
        }
        return t;
    }

    // 将json数组字符串转为指定对象List列表或者Map集合
    public static <T> T parseJSONArray(String jsonArray, TypeReference<T> reference) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonArray, reference);
        } catch (JsonProcessingException e) {
            logger.error("JSONArray转为List列表或者Map集合失败：{}", e.getMessage());
        }
        return t;
    }

    // 将json对象转为指定对象List列表或者Map集合
    public static <T> T parseJSONArray(JsonParser jsonParser, TypeReference<T> reference) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonParser, reference);
        } catch (IOException e) {
            logger.error("JSONArray转为List列表或者Map集合失败：{}", e.getMessage());
        }
        return t;
    }

    public static String toJSONString(Object object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Object转JSONString失败：{}", e.getMessage());
        }
        return jsonString;
    }

    public static byte[] toByteArray(Object object) {
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("Object转ByteArray失败：{}", e.getMessage());
        }
        return bytes;
    }

    public static void objectToFile(File file, Object object) {
        try {
            objectMapper.writeValue(file, object);
        } catch (JsonProcessingException e) {
            logger.error("Object写入文件失败：{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JsonNode和JSONObject一样，都是JSON树形模型，只不过在jackson中，存在的是JsonNode
    public static JsonNode parseJSONObject(String jsonString) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            logger.error("JSONString转为JsonNode失败：{}", e.getMessage());
        }
        return jsonNode;
    }

    public static JsonNode parseJSONObject(Object object) {
        JsonNode jsonNode = objectMapper.valueToTree(object);
        return jsonNode;
    }

    public static String toJSONString(JsonNode jsonNode) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            logger.error("JsonNode转JSONString失败：{}", e.getMessage());
        }
        return jsonString;
    }

    // JsonNode是一个抽象类，不能实例化，创建JSON树形模型，得用JsonNode的子类ObjectNode，用法和JSONObject大同小异
    public static ObjectNode newJSONObject() {
        return objectMapper.createObjectNode();
    }

    // 创建JSON数组对象，就像JSONArray一样用
    public static ArrayNode newJSONArray() {
        return objectMapper.createArrayNode();
    }

    public static String getString(JsonNode jsonObject, String key) {
        String s = jsonObject.get(key).asText();
        return s;
    }

    public static Integer getInteger(JsonNode jsonObject, String key) {
        Integer i = jsonObject.get(key).asInt();
        return i;
    }

    public static Boolean getBoolean(JsonNode jsonObject, String key) {
        Boolean bool = jsonObject.get(key).asBoolean();
        return bool;
    }

    public static JsonNode getJSONObject(JsonNode jsonObject, String key) {
        JsonNode json = jsonObject.get(key);
        return json;
    }

    public static boolean containsKey(JsonNode jsonObject, String key) {
        return jsonObject.has(key);
    }
}
