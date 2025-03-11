package com.whalewatch.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String content;
    private String username;
    private int recommendedCount;
    private LocalDateTime createdDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected Comment() {}

    public Comment(String content, String username, Post post) {
        this.content = content;
        this.username = username;
        this.post = post;
        this.recommendedCount = 0;
        this.createdDate = LocalDateTime.now();
    }

    // Getter/Setter
    public int getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getRecommendedCount() { return recommendedCount; }
    public void setRecommendedCount(int recommendedCount) { this.recommendedCount = recommendedCount; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}
