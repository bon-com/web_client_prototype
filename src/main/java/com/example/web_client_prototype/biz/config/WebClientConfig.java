package com.example.web_client_prototype.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

/**
 * WebClientのBean定義
 */
@Configuration
public class WebClientConfig {
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
				.build();
	}
}
