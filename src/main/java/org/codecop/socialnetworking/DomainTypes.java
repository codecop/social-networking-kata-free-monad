package org.codecop.socialnetworking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

class Messages {

    private final List<Message> messages;

    public Messages(List<Message> messages) {
        this.messages = Collections.unmodifiableList(messages);
    }

    public Stream<Message> stream() {
        return messages.stream();
    }

    public Stream<String> texts() {
        return stream().sorted().map(Message::getText);
    }

    // public Stream<String> usersWithTexts() {
    //     return stream().sorted().map(Message::getUserWithText);
    // }
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
