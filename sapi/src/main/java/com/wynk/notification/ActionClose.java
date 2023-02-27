package com.wynk.notification;

public enum ActionClose {
    IGNORE(0), INCREMENT_UNREAD_NOTIFICATION_COUNT(1), PUSH_NOTIFICATION(2);

    private final int opcode;

    ActionClose(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

}
