package com.whalewatch.controller;

import com.whalewatch.domain.Post;
import com.whalewatch.dto.PostDto;
import com.whalewatch.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDto> getPosts(){
        List<Post> posts = postService.getAllPosts();
        return posts.stream()
                .map(p -> new PostDto(p.getId(), p.getTitle(), p.getContent()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public PostDto createPost(@RequestBody PostDto post) {
        Post newPost = new Post(post.getTitle(), post.getContent());
        Post saved = postService.createPost(newPost);
        return new PostDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @PostMapping("/{id}")
    public PostDto updatePost(@PathVariable int id, @RequestBody PostDto post) {
        Post toUpdate = new Post(post.getTitle(), post.getContent());
        Post updated = postService.updatePost(id, toUpdate);
        return new PostDto(updated.getId(), updated.getTitle(), updated.getContent());
    }
}
