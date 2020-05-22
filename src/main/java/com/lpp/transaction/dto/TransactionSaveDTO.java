package com.lpp.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lpp.transaction.enums.OperationTypeEnum;
import com.lpp.transaction.enums.TradeStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ：liupanpan
 */
@Getter
@Setter
@ApiModel("TransactionData save dto model")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSaveDTO {
    
    @ApiModelProperty("tradeID")
    private Long tradeId;

    @ApiModelProperty("securityCode")
    @NotBlank
    @Size(min = 3,max = 3)
    private String securityCode;

    @ApiModelProperty("quantity")
    @NotNull
    @Min(1)
    private long quantity;

    @ApiModelProperty("Operation Type ")
    @NotNull
    private OperationTypeEnum operationType;
    
    @ApiModelProperty("Trade Status")
    @NotNull
    private TradeStatusEnum tradeStatus;
}
