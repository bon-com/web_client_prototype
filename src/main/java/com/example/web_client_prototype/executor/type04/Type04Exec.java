package com.example.web_client_prototype.executor.type04;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.resource.Resource;

public class Type04Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type1/1")
					.build()
					.toUri();

			// API導通
			ResponseEntity<Resource> resEntity = client.getEntity(uri, Resource.class);
			System.out.println(resEntity.getStatusCode());
			System.out.println(resEntity.getHeaders());
			System.out.println(resEntity.getBody());
		}
	}
}
