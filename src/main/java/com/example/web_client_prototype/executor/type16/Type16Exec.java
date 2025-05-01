package com.example.web_client_prototype.executor.type16;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.web_client_prototype.biz.WebClientHelper;
import com.example.web_client_prototype.resource.Resource;

public class Type16Exec {
	public static void main(String args[]) {
		try (var context = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml")) {
			var helper = context.getBean(WebClientHelper.class);

			try {
				// ------------POST------------
				// リクエストボディ
				var req = new Resource("4", "パリ", LocalDate.of(2025, 5, 1));
				ResponseEntity<Void> resEntity = helper.callForEntity(
						"http://localhost:8080/rest_prototype/type2/create/", 
						HttpMethod.POST, 
						req, 
						null, 
						null,
						new ParameterizedTypeReference<Void>() {});
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			try {
				// ------------GET------------
				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						"http://localhost:8080/rest_prototype/type5/", 
						HttpMethod.GET, 
						null, 
						null, 
						null,
						new ParameterizedTypeReference<List<Resource>>() {});
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			try {
				// ------------GET------------
				// クエリパラメータ
				var queryParams = new HashMap<String, Object>();
				queryParams.put("name", "ご");
				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						"http://localhost:8080/rest_prototype/type5/", 
						HttpMethod.GET, 
						null, 
						queryParams, 
						null,
						new ParameterizedTypeReference<List<Resource>>() {});
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			try {
				// ------------PUT------------
				// リクエストボディ
				var req = new Resource("4", "パソコン", LocalDate.of(2025, 3, 1));
				// パスパラメータ
				var pathParam = new HashMap<String, Object>();
				pathParam.put("id", 4);
				
				ResponseEntity<Void> resEntity = helper.callForEntity(
						"http://localhost:8080/rest_prototype/type3/{id}/", 
						HttpMethod.PUT, 
						req, 
						null, 
						pathParam,
						new ParameterizedTypeReference<Void>() {});
				
				// 動確
				helper.callForEntity(
						"http://localhost:8080/rest_prototype/type5/", 
						HttpMethod.GET, 
						null, 
						null, 
						null,
						new ParameterizedTypeReference<List<Resource>>() {});
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
			try {
				// ------------DELETE------------
				// パスパラメータ
				var pathParam = new HashMap<String, Object>();
				pathParam.put("id", 4);
				
				ResponseEntity<List<Resource>> resEntity = helper.callForEntity(
						"http://localhost:8080/rest_prototype/type4/{id}/", 
						HttpMethod.DELETE, 
						null, 
						null, 
						pathParam,
						new ParameterizedTypeReference<List<Resource>>() {});
				
				// 動確
				helper.callForEntity(
						"http://localhost:8080/rest_prototype/type5/", 
						HttpMethod.GET, 
						null, 
						null, 
						null,
						new ParameterizedTypeReference<List<Resource>>() {});
			} catch (WebClientResponseException e) {
				e.printStackTrace();
			}
			
		}
	}
}
