package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.AIChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/chat")
@Tag(name="AI聊天模块")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    @Operation(summary = "发送单条消息给AI", description = "通过后端中转调用大模型API")
    @PostMapping("/send")
    public Result sendMessage(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }
            String response = aiChatService.chat(message);
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI聊天失败: " + e.getMessage());
        }
    }

    @Operation(summary = "发送消息历史给AI", description = "通过后端中转调用大模型API，支持对话历史")
    @PostMapping("/history")
    public Result sendMessageWithHistory(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return Result.error("消息不能为空");
            }
            String response = aiChatService.chatWithHistory(messages);
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI聊天失败: " + e.getMessage());
        }
    }
}
