package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/manage/comment")
public class CommentController {

    private static final String PREFIX="/admin/commentmanage/";

    @GetMapping(value = "/list")
    public String commentListPage(){
        return PREFIX+"comment-manage";
    }


}