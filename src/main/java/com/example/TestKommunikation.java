package com.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TestKommunikation {

    public static void main(String[] args) {
        // Erstelle einen HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Definiere die Basis-URL und den Endpunkt
            String baseURL = "http://localhost:8080/api/logic";
            String endpoint = "/buttonIsClicked";

            HttpPost postRequest = new HttpPost(baseURL + endpoint);
            postRequest.setHeader("Content-Type", "application/json");

            String jsonBody = "{\"message\": \"Button wurde geklickt 1\"}";
            postRequest.setEntity(new StringEntity(jsonBody));

            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}