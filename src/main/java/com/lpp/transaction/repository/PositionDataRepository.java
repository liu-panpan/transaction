package com.lpp.transaction.repository;

import com.lpp.transaction.entity.PositionData;
import com.lpp.transaction.entity.TransactionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ：liupanpan
 */
public interface PositionDataRepository extends JpaRepository<PositionData, String> {
}
