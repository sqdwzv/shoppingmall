package cn.wangzhen;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列化和反序列化
 */
public class SerializableTest {
    @Test
    public void test01() {
        //将一下的List集合转化为Map集合。
        //Map的key是规格的名称，map的value是规格选项的List集合
        //eg：{"颜色"=['蓝色','黑色','金色','红色'],"版本":["6GB+128GB","6GB+64GB","4GB+64GB"]}

        List<String> list = new ArrayList<String>();
        list.add("{'颜色': '蓝色', '版本': '6GB+128GB'}");
        list.add("{'颜色': '蓝色', '版本': '6GB+64GB'}");
        list.add("{'颜色': '黑色', '版本': '4GB+64GB'}");
        list.add("{'颜色': '黑色', '版本': '6GB+128GB'}");
        list.add("{'颜色': '黑色', '版本': '6GB+64GB'}");
        list.add("{'颜色': '蓝色', '版本': '4GB+64GB'}");
        list.add("{'颜色': '黑色'}");
        list.add("{'颜色': '金色', '版本': '4GB+64GB'}");
        list.add("{'颜色': '蓝色'}");
        list.add("{'颜色': '红色'}");
        Map<String, List<String>> allMap = new HashMap<String, List<String>>();
        //1.遍历List集合
        for (String specJSON : list) {
            //2.将字符串反序列化为Map
            Map<String, String> specMap = JSON.parseObject(specJSON,Map.class);//{{'颜色': '黑色', '版本': '4GB+64GB'}
            //3.遍历Map集合
            for (String specKey : specMap.keySet()) {//key 颜色 版本
                String value = specMap.get(specKey);//value 黑色 4GB+64GB
                //如果不包含,就添加
                if (!allMap.containsKey(specKey)){
                    List<String> options = new ArrayList<String>();
                    options.add(value);
                    allMap.put(specKey,options);//{"颜色"=["蓝色"],"版本"=["6GB+128GB"]}
                }else {
                    List<String> options = allMap.get(specKey);
                    if (!options.contains(value)){
                        options.add(value);
                    }
                }
            }
        }
        System.out.println(allMap);
    }

    @Test
    public void objectPropertyCopy() {
        User user = new User("张三",20);
        //对象序列化和反序列化
        //对象序列化--->对象 JSON字符串
        String jsonString = JSON.toJSONString(user);
        System.out.println(jsonString);
        Object o = JSON.toJSON(user);
        System.out.println(o);
        //反序列化   JSON字符串--->对象
        UserInfo userInfo = JSON.parseObject(jsonString, UserInfo.class);
        System.out.println(userInfo);

    }
}
