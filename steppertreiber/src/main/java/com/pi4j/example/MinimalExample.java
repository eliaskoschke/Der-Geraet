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

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.Console;

import java.util.Arrays;

import static java.lang.Math.round;

/**
 * <p>This example fully describes the base usage of Pi4J by providing extensive comments in each step.</p>
 *
 * @author Frank Delporte (<a href="https://www.webtechie.be">https://www.webtechie.be</a>)
 * @version $Id: $Id
 */
public class MinimalExample {

    private static final int PIN_BUTTON = 24; // PIN 18 = BCM 24
    private static final int PIN_LED = 22; // PIN 15 = BCM 22

    private static int pressCount = 0;

    /**
     * This application blinks a led and counts the number the button is pressed. The blink speed increases with each
     * button press, and after 5 presses the application finishes.
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        // Create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        final var console = new Console();

        // Print program title/header
        console.title("<-- The Pi4J Project -->", "Minimal Example project");

        // ************************************************************
        //
        // WELCOME TO Pi4J:
        //
        // Here we will use this getting started example to
        // demonstrate the basic fundamentals of the Pi4J library.
        //
        // This example is to introduce you to the boilerplate
        // logic and concepts required for all applications using
        // the Pi4J library.  This example will do use some basic I/O.
        // Check the pi4j-examples project to learn about all the I/O
        // functions of Pi4J.
        //
        // ************************************************************

        // ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'
        var pi4j = Pi4J.newAutoContext();

        // ------------------------------------------------------------
        // Output Pi4J Context information
        // ------------------------------------------------------------
        // The created Pi4J Context initializes platforms, providers
        // and the I/O registry. To help you to better understand this
        // approach, we print out the info of these. This can be removed
        // from your own application.
        // OPTIONAL
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        // ------------------------------------------------------------
        // Output Pi4J Board information
        // ------------------------------------------------------------
        // When the Pi4J Context is initialized, a board detection is 
        // performed. You can use this info in case you need board-specific
        // functionality.
        // OPTIONAL
        console.println("Board model: " + pi4j.boardInfo().getBoardModel().getLabel());
        console.println("Operating system: " + pi4j.boardInfo().getOperatingSystem());
        console.println("Java versions: " + pi4j.boardInfo().getJavaInfo());
        // This info is also available directly from the BoardInfoHelper, 
        // and with some additional realtime data.
        console.println("Board model: " + BoardInfoHelper.current().getBoardModel().getLabel());
        console.println("Raspberry Pi model with RP1 chip (Raspberry Pi 5): " + BoardInfoHelper.usesRP1());
        console.println("OS is 64-bit: " + BoardInfoHelper.is64bit());
        console.println("JVM memory used (MB): " + BoardInfoHelper.getJvmMemory().getUsedInMb());
        console.println("Board temperature (°C): " + BoardInfoHelper.getBoardReading().getTemperatureInCelsius());

        // Here we will create the I/O interface for a LED with minimal code.
        var led = pi4j.digitalOutput().create(PIN_LED);

        // The button needs a bit more configuration, so we use a config builder.
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("button")
                .name("Press button")
                .address(PIN_BUTTON)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L);
        var button = pi4j.create(buttonConfig);
        button.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                pressCount++;
//                console.println("Button was pressed for the " + pressCount + "th time");
            }
        });

        // OPTIONAL: print the registry

//        while (pressCount < 5) {
//            if (led.state() == DigitalState.HIGH) {
////                console.println("LED low");
//                led.low();
//            } else {
////                console.println("LED high");
//                led.high();
//            }
//            Thread.sleep(500 / (pressCount + 1));
//        }

//        var serial = pi4j.create(Serial.newConfigBuilder(pi4j)
//                    .use_9600_N81()
//                    .dataBits_8()
//                    .parity(Parity.NONE)
//                    .stopBits(StopBits._1)
//                    .flowControl(FlowControl.NONE)
//                    .id("serial")
//                    .device("ttyS0")
////                    .provider("pigpio-serial")
//                    .build()
//        );
//
//        serial.open();
//
//        PrintInfo.printRegistry(console, pi4j);
//
//        while (!serial.isOpen()) {
//            console.println("Wait for Serial");
////            serial.getOutputStream().write(5);
////            serial.getOutputStream().flush();
//            Thread.sleep(500);
//        }

//        SerialPort[] commPorts = SerialPort.getCommPorts();
//
//        for (SerialPort port : commPorts) {
//            System.out.println(port.getSystemPortName() +": "+ port.getSystemPortPath());
//        }

        SerialPort commPort = SerialPort.getCommPorts()[0];
        commPort.setBaudRate(115200);
        commPort.setNumStopBits(1);
        commPort.openPort();

        while (!commPort.isOpen()) {
            System.out.println("Warte auf Serial");
            Thread.sleep(500);
        }

//        commPort.writeBytes("Hallo Welt!".getBytes(), 11);
//
//        byte[] buffer = new byte[11];
//
//        commPort.readBytes(buffer,11);
//
//        console.println("Von Seriel gelesen: "+new String(buffer));


        readDriverStatus(commPort);
        setCurrent(commPort, 25, 10, 0.5f);

        setVactual(commPort, 1000);
        Thread.sleep(5000);

        setVactual(commPort, 2000);
        Thread.sleep(5000);

        setVactual(commPort, -8000);
        Thread.sleep(5000);

        setVactual(commPort, 1000);
        Thread.sleep(5000);
        setVactual(commPort, 0);


        // ------------------------------------------------------------
        // Terminate the Pi4J library
        // ------------------------------------------------------------
        // We we are all done and want to exit our application, we must
        // call the 'shutdown()' function on the Pi4J static helper class.
        // This will ensure that all I/O instances are properly shutdown,
        // released by the the system and shutdown in the appropriate
        // manner. Terminate will also ensure that any background
        // threads/processes are cleanly shutdown and any used memory
        // is returned to the system.

        // Shutdown Pi4J
        pi4j.shutdown();
    }

    private static void setVactual(SerialPort commPort, int vActual) {
        writeRegister(commPort, (byte) 0x22, vActual);
    }

    private static void readDriverStatus(SerialPort commPort) {
//        readInt(commPort, (byte) 0x6F);
        int readInt = readInt(commPort, (byte) 0x6F);
        if (checkFlag(readInt, 31)) {
            System.out.println("Motor is standing still");
        } else {
            System.out.println("Motor is moving");
        }

        if (checkFlag(readInt, 30)) {
            System.out.println("StealthChop");
        } else {
            System.out.println("SpreadCycle");
        }

        int csActual = (readInt >> 16) & 0xFF;
        System.out.println("CS_ACTUAL: "+ csActual);

        if (checkFlag(readInt, 7)) {
            System.out.println("Open load on B");
        }

        if (checkFlag(readInt, 6)) {
            System.out.println("Open load on A");
        }

        if (checkFlag(readInt, 5)) {
            System.out.println("Short MOSFET on B");
        }

        if (checkFlag(readInt, 4)) {
            System.out.println("Short MOSFET on A");
        }

        if (checkFlag(readInt, 3)) {
            System.out.println("Short to GND on B");
        }

        if (checkFlag(readInt, 2)) {
            System.out.println("Short to GND on A");
        }

        if (checkFlag(readInt, 1)) {
            System.out.println("Driver is overheating");
        }

        if (checkFlag(readInt, 0)) {
            System.out.println("Prewarning: Driver is overheating");
        }
    }

    private static void setCurrent(SerialPort port, float runCurrent, int holdDelay, float holdMultiplier) {
//        float rSense = 0.11f;
//        float vfs = .325f;
//
//        setIScaleAnalog(port, false);
//
//        float csIRun = 32f * 1.41421f * runCurrent / 1000f * (rSense + .02f) / vfs - 1;
//        if (csIRun < 16) {
//            vfs = .18f;
//            csIRun = 32f * 1.41421f * runCurrent / 1000f * (rSense + .02f) / vfs - 1;
//            setVSense(port, true);
//        } else {
//            setVSense(port, false);
//        }

//        float csIRun = 16f;

        int csIrunInt = round(runCurrent);
        if (csIrunInt < 0) csIrunInt = 0;
        if (csIrunInt > 31) csIrunInt = 31;

        setIRun(port, csIrunInt, holdDelay, round(csIrunInt * holdMultiplier));
        setPDNDisable(port, true);
    }

    private static void setPDNDisable(SerialPort port, boolean disable) {
        int gConf = readInt(port, (byte) 0x00);

        if (disable) gConf = gConf | (1 << 6);
        else gConf = gConf & ((1 << 6) ^ 0xFF);

        writeRegister(port, (byte) 0x00, gConf);
    }

    private static void setVSense(SerialPort port, boolean enabled) {
        int chopConf = readInt(port, (byte) 0x6C);

        if (enabled) chopConf = chopConf | (1 << 17);
        else chopConf = chopConf & ((1 << 17) ^ 0xFF);

        writeRegister(port, (byte) 0x6C, chopConf);
    }

    private static void setIScaleAnalog(SerialPort port, boolean enabled) {
        int gconf = readInt(port, (byte) 0x00);

        if (enabled) gconf = gconf | (1 << 0);
        else gconf = gconf & ((1 << 0) ^ 0xFF);

        writeRegister(port, (byte) 0x00, gconf);
    }

    private static void setIRun(SerialPort port, int csIRun, int holdDelay, int csIHold) {
        int payload =
                csIHold +
                csIRun << 8 +
                holdDelay << 16;
        writeRegister(port, (byte) 0x10, payload);
    }

    private static boolean checkFlag(int register, int flag) {
        return (register & (1 << flag)) != 0;
    }

    private static int readInt(SerialPort commPort, byte address) {
        byte[] readRegister = readRegister(commPort, address);
        if (readRegister[11] != computeCRC(Arrays.copyOfRange(readRegister, 4, 12))) {
            System.out.println("CRC-Missmatch in Response");
        }

        return Byte.toUnsignedInt(readRegister[7])  << 24 +
                Byte.toUnsignedInt(readRegister[8]) << 16 +
                Byte.toUnsignedInt(readRegister[9]) <<  8 +
                Byte.toUnsignedInt(readRegister[10]);
    }

    private static byte[] readRegister(SerialPort commPort, byte address) {
        byte[] sendPacket = new byte[4];
//        sendPacket[0] = (byte) 0x55;
        sendPacket[0] = (byte) 0b00000101;
        sendPacket[1] = 0; // mtrId
        sendPacket[2] = (byte) (address);
        sendPacket[3] = computeCRC(sendPacket); // mtrId
//        sendPacket[3] = (byte) 193; // mtrId
        commPort.flushIOBuffers();

        System.out.println("Written Bytes: "+commPort.writeBytes(sendPacket, 4));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byte[] recvPacket = new byte[12];
        System.out.println("Read Bytes: "+commPort.readBytes(recvPacket, 12));

        return recvPacket;
    }

    private static void writeRegister(SerialPort port, byte register, int payload) {
        byte[] sendPacket = new byte[8];
//        sendPacket[0] = (byte) 0x55;
        sendPacket[0] = (byte) 0b00000101;
        sendPacket[1] = 0; // mtrId
        sendPacket[2] = (byte) (register | 0x80);

        sendPacket[3] = (byte) ((payload >> 24) & 0xFF);
        sendPacket[4] = (byte) ((payload >> 16) & 0xFF);
        sendPacket[5] = (byte) ((payload >>  8) & 0xFF);
        sendPacket[6] = (byte) ((payload      ) & 0xFF);

        sendPacket[7] = computeCRC(sendPacket); // mtrId
//        sendPacket[3] = (byte) 193; // mtrId
        port.flushIOBuffers();

        System.out.println("Written Bytes: "+port.writeBytes(sendPacket, 8));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte computeCRC(byte[] packet) {
        int crc = 0;
        for (int n = 0; n < packet.length -1; n++) {
            int currByte = packet[n] & 0xFF;
            for (int i = 0; i < 8; i++) {
                int crcMSB = (crc >> 7) & 0x01;
                int currByteLSB = currByte & 0x01;
                if ((crcMSB ^ currByteLSB) == 1) {
                    crc = (((crc << 1) ^ 0x07) & 0xFF);
                } else {
                    crc = ((crc << 1) & 0xFF);
                }
                currByte = (currByte >> 1);
            }
        }
        return (byte) crc;
    }
}
