package com.whalewatch.mapper;

import com.whalewatch.domain.Transaction;
import com.whalewatch.dto.TransactionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-20T16:47:07+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionDto toDto(Transaction entity) {
        if ( entity == null ) {
            return null;
        }

        int id = 0;
        String coin = null;
        Double tradePrice = null;
        Double tradeVolume = null;
        String askBid = null;
        Long tradeTimestamp = null;

        id = entity.getId();
        coin = entity.getCoin();
        tradePrice = entity.getTradePrice();
        tradeVolume = entity.getTradeVolume();
        askBid = entity.getAskBid();
        tradeTimestamp = entity.getTradeTimestamp();

        TransactionDto transactionDto = new TransactionDto( id, coin, tradePrice, tradeVolume, askBid, tradeTimestamp );

        return transactionDto;
    }

    @Override
    public Transaction toEntity(TransactionDto dto) {
        if ( dto == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setCoin( dto.getCoin() );
        transaction.setTradePrice( dto.getTradePrice() );
        transaction.setTradeVolume( dto.getTradeVolume() );
        transaction.setAskBid( dto.getAskBid() );
        transaction.setTradeTimestamp( dto.getTradeTimestamp() );

        return transaction;
    }
}
