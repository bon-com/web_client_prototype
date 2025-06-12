package com.example.web_client_prototype.executor.type17;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.web_client_prototype.biz.WebApiClient;

public class Type17Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// API導通
			String jsonStr = client.getBodyWithHandleError(URI.create("http://localhost:8080/rest_prototype/type13/timeout"), String.class);
			System.out.println("レスポンス：" + jsonStr);
		}
	}
}
