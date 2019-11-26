package com.changgou.search.controller;

import com.changgou.entity.Page;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/list")
    public String search(@RequestParam Map<String,String> searchMap , Model model) throws UnsupportedEncodingException {
        //执行查询返回值
        Map<String, String> resultMap = searchService.search(searchMap);
        //保存层级路径
        String url = "/search/list";
        StringBuilder stringBuilder = new StringBuilder(url);
        if (searchMap!=null &&searchMap.size()>0){
            stringBuilder.append("?");
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (!"sortType".equals(entry.getKey())&&!"sortFiled".equals(entry.getKey())&&!"pageNum".equals(entry.getKey())){

                    stringBuilder.append(entry.getKey());
                    stringBuilder.append("=");
                    stringBuilder.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
                    stringBuilder.append("&");
                }
            }
            //去除最够一个&
            stringBuilder.deleteCharAt(stringBuilder.toString().length()-1);
        }
        //封装数据
        model.addAttribute("resultMap",resultMap);
        model.addAttribute("searchMap",searchMap);
        //封装分页数据返回 1.总记录数 2.当前页 3.每页显示多少条
        Page<SkuInfo> skuInfoPage = new Page<SkuInfo>(
                Long.parseLong(String.valueOf(resultMap.get("total"))),
                Integer.parseInt(String.valueOf(resultMap.get("pageNum"))),
                        Page.pageSize
        );
        model.addAttribute("page",skuInfoPage);
        //向域中添加数据
        model.addAttribute("url", stringBuilder.toString());
        return "search";
    }




    @GetMapping
    @ResponseBody
    public Map search(@RequestParam Map<String,String> searchMap){
        //特殊符号处理
        //this.handleSearchMap(searchMap);
        Map<String, String> searchResult = searchService.search(searchMap);
        return searchResult;
    }

    private void handleSearchMap(Map<String, String> searchMap) {
        Set<Map.Entry<String, String>> entries = searchMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            //以spec开头
            if (entry.getKey().startsWith("spec_")){
                searchMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
            }
        }
    }
}
