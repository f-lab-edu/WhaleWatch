package com.whalewatch.mapper;

import com.whalewatch.domain.Comment;
import com.whalewatch.domain.Post;
import com.whalewatch.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Entity -> DTO
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto dto);

    default Comment toEntity(CommentDto dto, Post post) {
        Comment comment = toEntity(dto);
        comment.setPost(post);
        return comment;
    }
}
