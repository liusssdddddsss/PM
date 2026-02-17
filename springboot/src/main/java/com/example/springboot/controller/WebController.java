package com.example.springboot.controller;

import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class WebController {
    @GetMapping("/")
    public Result index(){
//        Result result = new Result();
//        result.setCode(200);
        HashMap<Object,Object> map = new HashMap<>();
        map.put("name","张三");
        return Result.success(map);
    }
}
