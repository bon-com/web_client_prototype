package com.example.web_client_prototype.executor.type17;

import java.net.URI;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.web_client_prototype.biz.WebApiClient;

public class Type17Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var client = context.getBean(WebApiClient.class);

			// API導通（応答しないダミーIP： ⇒ io.netty.channel.ConnectTimeoutExceptionが発生　※接続タイムアウト）
			String jsonStr = client.getBodyWithHandleError2(URI.create("http://192.0.2.1:8080/rest_prototype/type13/timeout"), String.class);
			System.out.println("レスポンス：" + jsonStr);
		}
	}
}
