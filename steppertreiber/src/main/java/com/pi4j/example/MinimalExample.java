package com.pi4j.example;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: EXAMPLE  :: Sample Code
 * FILENAME      :  MinimalExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2024 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.example.tmc2209.TMCCommunicationException;
import com.pi4j.example.tmc2209.TMCDeviceIsBusyException;
import com.pi4j.example.tmc2209.Tmc2209;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

import java.util.Scanner;


public class MinimalExample {

    private static final int PIN_INDEX  = 23;
    private static final int PIN_STEP  = 18;
    private static final int PIN_DIR  = 24;


    public static void main(String[] args) throws Exception {

        var pi4j = Pi4J.newAutoContext();

        testMotorDriver(pi4j);

        pi4j.shutdown();
    }

    private static void testMotorDriver(Context pi4j) throws InterruptedException, TMCCommunicationException, TMCDeviceIsBusyException {
        Tmc2209 steppermotor = new Tmc2209(pi4j, PIN_INDEX, PIN_DIR, PIN_STEP);
        Scanner scanner = new Scanner(System.in);

        outer: while (true) {
            System.out.println(
                    "Was wollen sie tun?\n\n\t" +
                    "1. Zu Position bewegen\n\t" +
                    "2. Aktuelle Position als Nullpunkt festlegen\n\t" +
                    "3. Mechanischen Endstopp suchen und als Nullpunkt festlegen\n\t" +
                    "4. Programm beenden\n\n" +
                    "Bitte Option Eingeben (1-4): "
            );
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Welche Position? ");
                    long position = scanner.nextLong();
                    System.out.println("Welche Geschwindigkeit? ");
                    int speed = scanner.nextInt();
                    steppermotor.moveToPosition(position, speed);
                    break;
                case 2:
                    steppermotor.resetPosition();
                    break;
                case 3:
                    System.out.println("Welche Geschwindigkeit (Negative Werte können für Rückwärts verwendet werden)? ");
                    int homeSpeed = scanner.nextInt();
                    System.out.println("Stall-Detection-Threshold? ");
                    int threshold = scanner.nextInt();
                    steppermotor.homePosition(homeSpeed, threshold);
                    break;
                case 4:
                    break outer;
            }
        }
    }
}
