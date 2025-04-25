package com.example.web_client_prototype.biz.logging;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Mono;

public class LoggingBodyInserter<T> implements BodyInserter<T, ClientHttpRequest> {

	private static final Logger logger = LoggerFactory.getLogger(LoggingBodyInserter.class);

	private final T body;
	private final Consumer<T> bodyLogger;

	public LoggingBodyInserter(T body, Consumer<T> bodyLogger) {
		this.body = body;
		this.bodyLogger = bodyLogger;
	}

	public static <T> LoggingBodyInserter<T> fromObject(T body) {
		return new LoggingBodyInserter<>(body,
				b -> logger.debug("\n\n----------★★リクエスト★★----------\n★Request Body: {}\n", b));
	}

	@Override
	public Mono<Void> insert(ClientHttpRequest outputMessage, BodyInserter.Context context) {
		bodyLogger.accept(body); // ログ出力
		return BodyInserters.fromValue(body).insert(outputMessage, context); // 本来のボディ挿入
	}
}
