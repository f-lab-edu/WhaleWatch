package com.whalewatch.controller;

import com.whalewatch.common.dto.PostDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private List<PostDto> posts = new ArrayList<>();

    public PostController(){
        posts.add(new PostDto(1,"Test1","Test1"));
        posts.add(new PostDto(2,"Test2","Test2"));
    }

    @GetMapping
    public List<PostDto> getPosts(){
        return posts;
    }

    @PostMapping
    public PostDto createPost(@RequestBody PostDto post) {
        posts.add(post);
        return post;
    }
}
