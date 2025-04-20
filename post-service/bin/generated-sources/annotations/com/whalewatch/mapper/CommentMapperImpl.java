package com.whalewatch.mapper;

import com.whalewatch.domain.Comment;
import com.whalewatch.domain.Post;
import com.whalewatch.dto.CommentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-20T16:47:07+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentDto toDto(Comment entity) {
        if ( entity == null ) {
            return null;
        }

        CommentDto commentDto = new CommentDto();

        commentDto.setPostId( entityPostId( entity ) );
        commentDto.setId( entity.getId() );
        commentDto.setContent( entity.getContent() );
        commentDto.setUsername( entity.getUsername() );
        commentDto.setRecommendedCount( entity.getRecommendedCount() );
        commentDto.setCreatedDate( entity.getCreatedDate() );

        return commentDto;
    }

    @Override
    public Comment toEntity(CommentDto dto) {
        if ( dto == null ) {
            return null;
        }

        String content = null;
        String username = null;

        content = dto.getContent();
        username = dto.getUsername();

        Post post = null;

        Comment comment = new Comment( content, username, post );

        comment.setRecommendedCount( dto.getRecommendedCount() );
        comment.setCreatedDate( dto.getCreatedDate() );

        return comment;
    }

    private int entityPostId(Comment comment) {
        if ( comment == null ) {
            return 0;
        }
        Post post = comment.getPost();
        if ( post == null ) {
            return 0;
        }
        int id = post.getId();
        return id;
    }
}
