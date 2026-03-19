package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.OptionLog;
import com.example.springboot.service.OptionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/option-log")
@Tag(name = "操作日志模块")
public class OptionLogController {

    @Resource
    private OptionLogService optionLogService;

    @Operation(summary = "新增一条操作日志", description = "前端在用户完成操作后调用此接口记录最新动态")
    @PostMapping("/add")
    public Result add(@RequestBody OptionLog log) {
        try {
            OptionLog saved = optionLogService.save(log);
            return Result.success(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存操作日志失败: " + e.getMessage());
        }
    }
}

