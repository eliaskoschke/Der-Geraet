package com.example;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

public class RestTamplateTest {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/test";

        while(true) {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                System.out.println("Erhaltene Daten: " + responseBody);
            } else {
                System.out.println("Fehler beim Abrufen der Daten: " + response.getStatusCode());
            }
            Thread.sleep(100);
        }

    }
}
