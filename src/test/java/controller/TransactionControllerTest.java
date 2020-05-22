package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpp.transaction.TransactionApplication;
import com.lpp.transaction.dto.TransactionSaveDTO;
import com.lpp.transaction.entity.PositionData;
import com.lpp.transaction.entity.TransactionData;
import com.lpp.transaction.enums.OperationTypeEnum;
import com.lpp.transaction.enums.TradeStatusEnum;
import com.lpp.transaction.service.TransactionDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ：liupanpan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionApplication.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    protected WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    private ObjectMapper jsonMapper = new ObjectMapper();
    
    @Autowired
    private TransactionDataService transactionDataService;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).dispatchOptions(true).build();
    }

    @Test
    public void saveTransactionByDtoTest() throws Exception {
        TransactionSaveDTO dto = new TransactionSaveDTO();
        dto.setQuantity(20L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("REB");
        dto.setTradeStatus(TradeStatusEnum.BUY);
        //test save transaction data success by OperationType INSERT
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //test save transaction data fail by OperationType INSERT
        dto.setTradeId(1L);
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        //test save transaction data success by OperationType UPDATE
        TransactionSaveDTO updateDto = new TransactionSaveDTO(2L, "REL", 30, OperationTypeEnum.UPDATE,
                TradeStatusEnum.SELL);
        mockMvc.perform(post("/api/trans/save").content(jsonMapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        
        //test save transaction data success by OperationType UPDATE and Buy
        updateDto.setQuantity(30L);
        updateDto.setOperationType(OperationTypeEnum.UPDATE);
        updateDto.setTradeStatus(TradeStatusEnum.BUY);
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(updateDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        //test save transaction data success by OperationType CANCEL with securityCode changed
        updateDto.setSecurityCode("ITC");
        updateDto.setOperationType(OperationTypeEnum.CANCEL);
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(updateDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        //test save transaction data fail by OperationType CANCEL
        updateDto.setOperationType(OperationTypeEnum.CANCEL);
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(updateDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //test save transaction data fail by tradeId not exist
        dto.setTradeId(0L);
        mockMvc.perform(post("/api/trans/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        //test INSERT transaction data by new securityCode and TradeStatus is SELL
        TransactionSaveDTO insertDto = new TransactionSaveDTO(null, "AKF", 30, OperationTypeEnum.INSERT,
                TradeStatusEnum.SELL);
        TransactionData transactionData = transactionDataService.saveTransactionDataByDto(insertDto);
        PositionData positionData = transactionDataService.queryPositionBySecurityCode(transactionData.getSecurityCode());
        assertNotNull(positionData);
        assertEquals(-30L, (long) positionData.getPositionValue());
        
        //test INSERT transaction data by securityCode exist in position table
        TransactionData transactionData1 = transactionDataService.saveTransactionDataByDto(insertDto);
        PositionData positionBySecurityCode = transactionDataService.queryPositionBySecurityCode("AKF");
        assertNotNull(positionData);
        assertEquals(-60L, (long) positionBySecurityCode.getPositionValue());

        //test CANCEL transaction data by securityCode not exist in position table
        TransactionSaveDTO cancelDto = new TransactionSaveDTO(transactionData1.getTradeId(), "BBC", 30, OperationTypeEnum.CANCEL,
                TradeStatusEnum.SELL);
        transactionDataService.saveTransactionDataByDto(cancelDto);
        PositionData position = transactionDataService.queryPositionBySecurityCode("BBC");
        assertNotNull(position);
        assertEquals(0L, (long) position.getPositionValue());
    }

    @Test
    public void queryPositionDataTest() throws Exception {
        TransactionSaveDTO dto = new TransactionSaveDTO();
        dto.setQuantity(20L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("ITC");
        dto.setTradeStatus(TradeStatusEnum.BUY);
        //test save transaction data success by OperationType INSERT
        transactionDataService.saveTransactionDataByDto(dto);
        
        //test query all Position data success
        String result = mockMvc.perform(get("/api/position/query/all")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        List<PositionData> positionDataList = jsonMapper.readValue(result, new TypeReference<List<PositionData>>() {
        });
        assertTrue(!positionDataList.isEmpty());
        Map<String, Long> positionDataMap = positionDataList.stream()
                .collect(Collectors.toMap(PositionData::getSecurityCode, PositionData::getPositionValue));
        assertEquals(20L, (long) positionDataMap.get("ITC"));

        //test query one Position data success by securityCode REA
        String content = mockMvc.perform(get("/api/position/query/one").param("securityCode", "REA"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        PositionData positionData = jsonMapper.readValue(content, PositionData.class);
        assertNotNull(positionData);

    }

    @Test
    public void saveTransactionByDtoExceptionTest() throws Exception {
        TransactionSaveDTO dto = new TransactionSaveDTO();
        dto.setQuantity(20L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("RELA");
        dto.setTradeStatus(TradeStatusEnum.BUY);
        //test save transaction data exception by error SecurityCode
        mockMvc.perform(post("/api/trans/save").content(jsonMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        //test save transaction data exception for transaction data status is cancel
        TransactionSaveDTO saveDTO = new TransactionSaveDTO(1L, "REC", 10, OperationTypeEnum.UPDATE,
                TradeStatusEnum.SELL);
        mockMvc.perform(post("/api/trans/save").content(jsonMapper.writeValueAsString(saveDTO))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        //test save transaction data exception for trade id is null
        TransactionSaveDTO tradeIdNullDTO = new TransactionSaveDTO(null, "REC", 10, OperationTypeEnum.UPDATE,
                TradeStatusEnum.SELL);
        mockMvc.perform(post("/api/trans/save").content(jsonMapper.writeValueAsString(tradeIdNullDTO))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        //test save transaction data exception for last version transaction is not exist for trade id
        TransactionSaveDTO tradeIdErrorDTO = new TransactionSaveDTO(0L, "REC", 10, OperationTypeEnum.UPDATE,
                TradeStatusEnum.SELL);
        mockMvc.perform(post("/api/trans/save").content(jsonMapper.writeValueAsString(tradeIdErrorDTO))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
