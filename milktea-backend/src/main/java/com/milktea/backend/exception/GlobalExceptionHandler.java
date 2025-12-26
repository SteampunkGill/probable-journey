package com.milktea.backend.exception;

import com.milktea.backend.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleServiceException(ServiceException e) {
        log.error("业务异常: code={}, message={}", e.getErrorCode(), e.getMessage());
        // 业务异常返回 400 业务码，但 HTTP 状态码保持 200
        return ApiResponse.error(400, e.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleDataException(org.springframework.dao.DataIntegrityViolationException e) {
        log.error("数据库约束异常: ", e);
        String message = "数据库操作失败";
        Throwable rootCause = e.getRootCause();
        if (rootCause != null) {
            String rootMsg = rootCause.getMessage();
            if (rootMsg != null) {
                if (rootMsg.contains("Duplicate entry")) {
                    message = "数据已存在，请检查唯一性字段";
                } else if (rootMsg.contains("cannot be null")) {
                    String column = rootMsg.substring(rootMsg.indexOf("'") + 1);
                    column = column.substring(0, column.indexOf("'"));
                    message = "必填字段缺失: " + column;
                } else if (rootMsg.contains("Data too long")) {
                    message = "输入内容过长";
                } else {
                    message = "数据库约束错误: " + rootMsg;
                }
            }
        }
        return ApiResponse.error(500, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统未解析异常: ", e);
        return ApiResponse.error(500, "服务器内部错误: " + e.getMessage());
    }
}