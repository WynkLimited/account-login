package com.wynk.notification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anurag on 3/19/15.
 */
public enum NotificationAction {

    ListenNow(1,"listennow");

    private final int                       opcode;
    private final String action;

    private static Map<Integer, NotificationAction> opcodeToNotificationAction = new HashMap<Integer, NotificationAction>();
    private static Map<String,NotificationAction> actionMap=new HashMap<String,NotificationAction>();

    static {
        for(NotificationAction code : NotificationAction.values()) {
            opcodeToNotificationAction.put(code.getOpcode(), code);
        }
        for (NotificationAction action: NotificationAction.values()) {
            actionMap.put(action.getAction().toLowerCase(), action);
        }
    }

    public static NotificationAction fromOpcode(int opcode) {
        return opcodeToNotificationAction.get(opcode);
    }

    public static NotificationAction getNotificationAction(String action) {

        NotificationAction notificationAction=actionMap.get(action.toLowerCase());
        if(notificationAction ==null) {
        	return null;
        }

        return notificationAction;
    }

    NotificationAction(int opcode, String action) {
        this.opcode = opcode;
        this.action=action;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getAction() { return action;}
}
