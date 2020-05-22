package com.lpp.transaction.repository;

import com.lpp.transaction.entity.TransactionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ：liupanpan
 */
public interface TransactionDataRepository extends JpaRepository<TransactionData,Long> {
    Optional<TransactionData> findFirstByTradeIdOrderByVersionDesc(Long tradeID);
}
