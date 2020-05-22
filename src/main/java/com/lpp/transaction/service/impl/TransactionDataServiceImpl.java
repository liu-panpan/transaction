package com.lpp.transaction.service.impl;

import com.lpp.transaction.dto.TransactionSaveDTO;
import com.lpp.transaction.entity.PositionData;
import com.lpp.transaction.entity.TransactionData;
import com.lpp.transaction.enums.OperationTypeEnum;
import com.lpp.transaction.enums.TradeStatusEnum;
import com.lpp.transaction.exception.ApplicationException;
import com.lpp.transaction.exception.ErrorMessageConstraints;
import com.lpp.transaction.repository.PositionDataRepository;
import com.lpp.transaction.repository.TradeSequenceRepository;
import com.lpp.transaction.repository.TransactionDataRepository;
import com.lpp.transaction.service.TransactionDataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ：liupanpan
 */
@Service
@Log4j2
public class TransactionDataServiceImpl implements TransactionDataService {
    
    @Autowired
    private TransactionDataRepository transactionDataRepository;
    
    @Autowired
    private PositionDataRepository positionDataRepository;
    
    @Autowired
    private TradeSequenceRepository tradeSequenceRepository;

    /**
     * save transaction data by transactionSaveDTO
     * @param transactionSaveDTO
     */
    @Override
    @Transactional
    public TransactionData saveTransactionDataByDto(TransactionSaveDTO transactionSaveDTO) {
        Long tradeID = transactionSaveDTO.getTradeId();
        OperationTypeEnum operationType = transactionSaveDTO.getOperationType();
        TradeStatusEnum tradeStatus = transactionSaveDTO.getTradeStatus();
        TransactionData newVersionData = new TransactionData();
        if (operationType.equals(OperationTypeEnum.INSERT)) {
            //1.save TransactionData by INSERT Operation
            if (tradeID != null) {
                throw new ApplicationException(ErrorMessageConstraints.OPERATION_TYPE_ERROR,
                        "operation type error:" + operationType);
            }
            newVersionData.setVersion(1L);
            BeanUtils.copyProperties(transactionSaveDTO, newVersionData);
            newVersionData.setTradeId(tradeSequenceRepository.getTradeId());
            transactionDataRepository.save(newVersionData);
            //2.update PositionData by TransactionData by INSERT Operation
            Optional<PositionData> optional = positionDataRepository.findById(transactionSaveDTO.getSecurityCode());
            //3.if positionMap don't have this new version securityCode 
            // then add this securityCode for PositionData default value = 0;
            PositionData positionData = new PositionData(transactionSaveDTO.getSecurityCode(),0L);
            if (optional.isPresent()){
                positionData = optional.get();
            }
            //4.update PositionData value by new version transaction
            // position value = position value + same security code with new version transaction's quantity
            // The new version transaction status is buy is added,and sell is subtracted
            if (tradeStatus.equals(TradeStatusEnum.BUY)) {
                positionData.setPositionValue(positionData.getPositionValue() + transactionSaveDTO.getQuantity());
            } else {
                positionData.setPositionValue(positionData.getPositionValue() - transactionSaveDTO.getQuantity());
            }
            positionData.setSecurityCode(transactionSaveDTO.getSecurityCode());
            positionDataRepository.save(positionData);
        } else {
            if (tradeID == null) {
                throw new ApplicationException(ErrorMessageConstraints.TRAD_ID_CAN_NOT_NULL,
                        "TradeId can not be null!");
            }
            //1.get last version TransactionData before save this TransactionData
            Optional<TransactionData> optionalTransactionData = transactionDataRepository
                    .findFirstByTradeIdOrderByVersionDesc(tradeID);
            if (!optionalTransactionData.isPresent()) {
                log.error("transaction not exist for tradId:【{}】", transactionSaveDTO.getTradeId());
                throw new ApplicationException(ErrorMessageConstraints.TRANSACTION_NOT_EXIST,
                        "transaction not exist for tradId:" + transactionSaveDTO.getTradeId());
            }
            if (optionalTransactionData.get().getOperationType().equals(OperationTypeEnum.CANCEL)) {
                log.error("operation type error:【{}】", operationType);
                throw new ApplicationException(ErrorMessageConstraints.OPERATION_TYPE_ERROR,
                        "error operation type:" + operationType);
            }
            //2.save new version transaction data for this trade by transactionSaveDTO
            TransactionData lastVersionData = optionalTransactionData.get();
            newVersionData.setVersion(lastVersionData.getVersion() + 1);
            BeanUtils.copyProperties(transactionSaveDTO, newVersionData);
            transactionDataRepository.save(newVersionData);
            //3.update PositionData by last Version data and new version transaction data 
            List<String> securityCodes = new ArrayList<>();
            securityCodes.add(lastVersionData.getSecurityCode());
            securityCodes.add(transactionSaveDTO.getSecurityCode());
            //query PositionData list by last version transaction's securityCode and new version transaction's securityCode
            List<PositionData> positionDataList = positionDataRepository.findAllById(securityCodes);
            Map<String, PositionData> positionMap = new HashMap<>();
            for (PositionData positionData : positionDataList) {
                positionMap.put(positionData.getSecurityCode(), positionData);
            }
            //4.if positionMap don't have this new version securityCode 
            // then add this securityCode for PositionData default value = 0;
            if (positionMap.get(transactionSaveDTO.getSecurityCode()) == null) {
                positionMap.put(transactionSaveDTO.getSecurityCode(),
                        new PositionData(transactionSaveDTO.getSecurityCode(), 0L));
            }
            //5.update position data by transaction CANCEL and UPDATE
            if (operationType.equals(OperationTypeEnum.CANCEL)) {
                transactionSaveDTO.setQuantity(0L);
                updatePositionDataMap(transactionSaveDTO, lastVersionData, positionMap);
            } else if (operationType.equals(OperationTypeEnum.UPDATE)) {
                updatePositionDataMap(transactionSaveDTO, lastVersionData, positionMap);
            }
            positionDataRepository.saveAll(positionMap.values());
        }
        return newVersionData; 
    }

