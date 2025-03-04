package com.whalewatch.controller;

import com.whalewatch.domain.Post;
import com.whalewatch.dto.PostDto;
import com.whalewatch.mapper.PostMapper;
import com.whalewatch.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public PostController(PostService postService, PostMapper postMapper, CommentMapper commentMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    // 전체 게시글 조회
    @GetMapping
    public List<PostDto> getPosts() {
        return postService.getAllPosts().stream()
                .map(entity -> postMapper.toDto(entity, commentMapper))
                .collect(Collectors.toList());
    }

    // 특정 게시글 조회 (조회 시 viewCount 1 증가, 댓글 포함)
    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable int id) {
        Post post = postService.incrementViewCount(id);
        return postMapper.toDto(post, commentMapper);
    }

    // 내 게시글 조회 (로그인시)
    @GetMapping("/my")
    public List<PostDto> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return postService.getPostsByUsername(username).stream()
                .map(entity -> postMapper.toDto(entity, commentMapper))
                .collect(Collectors.toList());
    }


    @PostMapping
    public PostDto createPost(@RequestBody PostDto postDto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        postDto.setUsername(userDetails.getUsername());
        Post entity = postMapper.toEntity(postDto);
        Post saved = postService.createPost(entity);
        return postMapper.toDto(saved, commentMapper);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable int id, @RequestBody PostDto postDto) {
        Post entity = postMapper.toEntity(postDto);
        Post updated = postService.updatePost(id, entity);
        return postMapper.toDto(updated, commentMapper);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable int id) {
        postService.deletePost(id);
    }
}
