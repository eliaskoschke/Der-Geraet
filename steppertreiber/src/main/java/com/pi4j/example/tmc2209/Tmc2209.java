package com.pi4j.example.tmc2209;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.Arrays;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static java.lang.Math.round;

public class Tmc2209 {
    SerialPort port;

    private boolean isHoming = false;
    private long position = 0;
    private long targetPosition = 0;
    private Direction direction = Direction.FORWARD;

    private static final int SGTHRS = 0xAFFEE;

    public Tmc2209(Context p4jContext, int enablePin, int indexPin, int diagPin) throws InterruptedException, TMCCommunicationException {
        port = SerialPort.getCommPorts()[0];
        port.setBaudRate(115200);
        port.setNumStopBits(1);
        port.setComPortTimeouts(TIMEOUT_READ_BLOCKING, 500, 500);
        port.openPort();

        while (!port.isOpen()) {
            System.out.println("Warte auf Serial");
            Thread.sleep(250);
        }
        readDriverStatus();
        setCurrent(20, 10, 0.5f);

        var indexConfig = DigitalInput.newConfigBuilder(p4jContext)
                .id("tmc-index")
                .name("TMC 2209 - Index Pin")
                .address(indexPin)
                .pull(PullResistance.PULL_DOWN)
                .debounce(100L);
        var indexInput = p4jContext.create(indexConfig);
        indexInput.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                if (direction == Direction.FORWARD) position++;
                else position--;

                if (position == targetPosition) setVactual(0);
            }
        });

        var diagConfig = DigitalInput.newConfigBuilder(p4jContext)
                .id("tmc-diag")
                .name("TMC 2209 - Diag Pin")
                .address(diagPin)
                .pull(PullResistance.PULL_DOWN)
                .debounce(100L);
        var diagInput = p4jContext.create(diagConfig);
        diagInput.addListener(e -> {
            if (e.state() == DigitalState.HIGH && isHoming) {
                System.out.println("Diag-Flanke while Homing");

//                int sgResult = readInt(Register.SG_RESULT.address);

//                // TODO: double SGTHRS?
//                if (sgResult <= SGTHRS) {
//
//                }

                setVactual(0);
                position = 0;
                isHoming = false;
                try {
                    setSpreadCycle(false);
                } catch (TMCCommunicationException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Bewegt den Motor mit der angegebenen Geschwindigkeit zur gewünschten Position.
     * @param targetPosition Die Position, zu der der Motor gedreht werden soll.
     * @param speed Die Geschwindigkeit, mit der der Motor bewegt werden soll
     */
    public void moveToPosition(long targetPosition, int speed) throws TMCDeviceIsBusyException {
        if (speed < 1) throw new IllegalArgumentException("Speed must be positive");
        if (isHoming) throw new TMCDeviceIsBusyException("Motor is busy with homing");

        setVactual(0);
        this.targetPosition = targetPosition;
        if (position < targetPosition) {
            direction = Direction.FORWARD;
            setVactual(speed);
        } else if (position > targetPosition) {
            direction = Direction.BACKWARD;
            setVactual(-speed);
        }
    }

    /**
     * Setzt den internet Positionszähler zurück. Die aktuelle Postion wird als Nullpunkt gesetzt.
     */
    public void resetPosition() throws TMCCommunicationException, TMCDeviceIsBusyException {
        int vActual = readInt(Register.V_ACTUAL.address);
        if (vActual != 0) throw new TMCDeviceIsBusyException("Can't reset position while Motor is moving");
        position = 0;
    }

    /**
     * Fährt den Motor in den Endstopp und setzt die Position als 0 fest.
     *
     * @param speed Die Geschwindigkeit, mit der der Motor zum Endstopp gefahren werden soll. Negative Werte können verwendet werden, um rückwärtszufahren.
     */
    public void homePosition(int speed) throws TMCCommunicationException {
        setVactual(0);
        setSpreadCycle(false);
        setVactual(speed);
        isHoming = true;

        new Thread(() -> {
            try {
                int sgResult = readInt(Register.SG_RESULT.address);
                System.out.println(sgResult);
            } catch (TMCCommunicationException e) {
                System.out.println(e);
            }
        });
    }

    private void setSpreadCycle(boolean enabled) throws TMCCommunicationException {
        int gConf = readInt(Register.GCONF.address);

        gConf = setFlag(gConf, 2, enabled);

        writeRegister(Register.GCONF.address, gConf);
    }

    private void setVactual(int vActual) {
        writeRegister(Register.V_ACTUAL.address, vActual);
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

        return  Byte.toUnsignedInt(readRegister[7]) << 24 +
                Byte.toUnsignedInt(readRegister[8]) << 16 +
                Byte.toUnsignedInt(readRegister[9]) <<  8 +
                Byte.toUnsignedInt(readRegister[10]);
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
