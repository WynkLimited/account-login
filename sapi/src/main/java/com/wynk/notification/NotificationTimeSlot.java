package com.wynk.notification;

import org.apache.commons.lang.StringUtils;

public enum NotificationTimeSlot {
	
	MORNING(8,00),
	LATE_MORNING(11,0),
	EARLY_AFTERNOON(13,30),
	LATE_AFTERNOON(16,0),
	EARLY_EVENING(17,30),
	LATE_EVENING(19,30),
	NIGHT(22,0);
	
	private long startHour;
	private long startMinute;
	
	NotificationTimeSlot(long startHour, long startMinute) {
		this.startHour = startHour;
		this.startMinute = startMinute;
	}

	
	public static NotificationTimeSlot getNotificationTimeSlot(String name) {
		if(StringUtils.isEmpty(name)) {
            return NotificationTimeSlot.MORNING;
        }
	
		for (NotificationTimeSlot timeSlot : NotificationTimeSlot.values()) {
			if (timeSlot.name().equals(name.toUpperCase())) {
				return timeSlot;
			}
		}
		return NotificationTimeSlot.MORNING;
	}


	public long getStartMinute() {
		return startMinute;
	}

	public long getStartHour() {
		return startHour;
	}

}