package com.example.elasticsearchdemo.util;


import com.example.elasticsearchdemo.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {

    public static void main(String[] args) {



    }

    public List<Content> parseJD(String keyWords){
        ArrayList<Content> lists=new ArrayList<Content>();

        // 获得请求，前体需要联网，不能获取到ajax的数据
        String url="https://search.jd.com/Search?keyword="+keyWords;

        // 解析网页
        try {
            // 返回的Document就是浏览器的document对象
            Document doc= Jsoup.parse(new URL(url),30000);
            // 所有js中可以使用的方法，这里都能用
            Element element=doc.getElementById("J_goodsList");
            System.out.println(element);
            Elements lis=element.getElementsByTag("li");
            // 获取li的每个标签
            for(Element el:lis){
                // 关于图片特别多的网站，所有的图片都是延迟加载
                // 获取img标签
                String img=el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                String price=el.getElementsByClass("p-price").eq(0).text();
                String title=el.getElementsByClass("p-name").eq(0).text();
                System.out.println(price);
                System.out.println(title);
                Content content=new Content(title,"http://"+img,price);
                lists.add(content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lists;

    }
}
