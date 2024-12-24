package com.whalewatch.mapper;

import com.whalewatch.common.dto.AlertSettingsDto;
import com.whalewatch.domain.AlertSetting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertSettingsMapper {

    // Entity -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "threshold", source = "threshold")
    @Mapping(target = "notifyByEmail", source = "notifyByEmail")
    AlertSettingsDto toDto(AlertSetting entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    AlertSetting toEntity(AlertSettingsDto dto);
}
