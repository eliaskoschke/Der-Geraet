package com.example;

import com.example.tcm2209.TMCDeviceIsBusyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")

//Todo: AnfangsEndpunkte überarbeiten
//      Kommentare für Hauptpunkte einfügen (Eingangsdaten, Ausgangsdaten, Was macht es konkret nicht zu detailiert)
public class Controller {
    private GameService gameService;
    private ObjectMapper mapper = new ObjectMapper();
    int counter = 0;
    int numberOfAllPlayersInGame = 0;
    static boolean executingComand = false;


    @Autowired
    public Controller(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz() throws JsonProcessingException {
        List<String> playerIds = getListOfAllActiveID();
//        System.out.println(playerIds);
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
        return new ResponseMessage(mapper.writeValueAsString(gameService.isConnected()));
    }

    @PostMapping("/user/playerJoinedTheTable")
    public ResponseMessage playerJoinedTheTable(@RequestBody Message message) throws InterruptedException, CloneNotSupportedException {
//        System.out.println("Nachricht erhalten: " + message.getMessage());
        //gameService.buttonClicked();
        List<String> playerIds = getListOfAllActiveID();
        if (!playerIds.contains(message.getMessage())) {
            GameService.registerPlayer();
            gameService.getListOfAllPlayers().add(new Player(message.getMessage()));
            GameService.registerPlayer();
            return new ResponseMessage("true");
        }
        return new ResponseMessage("false");
    }

    @PostMapping("/user/playerLeftTheTable")
    public ResponseMessage playerLeftTheTable(@RequestBody Message message) throws InterruptedException, CloneNotSupportedException {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        List<String> playerIds = getListOfAllActiveID();
        if (playerIds.contains(message.getMessage())) {
            gameService.getListOfAllPlayers().removeIf(player -> player.getId().equals(message.getMessage()));
            playerIds = getListOfAllActiveID();
            System.out.println("Es sollte aus der Liste sein " + playerIds);
            GameService.registerPlayer();
        }
        return new ResponseMessage("thanks");
    }

    //Todo: /Logic
    @GetMapping({"/user/ping", "logic/ping"})
    public ResponseMessage userPing() throws JsonProcessingException, InterruptedException {
        if (gameService.isGameReset()) {
            gameService.setPlayerGotReseted(gameService.getPlayerGotReseted() + 1);
            if (gameService.getPlayerGotReseted() >= gameService.getPlayerAtReset()) {

                gameService.setPlayerAtReset(0);
                gameService.setPlayerGotReseted(0);
                delayReset();
                //System.out.println("Alle Spieler Wurde reseted");
                return new ResponseMessage(mapper.writeValueAsString("true"));
            }
            if(gameService.isConnected()) {
                gameService.setPlayerAtReset(0);
                gameService.setPlayerGotReseted(0);
                gameService.setGameReset(false); //Wenn es Connected ist muss man nicht auf alle wartren bis es auf false gesetzt wird
                return new ResponseMessage(mapper.writeValueAsString("true"));
            }
            return new ResponseMessage(mapper.writeValueAsString("true"));

        } else if(gameService.isGameHasEnded()){
            return new ResponseMessage("Game beendet");
        } else if (gameService.isGameStarted()) {
            return new ResponseMessage("Game has started");
        }
        return new ResponseMessage("false");
    }


    @GetMapping("/user/getGameMode")
    public ResponseMessage getGameMode() throws JsonProcessingException, InterruptedException {
        String gamemode = "";
        switch (gameService.getGamemode()){
            case POKER -> {
                gamemode = "Poker";
            }
            case BLACKJACK -> {
                gamemode = "Blackjack";
            }
        }
        return new ResponseMessage(gamemode);
    }


    public void delayReset(){
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                gameService.setGameReset(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    //Todo: Name "ping" umändern
    @GetMapping("/admin/ping")
    public ResponseMessage adminPing() throws JsonProcessingException {
        return new ResponseMessage(String.valueOf(gameService.getListOfAllPlayers().size()));
    }

    //Todo: Name des Endpunktes und der Methode ändern
    //      Passwort sollte ins Property
    //      Was wenn der admin sich nicht daran hält?
    @PostMapping("/admin/sendPassword")
    public ResponseMessage sendPassword(@RequestBody Message postPassword) {
        if (postPassword.getMessage().equals("1111")) { // Ja ist schon richtig Kacke ABER ist auch nur Projekt für Ausbildungsmessen!!!!
            return new ResponseMessage("true");
        }
        return new ResponseMessage("false");
    }

    @PostMapping("/admin/startGame")
    public ResponseMessage startGame(@RequestBody Message message) throws InterruptedException, CloneNotSupportedException, TMCDeviceIsBusyException {
        if(!executingComand) {
            executingComand = true;
            gameService.setGameStarted(true);
            numberOfAllPlayersInGame = gameService.getListOfAllPlayers().size();
            //TODO: Gamemode.valueOf(message.getMessage().toUpperCase());
            if (message.getMessage().toLowerCase().equals("blackjack")) {
                gameService.setGamemode(Gamemode.BLACKJACK);
            } else {
                gameService.setGamemode(Gamemode.POKER);
            }
            Collections.sort(gameService.getListOfAllPlayers(), new Comparator<Player>() {
                @Override
                public int compare(Player p1, Player p2) {
                    return Integer.compare(Integer.parseInt(p1.getId()), Integer.parseInt(p2.getId()));
                }
            });
            gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(0));
            gameService.startGame();
            executingComand = false;
        }
        return new ResponseMessage("true");
    }

    @PostMapping("/admin/sendReset")
    public ResponseMessage sendReset(@RequestBody Message postPassword) throws InterruptedException, CloneNotSupportedException {
        if(gameService.getListOfAllPlayers().size()>0) {
            System.out.println("reset wurde geklickt");
            gameService.setGameReset(true);
            gameService.setPlayerAtReset(numberOfAllPlayersInGame);
            gameService.getListOfAllPlayers().clear();
            GameService.registerPlayer();
        }
        return new ResponseMessage("true");
    }

    @GetMapping("/admin/adminPanel/endProgramm")
    public ResponseMessage endProgramm() throws JsonProcessingException {
        System.exit(200);
        return new ResponseMessage("");
    }

//    @GetMapping("/admin/kickCurrentPlayer")
//    public ResponseMessage kickCurrentPlayer() {
//        if (gameService.getCurrenPlayerIndex() + 1 >= gameService.getListOfAllPlayers().size()) {
//            gameService.setCurrenPlayerIndex(0);
//        } else {
//            gameService.setCurrenPlayerIndex(gameService.getCurrenPlayerIndex() + 1);
//            System.out.println("Ein Spieler wurde gekickt");
//            //setze den taster der nächste Person auf high
//        }
//        Player playerWhoHasToBeKicked = gameService.getCurrentPlayer();
//        gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(gameService.getCurrenPlayerIndex()));
//        gameService.getListOfAllPlayers().remove(playerWhoHasToBeKicked);
//        return new ResponseMessage("Spieler wurde gekickt");
//    }

    @PostMapping("/admin/adminPanel/rotateStepper")
    public ResponseMessage rotateStepper(@RequestBody Message message) throws TMCDeviceIsBusyException {
        if(!executingComand) {
            executingComand = true;
            System.out.println("STEPPER wurde geklickt");
            GameService.rotateStepperMotor(Integer.parseInt(message.getMessage()));
            executingComand = false;
        }
        return new ResponseMessage("thanks");
    }

    @PostMapping("/admin/restartGame")
    public ResponseMessage restartGame(@RequestBody Message message) {
        gameService.setGameRestarted(true);
        gameService.restartTheCurrentGame();
        return new ResponseMessage("thanks");
    }

    @PostMapping("/admin/restartGameChoice")
    public ResponseMessage restartGameChoice(@RequestBody Message message) {
        gameService.setGameChoiceReseted(true);
        if(!gameService.isConnected()){
            System.out.println("reset wurde geklickt");
            gameService.setGameReset(true);
            gameService.setPlayerAtReset(numberOfAllPlayersInGame);
            gameService.getListOfAllPlayers().clear();
        }
        gameService.resetGameChoice();
        return new ResponseMessage("thanks");
    }

    @PostMapping("/admin/adminPanel/activateCardMotor")
    public ResponseMessage activateCardMotor(@RequestBody Message message) throws InterruptedException {
        if(!executingComand) {
            executingComand = true;
            System.out.println("CARD MOTOR wurde geklickt");
            GameService.executeCardThrow();
            gameService.setAdminPanelCardThrowActivated(true);
            executingComand = false;
        }
        return new ResponseMessage("thanks");
    }

    @PostMapping("/admin/adminPanel/saveUpdateInDatabase")
    public ResponseMessage saveUpdateInDatabase(@RequestBody Message message) {
        String[] list = message.getMessage().split("-");
        DatenbankController.updateByName(list[0], Integer.parseInt(list[1]));
        GameService.updateCardMotorDatabase();
        return new ResponseMessage("");
    }

    @PostMapping("/admin/adminPanel/getDatabaseValuesByName")
    public ResponseMessage getDatabaseValuesByName(@RequestBody Message message) {
        return new ResponseMessage(String.valueOf(DatenbankController.getValueByName(message.getMessage())));
    }

    //Todo: Warum CSV? Warum nicht anders?
    @GetMapping("/game/ping/getDealerHand")
    public ResponseMessage getDealerHand() throws JsonProcessingException {
        if (gameService.getDealer().getDealerHand() != null) {
            String idCSV = castKartenObjectToBildId(gameService.getDealer().getDealerHand());
            return new ResponseMessage(idCSV);
        }
        return new ResponseMessage("");
    }
///ping/getPlayerTurn
    @GetMapping({"/game/ping/getPlayerTurn", "/logic/ping/getPlayerTurn"})
    public ResponseMessage getPlayerTurn() throws JsonProcessingException {
        return new ResponseMessage(gameService.getCurrentPlayer().getId());
    }

    @GetMapping("/game/getWinner")
    public ResponseMessage getWinner() throws JsonProcessingException {
        String gewinnerMessage = castWinnerMapIntoString(gameService.getMapOfAllWinners());
        return new ResponseMessage(gewinnerMessage);
    }


    @PostMapping({"/logic/buttonIsClickedOnce", "/user/hit"})
    public ResponseMessage buttonIsClickedOnce(@RequestBody Message message) throws InterruptedException, TMCDeviceIsBusyException {
        if(!executingComand) {
            executingComand = true;
            System.out.println("Hallo");
            String buttonId = message.getMessage().substring(message.getMessage().indexOf(" ") + 1);
            System.out.println("Es wurde ein Button geklickt: " + buttonId);
            gameService.setButtonClickedOnce(true);
            GameService.hitEvent();
            executingComand = false;
            return new ResponseMessage("true");
        }
        return  new ResponseMessage("true");
    }

    @PostMapping({"/logic/buttonIsClickedTwice", "/user/stay"})
    public ResponseMessage buttonIsClickedTwice(@RequestBody Message message) throws InterruptedException, TMCDeviceIsBusyException {
        if(!executingComand) {
            executingComand = true;
        System.out.println("Hallo");
        String buttonId = message.getMessage().substring(message.getMessage().indexOf(" ") + 1);
        System.out.println("Es wurde ein Button geklickt: " + buttonId);
        gameService.setButtonClickedTwice(true);
        GameService.stayEvent();
            executingComand = false;
            return new ResponseMessage("true");
        }
        return  new ResponseMessage("true");
    }

//    @PostMapping("/logic/cardGotScanned")
//    public ResponseMessage cardGotScanned(@RequestBody Message message) throws JsonProcessingException {
//        try {
//            Karte karte = mapper.readValue(message.getMessage(), Karte.class);
//            return new ResponseMessage("true");
//        } catch (Exception e) {
//
//        }
//        return new ResponseMessage("false");
//    }

    @PostMapping("/logic/registerPlayerAtTable")
    public ResponseMessage registerPlayerAtTable(@RequestBody Message message) throws JsonProcessingException, InterruptedException, CloneNotSupportedException {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        //gameService.buttonClicked();
        List<String> playerIds = getListOfAllActiveID();
        if (!playerIds.contains(message.getMessage())) {
            gameService.getListOfAllPlayers().add(new Player(message.getMessage()));
            GameService.registerPlayer();
            return new ResponseMessage("acknowledged");
        }
        return new ResponseMessage("not acknowledged");
    }

    public String castKartenObjectToBildId(List<Karte> dealerHand) {
        String idCSV = "";
        for (int i = 0; i < dealerHand.size(); i++) {
            int idValue = 0;
            if (i < gameService.getNumberOfCardFaceup()) {
                switch (dealerHand.get(i).typ) {
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
                switch (dealerHand.get(i).name.substring(dealerHand.get(i).name.indexOf(" ") + 1)) {
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
                        idValue += dealerHand.get(i).wert;
                        break;
                }
            } else {
                idValue = 500;
            }
            idCSV += String.valueOf(idValue) + ",";
        }
        idCSV = idCSV.substring(0, idCSV.length() - 1);
        return idCSV;
    }

    private String castWinnerMapIntoString(HashMap<String, String> map){
        if(map == null || map.isEmpty()){
            if(gameService.getGamemode() == Gamemode.POKER){
                return "";
            } else {
                return "Kein Spieler hat gewonnen";
            }
        }
        String returnString = "";
        for(String key : map.keySet()){
            returnString += key + " " + map.get(key) +",";
        }
        returnString = returnString.substring(0, returnString.length()-1);
        return  returnString;
    }

}
