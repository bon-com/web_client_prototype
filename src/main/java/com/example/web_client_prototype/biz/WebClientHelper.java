package com.example.web_client_prototype.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

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
	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(WebClientHelper.class);
	
	@Autowired
	private WebClient webClient;
	
	/**
	 * 汎用的なAPI通信を行う
	 * @param <T>
	 * @param req
	 * @param typeRef
	 * @return
	 */
	public <T> ResponseEntity<T> callForEntity(WebClientRequest req, ParameterizedTypeReference<T> typeRef) {
		return call(req, typeRef);
	}
	
	/**
	 * リクエスト送信
	 * @param <T>
	 * @param request
	 * @param typeRef
	 * @return
	 */
	private <T> ResponseEntity<T> call(WebClientRequest req, ParameterizedTypeReference<T> typeRef) {
	    WebClient.RequestBodySpec spec = webClient
	        .method(req.getMethod())
	        .uri(req.getUri())
	        .headers(httpHeaders -> httpHeaders.addAll(req.getHeaders()));

	    Mono<ResponseEntity<T>> mono = (req.getBody() != null)
	        ? spec.body(LoggingBodyInserter.fromObject(req.getBody())).exchangeToMono(res -> handleResponse(res, typeRef))
	        : spec.exchangeToMono(res -> handleResponse(res, typeRef))
			.doOnError(e -> {
				logger.warn("WebClientエラー発生: {}", e.toString());
			})
	        .onErrorResume(e -> {
	            // 特定の想定された例外はそのまま通す
	            if (e instanceof ClientErrorException ||
	                e instanceof ServerErrorException ||
	                e instanceof UnknownErrorException) {
	                return Mono.error(e); // rethrow
	            } else {
	                // 想定外の例外を IllegalStateException にラップして通知
	                return Mono.error(new IllegalStateException("想定外エラー", e));
	            }
	        });

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
