package cn;

import com.changgou.search.SearchApplication;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class wangzhen {
    /**
     * 关键字查询
     * 词搜索
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Test
    public void test1(){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //封装查询条件
        //QueryBuilders.matchQuery();---->关键字搜索 name 华为手机 -- >手机华为
        //QueryBuilders.termQuery();----->词搜索 name 华为手机 不分词
        MatchQueryBuilder operator = QueryBuilders.matchQuery("name", "华为手机").operator(Operator.AND);
        nativeSearchQueryBuilder.withQuery(operator);
        //执行查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

    }
}
