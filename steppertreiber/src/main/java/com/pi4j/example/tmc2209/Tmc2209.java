package com.pi4j.example.tmc2209;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static java.lang.Math.round;

public class Tmc2209 {
    SerialPort port;

    private final AtomicBoolean isHoming = new AtomicBoolean(false);
    private final AtomicLong position = new AtomicLong();
    private final AtomicLong targetPosition = new AtomicLong();
    private final AtomicReference<Direction> direction = new AtomicReference<>(Direction.FORWARD);
    private final Pwm stepPin;
    private final DigitalOutput dirPin;
    private final AtomicInteger currSpeed = new AtomicInteger();

    public Tmc2209(Context p4jContext, int indexPin, int dirPin, int stepPin) throws InterruptedException, TMCCommunicationException {
        port = SerialPort.getCommPorts()[0];
        port.setBaudRate(115200);
        port.setNumStopBits(1);
        port.setComPortTimeouts(TIMEOUT_READ_BLOCKING, 500, 500);
        port.openPort();

        while (!port.isOpen()) {
            System.out.println("Warte auf Serial");
            Thread.sleep(250);
        }
        setSpreadCycle(false);
        readDriverStatus();
        setCurrent(20, 10, 0.5f);

        PwmConfig pwmConfig = Pwm.newConfigBuilder(p4jContext)
                .id("tmx-step")
                .name("TMC 2209 - STEP Pin")
                .address(stepPin)
                .pwmType(PwmType.SOFTWARE) // Hier kann man mal probieren ob HARDWARE auch geht
                .initial(0)
                .shutdown(0)
                .provider("pigpio-pwm")
                .build();
        this.stepPin = p4jContext.create(pwmConfig);

        var indexConfig = DigitalInput.newConfigBuilder(p4jContext)
                .id("tmc-index")
                .name("TMC 2209 - Index Pin")
                .address(indexPin)
                .pull(PullResistance.PULL_UP)
                .debounce(10L);
        var indexInput = p4jContext.create(indexConfig);
        indexInput.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                if (isHoming.get()) return;
                if (direction.get() == Direction.FORWARD) position.getAndIncrement();
                else position.getAndDecrement();

                if (position.get() == targetPosition.get()) {
                    this.stepPin.off();
                    this.currSpeed.set(0);
                }
            }
        });

        DigitalOutputConfig dirConfig = DigitalOutput.newConfigBuilder(p4jContext)
                .id("tmc-dir")
                .name("TMC 2209 - Dir Pin")
                .address(dirPin)
                .build();
        this.dirPin = p4jContext.create(dirConfig);
    }

    /**
     * Bewegt den Motor mit der angegebenen Geschwindigkeit zur gewünschten Position.
     * @param targetPosition Die Position, zu der der Motor gedreht werden soll.
     * @param speed Die Geschwindigkeit, mit der der Motor bewegt werden soll
     */
    public void moveToPosition(long targetPosition, int speed) throws TMCDeviceIsBusyException {
        if (speed < 1) throw new IllegalArgumentException("Speed must be positive");
        if (isHoming.get()) throw new TMCDeviceIsBusyException("Motor is busy with homing");

        stepPin.off();
        this.targetPosition.set(targetPosition);
        if (position.get() < targetPosition) {
            currSpeed.set(speed);
            direction.set(Direction.FORWARD);
            dirPin.on();
            stepPin.on(50, speed);
        } else if (position.get() > targetPosition) {
            currSpeed.set(-speed);
            direction.set(Direction.BACKWARD);
            dirPin.off();
            stepPin.on(50, speed);
        } else {
            currSpeed.set(0);
        }
    }

    /**
     * Setzt den internet Positionszähler zurück. Die aktuelle Postion wird als Nullpunkt gesetzt.
     */
    public void resetPosition() throws TMCDeviceIsBusyException {
        if (currSpeed.get() != 0) throw new TMCDeviceIsBusyException("Can't reset position while Motor is moving");
        position.set(0);
    }

    /**
     * Fährt den Motor in den Endstopp und setzt die Position als 0 fest.
     *
     * @param speed Die Geschwindigkeit, mit der der Motor zum Endstopp gefahren werden soll. Negative Werte können verwendet werden, um rückwärtszufahren.
     */
    public synchronized void homePosition(int speed, int threshold) throws TMCDeviceIsBusyException {
        if (currSpeed.get() != 0) throw new TMCDeviceIsBusyException("Can't home position while Motor is moving");
        if (isHoming.get()) throw new TMCDeviceIsBusyException("Motor is already homing");
        if (speed == 0) throw new IllegalArgumentException("Speed must not be 0");
        isHoming.set(true);
        stepPin.off();

        if (speed > 0) {
            direction.set(Direction.FORWARD);
            dirPin.on();
        } else {
            direction.set(Direction.BACKWARD);
            dirPin.off();
        }
        stepPin.on(50, speed);

        new Thread(() -> {
            while (isHoming.get())
                try {
                    Thread.sleep(0);
                    int sgResult = readInt(Register.SG_RESULT.address);
                    if (sgResult <= threshold) {
                        stepPin.off();
                        position.set(0);
                        isHoming.set(false);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
        }).start();
    }

    private void setSpreadCycle(boolean enabled) throws TMCCommunicationException {
        int gConf = readInt(Register.GCONF.address);
        gConf = setFlag(gConf, 2, enabled);
        writeRegister(Register.GCONF.address, gConf);
    }

    private void readDriverStatus() throws TMCCommunicationException {
        int readInt = readInt(Register.DRV_STATUS.address);

        if (checkFlag(readInt, 31)) {
            System.out.println("Motor is disabled");
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

        readInt = readInt(Register.GCONF.address);

        System.out.println("index_otpw is set: " + checkFlag(readInt, 4));
        System.out.println("index_step is set: " + checkFlag(readInt, 5));
    }

    private void setCurrent(float runCurrent, int holdDelay, float holdMultiplier) throws TMCCommunicationException {
        int csIrunInt = round(runCurrent);
        if (csIrunInt < 0) csIrunInt = 0;
        if (csIrunInt > 31) csIrunInt = 31;

        setIRun(csIrunInt, holdDelay, round(csIrunInt * holdMultiplier));
        setPDNDisable(true);
    }

    private void setPDNDisable(boolean disable) throws TMCCommunicationException {
        int gConf = readInt(Register.GCONF.address);

        gConf = setFlag(gConf, 6, disable);

        writeRegister(Register.GCONF.address, gConf);
    }

    private void setIRun(int csIRun, int holdDelay, int csIHold) {
        int payload =
                csIHold        +
                csIRun    << 8 +
                holdDelay << 16;
        writeRegister(Register.IHOLD_IRUN.address, payload);
    }

    private static boolean checkFlag(int registerValue, int flag) {
        return (registerValue & (1 << flag)) != 0;
    }

    private static int setFlag(int registerValue, int flag, boolean enable) {
        if (enable) registerValue |= 1 << flag;
        else registerValue &= ~(1 << flag);
        return registerValue;
    }

    private int readInt(byte address) throws TMCCommunicationException {
        byte[] readRegister = readRegister(address);
        if (readRegister[11] != computeCRC(Arrays.copyOfRange(readRegister, 4, 12))) {
            throw new TMCCommunicationException("CRC-Missmatch in Response");
        }

        return  (Byte.toUnsignedInt(readRegister[7]) << 24) +
                (Byte.toUnsignedInt(readRegister[8]) << 16) +
                (Byte.toUnsignedInt(readRegister[9]) <<  8) +
                (Byte.toUnsignedInt(readRegister[10])     );
    }

    private synchronized byte[] readRegister(byte address) throws TMCCommunicationException {
        byte[] sendPacket = new byte[4];
        sendPacket[0] = (byte) 0b00000101;
        sendPacket[1] = 0; // mtrId
        sendPacket[2] = address;
        sendPacket[3] = computeCRC(sendPacket);

        port.flushIOBuffers();
        port.writeBytes(sendPacket, 4);

        byte[] recvPacket = new byte[12];
        int bytesRead = port.readBytes(recvPacket, 12);
        if (bytesRead != 12) throw new TMCCommunicationException("Expected 12 bytes, but received: " + bytesRead);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return recvPacket;
    }

    private synchronized void writeRegister(byte register, int payload) {
        byte[] sendPacket = new byte[8];
        sendPacket[0] = (byte) 0b00000101;
        sendPacket[1] = 0; // mtrId
        sendPacket[2] = (byte) (register | 0x80);

        sendPacket[3] = (byte) ((payload >> 24) & 0xFF);
        sendPacket[4] = (byte) ((payload >> 16) & 0xFF);
        sendPacket[5] = (byte) ((payload >>  8) & 0xFF);
        sendPacket[6] = (byte) ((payload      ) & 0xFF);

        sendPacket[7] = computeCRC(sendPacket);

        port.flushIOBuffers();
        port.writeBytes(sendPacket, 8);
        try {
            Thread.sleep(10);
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
