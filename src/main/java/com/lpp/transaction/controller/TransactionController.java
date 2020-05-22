package com.lpp.transaction.controller;

import com.lpp.transaction.dto.TransactionSaveDTO;
import com.lpp.transaction.entity.PositionData;
import com.lpp.transaction.service.TransactionDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author ：liupanpan
 */
@RestController
@Api(tags = { "TransactionData Controller" })
@RequestMapping(value = "/api")
@Log4j2
public class TransactionController {
    @Autowired
    private TransactionDataService transactionService;

    @PostMapping(value = "/trans/save")
    @ApiOperation("save TransactionData data by dto")
    public void saveTransactionByDto(@Valid @ApiParam(required = true) @RequestBody TransactionSaveDTO transactionSaveDTO)
            throws Exception {
        log.info("Start to saveTransactionByDto");
        transactionService.saveTransactionDataByDto(transactionSaveDTO);
    }


    @GetMapping(value = "/position/query/all")
    @ApiOperation("query all position data")
    public ResponseEntity<List<PositionData>> getPositionData() throws Exception {
        log.info("Start to getPositionData");
        List<PositionData> positionDataList = transactionService.queryAllPositionList();
        return ResponseEntity.ok(positionDataList);
    }

    @GetMapping(value = "/position/query/one")
    @ApiOperation("query position data by securityCode")
    public ResponseEntity<PositionData> getPositionDataBySecurityCode(@Valid @Size(min = 3,max = 3)
    @ApiParam(required = true) @RequestParam String securityCode) throws Exception {
        log.info("Start to getPositionDataBySecurityCode");
        PositionData positionData = transactionService.queryPositionBySecurityCode(securityCode);
        return ResponseEntity.ok(positionData);
    }
}
