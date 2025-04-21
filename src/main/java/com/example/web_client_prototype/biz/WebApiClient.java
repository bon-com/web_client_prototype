package com.example.web_client_prototype.biz;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.exception.ServerErrorException;
import com.example.web_client_prototype.exception.UnknownErrorException;

import reactor.core.publisher.Mono;

/**
 * WebClientを使用したAPI疎通クラス
 */
@Component
public class WebApiClient {

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

}
