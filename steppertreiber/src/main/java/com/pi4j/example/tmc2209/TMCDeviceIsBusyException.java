package com.pi4j.example.tmc2209;

public class TMCDeviceIsBusyException extends Exception {
    public TMCDeviceIsBusyException(String msg) {
        super(msg);
    }
}
