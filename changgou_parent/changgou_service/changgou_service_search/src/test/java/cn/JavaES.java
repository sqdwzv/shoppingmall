package cn;

import com.changgou.search.SearchApplication;
import com.changgou.search.pojo.SkuInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Spring Data ES搜索
 * ElasticSearchTemplate
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class JavaES {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 关键字搜索
     * 词搜索
     * boolean查询
     * 分页
     * 排序
     * 高亮
     */
    @Test
    public void test01() {
        /**
         *
         * 实体类字节码类型
         */
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //封装查询条件
        //QueryBuilders.matchQuery(, );------->关键字搜索 name 华为手机--->华为 手机
        //QueryBuilders.termQuery(, )-------->词搜索   name 华为手机
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "OPPO");
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", "手机");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);


        //封装查询条件
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        //封装分页参数
        nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        //封装排序字段，和排序类型
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //高亮
        //设置高亮域 ，高亮前后缀
        //执行查询
        //解析高亮
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        field.preTags("<span style='color:red'>");
        field.postTags("</span>");
        nativeSearchQueryBuilder.withHighlightFields(field);

        //执行查询
        //第三个参数，手动解析查询结构
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class,new MySearchMapper());

        //解析结果
        List<SkuInfo> skuInfos = page.getContent(); //skuInfos满足条件数据
        long totalElements = page.getTotalElements(); //总记录数
        int totalPages = page.getTotalPages(); //总页数

        System.out.println("总记录数" + totalElements);
        System.out.println("总页数" + totalPages);
        for (SkuInfo skuInfo : skuInfos) {
            System.out.println(skuInfo.getId() + "-" +skuInfo.getName() + "--" + skuInfo.getCategoryName() +"---"+skuInfo.getPrice());
        }
    }

    class MySearchMapper implements SearchResultMapper {

        @Override
        public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
            SearchHits hits = response.getHits();
            List<SkuInfo> skuInfos = new ArrayList<>();
            for (SearchHit hit : hits) {
                SkuInfo skuInfo = new SkuInfo();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                try {
                    BeanUtils.populate(skuInfo, sourceAsMap);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                skuInfos.add(skuInfo);

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameHighlightField = highlightFields.get("name");
                Text[] fragments = nameHighlightField.getFragments();
                Text t = fragments[0];
                String string = t.string();
                skuInfo.setName(string);
            }
            return (AggregatedPage<T>) new AggregatedPageImpl<SkuInfo>(skuInfos, pageable, hits.getTotalHits(),response.getAggregations());
        }
    }
}
