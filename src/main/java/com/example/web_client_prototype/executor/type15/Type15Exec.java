package com.example.web_client_prototype.executor.type15;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;

public class Type15Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type11/xml")
					.build()
					.toUri();

			// API導通
			ResponseEntity<byte[]> resEntity = client.getEntity(uri, byte[].class);
			System.out.println(resEntity.getStatusCode());
			System.out.println(resEntity.getHeaders());
			System.out.println(resEntity.getBody());
		}
	}
}
