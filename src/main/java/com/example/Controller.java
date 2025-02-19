package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class Controller {
    private final GameService gameService;
    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    public Controller(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz() throws JsonProcessingException {
        List<String> playerIds = getListOfAllActiveID();
        System.out.println(playerIds);
        return new ResponseMessage(mapper.writeValueAsString(playerIds));
    }

    private List<String> getListOfAllActiveID() {
        List<String> playerIds = gameService.getListOfAllPlayers().stream()
                .map(Player::getId)
                .toList();
        return playerIds;
    }

    @GetMapping("/isConnected")
    public ResponseMessage isConnected() throws JsonProcessingException {
        return new ResponseMessage("true");
    }

    @PostMapping("/user/playerJoinedTheTable")
    public ResponseMessage playerJoinedTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        //gameService.buttonClicked();
        List<String> playerIds = getListOfAllActiveID();
        if (!playerIds.contains(message.getMessage())) {
            gameService.getListOfAllPlayers().add(new Player(message.getMessage()));
            return new ResponseMessage("acknowledged");
        }
        return new ResponseMessage("not acknowledged");
    }

    @PostMapping("/user/playerLeftTheTable")
    public ResponseMessage playerLeftTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        List<String> playerIds = getListOfAllActiveID();
        if (playerIds.contains(message.getMessage())) {
            gameService.getListOfAllPlayers().removeIf(player -> player.getId().equals(message.getMessage()));
            playerIds = getListOfAllActiveID();
            System.out.println("Es sollte aus der Liste sein " + playerIds);
        }
        return null;
    }

    @GetMapping("/user/ping")
    public ResponseMessage userPing() throws JsonProcessingException {
        if (gameService.isGameStarted()) {
            return new ResponseMessage("Game has started");
        }
        if (gameService.isGameReset()) {
            gameService.setPlayerGotReseted(gameService.getPlayerGotReseted()+1);
            if (gameService.getPlayerGotReseted() >= gameService.getPlayerAtReset()) {
                gameService.setPlayerAtReset(0);
                gameService.setPlayerGotReseted(0);
                gameService.setGameReset(false);
            }
            return new ResponseMessage("true");
        }
        return new ResponseMessage(mapper.writeValueAsString("false"));
    }

    @GetMapping("/admin/ping")
    public ResponseMessage adminPing() throws JsonProcessingException {
        return new ResponseMessage(String.valueOf(gameService.getListOfAllPlayers().size()));
    }

    @PostMapping("/admin/sendPassword")
    public ResponseMessage sendPassword(@RequestBody Message postPassword) {
        if (postPassword.getMessage().equals("1111")) {
            return new ResponseMessage("true");
        }
        return new ResponseMessage("false");
    }

    @PostMapping("/admin/startGame")
    public ResponseMessage startGame(@RequestBody Message postPassword) {
        gameService.setGameStarted(true);
        Collections.sort(gameService.getListOfAllPlayers(), new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(Integer.parseInt(p1.getId()),Integer.parseInt(p2.getId()));
            }
        });
        gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(0));
        return new ResponseMessage("true");
    }

    @PostMapping("/admin/sendReset")
    public ResponseMessage sendReset(@RequestBody Message postPassword) {
        gameService.setGameReset(true);

        gameService.setPlayerAtReset(gameService.getListOfAllPlayers().size());
        gameService.setListOfAllPlayers(new ArrayList<>());
        return new ResponseMessage("true");
    }

    @GetMapping("/game/ping/getDealerHand")
    public ResponseMessage getDealerHand() throws JsonProcessingException {
        gameService.setDealerHandBildId(castKartenObjectToBildId(gameService.getDealerHand()));
        return new ResponseMessage(String.valueOf(gameService.getDealerHandBildId()));
    }

    @GetMapping("/game/ping/getPlayerTurn")
    public ResponseMessage getPlayerTurn() throws JsonProcessingException {
        return new ResponseMessage(gameService.getCurrentPlayer().getId());
    }

    @PostMapping("/logic/buttonIsClicked")
    public ResponseMessage buttonIsClicked(@RequestBody Message message) {
        System.out.println("Hallo");
        String buttonId = message.getMessage().substring(message.getMessage().indexOf(" ")+1);
        System.out.println("Es wurde ein Button geklickt: " + buttonId);
        gameService.setButtonClicked(true);
        return new ResponseMessage("true");
    }

    @PostMapping("/logic/cardGotScanned")
    public ResponseMessage cardGotScanned(@RequestBody Message message) throws JsonProcessingException {
        try{
            Karte karte = mapper.readValue(message.getMessage(), Karte.class);
            return new ResponseMessage("true");
        } catch (Exception e){

        }
        return new ResponseMessage("false");
    }

    public ArrayList<String> castKartenObjectToBildId(ArrayList<Karte>dealerHand){
        ArrayList dealerHandBildId = new ArrayList();
        for(Karte karte : dealerHand){
            int idValue = 0;
            switch (karte.typ){
                case "Karo":
                    idValue += 100;
                    break;
                case "Pik":
                    idValue += 200;
                    break;
                case "Herz":
                    idValue += 300;
                    break;
                case "Kreuz":
                    idValue += 400;
                    break;
            }
            switch (karte.name.substring(karte.name.indexOf(" ")+1)){
                case "Ass":
                    idValue += 1;
                    break;
                case "Bube":
                    idValue += 11;
                    break;
                case "Dame":
                    idValue += 12;
                    break;
                case "König":
                    idValue += 13;
                    break;
                default:
                    idValue += karte.wert;
                    break;
            }
            dealerHandBildId.add(String.valueOf(idValue));
        }
        return  dealerHandBildId;
    }


    public static class Message {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}