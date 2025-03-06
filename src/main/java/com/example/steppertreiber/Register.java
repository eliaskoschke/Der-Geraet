package com.pi4j.example.tmc2209;

public enum Register {
    GCONF         ((byte) 0x00),
    IHOLD_IRUN    ((byte) 0x10),
    V_ACTUAL      ((byte) 0x22),
    SGTHRS        ((byte) 0x40),
    SG_RESULT     ((byte) 0x41),
    DRV_STATUS    ((byte) 0x6F);

    public final byte address;

    Register(byte address) {
        this.address = address;
    }
}
