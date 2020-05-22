package com.lpp.transaction.service;

import com.lpp.transaction.dto.TransactionSaveDTO;
import com.lpp.transaction.entity.PositionData;
import com.lpp.transaction.entity.TransactionData;

import java.util.List;

/**
 * @author ：liupanpan
 */
public interface TransactionDataService {
    TransactionData saveTransactionDataByDto(TransactionSaveDTO transactionSaveDTO) throws Exception;

    List<PositionData> queryAllPositionList() throws Exception;

    PositionData queryPositionBySecurityCode(String securityCode) throws Exception;
}
