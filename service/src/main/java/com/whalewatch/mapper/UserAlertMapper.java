package com.whalewatch.mapper;

import com.whalewatch.domain.UserAlert;
import com.whalewatch.dto.UserAlertDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAlertMapper {
    // Entity -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "tradePrice", source = "tradePrice")
    @Mapping(target = "tradeVolume", source = "tradeVolume")
    @Mapping(target = "tradeTimestamp", source = "tradeTimestamp")
    @Mapping(target = "alertedAt", source = "alertedAt")
    UserAlertDto toDto(UserAlert entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alertedAt", ignore = true)
    UserAlert toEntity(UserAlertDto dto);
}
