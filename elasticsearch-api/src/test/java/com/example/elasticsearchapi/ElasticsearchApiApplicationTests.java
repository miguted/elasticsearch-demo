package com.example.elasticsearchapi;

import com.alibaba.fastjson.JSON;
import com.example.elasticsearchapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ElasticsearchApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    // 测试索引的创建
    @Test
    void testCreateIndex() {
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("tedindex");
        // 执行请求
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询索引是否存在
     */
    @Test
    void testGetIndex() {
        // 创建索引请求
        GetIndexRequest request = new GetIndexRequest("tedindex");
        // 执行请求
        try {
            Boolean result = client.indices().exists(request, RequestOptions.DEFAULT);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除索引
     */
    @Test
    void testDeleteIndex() {
        // 创建索引请求
        DeleteIndexRequest request = new DeleteIndexRequest("tedtest");
        // 执行请求
        try {
            AcknowledgedResponse result = client.indices().delete(request, RequestOptions.DEFAULT);
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文档
     */
    @Test
    void testCreateDoc() {
        User user = new User("ted",3);
        user.setName("ted");
        // 创建请求
        IndexRequest index = new IndexRequest("tedindex");
        // 规则 put /tedindex/_doc/1
        // 设置id
        index.id("1");
        index.timeout(TimeValue.timeValueSeconds(1));
        // 设置source
        index.source(JSON.toJSONString(user), XContentType.JSON);
        try {
            IndexResponse response = client.index(index, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取文档
     */
    @Test
    void testGetDoc() {
        // 查看文档是否存在
        GetRequest request = new GetRequest("tedindex", "1");
        // 不获取source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("age");
        try {
            boolean result = client.exists(request, RequestOptions.DEFAULT);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取文档信息
        try {
            GetRequest getRequest = new GetRequest("tedindex", "1");
            GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 修改文档记录
     *
     * @throws IOException
     */
    @Test
    void TestUpdateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("tedindex", "1");
        request.timeout(TimeValue.timeValueSeconds(1));
        User user = new User();
        user.setName("ted1");
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response);

    }

    /**
     * 删除文档
     */
    @Test
    void testDeleteDoc() {
        DeleteRequest request = new DeleteRequest("tedindex", "1");
        request.timeout(TimeValue.timeValueSeconds(1));
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询请求
     */
    @Test
    void testSearch() {
        SearchRequest request = new SearchRequest("tedindex");
        // 构建搜索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 精确匹配，匹配所有
        builder.query(QueryBuilders.termQuery("name", "ted"));
        builder.timeout(TimeValue.timeValueSeconds(60));
        builder.from(0);
        builder.size(100);

        // 高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        builder.highlighter(highlightBuilder);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(response.getHits()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    @Test
    void contextLoads() {
    }

}
