package com.wynk.music.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anurag on 5/24/16.
 */
public enum ShufflePriority {

	LOW (3),
	MEDIUM(2),
	HIGH(1),
	DEFAULT(-1);

	public int code;
	private static Map<Integer, ShufflePriority> priorityMapping = new HashMap<>();

	static {
		for(ShufflePriority priority : ShufflePriority.values())
			priorityMapping.put(priority.getCode(), priority);
	}

	ShufflePriority(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public static ShufflePriority getPriorityByCode(int code) {
		ShufflePriority status = priorityMapping.get(code);
		if(status == null) {
			return ShufflePriority.DEFAULT;
		}
		return status;
	}
	public static ShufflePriority[] getShufflePriorityInOrder(){
		ShufflePriority[] shufflePriorityOrder = {ShufflePriority.HIGH,ShufflePriority.MEDIUM,ShufflePriority.LOW,ShufflePriority.DEFAULT};
		return shufflePriorityOrder;
	}
}
