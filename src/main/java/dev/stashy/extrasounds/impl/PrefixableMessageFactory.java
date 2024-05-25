package dev.stashy.extrasounds.impl;

import org.apache.logging.log4j.message.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PrefixableMessageFactory implements MessageFactory {
    private final CharSequence prefix;

    public PrefixableMessageFactory() {
        this(null);
    }

    public PrefixableMessageFactory(@Nullable CharSequence prefix) {
        this.prefix = Objects.requireNonNullElse(prefix, "");
    }

    @Override
    public Message newMessage(Object message) {
        return this.newMessage(message == null ? "null" : message.toString());
    }

    @Override
    public Message newMessage(String message) {
        return new SimpleMessage(this.appendPrefix(message));
    }

    @Override
    public Message newMessage(String message, Object... params) {
        return new FormattedMessage(this.appendPrefix(message), params);
    }

    private String appendPrefix(String message) {
        return (this.prefix.isEmpty()) ? message : "[%s] %s".formatted(this.prefix, message);
    }
}
