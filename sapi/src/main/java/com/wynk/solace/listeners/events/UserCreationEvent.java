package com.wynk.solace.listeners.events;

import com.wynk.user.dto.User;
import org.springframework.context.ApplicationEvent;

public class UserCreationEvent extends ApplicationEvent {
    private static final long serialVersionUID = -8543209281053558159L;
    private User user;
    private String source;

    public UserCreationEvent(String source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getSource(String source) {
        return source;
    }
}
