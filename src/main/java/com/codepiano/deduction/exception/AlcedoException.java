package com.codepiano.deduction.exception;

/**
 * 通用的异常信息
 */
public class AlcedoException extends RuntimeException {

    private static final long serialVersionUID = 3882304307228668534L;
    /**
     * 错误码信息 *
     */
    private int errorCode;

    private String message;

    public AlcedoException() {
        super();
    }

    public AlcedoException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getDescription();
    }

    public AlcedoException(ErrorCode errorCode, String message) {
        super(errorCode.getDescription());
        this.errorCode = errorCode.getCode();
        this.message = message;
    }

    public AlcedoException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode.getCode();
    }

    public AlcedoException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
