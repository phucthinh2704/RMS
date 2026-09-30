package com.example.rms.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "RMS_VALIDATION_FAILED",
            "Dữ liệu đầu vào tại RMS không hợp lệ",
            LocalDateTime.now(),
            errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Lỗi truyền sai kiểu dữ liệu trên URL hoặc JSON sai cú pháp
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "RMS_MALFORMED_REQUEST",
            "Định dạng tham số hoặc chuỗi JSON không hợp lệ.",
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 3. Lỗi Không tìm thấy dữ liệu (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "RMS_RESOURCE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 4. Lỗi Quy tắc nghiệp vụ tại RMS (409 Conflict)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "RMS_BUSINESS_VIOLATION",
            ex.getMessage(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 5. Lỗi trùng khóa chính / khóa ngoại trong Database RMS
    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseConstraint(Exception ex) {
        log.warn("Lỗi ràng buộc dữ liệu tại RMS: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "RMS_DATA_INTEGRITY_VIOLATION",
            "Vi phạm ràng buộc dữ liệu (Trùng lặp ID hoặc sai khóa ngoại).",
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 6. Lỗi hệ thống nội bộ (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        log.error("Lỗi hệ thống nghiêm trọng tại RMS: ", ex);
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "RMS_INTERNAL_ERROR",
            "Lỗi hệ thống nội bộ bên phía RMS. Vui lòng liên hệ quản trị viên.",
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}