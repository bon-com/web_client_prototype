package com.example.test_package;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.HttpMethod;

import com.example.web_client_prototype.biz.WebClientRequest;

public class ThreadSafetyTest {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // 10スレッド並列実行

        for (int i = 0; i < 10; i++) {
            int threadNumber = i;
            
            // ExecutorServiceは複数スレッドを立ち上げて、並行実行の動作確認をするのに便利らしい
            // 以下は複数スレッドが同時にBuilderを使っても安全かという確認
            executor.submit(() -> {
				try {
					WebClientRequest request = WebClientRequest.builder()
					        .method(HttpMethod.GET)
					        .uri(new URI("http://example.com/" + threadNumber))
					        .header("X-Thread", String.valueOf(threadNumber))
					        .body(String.valueOf(threadNumber))
					        .build();
					System.out.println("Thread " + threadNumber + ": " + request);
				} catch (URISyntaxException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
            });
        }

        executor.shutdown();
    }
}
