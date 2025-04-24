package com.example.web_client_prototype.biz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * WebClientのBean定義
 */
@Configuration
public class WebClientConfig {

	private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

	@Bean
	public HttpClient httpClient() {
		return HttpClient.create();
	}

	@Bean
	public ReactorClientHttpConnector reactorClientHttpConnector(HttpClient httpClient) {
		return new ReactorClientHttpConnector(httpClient);
	}

	@Bean
	public WebClient webClient(ReactorClientHttpConnector reactorClientHttpConnector) {
		return WebClient.builder()
				.clientConnector(reactorClientHttpConnector)
				.filter(logRequest())
				.filter(logResponse())
				.build();
	}

	// リクエストのログを出力
	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			// リクエストメソッド、URLをログに出力
			StringBuilder sb = new StringBuilder();
			sb.append("----------★★リクエスト★★----------\n")
					.append("★Request Method: ").append(clientRequest.method()).append("\n")
					.append("★Request URI: ").append(clientRequest.url()).append("\n")
					.append("★Request Headers:\n");

			clientRequest.headers().forEach((name, values) -> values
					.forEach(value -> sb.append("  ")
							.append(name)
							.append(": ")
							.append(value)
							.append("\n")));

			logger.debug(sb.toString());

			// リクエストボディのログ出力はここで共通化できない
			return Mono.just(clientRequest);
		});
	}

	// レスポンスのログを出力
	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			// レスポンスボディをキャッシュしてログに出す
			return response.bodyToMono(String.class)
					.flatMap(body -> {
						StringBuilder sb = new StringBuilder();
						sb.append("----------★★レスポンス★★----------\n")
								.append("★Response Status Code: ").append(response.statusCode()).append("\n")
								.append("★Response Headers:\n");

						response.headers().asHttpHeaders().forEach((name, values) -> values.forEach(
								value -> sb.append("  ")
								.append(name)
								.append(": ")
								.append(value)
								.append("\n")));

						sb.append("★Response Body: ").append(body);
						logger.debug(sb.toString());

						// bodyを再度、呼び出し元で使えるように再作成
						ClientResponse newResponse = ClientResponse.create(response.statusCode())
								.headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
								.body(body)
								.build();

						return Mono.just(newResponse);
					});
		});
	}
}
