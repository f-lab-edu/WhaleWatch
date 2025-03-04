package com.whalewatch.service;


import com.whalewatch.domain.Post;
import com.whalewatch.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    // 게시글 조회 시 viewCount 증가
    public Post incrementViewCount(int id) {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(int id, Post updatedPost) {
        Post post = getPostById(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setRecommendedCount(updatedPost.getRecommendedCount());
        post.setViewCount(updatedPost.getViewCount());
        post.setCreatedDate(updatedPost.getCreatedDate());
        return postRepository.save(post);
    }

    public void deletePost(int id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }

    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByUsername(username);
    }
}
