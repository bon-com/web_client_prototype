package com.example.web_client_prototype.executor.type10;

import java.net.URI;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.exception.ClientErrorException;
import com.example.web_client_prototype.resource.Resource;

public class Type10Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type5/")
					.build()
					.toUri();

			try {
				// API導通
				ResponseEntity<List<Resource>> resEntity = client.getEntityListWithHandle(uri, Resource.class);
			} catch (ClientErrorException e) {
				// 4xxエラーのみハンドリング
				System.out.println("エラーメッセージ：" + e.getMessage());
				System.out.println("ステータス：" + e.getStatus());
			}
		}
	}
}
