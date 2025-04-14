package com.house.rental.common.exception;

import com.house.rental.common.response.ResultCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final int code;

    public ServiceException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public ServiceException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
} 