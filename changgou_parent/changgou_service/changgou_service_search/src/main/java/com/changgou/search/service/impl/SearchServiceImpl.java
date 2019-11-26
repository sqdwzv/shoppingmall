package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Override
    public Map search(Map<String, String> paramMap) {

        Map<String,Object> resultMap = new HashMap<>();
        //构建查询
        if (paramMap!=null){
            //构建查询条件封装对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            //按照关键字查询
            if (StringUtils.isNotEmpty(paramMap.get("keywords"))) {
                //不为空 分词关键字查询  并且
                boolQuery.must(QueryBuilders.matchQuery("name",paramMap.get("keywords")).operator(Operator.AND));
            }

            //按照品牌进行过滤查询
            if (StringUtils.isNotEmpty(paramMap.get("brand"))){
                                        //不分词
                boolQuery.filter(QueryBuilders.termQuery("brandName",paramMap.get("brand")));
            }
            //must:关键字,词查询
            //filter:范围查询:时间范围:价格查询 官方推荐

            //按照规格进行查询
            for (String key : paramMap.keySet()) {
                if (key.startsWith("spec_")){
                    String value = paramMap.get(key).replace("%2B","+");
                    //spec_网络制式
                    boolQuery.filter(QueryBuilders.termQuery(("specMap."+key.substring(5)+".keyword"),value));
                }
            }

            //按照价格区间进行查询
            if (StringUtils.isNotEmpty(paramMap.get("price"))){
                String[] prices = paramMap.get("price").split("-");
                //0-500 100-200 900
                if (prices.length==2){
                    //                 es中的域名                       lte小于 gte大于
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]).gte(prices[0]));
                }else {
                    boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
                }
            }
            //上面查询
            //查询总条件
            nativeSearchQueryBuilder.withQuery(boolQuery);
            //下面展示

            //按照品牌进行分组(聚合)查询
                //当前聚合结果的列名
            String skuBrand="skuBrand";
            //设置分组域
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            //按照规格进行聚合查询
            String skuSpec="skuSpec";
            //设置分组域
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            //开启分页功能
            String pageNum = paramMap.get("pageNum");//当前页
            String pageSize = paramMap.get("pageSize");//每页显示条数
            if (StringUtils.isEmpty(pageNum)){
                pageNum = "1";
            }
            if (StringUtils.isEmpty(pageSize)){
                pageSize = "30";
            }
            //设置分页
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize)));

            //排序
            //1.接收前段传来的排序域名和排序方式
            String  sortFiled = paramMap.get("sortFiled");
            String  sortType  = paramMap.get("sortType");
            if (StringUtils.isNotEmpty(sortFiled) && StringUtils.isNotEmpty(sortType)){
                if (sortType.equals("ASC")){//升序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortFiled).order(SortOrder.ASC));
                }else if (sortType.equals("DESC")){//降序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortFiled).order(SortOrder.DESC));
                }
            }
            //高亮
            HighlightBuilder.Field field = new HighlightBuilder.Field("name")//高亮域
                                                .preTags("<span  style='color:red'>")//高亮前缀
                                                .postTags("</span>");//高亮后缀
            nativeSearchQueryBuilder.withHighlightFields(field);


            //开启查询
            /**
             * 第一个参数:条件构建对象
             * 第二个参数:查询操作实体类
             * 第三个参数:查询结果操作对象
             */
            AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    long totalHits = searchResponse.getHits().getTotalHits();//总记录数
                    //查询结果的相关操作
                    List<T> list = new ArrayList<>();
                    //查询结果命中数据
                    SearchHits hits = searchResponse.getHits();
                    if (hits!=null){
                        //有查询结果
                        for (SearchHit hit : hits) {
                            //转换SKUinfo对象  得到JSON串
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

                            //获取所有高亮域
                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                           if (highlightFields.size()>0 && highlightFields!=null){
                               //替换数据
                                skuInfo.setName(highlightFields.get("name").getFragments()[0].string());
                           }

                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
                }
            });
            //封装最终返回结果
                //总记录数
            resultMap.put("total",skuInfos.getTotalElements());
            //总页数
            resultMap.put("totalPage",skuInfos.getTotalPages());
            //数据集合
            resultMap.put("rows",skuInfos.getContent());
            //当前页
            resultMap.put("pageNum",pageNum);


            //封装品牌的分组结果
            StringTerms brandTerms = (StringTerms) skuInfos.getAggregation(skuBrand);

            //封装品牌的分组结果 流运算
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);


            //封装规格分组结果
            StringTerms specTerms = (StringTerms) skuInfos.getAggregation(skuSpec);
            List<StringTerms.Bucket> buckets = specTerms.getBuckets();
            List<String> specList =  new ArrayList<>();
            for (StringTerms.Bucket bucket : buckets) {
                specList.add(bucket.getKeyAsString());
            }
            resultMap.put("specList",parseSpecList(specList));

            return resultMap;
        }
        //封装查询结果
        return null;
    }

    /**
     * [
     * "{'颜色': '蓝色', '版本': '6GB+128GB'}",
     * "{'颜色': '黑色', '版本': '6GB+128GB'}",
     * "{'颜色': '黑色', '版本': '4GB+64GB'}",
     * "{'颜色': '蓝色', '版本': '4GB+64GB'}",
     * "{'颜色': '蓝色', '版本': '6GB+64GB'}",
     * "{'颜色': '黑色', '版本': '6GB+64GB'}",
     * "{'颜色': '黑色'}",
     * "{'颜色': '蓝色'}",
     * "{'颜色': '金色', '版本': '4GB+64GB'}",
     * "{'颜色': '粉色', '版本': '6GB+128GB'}"
     * ]
     */
    private Map<String,Set<String>> parseSpecList(List<String> specList){
        //创建一个最大的Map<String,Set<String>>
        Map<String,Set<String>> allMap = new HashMap<>();
        //遍历数据
        for (String spec : specList) {
            //将字符串转换为map
            Map<String,String> mapSpec = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entries = mapSpec.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String value = entry.getValue();
                //判断最大的map包含key不
                if (!allMap.containsKey(entry.getKey())){
                    //不存在的话
                    Set<String> set = new LinkedHashSet<>();
                    set.add(value);
                    allMap.put(entry.getKey(),set);
                }else {
                    Set<String> set = allMap.get(entry.getKey());
                    set.add(entry.getValue());
                }
            }
        }
        return allMap;
    }
}
