package com.readingshare.room.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event fired when the active users list is updated.
 * This event is used to notify WebSocket subscribers about user changes.
 */
public class MemberUpdateEvent extends ApplicationEvent {
    public MemberUpdateEvent(Object source) {
        super(source);
    }
}
