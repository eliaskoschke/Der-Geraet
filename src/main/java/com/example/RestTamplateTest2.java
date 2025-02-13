package com.example;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTamplateTest2 {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/test";

        // Zu sendende Daten
        Message requestBody = new Message("Wert1");

        // Header erstellen
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // HttpEntity erstellt eine Kombination aus Header und Body
        HttpEntity<Message> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST-Request durchführen und Antwort prüfen
        try {
            ResponseEntity<MyResponseBody> response = restTemplate.postForEntity(url, requestEntity, MyResponseBody.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode().is2xxSuccessful()) {
                MyResponseBody responseBody = response.getBody();
                System.out.println("Erfolg: " + responseBody);
            } else {
                System.out.println("Fehler: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Ein Fehler ist aufgetreten: " + e.getMessage());
        }
    }
}
class MyRequestBody {
    private String field1;
    private String field2;

    public MyRequestBody(String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }
}
class Message {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String message){this.message = message;}
}

class MyResponseBody {
    private String responseField;

    public String getResponseField() {
        return responseField;
    }

    public void setResponseField(String responseField) {
        this.responseField = responseField;
    }

    @Override
    public String toString() {
        return "MyResponseBody{" +
                "responseField='" + responseField + '\'' +
                '}';
    }
}
