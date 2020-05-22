package com.lpp.transaction.enums;

/**
 * @author ：liupanpan
 */
public enum  OperationTypeEnum {
    /**
     * INSERT
     */
    INSERT("Insert"),
    /**
     * UPDATE
     */
    UPDATE("Update"),
    /**
     * CANCEL
     */
    CANCEL("Cancel");
    
    private String value;

    OperationTypeEnum(String value) {
        this.value = value;
    }
}
