package com.example.web_client_prototype.biz;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.logging.LoggingBodyInserter;
import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.exception.ServerErrorException;
import com.example.web_client_prototype.exception.UnknownErrorException;

import reactor.core.publisher.Mono;

/**
 * 汎用的なAPIアクセスを行うヘルパー
 */
@Component
public class WebClientHelper {
	
	@Autowired
	private WebClient webClient;
	
	/**
	 * 汎用的なAPI通信メソッド
	 * @param <T>
	 * @param url
	 * @param method
	 * @param body
	 * @param queryParams
	 * @param pathParams
	 * @param typeRef
	 * @return ResponseEntity<T>またはResponseEntity<List<T>>などを返却
	 */
	public <T> ResponseEntity<T> callForEntity(String url, HttpMethod method, Object body, 
			Map<String, Object> queryParams, Map<String, Object> pathParams, ParameterizedTypeReference<T> typeRef) {
		
		URI uri = createUri(url, queryParams, pathParams);
		WebClientRequest req = WebClientRequest.builder()
				.uri(uri)
				.method(method)
				.header("Cookie", "xxxx") // 追加したいヘッダー項目があれば
				.body(body)
				.build();
		
		return call(req, typeRef);
	}
	
	/**
	 * URIを作成
	 * @param url
	 * @param queryParams
	 * @param pathParams
	 * @return
	 */
	private URI createUri(String url, Map<String, Object> queryParams, Map<String, Object> pathParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

		// クエリパラメータ設定
		if (queryParams != null) {
			for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}
		}

		// 最終的にURIを生成
		URI uri = builder.buildAndExpand(pathParams != null ? pathParams : Collections.emptyMap()) // パスパラメータ設定
				.encode() // URIエンコード
				.toUri();
		
		return uri;
	}
	
	/**
	 * API疎通を行う
	 * @param <T>
	 * @param request
	 * @param typeRef
	 * @return
	 */
	private <T> ResponseEntity<T> call(WebClientRequest request, ParameterizedTypeReference<T> typeRef) {
	    WebClient.RequestBodySpec spec = webClient
	        .method(request.getMethod())
	        .uri(request.getUri())
	        .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()));

	    Mono<ResponseEntity<T>> mono = (request.getBody() != null)
	        ? spec.body(LoggingBodyInserter.fromObject(request.getBody())).exchangeToMono(res -> handleResponse(res, typeRef))
	        : spec.exchangeToMono(res -> handleResponse(res, typeRef));

	    return mono.block();
	}

	/**
	 * API疎通結果（レスポンス）を制御する
	 * @param <T>
	 * @param res
	 * @param typeRef
	 * @return 200ステータスの場合、ResponseEntityを返却
	 */
    private <T> Mono<ResponseEntity<T>> handleResponse(ClientResponse res, ParameterizedTypeReference<T> typeRef) {
        if (res.statusCode().is4xxClientError()) {
            return res.createException().flatMap(ex ->
                Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(), ex.getStatusCode()))
            );
        } else if (res.statusCode().is5xxServerError()) {
            return res.createException().flatMap(ex ->
                Mono.error(new ServerErrorException("Server Error: " + ex.getMessage()))
            );
        } else if (!res.statusCode().is2xxSuccessful()) {
            return res.createException().flatMap(ex ->
                Mono.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage()))
            );
        }

        return res.toEntity(typeRef);
    }
}
