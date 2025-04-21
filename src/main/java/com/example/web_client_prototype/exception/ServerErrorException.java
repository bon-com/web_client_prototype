package com.example.web_client_prototype.exception;

// 5xx系エラー
public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }

}
