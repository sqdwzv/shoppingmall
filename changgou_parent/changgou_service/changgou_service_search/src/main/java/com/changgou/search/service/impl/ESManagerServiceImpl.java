package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class ESManagerServiceImpl implements ESManagerService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private ESManagerMapper esManagerMapper;

    //创建索引库结构
    @Override
    public void createMapperAndIndex() {
        //创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    //导入全部sku集合进入到索引库
    @Override
    public void importAll() {
        //查询sku集合
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        if (skuList == null || skuList.size()<=0){
            throw new RuntimeException("当前没有数据被查询到,无法导入索引库");
        }

        //skulist转换为json
        String jsonSkuList = JSON.toJSONString(skuList);
        //将json转换为skuinfo
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息转换为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }

        //导入索引库
        esManagerMapper.saveAll(skuInfoList);
    }
    //根据spuId将查询的数据导入到索引库
    @Override
    public void improtDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size()<=0){
            throw new RuntimeException("当前没有数据被检查到,无法导入到索引库");
        }
        //将skuList转换为json数据
        String jsonSkuList = JSON.toJSONString(skuList);
        //将json数据转换为SkuInfo
        List<SkuInfo> skuInfos = JSON.parseArray(jsonSkuList, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        //导入索引库
        esManagerMapper.saveAll(skuInfos);
    }
    //根据spuId删除索引库数据
    @Override
    public void delDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size()<=0 ){
            throw new RuntimeException("当前没有数据被检测到,删除索引库数据");
        }
        for (Sku sku : skuList) {
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
