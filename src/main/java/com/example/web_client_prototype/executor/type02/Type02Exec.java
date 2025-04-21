package com.example.web_client_prototype.executor.type02;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.resource.Resource;

public class Type02Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type1/{id}")
					.buildAndExpand(2)
					.toUri();
			// API導通
			Resource resBody = client.getBody(uri, Resource.class);
			System.out.println(resBody);
		}
	}
}
