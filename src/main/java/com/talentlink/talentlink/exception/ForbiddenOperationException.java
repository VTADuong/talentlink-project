package com.talentlink.talentlink.exception;

/**
 * Ngoại lệ khi người dùng thực hiện thao tác không được cấp quyền (HTTP 403).
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
