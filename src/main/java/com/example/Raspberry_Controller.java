package com.example;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Response;
import org.springframework.http.MediaType;

import java.util.Base64;

import static com.pi4j.Pi4J.newAutoContext;

public class Raspberry_Controller {
    static private String baseURL = "localhost:8080/api/logic";
    public static void main(String[] args) {
        var pi4j = newAutoContext();
        Client firstClient = ClientBuilder.newClient();
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
               WebTarget webTarget = firstClient.target(baseURL + "/buttonIsClicked");
               String message = "{\"message\":\"Button clicked: "+ thisButtonNumber+"\"}";
               Invocation.Builder request = webTarget.request(String.valueOf(MediaType.APPLICATION_JSON));
               Response response = request.post(Entity.entity(message, String.valueOf(MediaType.APPLICATION_JSON)));
               //Response response = request.get();
               if (response.getStatus() == 200) {
                   System.out.println("Antwort vom Server: " + response.readEntity(String.class));
               } else {
                   System.out.println("Fehler: " + response.getStatus());
               }
           }
        });
    }
}
