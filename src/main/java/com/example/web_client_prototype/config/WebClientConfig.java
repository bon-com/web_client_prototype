package com.example.web_client_prototype.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
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
	    return HttpClient.create()
	            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 接続タイムアウト（10秒）
	            .responseTimeout(Duration.ofSeconds(10)); // レスポンス全体のタイムアウト（10秒）
	}

	@Bean
	public ReactorClientHttpConnector reactorClientHttpConnector(HttpClient httpClient) {
		return new ReactorClientHttpConnector(httpClient);
	}

	@Bean
	public WebClient webClient(ReactorClientHttpConnector reactorClientHttpConnector) {
		return WebClient.builder()
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE + ", " + MediaType.APPLICATION_PROBLEM_JSON_VALUE)
				.clientConnector(reactorClientHttpConnector)
				.filter(logRequest())
				.filter(logResponse())
				.build();
	}

	/**
	 * WebClientのリクエストログ出力
	 * リクエストボディは別途「LoggingBodyInserter」で処理している
	 * @return
	 */
	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(req -> {
			// リクエストメソッド、URLをログに出力
			StringBuilder sb = new StringBuilder();
			sb.append("\n\n----------★★リクエスト★★----------\n")
					.append("★Request Method: ").append(req.method()).append("\n")
					.append("★Request URI: ").append(req.url()).append("\n")
					.append("★Request Headers:\n");

			req.headers().forEach((name, values) -> values
					.forEach(value -> sb.append("  ")
							.append(name)
							.append(": ")
							.append(value)
							.append("\n")));
			logger.debug(sb.toString());

			return Mono.just(req);
		});
	}

	/**
	 * WebClientのレスポンスログ出力
	 * @return
	 */
	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(res -> {
			// HTTPステータスとヘッダー情報取得
			StringBuilder sb = createLogWithoutBody(res);

			// レスポンスボディ判定
			long length = res.headers().contentLength().orElse(-1L);
			boolean hasChunked = res.headers().asHttpHeaders()
					.getFirst("Transfer-Encoding") != null &&
					res.headers().asHttpHeaders().getFirst("Transfer-Encoding").equalsIgnoreCase("chunked");

			if ((length == 0 && !hasChunked)
					|| res.statusCode() == HttpStatus.NO_CONTENT // 204
				    || res.statusCode() == HttpStatus.RESET_CONTENT // 205
				    || res.statusCode() == HttpStatus.NOT_MODIFIED) { // 304
				// ボディなしの場合
				sb.append("★Response Body: ").append("No Body\n");
				logger.debug(sb.toString());

				return Mono.just(res);
			}

			// ボディありの場合
			return res.bodyToMono(String.class)
					.flatMap(body -> {
						sb.append("★Response Body: ").append(body).append("\n");
						logger.debug(sb.toString()); // ログにボディ部追加

						// bodyを再度、呼び出し元で使えるように再作成
						ClientResponse newResponse = ClientResponse.create(res.statusCode())
								.headers(headers -> headers.addAll(res.headers().asHttpHeaders()))
								.body(body)
								.build();

						return Mono.just(newResponse);
					});
		});
	}

	/**
	 * レスポンスボディ以外のログ情報を作成する
	 * @param res
	 * @return
	 */
	private StringBuilder createLogWithoutBody(ClientResponse res) {
		var sb = new StringBuilder();
		sb.append("\n\n----------★★レスポンス★★----------\n")
				.append("★Response Status Code: ").append(res.statusCode()).append("\n")
				.append("★Response Headers:\n");

		res.headers().asHttpHeaders().forEach((name, values) -> values.forEach(
				value -> sb.append("  ")
						.append(name)
						.append(": ")
						.append(value)
						.append("\n")));

		return sb;
	}
}
