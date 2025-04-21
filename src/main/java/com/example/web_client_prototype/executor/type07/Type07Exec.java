package com.example.web_client_prototype.executor.type07;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.resource.Resource;

public class Type07Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type1/2")
					.build()
					.toUri();

			try {
				// API導通
				Resource res = client.getBodyWithHandle(uri, Resource.class);
				System.out.println(res);
			} catch (ClientErrorException e) {
				// 4xxエラーのみハンドリング
				System.out.println("エラーメッセージ：" + e.getMessage());
				System.out.println("ステータス：" + e.getStatus());
			}
		}
	}
}
