package com.loudpacks.net;

public enum RequestType {

    LEFT_CLICK(0),
    RIGHT_CLICK(1),
    SHIFT_CLICK(2), //not used - better to send shift key down, do clicks, send shift key up
    KEY_PRESS(3),
    KEY_DOWN(4),
    KEY_UP(5),
    EXIT(6),
    INFO(7);

    private final int id;

    RequestType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
