package com.whalewatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.domain.Post;
import com.whalewatch.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        postRepository.deleteAll(); // DB 초기화
    }

    @Test
    void testGetPosts() throws Exception {
        // given
        Post p = new Post("Title1", "Content1");
        postRepository.save(p);

        // when & then
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Title1")))
                .andExpect(jsonPath("$[0].content", is("Content1")));
    }

    @Test
    void testCreatePost() throws Exception {
        // given
        Post request = new Post("New Title", "New Content");

        // when
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.content").value("New Content"));

        // then
        List<Post> all = postRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("New Title", all.get(0).getTitle());
        assertEquals("New Content", all.get(0).getContent());
    }

    @Test
    void testUpdatePost() throws Exception {
        // given
        Post saved = postRepository.save(new Post("Old Title", "Old Content"));
        Post updateRequest = new Post("Updated Title", "Updated Content");

        // when & then
        mockMvc.perform(post("/api/posts/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));

        // DB 확인
        Post updated = postRepository.findById(saved.getId()).get();
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Content", updated.getContent());
    }
}
