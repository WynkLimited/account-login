package com.wynk.solace.publishers;

import com.wynk.solace.listeners.events.UserCreationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class MusicUserEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(UserCreationEvent userCreationEvent) {
        this.applicationEventPublisher.publishEvent(userCreationEvent);
    }
}
