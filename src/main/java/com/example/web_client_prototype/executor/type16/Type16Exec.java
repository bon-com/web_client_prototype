package com.example.web_client_prototype.executor.type16;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.web_client_prototype.biz.WebClientHelper;
import com.example.web_client_prototype.biz.WebClientRequest;
import com.example.web_client_prototype.resource.Resource;

public class Type16Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var helper = context.getBean(WebClientHelper.class);

			try {
				// ------------POST------------
				// リクエストボディ
				var body = new Resource("4", "パリ", LocalDate.of(2025, 5, 1));
				var req = WebClientRequest.builder()
						.method(HttpMethod.POST)
						.url("http://localhost:8080/rest_prototype/type2/create/")
						.body(body)
						.build();

				ResponseEntity<Void> resEntity = helper.callForEntity(req,
						new ParameterizedTypeReference<Void>() {
						});
				
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------GET------------
				var req = WebClientRequest.builder()
						.method(HttpMethod.GET)
						.url("http://localhost:8080/rest_prototype/type1/{id}")
						.pathParam("id", "1") // パスパラメータ
						.build();
				
				ResponseEntity<Resource> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<Resource>() {
						});
				
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------GET------------
				var req = WebClientRequest.builder()
						.method(HttpMethod.GET)
						.url("http://localhost:8080/rest_prototype/type5")
						.queryParam("name", "ご") // クエリパラメータ
						.build();

				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<List<Resource>>() {
						});
				
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------PUT------------
				// リクエストボディ
				var body = new Resource("4", "パソコン", LocalDate.of(2025, 3, 1));
				var req = WebClientRequest.builder()
						.method(HttpMethod.PUT)
						.url("http://localhost:8080/rest_prototype/type3/{id}/")
						.pathParam("id", "4")
						.body(body)
						.build();
				
				ResponseEntity<Void> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<Void>() {
						});
				
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------DELETE------------
				var req = WebClientRequest.builder()
						.method(HttpMethod.DELETE)
						.url("http://localhost:8080/rest_prototype/type4/{id}")
						.pathParam("id", "4")
						.build();

				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<List<Resource>>() {
						});

				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			var req = WebClientRequest.builder()
					.method(HttpMethod.GET)
					.url("http://localhost:8080/rest_prototype/type5")
					.build();

			ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
					req,
					new ParameterizedTypeReference<List<Resource>>() {
					});
			resEntity.getBody().forEach(r -> System.out.println(r));
			

		}
	}
}
