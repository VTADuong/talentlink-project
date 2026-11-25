package com.talentlink.talentlink.exception;

/**
 * Ngoại lệ cho các lỗi vi phạm quy tắc nghiệp vụ (Business Logic).
 */
public class BusinessException extends RuntimeException {

    private final String redirectUrl;

    public BusinessException(String message) {
        super(message);
        this.redirectUrl = null;
    }

    public BusinessException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
