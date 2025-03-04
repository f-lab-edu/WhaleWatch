package com.whalewatch.mapper;

import com.whalewatch.dto.PostDto;
import com.whalewatch.domain.Post;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface PostMapper {

    // Entity -> DTO
    @Mapping(target = "comments", expression = "java(entity.getComments().stream().map(commentMapper::toDto).collect(java.util.stream.Collectors.toList()))")
    PostDto toDto(Post entity, @Context CommentMapper commentMapper);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto dto);
}
