package com.itheima.canal.utils;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanalColumnUtils {

    public static Map<String,Object> list2Map(List<CanalEntry.Column> beforeColumnsList){
        Map<String,Object> map = new HashMap<>();
        for (CanalEntry.Column column : beforeColumnsList) {
             map.put(column.getName(),column.getValue());
        }
        return map;
    }
}
