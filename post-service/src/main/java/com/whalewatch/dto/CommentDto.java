package com.whalewatch.dto;

import java.time.LocalDateTime;

public class CommentDto {
    private int id;
    private String content;
    private String username;
    private int recommendedCount;
    private LocalDateTime createdDate;
    private int postId;

    public CommentDto() {}

    public CommentDto(int id, String content, String username, int recommendedCount, LocalDateTime createdDate, int postId) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.recommendedCount = recommendedCount;
        this.createdDate = createdDate;
        this.postId = postId;
    }

    // Getter/Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getRecommendedCount() { return recommendedCount; }
    public void setRecommendedCount(int recommendedCount) { this.recommendedCount = recommendedCount; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
}