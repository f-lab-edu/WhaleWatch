package com.whalewatch.service;

import com.whalewatch.WhaleWatchApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WhaleWatchApplication.class)
public class PostServiceTest {
    @Autowired
    PostService postService;

    @Test
    void testGetAllPostsHasDummyData() {
        var posts = postService.getAllPosts();
        Assertions.assertFalse(posts.isEmpty());
        Assertions.assertTrue(posts.stream().anyMatch(p -> p.getTitle().equals("Test1") && p.getContent().equals("Test1")));
        Assertions.assertTrue(posts.stream().anyMatch(p -> p.getTitle().equals("Test2") && p.getContent().equals("Test2")));
    }
}
