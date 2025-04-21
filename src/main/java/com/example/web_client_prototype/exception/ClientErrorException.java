package com.example.web_client_prototype.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

// 4xx系エラー
@Getter
@Setter
public class ClientErrorException extends RuntimeException {
	
	private HttpStatus status;
	
    public ClientErrorException(String message) {
        super(message);
    }
    
    public ClientErrorException(String message, HttpStatus status) {
    	super(message);
    	this.status = status;
    }
}
