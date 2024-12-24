package com.whalewatch.mapper;

import com.whalewatch.common.dto.PostDto;
import com.whalewatch.domain.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    // Entity -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    PostDto toDto(Post entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    Post toEntity(PostDto dto);
}
