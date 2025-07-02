package com.example.web_client_prototype.biz;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

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
	private HttpHeaders headers;
	private final Object body;
	
	/** WebClientRequest構築後に別途ヘッダーを設定したいとき */
	public void addHeader(String name, String val) {
	    this.headers.add(name, val);
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private HttpMethod method;
		private HttpHeaders headers = new HttpHeaders();
		private Object body;
		private String urlTemplate;
		private final Map<String, Object> queryParams = new HashMap<>();
		private final Map<String, Object> pathParams = new HashMap<>();


        public Builder method(HttpMethod method) {
            this.method = method;
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
        
        public Builder url(String urlTemplate) {
            this.urlTemplate = urlTemplate;
            return this;
        }
        
        public Builder queryParam(String key, String val) {
            this.queryParams.put(key, val);
            return this;
        }
        
        public Builder pathParam(String key, String val) {
            this.pathParams.put(key, val);
            return this;
        }
        
        public WebClientRequest build() {
        	if (method == null) {
        		throw new IllegalStateException("HTTP method must not be null.");
        	}
        	
        	if (urlTemplate == null) {
        		throw new IllegalStateException("URI must not be null.");
        	}
        	
        	UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
    		// クエリパラメータ設定
			if (queryParams != null) {
				for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
					uriBuilder.queryParam(entry.getKey(), entry.getValue());
				}
			}

			// 最終的にURIを生成
			URI uri = uriBuilder.buildAndExpand(pathParams != null ? pathParams : Collections.emptyMap()) // パスパラメータ設定
					.encode() // URIエンコード
					.toUri();
        	return new WebClientRequest(this.method, uri, this.headers, this.body);
        }
	}
}
