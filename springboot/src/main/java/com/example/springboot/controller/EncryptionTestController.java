package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.util.AESUtil;
import com.example.springboot.util.BCryptUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class EncryptionTestController {

    @GetMapping("/encryption")
    public Result testEncryption() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 测试BCrypt加密
            String password = "123456";
            String encryptedPassword = BCryptUtil.encrypt(password);
            result.put("password", password);
            result.put("bcrypt_encrypted", encryptedPassword);
            result.put("bcrypt_matches", BCryptUtil.matches(password, encryptedPassword));
            
            // 测试AES加密
            String email = "test@example.com";
            String encryptedEmail = AESUtil.encrypt(email);
            result.put("email", email);
            result.put("aes_encrypted_email", encryptedEmail);
            result.put("aes_decrypted_email", AESUtil.decrypt(encryptedEmail));
            
            String avatar = "https://example.com/avatar.jpg";
            String encryptedAvatar = AESUtil.encrypt(avatar);
            result.put("avatar", avatar);
            result.put("aes_encrypted_avatar", encryptedAvatar);
            result.put("aes_decrypted_avatar", AESUtil.decrypt(encryptedAvatar));
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("测试失败: " + e.getMessage());
        }
    }
}
