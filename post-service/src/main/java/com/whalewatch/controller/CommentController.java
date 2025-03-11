package com.whalewatch.controller;

import com.whalewatch.domain.Comment;
import com.whalewatch.dto.CommentDto;
import com.whalewatch.mapper.CommentMapper;
import com.whalewatch.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable int postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return comments.stream().map(commentMapper::toDto).collect(Collectors.toList());
    }

    // 댓글 생성
    @PostMapping("/post/{postId}")
    public CommentDto createComment(@PathVariable int postId,
                                    @RequestBody CommentDto commentDto,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        commentDto.setUsername(userDetails.getUsername());
        Comment entity = commentMapper.toEntity(commentDto);
        Comment saved = commentService.createComment(entity, postId);
        return commentMapper.toDto(saved);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable int commentId, @RequestBody CommentDto commentDto) {
        Comment entity = commentMapper.toEntity(commentDto);
        Comment updated = commentService.updateComment(commentId, entity);
        return commentMapper.toDto(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
    }
}

