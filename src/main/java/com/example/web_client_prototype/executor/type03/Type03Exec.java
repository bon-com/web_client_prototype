package com.example.web_client_prototype.executor.type03;

import java.net.URI;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.web_client_prototype.biz.WebApiClient;
import com.example.web_client_prototype.resource.Resource;

public class Type03Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// URI定義
			URI uri = UriComponentsBuilder
					.fromUriString("http://localhost:8080/rest_prototype/type5/")
					.build()
					.toUri();

			// API導通
			List<Resource> resList = client.getBody(uri, new ParameterizedTypeReference<List<Resource>>() {
			});
			resList.forEach(r -> System.out.println(r));
		}
	}
}
