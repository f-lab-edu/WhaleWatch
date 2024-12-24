package com.whalewatch.mapper;

import com.whalewatch.common.dto.TransactionDto;
import com.whalewatch.domain.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hash", source = "hash")
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "amount", source = "amount")
    TransactionDto toDto(Transaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hash", source = "hash")
    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "amount", source = "amount")
    Transaction toEntity(TransactionDto dto);
}
