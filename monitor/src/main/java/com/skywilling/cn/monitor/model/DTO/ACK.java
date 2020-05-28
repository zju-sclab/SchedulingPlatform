package com.skywilling.cn.monitor.model.DTO;

public enum ACK {
    COMMAND(0xFE, "command"),
    SUCCESS(0x00, "success"),
    FAILURE(0x02, "failure");
    private int code;
    private String desc;

    ACK(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ACK valueOf(int code) {
        for (ACK ack : ACK.values()) {
            if (ack.code == code) {
                return ack;
            }
        }
        return null;
    }

    public static boolean getResult(int code) {
        if (SUCCESS.code == code) {
            return true;
        } else return false;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
