package com.example.web_client_prototype.executor.type05;

import java.net.URI;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.resource.Resource;

public class Type05Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type5/")
					.build()
					.toUri();

			// API導通
			ResponseEntity<List<Resource>> resEntity = client.getEntityList(uri, Resource.class);
			System.out.println(resEntity.getStatusCode());
			System.out.println(resEntity.getHeaders());
			System.out.println(resEntity.getBody());
		}
	}
}
