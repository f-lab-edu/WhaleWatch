package com.whalewatch.controller;

import com.whalewatch.domain.Post;
import com.whalewatch.common.dto.PostDto;
import com.whalewatch.mapper.PostMapper;
import com.whalewatch.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    public PostController(PostService postService,PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping
    public List<PostDto> getPosts(){
        return postService.getAllPosts().stream()
                .map(postMapper::toDto) //entity -> dto
                .collect(Collectors.toList());
    }

    @PostMapping
    public PostDto createPost(@RequestBody PostDto post) {
        Post entity = postMapper.toEntity(post); // Dto -> entity
        Post saved = postService.createPost(entity);
        return postMapper.toDto(saved);
    }

    @PostMapping("/{id}")
    public PostDto updatePost(@PathVariable int id, @RequestBody PostDto post) {
        Post entity = postMapper.toEntity(post);
        Post updated = postService.updatePost(id,entity);
        return postMapper.toDto(entity);
    }
}
