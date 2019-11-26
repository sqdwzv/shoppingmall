package com.changgou.search.service;

public interface ESManagerService {
    //创建索引库结构
    void createMapperAndIndex();
    //导入所以有数据到索引库
    void importAll();
    //根据SpuId导入数据到索引库
    void improtDataBySpuId(String spuId);
    //根据SpuId删除索引库数据
    void delDataBySpuId(String spuId);
}
