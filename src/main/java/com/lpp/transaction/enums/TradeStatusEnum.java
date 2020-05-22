package com.lpp.transaction.enums;

/**
 * @author ：liupanpan
 */
public enum  TradeStatusEnum {
    /**
     * Buy
     */
    BUY("Buy"),
    /**
     * Sell
     */
    SELL("Sell");
    
    private String value;

    TradeStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
