package com.example.web_client_prototype.executor.type12;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.resource.Resource;

public class Type12Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type2/create")
					.build()
					.toUri();

			// リクエストボディ
			var req = new Resource("4", "パスタ", LocalDate.of(2022, 5, 1));
			try {
				ResponseEntity<Void> resEntity = client.postForEntityWithNoBody(uri, req);
				System.out.println("★★結果★★");
				System.out.println(resEntity.getStatusCode());
				System.out.println(resEntity.getHeaders());
				System.out.println(resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
		}
	}
}
