package com.wynk.notification;

import java.util.HashMap;
import java.util.Map;

public enum ActionOpen {
    IGNORE(0), BATCH_COUNT(1), ALERT(2), INFO_BOX(3), NAVIGATE(4), DELAYED_ALERT(5), DELAYED_WEBVIEW(6), BROWSER_VIEW(7), SILENT_PUSH(8);

    private final int                       opcode;

    private static Map<Integer, ActionOpen> opcodeToActionOpen = new HashMap<Integer, ActionOpen>();

    static {
        for(ActionOpen code : ActionOpen.values()) {
            opcodeToActionOpen.put(code.getOpcode(), code);
        }
    }

    public static ActionOpen fromOpcode(int opcode) {
        return opcodeToActionOpen.get(opcode);
    }

    ActionOpen(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

}
