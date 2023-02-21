package org.codecop.socialnetworking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Values, immutable, pure.
 */
public class DomainTypes {

}

// maybe static class User

class Message implements Comparable<Message> {

    final String fromUser;
    private final String text;
    private final long atTime;

    Message(String fromUser, String text, long time) {
        this.fromUser = fromUser;
        this.text = text;
        this.atTime = time;
    }

    public String getText() {
        return text;
    }

    public String getUserWithText() {
        return fromUser + " - " + text;
    }

    @Override
    public int compareTo(Message other) {
        if (atTime < other.atTime) {
            return -1;
        }
        if (atTime > other.atTime) {
            return 1;
        }
        return 0;
    }
}

interface Joining<T> {
    public T join(T other);
}

class Messages implements Joining<Messages> {

    private final List<Message> messages;

    public Messages(List<Message> messages) {
        this.messages = Collections.unmodifiableList(messages);
    }

    public Optional<String> texts() {
        if (messages.size() > 0) {
            String lines = messages.stream(). //
                    sorted(). //
                    map(Message::getText). //
                    collect(Collectors.joining("\n"));
            return Optional.of(lines);
        }
        return Optional.empty();
    }

    public Optional<String> usersWithTexts() {
        if (messages.size() > 0) {
            String lines = messages.stream(). //
                    sorted(). //
                    map(Message::getUserWithText). //
                    collect(Collectors.joining("\n"));
            return Optional.of(lines);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        // debugging
        return usersWithTexts().orElse("<empty>");
    }
    
    @Override
    public Messages join(Messages other) {
        List<Message> newMessages = new ArrayList<>();
        newMessages.addAll(messages);
        newMessages.addAll(other.messages);
        return new Messages(newMessages);
    }

    public static Messages empty() {
        return new Messages(Collections.emptyList());
    }

}

class Joiner<T extends Joining<T>> implements Function<T, T> {
    private T joined;

    public Joiner(T initial) {
        joined = initial;
    }

    @Override
    public T apply(T nextMessages) {
        joined = joined.join(nextMessages);
        return joined;
    }
}

class WallUsers {

    private final String thisUser;
    private final List<String> following;

    public WallUsers(String thisUser, List<String> following) {
        this.thisUser = thisUser;
        this.following = Collections.unmodifiableList(following);
    }

    public Stream<String> users() {
        List<String> users = new ArrayList<>();
        users.add(thisUser);
        users.addAll(following);
        return users.stream();
    }
}
