package com.example.elasticsearchdemo.controller;

import com.example.elasticsearchdemo.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 业务编写
 */
@RestController
public class ContentController {

   @Autowired
   private ContentService contentService;

   @RequestMapping("/search/{keywords}")
   public boolean search(@PathVariable("keywords") String keywords){
       return contentService.parseContent(keywords);
   }


   @RequestMapping("/query/{keywords}/{start}/{limit}")
   public List<Map<String,Object>> query(@PathVariable("keywords") String keywords,@PathVariable("start") String start,@PathVariable("limit") String limit){
      return contentService.query(keywords,start,limit);

   }


}
