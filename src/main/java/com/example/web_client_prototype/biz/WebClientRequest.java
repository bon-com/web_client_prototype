package com.example.web_client_prototype.biz;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RequestEntityライクなラッパークラス
 */
@Data
@AllArgsConstructor
public class WebClientRequest {
	/*
	 * WebClientRequest.builder()を呼び出し
	 * メソッドチェーンにて各種リクエスト情報を設定し、
	 * 最終的にbuild()を呼び出してWebClientRequestインスタンスを生成する
	 * new WebClientRequest(...)で生成していないだけ
	 */
	
	private final HttpMethod method;
	private final URI uri;
	private final HttpHeaders headers;
	private final Object body;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private HttpMethod method;
		private URI uri;
		private HttpHeaders headers = new HttpHeaders();
		private Object body;

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }
        
        public Builder uri(URI uri) {
        	this.uri = uri;
        	return this;
        }
        
        public Builder header(String name, String val) {
        	this.headers.add(name, val);
        	return this;
        	
        }
        
        public Builder body(Object body) {
        	this.body = body;
        	return this;
        }
        
        public WebClientRequest build() {
        	return new WebClientRequest(this.method, this.uri, this.headers, this.body);
        }
	}
}
