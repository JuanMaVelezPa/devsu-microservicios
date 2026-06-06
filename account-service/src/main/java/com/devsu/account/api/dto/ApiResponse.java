package com.devsu.account.api.dto;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorInfo error,
        String correlationId
) {

    public record ErrorInfo(String code, String message) {
    }

    public static <T> ApiResponse<T> success(T data, String correlationId) {
        return new ApiResponse<>(true, data, null, correlationId);
    }

    public static <T> ApiResponse<T> error(String code, String message, String correlationId) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message), correlationId);
    }
}
