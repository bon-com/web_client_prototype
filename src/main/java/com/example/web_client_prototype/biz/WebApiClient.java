package com.example.web_client_prototype.biz;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.web_client_prototype.biz.logging.LoggingBodyInserter;
import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.exception.ServerErrorException;
import com.example.web_client_prototype.exception.UnknownErrorException;

import reactor.core.publisher.Mono;

/**
 * WebClientを使用したAPI疎通クラス
 */
@Component
public class WebApiClient {

	private static final Logger logger = LoggerFactory.getLogger(WebApiClient.class);
	
	@Autowired
	private WebClient webClient;

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBody(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.retrieve() // リクエスト送信
				.bodyToMono(responseType) // レスポンスボディを指定した型で受け取る
				.block();
	}

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBodyWithHandleStatus(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.retrieve() // リクエスト送信
				.onStatus(status -> status.is4xxClientError(), res -> {
					// 4xx系エラー
					return res.createException().flatMap(ex -> Mono
							.error(new ClientErrorException("Client Error: " + ex.getMessage(), ex.getStatusCode())));
				})
				.onStatus(status -> status.is5xxServerError(), res -> {
					// 5xx系エラー
					return res.createException()
							.flatMap(ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
				})
				.onStatus(status-> !status.is2xxSuccessful(), res -> {
					// 2xx系以外
					return res.createException()
							.flatMap(ex -> Mono
									.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
				})
				.bodyToMono(responseType) // レスポンスボディを指定した型で受け取る
				.block();
	}

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得（ジェネリクスを含む）
	 * ParameterizedTypeReferenceを渡すと、戻り値は引数で指定したTとなる
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBody(URI uri, ParameterizedTypeReference<T> responseType) {
		return webClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(responseType)
				.block();
	}

	/**
	 * GETリクエストを行ない、指定した型のボディをもつResponseEntityを取得する
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> ResponseEntity<T> getEntity(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.retrieve()
				.toEntity(responseType)
				.block();
	}

	/**
	 * GETリクエストを行ない、指定した型のリストのボディをもつResponseEntityを取得する
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> ResponseEntity<List<T>> getEntityList(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.retrieve()
				.toEntityList(responseType)
				.block();
	}

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBodyWithHandle(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.bodyToMono(responseType);
				})
				.block();
	}

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型のリストで取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> List<T> getBodyListWithHandle(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.bodyToFlux(responseType).collectList();
				})
				.block();
	}

	/**
	 * GETリクエストを行ない、指定した型のレスポンスボディを保持するResponseEntityで取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> ResponseEntity<T> getEntityWithHandle(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.toEntity(responseType);
				})
				.block();
	}

	/**
	 * GETリクエストを行ない、指定した型のレスポンスボディのリストを保持するResponseEntityで取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> ResponseEntity<List<T>> getEntityListWithHandle(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.toEntityList(responseType);
				})
				.block();
	}
	
	/**
	 * POST通信を行う
	 * レスポンスはボディ部なしのResponseEntityで取得
	 * @param uri
	 * @param requestBody
	 * @return
	 */
	public ResponseEntity<Void> postForEntityWithNoBody(URI uri, Object requestBody) {
	    return webClient.post()
	            .uri(uri)
	            .bodyValue(requestBody)
	            .retrieve()
	            .toBodilessEntity() // レスポンスボディを無視
	            .block();
	}
	
	/**
	 * POST通信を行う
	 * HTTPステータスを返却
	 * @param requestBody
	 */
	public HttpStatus postForStatus(URI uri, Object requestBody) {
	    return webClient.post()
	            .uri(uri)
	            .bodyValue(requestBody)
	            .exchangeToMono(response -> Mono.just(response.statusCode()))
	            .block();
	}
	

	public ResponseEntity<Void> postForEntityWithHandle(URI uri, Object requestBody) {
		return webClient.post()
				.uri(uri)
				.body(LoggingBodyInserter.fromObject(requestBody)) // リクエストボディをログ出力
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.toEntity(Void.class);
				})
				.block();
	}
	
	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBodyWithHandleError(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.bodyToMono(responseType);
				})
				.doOnError(e -> {
					// doOnError：エラーが発生したときにログ出力や通知などの副作用（ログ、監視）を行うだけ
					logger.warn("WebClientエラー発生: {}", e.toString());
				})
				.block();
	}

	/**
	 * GETリクエストを行ない、レスポンスボディを指定した型で取得
	 * エラー時はカスタム例外をスローする
	 * @param <T>
	 * @param uri
	 * @param responseType
	 * @return
	 */
	public <T> T getBodyWithHandleError2(URI uri, Class<T> responseType) {
		return webClient.get()
				.uri(uri)
				.exchangeToMono(res -> { // ClientResponseが返却される
					if (res.statusCode().is4xxClientError()) {
						// 4xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ClientErrorException("Client Error: " + ex.getMessage(),
												ex.getStatusCode())));
					} else if (res.statusCode().is5xxServerError()) {
						// 5xxエラー
						return res.createException()
								.flatMap(
										ex -> Mono.error(new ServerErrorException("Server Error: " + ex.getMessage())));
					} else if (!res.statusCode().is2xxSuccessful()) {
						// 想定外エラー（2xx, 4xx, 5xx以外）
						return res.createException()
								.flatMap(ex -> Mono
										.error(new UnknownErrorException("Unexpected Error: " + ex.getMessage())));
					}

					// 2xxステータス
					return res.bodyToMono(responseType);
				})
				.doOnError(e -> { // doOnError：エラーが発生したときにログ出力や通知などの副作用（ログ、監視）を行うだけ
					logger.warn("WebClientエラー発生: {}", e.toString());
				})
				.onErrorResume(e -> { // リアクティブストリーム（Mono / Flux）内で発生した あらゆる例外（Throwable）をキャッチして処理する
					if (e instanceof ClientErrorException || e instanceof ServerErrorException || e instanceof UnknownErrorException) {
						return Mono.error(e);
					} else {
						return Mono.error(new IllegalStateException("想定外エラー", e));
					}
				})
				.block();
	}
}
