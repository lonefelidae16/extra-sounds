package dev.stashy.extrasounds.impl;

import org.apache.logging.log4j.message.*;

public class CustomizedLog4jMessageFactory implements MessageFactory {
    private final String prefix;

    public CustomizedLog4jMessageFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Message newMessage(Object message) {
        return new ObjectMessage(this.appendPrefix(message == null ? "null" : message.toString()));
    }

    @Override
    public Message newMessage(String message) {
        return new SimpleMessage(this.appendPrefix(message));
    }

    @Override
    public Message newMessage(String message, Object... params) {
        return new FormattedMessage(this.appendPrefix(message), params);
    }

    private String appendPrefix(CharSequence message) {
        return "[%s] %s".formatted(this.prefix, message);
    }
}
