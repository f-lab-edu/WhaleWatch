package com.whalewatch.service;

import com.whalewatch.domain.Comment;
import com.whalewatch.domain.Post;
import com.whalewatch.repository.CommentRepository;
import com.whalewatch.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment createComment(Comment comment, int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    public Comment updateComment(int commentId, Comment updatedComment) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.setContent(updatedComment.getContent());
        comment.setUsername(updatedComment.getUsername());
        comment.setRecommendedCount(updatedComment.getRecommendedCount());
        comment.setCreatedDate(updatedComment.getCreatedDate());
        return commentRepository.save(comment);
    }

    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        commentRepository.delete(comment);
    }
}
