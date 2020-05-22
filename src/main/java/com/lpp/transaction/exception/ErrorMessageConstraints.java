package com.lpp.transaction.exception;

/**
 * @author Huang Lin
 * @version Date：Jun 25, 2019 6:01:48 PM
 */
public class ErrorMessageConstraints {

    // 1000~2000 common error code
    public static final String OPERATION_TYPE_ERROR = "1000";
    public static final String TRANSACTION_NOT_EXIST = "1001";
    public static final String TRAD_ID_CAN_NOT_NULL = "1002";
    private ErrorMessageConstraints() {

    }
}
