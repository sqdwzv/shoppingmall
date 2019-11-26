package com.changgou.search.service;

import java.util.Map;

public interface SearchService {
    /**
     * 根据条件查询数据
     * @param paramMap
     * @return
     */
    public Map<String,String> search(Map<String,String> paramMap);

}
