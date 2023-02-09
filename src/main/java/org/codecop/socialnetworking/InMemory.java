package org.codecop.socialnetworking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A side effect, impure.
 */
public class InMemory {

    private static final Map<String, List<Message>> messagesByUser = new HashMap<>();

    static Messages queryMessagesFor(String user) {
        return new Messages(getMessages(user));
    }

    static void save(Message message) {
        List<Message> messages = getMessages(message.fromUser);
        messages.add(message);
        messagesByUser.put(message.fromUser, messages);
    }

    private static List<Message> getMessages(String user) {
        return messagesByUser.getOrDefault(user, new ArrayList<Message>());
    }

    /**
     * A side effect, impure.
     */
    private static final Map<String, List<String>> followingByUser = new HashMap<>();

    static WallUsers queryWallUsersFor(String user) {
        return new WallUsers(user, getFollowing(user));
    }

    static void saveFollowingFor(String user, String other) {
        List<String> following = getFollowing(user);
        following.add(other);
        followingByUser.put(user, following);
    }

    private static List<String> getFollowing(String user) {
        return followingByUser.getOrDefault(user, new ArrayList<String>());
    }

    static void initDatabase() {
        messagesByUser.clear();
        followingByUser.clear();
    }

}

class FreeInMemory {

    static Free<Void> initDatabase() {
        return new FreeInitDatabase();
    }

    static class FreeInitDatabase extends Free<Void> {
    }

    static Free<Messages> queryMessagesFor(String user) {
        return new FreeQueryMessages(user);
    }

    static class FreeQueryMessages extends Free<Messages> {
        final String user;

        public FreeQueryMessages(String user) {
            this.user = user;
        }
    }

    static Free<Void> save(Message message) {
        return new FreeSaveMessages(message);
    }

    static class FreeSaveMessages extends Free<Void> {
        final Message message;

        public FreeSaveMessages(Message message) {
            this.message = message;
        }
    }

    static Free<WallUsers> queryWallUsersFor(String user) {
        return new FreeQueryWall(user);
    }

    static class FreeQueryWall extends Free<WallUsers> {
        final String user;

        public FreeQueryWall(String user) {
            this.user = user;
        }
    }

    static Free<Void> saveFollowingFor(String user, String other) {
        return new FreeSaveFollowing(user, other);
    }

    static class FreeSaveFollowing extends Free<Void> {
        final String user;
        final String other;

        public FreeSaveFollowing(String user, String other) {
            this.user = user;
            this.other = other;
        }
    }

}
