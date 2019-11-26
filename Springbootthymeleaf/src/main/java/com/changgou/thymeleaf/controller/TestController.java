package com.changgou.thymeleaf.controller;

import com.changgou.thymeleaf.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/demo")
public class TestController {
    @RequestMapping("/hello")
    public String hello(Model model, String id){
        System.out.println(id);
        model.addAttribute("hello","hello welcome");
        return "hello";
    }
    @RequestMapping("/list")
    public String list(Model model){
        List<User> list = new ArrayList<>();
        list.add(new User(1,"zs","bj"));
        list.add(new User(2,"wu","xa"));
        list.add(new User(3,"zl","sh"));
        model.addAttribute("list",list);
        String [] names={"王振","北京","房子"};
        model.addAttribute("names",names);
        return "hello";
    }
    @RequestMapping("/map")
    public String map(Model model){
        Map<String,User> map = new HashMap<>();
        map.put("w",new User(1,"zs","bj"));
        map.put("q",new User(2,"wu","xa"));
        map.put("e",new User(3,"ss","sh"));
        model.addAttribute("map",map);
        return "hello";
    }
    @RequestMapping("/date")
    public String date(Model model){
        Date date = new Date();
        model.addAttribute("date",date);
        model.addAttribute("age",25);
        return "hello";
    }
}
