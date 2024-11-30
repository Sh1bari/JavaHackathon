package com.example.mockservice.exceptions;

import lombok.Data;

import java.util.Date;

/**
 * @author Vladimir Krasnov
 */
@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
