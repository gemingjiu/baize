package com.baize.common.security.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import com.baize.common.core.utils.algorithm.Base64;

/**
 * RSA加密算法
 * 
 * @author gemj
 * @since 2024/05/08 20:15
 */
public class RSAEncryptUtil {

    /**
     * 参数分别代表
     */
    private static final String ALGORITHMS = "RSA";

    private static final String PRIVATEKEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY"
        + "7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKN"
        + "PuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gA"
        + "kM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWow"
        + "cSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99Ecv"
        + "DQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthh"
        + "YhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3" + "UP8iWi1Qw0Y=";

    /**
     * 生成公钥，私钥
     * 
     * @return 返回值
     * @throws NoSuchAlgorithmException
     */
    public static HashMap<String, String> generatePublicPrivateKeys() throws NoSuchAlgorithmException {
        HashMap<String, String> keys = new HashMap<>();
        // KeyPairGenerator:秘钥对生成器对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHMS);
        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 生成私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 生成公钥
        PublicKey publicKey = keyPair.getPublic();
        // 获取私销的字节数组
        byte[] privateKeyEncoded = privateKey.getEncoded();
        // 获取公销字节数组
        byte[] publicKeyEncoded = publicKey.getEncoded();
        // 使用base64进行编码
        String privateEncodeString = Base64.encode(privateKeyEncoded);
        String publicEncodeString = Base64.encode(publicKeyEncoded);
        // 打印公钥和私钥
        System.out.println("privateEncodeString=" + privateEncodeString);
        System.out.println("publicEncodeString=" + publicEncodeString);
        keys.put("privateKey", privateEncodeString);
        keys.put("publicKey", publicEncodeString);
        return keys;
    }

    /**
     *
     * @param content 未加密的字符串
     * @param publicKeyEncoded 公钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, byte[] publicKeyEncoded) throws Exception {
        // 创建key的工厂
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHMS);
        // 创建 已编码的公钥规格
        X509EncodedKeySpec encPubKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
        // 获取指定算法的密钥工厂, 根据 已编码的公钥规格, 生成公钥对象
        PublicKey publicKey = keyFactory.generatePublic(encPubKeySpec);
        // 获取指定算法的密码器
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        // 初始化密码器（公钥加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 加密数据, 返回加密后的密文
        byte[] cipherData = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encode(cipherData);
    }

    /**
     *
     * @param encodeEncryptString 未解密的字符串
     * @param privateKeyEncoded 私钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String encodeEncryptString, byte[] privateKeyEncoded) throws Exception {
        // 创建key的工厂
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHMS);
        // 创建 已编码的私钥规格
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
        // 获取指定算法的密钥工厂, 根据 已编码的私钥规格, 生成私钥对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 获取指定算法的密码器
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        // 初始化密码器（私钥解密模型）
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 解密数据, 返回解密后的明文
        byte[] plainData = cipher.doFinal(Base64.decode(encodeEncryptString));
        // 采用base64算法进行转码,避免出现中文乱码
        return new String(plainData, StandardCharsets.UTF_8);
    }

    // 测试方法
    public static void main(String[] args) throws Exception {
        byte[] decode = Base64.decode(PRIVATEKEY);
        decrypt("", decode);

        // generatePublicPrivateKeys();
    }

}
