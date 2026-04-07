package com.example.springboot.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    // 密钥，实际使用时应该从配置文件中读取
    private static final String KEY = "your-secret-key1";
    
    // 加密算法
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    
    /**
     * AES加密
     * @param data 待加密数据
     * @return 加密后的Base64编码字符串
     */
    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * AES解密
     * @param encryptedData 加密后的Base64编码字符串
     * @return 解密后的数据
     */
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    }
}