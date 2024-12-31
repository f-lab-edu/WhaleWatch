package com.whalewatch.service;

import com.whalewatch.domain.Post;
import com.whalewatch.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getAllPosts() {
        // given
        Post p1 = new Post("Title1", "Content1");
        Post p2 = new Post("Title2", "Content2");
        given(postRepository.findAll()).willReturn(Arrays.asList(p1, p2));

        // when
        List<Post> result = postService.getAllPosts();

        // then
        assertEquals(2, result.size()); // 리스트 크기 검증

        // 첫 번째 객체 검증
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Content1", result.get(0).getContent());

        // 두 번째 객체 검증
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Content2", result.get(1).getContent());
    }

    @Test
    void createPost() {
        // given
        Post input = new Post("New Title", "New Content");
        Post saved = new Post("New Title", "New Content");

        given(postRepository.save(input)).willReturn(saved);

        // when
        Post result = postService.createPost(input);

        // then
        assertNotNull(result);
        assertEquals("New Title", result.getTitle()); // 제목 검증
        assertEquals("New Content", result.getContent()); // 내용 검증
    }

    @Test
    void updatePost() {
        // given
        int postId = 1;
        Post existing = new Post("Old Title", "Old Content");
        given(postRepository.findById(postId)).willReturn(Optional.of(existing));

        Post updated = new Post("Updated Title", "Updated Content");
        given(postRepository.save(existing)).willAnswer(invocation -> {
            Post toUpdate = invocation.getArgument(0);
            toUpdate.setTitle(updated.getTitle());
            toUpdate.setContent(updated.getContent());
            return toUpdate;
        });

        // when
        Post result = postService.updatePost(postId, updated);

        // then
        assertEquals("Updated Title", result.getTitle()); // 업데이트된 제목 검증
        assertEquals("Updated Content", result.getContent()); // 업데이트된 내용 검증
    }

}
