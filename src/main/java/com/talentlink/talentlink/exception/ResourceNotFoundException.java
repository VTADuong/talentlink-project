package com.talentlink.talentlink.exception;

/**
 * Ngoại lệ khi không tìm thấy tài nguyên yêu cầu trong hệ thống (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("Không tìm thấy %s với mã định danh: %s", resourceName, id));
    }
}
