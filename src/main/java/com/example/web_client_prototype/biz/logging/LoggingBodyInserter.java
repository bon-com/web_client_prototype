package com.example.web_client_prototype.biz.logging;

import java.util.function.Consumer;

import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Mono;

public class LoggingBodyInserter<T> implements BodyInserter<T, ClientHttpRequest> {

    private final T body;
    private final Consumer<T> logger;

    public LoggingBodyInserter(T body, Consumer<T> logger) {
        this.body = body;
        this.logger = logger;
    }

    public static <T> LoggingBodyInserter<T> fromObject(T body) {
        return new LoggingBodyInserter<>(body, b -> System.out.println("★Request Body: " + b));
    }

    @Override
    public Mono<Void> insert(ClientHttpRequest outputMessage, BodyInserter.Context context) {
        logger.accept(body); // ログ出力
        return BodyInserters.fromValue(body).insert(outputMessage, context); // 本来のボディ挿入
    }
}
