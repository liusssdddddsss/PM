package com.example.springboot.util;

public class EncryptionTest {
    public static void main(String[] args) {
        try {
            // 测试BCrypt加密
            System.out.println("=== BCrypt加密测试 ===");
            String password = "123456";
            String encryptedPassword = BCryptUtil.encrypt(password);
            System.out.println("原始密码: " + password);
            System.out.println("BCrypt加密后: " + encryptedPassword);
            System.out.println("密码验证: " + BCryptUtil.matches(password, encryptedPassword));
            System.out.println("错误密码验证: " + BCryptUtil.matches("wrong", encryptedPassword));
            
            // 测试AES加密
            System.out.println("\n=== AES加密测试 ===");
            String email = "test@example.com";
            String encryptedEmail = AESUtil.encrypt(email);
            System.out.println("原始邮箱: " + email);
            System.out.println("AES加密后: " + encryptedEmail);
            System.out.println("AES解密后: " + AESUtil.decrypt(encryptedEmail));
            
            String avatar = "https://example.com/avatar.jpg";
            String encryptedAvatar = AESUtil.encrypt(avatar);
            System.out.println("\n原始头像: " + avatar);
            System.out.println("AES加密后: " + encryptedAvatar);
            System.out.println("AES解密后: " + AESUtil.decrypt(encryptedAvatar));
            
            System.out.println("\n=== 测试完成 ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
