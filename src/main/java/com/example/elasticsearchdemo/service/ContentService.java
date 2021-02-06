package com.example.elasticsearchdemo.service;

import com.alibaba.fastjson.JSON;
import com.example.elasticsearchdemo.pojo.Content;
import com.example.elasticsearchdemo.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private HtmlParseUtil htmlParseUtil;

    //1.解析数据，放入到es中


    public boolean parseContent(String keywords){
        List<Content> contents = htmlParseUtil.parseJD(keywords);
        // 把查询到的数据到如es中
        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.timeout("2m");
        for(int i=0;i<contents.size();i++){
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        try {
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println(bulk.buildFailureMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;


    }

    public List<Map<String, Object>> query(String keywords, String start, String limit) {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        SearchRequest request = new SearchRequest("jd_goods");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(Integer.valueOf(start));
        builder.size(Integer.valueOf(limit));

        TermQueryBuilder queryBuilder;
        queryBuilder = QueryBuilders.termQuery("title",keywords);

        builder.query(queryBuilder);
        builder.timeout(TimeValue.timeValueMillis(60));

        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("titile");
        builder.highlighter(highlightBuilder);

        try {
            SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = search.getHits().getHits();
            for(SearchHit hit:hits){
                arrayList.add(hit.getSourceAsMap()); // 获取数据来源
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return arrayList;

    }
}
