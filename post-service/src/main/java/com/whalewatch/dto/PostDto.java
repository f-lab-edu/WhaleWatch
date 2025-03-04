package com.whalewatch.dto;

import java.time.LocalDateTime;

public class PostDto {
    private int id;
    private String title;
    private String content;
    private String username;
    private int recommendedCount;
    private int viewCount;
    private LocalDateTime createdDate;
    private List<CommentDto> comments;

    public PostDto() {}

    public PostDto(int id, String title, String content, String username, int recommendedCount,
                   int viewCount, LocalDateTime createdDate, List<CommentDto> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.recommendedCount = recommendedCount;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
        this.comments = comments;
    }

    // Getter/Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }
}
