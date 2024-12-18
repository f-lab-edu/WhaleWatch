package com.whalewatch.common.dto;

public class PostDto {
    private int id;
    private String title;
    private String content;

    public PostDto() {}

    public PostDto(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter (id는 조회만 가능)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
