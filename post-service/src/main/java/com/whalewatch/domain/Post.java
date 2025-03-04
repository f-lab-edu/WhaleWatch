package com.whalewatch.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String content;
    private String username;
    private int recommendedCount;
    private int viewCount;
    private LocalDateTime createdDate;

    // 댓글 리스트 양방향 연관
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    protected Post() {}

    public Post(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.recommendedCount = 0;
        this.viewCount = 0;
        this.createdDate = LocalDateTime.now();
    }

    // Getter/Setter
    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getRecommendedCount() { return recommendedCount; }
    public void setRecommendedCount(int recommendedCount) { this.recommendedCount = recommendedCount; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}