package com.example.web_client_prototype.executor.type11;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.resource.Resource;

public class Type11Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type5")
					.build()
					.toUri();

			try {
				// API導通：WebClientResponseExceptionがスローされるケース
				Resource res = client.getBody(uri, Resource.class);
				System.out.println(res);
			} catch (WebClientResponseException e) {
			    if (e.getStatusCode().is4xxClientError()) {
			        // 4xxエラーをハンドリングする場合
			    	System.out.println("4xxエラー");
					System.out.println("エラーメッセージ：" + e.getMessage());
					System.out.println("ステータス：" + e.getStatusCode());
			    } else if (e.getStatusCode().is5xxServerError()) {
			        // 5xxエラーをハンドリングする場合
			    	System.out.println("5xxエラー");
					System.out.println("エラーメッセージ：" + e.getMessage());
					System.out.println("ステータス：" + e.getStatusCode());
			    } else {
			        // その他のエラー
			    	System.out.println("その他エラー");
					System.out.println("エラーメッセージ：" + e.getMessage());
					System.out.println("ステータス：" + e.getStatusCode());
			    }
			} catch (WebClientException e) {
			    // その他の WebClient に関するエラー
			    System.out.println("WebClient error: " + e.getMessage());
			}
			
			System.out.println(); // 単純な改行
			
			try {
				// API導通：カスタム例外がスローされるケース
				Resource res = client.getBodyWithHandleStatus(uri, Resource.class);
				System.out.println(res);
			} catch (ClientErrorException e) {
				// 4xxエラーのみハンドリングする場合
				System.out.println("エラーメッセージ：" + e.getMessage());
				System.out.println("ステータス：" + e.getStatus());
			} catch (WebClientException e) {
			    // その他の WebClient に関するエラー
			    System.out.println("WebClient error: " + e.getMessage());
			}
			
			
		}
	}
}
