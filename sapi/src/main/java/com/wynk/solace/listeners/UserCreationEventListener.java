package com.wynk.solace.listeners;

import com.wynk.common.UserType;
import com.wynk.solace.CircleSolaceMapping;
import com.wynk.solace.SolaceQueueMessageProducer;
import com.wynk.solace.dto.SolaceUserData;
import com.wynk.solace.listeners.events.UserCreationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.util.parsing.combinator.testing.Str;

import java.sql.Time;
import java.util.Date;

@Service
public class UserCreationEventListener extends AbstractMusicEventListener<UserCreationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UserCreationEventListener.class.getCanonicalName());


    private boolean isEnabled = true;

    @Override
    protected boolean isEnabled() {
        return isEnabled;
    }

    @Autowired
    private SolaceQueueMessageProducer solaceQueueMessageProducer;

    @Override
    protected boolean canHandleEvent(UserCreationEvent event) {
        return true;
    }

    @Override
    protected void handleEvent(UserCreationEvent event) {
        String msisdn = event.getUser().getMsisdn();
        String userType = event.getUser().getUserType();
        String circle = event.getUser().getCircle();
        logger.info("UserCircle in Solace is: {}", circle);
        CircleSolaceMapping circleSolaceMapping = CircleSolaceMapping.getCircleMappingByName(circle);
        long timestamp = event.getUser().getCreationDate();    // timestamp of user creation.
        String uId = event.getUser().getUid();
        SolaceUserData solaceUserData = (new SolaceUserData.BuilderClass()).circle(circleSolaceMapping).msisdn(msisdn).timestamp(timestamp).userType(userType).uId(uId).build();
        solaceQueueMessageProducer.publishMessage(solaceUserData);

    }
}
