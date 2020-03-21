package com.cjc.blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {
    @GetMapping("/")
    public String index(){
        String blog = null;
        if (blog==null){
            throw new NotFoundException("博客不存在");
        }
//        int i = 9/0;
        return "index";
    }
}
