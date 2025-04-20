package com.whalewatch.mapper;

import com.whalewatch.domain.User;
import com.whalewatch.dto.UserDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-20T16:47:07+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        int id = 0;
        String email = null;
        String username = null;

        id = entity.getId();
        email = entity.getEmail();
        username = entity.getUsername();

        UserDto userDto = new UserDto( id, email, username );

        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        if ( dto == null ) {
            return null;
        }

        String email = null;
        String username = null;

        email = dto.getEmail();
        username = dto.getUsername();

        User user = new User( email, username );

        return user;
    }
}
