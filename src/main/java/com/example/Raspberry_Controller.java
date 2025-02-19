package com.example;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.util.Base64;

import static com.pi4j.Pi4J.newAutoContext;

public class Raspberry_Controller {
    static private String baseURL = "http://localhost:8080/api/logic";
    public static void main(String[] args) {
        var pi4j = newAutoContext();
        int buttonNumber = 27;

        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .name("Button")
                .id("Button " + String.valueOf(buttonNumber))
                .address(buttonNumber)
                .pull(PullResistance.PULL_DOWN)
                .debounce(1000L);

        var button = pi4j.create(buttonConfig);

        button.addListener(e -> {
           if (e.state() == DigitalState.HIGH) {
               System.out.println("Knopf gedrückt");
                int thisButtonNumber = buttonNumber;
               try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                   String message = "{\"message\":\"Button clicked: "+ thisButtonNumber+"\"}";

                   HttpPost postRequest = new HttpPost(baseURL + "/buttonIsClicked");
                   postRequest.setHeader("Content-Type", "application/json");

                   postRequest.setEntity(new StringEntity(message));

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
               } catch (Exception exception){
                   exception.getStackTrace();
               }
           }
        });
    }
}
