package com.wynk.solace.listeners;

import com.wynk.solace.listeners.events.UserCreationEvent;
import org.springframework.context.ApplicationListener;

public abstract class AbstractMusicEventListener<T extends UserCreationEvent> implements ApplicationListener<T> {
    @Override
    public void onApplicationEvent(T event) {
        if (isEnabled() && canHandleEvent(event)) {
            handleEvent(event);
        }
    }

    /**
     * Flag to indicate whether this listener is ON or OFF.
     *
     * @return boolean
     */
    protected abstract boolean isEnabled();

    /**
     * Criteria to decide if this event can be handled or not.
     *
     * @param event the event
     * @return boolean
     */
    protected abstract boolean canHandleEvent(T event);

    /**
     * Implementation to handle this event.
     *
     * @param event the event
     */
    protected abstract void handleEvent(T event);
}
