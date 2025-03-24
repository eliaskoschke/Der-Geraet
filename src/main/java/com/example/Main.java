package com.example;

import com.example.tcm2209.StepperController;
import com.example.tcm2209.TMCDeviceIsBusyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.NotFoundException;
import com.pi4j.Pi4J;
import javafx.util.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.pwm.Pwm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    //Todo: Alles in Packages unterteilen
    //      Api dokumentieren (Welche Endpunkte gibt es? Warum? Nach welchem System?)

    //    public static Context pi4j =   Pi4J.newAutoContext();;
    static GameService gameService;
//    static Camera camera = new Camera();
//    static ObjectMapper mapper = new ObjectMapper();
//    static Gamemode gamemode;
//    static boolean turnHasEnded = false;
//    static GameGraphics gameGraphics = new GameGraphics(false);
//    static GameFX gameFx = new GameFX();
//    static boolean gameRestarted = false;
//    static boolean gameChoiceReseted = false;
//    static ArrayList<Player> listOfAllPlayerAtTheBeginningOfTheGame;
//    static StepperController stepperController;
//    static boolean someOneStartedWithBlackJack = false;
//    static boolean stepperIsHome = true;
//    static ArrayList<Player> blackJackList = new ArrayList<>();
//    static ArrayList<Player> listOfAllPlayersWhoPlayTheGame = new ArrayList<>();
//    static IKartenMotor cardMotor;
//    static Karte oldCard = new Karte();

    //--add-opens=Der.Geraet.Maven/com.example=ALL-UNNAMED --add-reads Der.Geraet.Maven=ALL-UNNAMED
    public static void main(String[] args) throws InterruptedException, TMCDeviceIsBusyException, CloneNotSupportedException {

        ApplicationContext context = SpringApplication.run(Main.class, args);
        gameService = context.getBean(GameService.class);
    }
}
