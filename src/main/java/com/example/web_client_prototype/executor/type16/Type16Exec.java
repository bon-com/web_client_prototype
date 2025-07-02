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
				// リクエスト情報設定
				var body = new Resource("4", "パリ", LocalDate.of(2025, 5, 1));
				var req = WebClientRequest.builder()
						.method(HttpMethod.POST)
						.url("http://localhost:8080/rest_prototype/type2/create/")
						.body(body)
						.build();
				
				// API通信
				ResponseEntity<Void> resEntity = helper.callForEntity(req,
						new ParameterizedTypeReference<Void>() {
						});
				
				// 動作確認
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------GET------------
				// リクエスト情報設定
				var req = WebClientRequest.builder()
						.method(HttpMethod.GET)
						.url("http://localhost:8080/rest_prototype/type1/{id}")
						.pathParam("id", "1") // パスパラメータ
						.build();
				
				// API通信
				ResponseEntity<Resource> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<Resource>() {
						});
				
				// 動作確認
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------GET------------
				// リクエスト情報設定
				var req = WebClientRequest.builder()
						.method(HttpMethod.GET)
						.url("http://localhost:8080/rest_prototype/type5")
						.queryParam("name", "ご") // クエリパラメータ
						.build();

				// API通信
				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<List<Resource>>() {
						});
				
				// 動作確認
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------PUT------------
				// リクエスト情報設定
				var body = new Resource("4", "パソコン", LocalDate.of(2025, 3, 1));
				var req = WebClientRequest.builder()
						.method(HttpMethod.PUT)
						.url("http://localhost:8080/rest_prototype/type3/{id}/")
						.pathParam("id", "4")
						.body(body)
						.build();
				
				// API通信				
				ResponseEntity<Void> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<Void>() {
						});
				
				// 動作確認
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}

			try {
				// ------------DELETE------------
				// リクエスト情報設定
				var req = WebClientRequest.builder()
						.method(HttpMethod.DELETE)
						.url("http://localhost:8080/rest_prototype/type4/{id}")
						.pathParam("id", "4")
						.build();

				// API通信
				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						req,
						new ParameterizedTypeReference<List<Resource>>() {
						});

				// 動作確認
				System.out.println("★★★動作確認★★★");
				System.out.println("★ステータス★： " + resEntity.getStatusCode());
				System.out.println("★ヘッダー★： " + resEntity.getHeaders());
				System.out.println("★ボディ★： " + resEntity.getBody());
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			// 動作確認
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