    @Override
    public List<PositionData> queryAllPositionList() {
        return positionDataRepository.findAll();
    }

    @Override
    public PositionData queryPositionBySecurityCode(String securityCode) {
        Optional<PositionData> optionalPositionData = positionDataRepository.findById(securityCode);
        return optionalPositionData.orElse(null);
    }

    /**
     * 
     * @param newVersionData
     * @param lastVersionData
     * @param positionMap
     * updatePositionDataMap by lastVersionData and newVersionData when transaction operationType is UPDATE or CANCEL
     */
    private void updatePositionDataMap(TransactionSaveDTO newVersionData, TransactionData lastVersionData,
            Map<String, PositionData> positionMap) {
        //1.Subtract the value of the previous version
        // position value = position value - same security code with previous version transaction's quantity
        // The previous version transaction status is buy is subtracted, and sell is added
        if (lastVersionData.getTradeStatus().equals(TradeStatusEnum.BUY)) {
            positionMap.get(lastVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(lastVersionData.getSecurityCode()).getPositionValue() - lastVersionData
                            .getQuantity());
        } else {
            positionMap.get(lastVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(lastVersionData.getSecurityCode()).getPositionValue() + lastVersionData
                            .getQuantity());
        }
        //2.add the value of the new version transaction data quantity
        // position value = position value + same security code with new version transaction's quantity
        // The new version transaction status is buy is added,and sell is subtracted
        if (newVersionData.getTradeStatus().equals(TradeStatusEnum.BUY)) {
            positionMap.get(newVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(newVersionData.getSecurityCode()).getPositionValue() + newVersionData
                            .getQuantity());
        } else {
            positionMap.get(newVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(newVersionData.getSecurityCode()).getPositionValue() - newVersionData
                            .getQuantity());
        }
    }
}
