package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ESManagerController {
    @Autowired
    private ESManagerService esManagerService;
    //创建索引库
    @GetMapping ("/create")
    public Result create(){
    esManagerService.createMapperAndIndex();
    return new Result(true, StatusCode.OK,"索引库创建成功");
    }
    //导入全部数据
    @GetMapping("/importAll")
    private Result importAll(){
        esManagerService.importAll();
        return new Result(true,StatusCode.OK,"导入全部数据成功");
    }
}
