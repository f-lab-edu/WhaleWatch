package com.whalewatch.mapper;

import com.whalewatch.dto.UserDto;
import com.whalewatch.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Entity -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    UserDto toDto(User entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    User toEntity(UserDto dto);
}
