package com.whalewatch.mapper;

import com.whalewatch.dto.TransactionDto;
import com.whalewatch.domain.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // Entity -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "tradePrice", source = "tradePrice")
    @Mapping(target = "tradeVolume", source = "tradeVolume")
    @Mapping(target = "askBid", source = "askBid")
    @Mapping(target = "tradeTimestamp", source = "tradeTimestamp")
    TransactionDto toDto(Transaction entity);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "tradePrice", source = "tradePrice")
    @Mapping(target = "tradeVolume", source = "tradeVolume")
    @Mapping(target = "askBid", source = "askBid")
    @Mapping(target = "tradeTimestamp", source = "tradeTimestamp")
    Transaction toEntity(TransactionDto dto);
}
