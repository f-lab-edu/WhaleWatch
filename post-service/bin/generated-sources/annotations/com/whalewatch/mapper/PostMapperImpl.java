package com.whalewatch.mapper;

import com.whalewatch.domain.Post;
import com.whalewatch.dto.PostDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-09T19:08:01+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDto toDto(Post entity, CommentMapper commentMapper) {
        if ( entity == null ) {
            return null;
        }

        PostDto postDto = new PostDto();

        postDto.setId( entity.getId() );
        postDto.setTitle( entity.getTitle() );
        postDto.setContent( entity.getContent() );
        postDto.setUsername( entity.getUsername() );
        postDto.setRecommendedCount( entity.getRecommendedCount() );
        postDto.setViewCount( entity.getViewCount() );
        postDto.setCreatedDate( entity.getCreatedDate() );

        postDto.setComments( entity.getComments().stream().map(commentMapper::toDto).collect(java.util.stream.Collectors.toList()) );

        return postDto;
    }

    @Override
    public Post toEntity(PostDto dto) {
        if ( dto == null ) {
            return null;
        }

        String title = null;
        String content = null;
        String username = null;

        title = dto.getTitle();
        content = dto.getContent();
        username = dto.getUsername();

        Post post = new Post( title, content, username );

        post.setRecommendedCount( dto.getRecommendedCount() );
        post.setViewCount( dto.getViewCount() );
        post.setCreatedDate( dto.getCreatedDate() );

        return post;
    }
}
