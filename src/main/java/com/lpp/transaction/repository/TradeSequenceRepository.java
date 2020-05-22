package com.lpp.transaction.repository;

import com.lpp.transaction.entity.TradeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author ：liupanpan
 */
public interface TradeSequenceRepository extends JpaRepository<TradeSequence,Long> {
    @Query(value = "select nextval('seq_sys_trade_id')", nativeQuery = true)
    Long getTradeId();
}
