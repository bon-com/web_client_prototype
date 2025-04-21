package com.example.web_client_prototype.exception;

// 想定外エラー
public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String message) {
        super(message);
    }
}
