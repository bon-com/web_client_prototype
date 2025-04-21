package com.example.web_client_prototype.executor.type01;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;

public class Type01Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type5/")
					.build()
					.toUri();

			// API導通
			String jsonStr = client.getBody(uri, String.class);
			System.out.println(jsonStr);
		}
	}
}
