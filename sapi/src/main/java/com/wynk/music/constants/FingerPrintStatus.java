package com.wynk.music.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anurag on 1/14/16.
 */
public enum FingerPrintStatus {

	UNMATCHED(0),MATCHED(1),QUEUED(2),FAILURE(3);

	private final int          opcode;
	private static Map<Integer, FingerPrintStatus> opcodeToStatusMap = new HashMap<Integer, FingerPrintStatus>();


	FingerPrintStatus(int code) {
		this.opcode = code;
	}

	static {
		for(FingerPrintStatus status : FingerPrintStatus.values()) {
			opcodeToStatusMap.put(status.getOpcode(), status);
		}
	}

	public int getOpcode() {
		return opcode;
	}

	public static FingerPrintStatus getFingerPrintStatus(int opcode) {

		FingerPrintStatus status = opcodeToStatusMap.get(opcode);
		if(status ==null) {
			return null;
		}

		return status;
	}
}
