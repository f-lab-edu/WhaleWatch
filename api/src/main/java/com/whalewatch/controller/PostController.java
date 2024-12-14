package com.whalewatch.controller;

import com.whalewatch.common.dto.PostDto;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @GetMapping
    public List<PostDto> getPosts(){
        return Arrays.asList(
                new PostDto(1,"Post1","post1"),
                new PostDto(2,"Post2","post2")
        );
    }

    @PostMapping
    public String createPost(@RequestBody PostDto post) {
        return "게시글 생성 : " + post.getTitle();
    }
}
