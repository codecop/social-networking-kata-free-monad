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

interface InMemoryOps {

    static Free<DslCommand<Void>, Void> initDatabase() {
        return Free.liftF(new InitDatabase());
    }

    static class InitDatabase extends DslCommand<Void> {
    }

    static Free<DslCommand<Messages>, Messages> queryMessagesFor(String user) {
        return Free.liftF(new QueryMessages(user));
    }

    static class QueryMessages extends DslCommand<Messages> {
        final String user;

        public QueryMessages(String user) {
            this.user = user;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " for " + user;
        }
    }

    static Free<DslCommand<Void>, Void> save(Message message) {
        return Free.liftF(new SaveMessages(message));
    }

    static class SaveMessages extends DslCommand<Void> {
        final Message message;

        public SaveMessages(Message message) {
            this.message = message;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " of " + message.getText();
        }
    }

    static Free<DslCommand<WallUsers>, WallUsers> queryWallUsersFor(String user) {
        return Free.liftF(new QueryWall(user));
    }

    static class QueryWall extends DslCommand<WallUsers> {
        final String user;

        public QueryWall(String user) {
            this.user = user;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " for " + user;
        }
    }

    static Free<DslCommand<Void>, Void> saveFollowingFor(String user, String other) {
        return Free.liftF(new SaveFollowing(user, other));
    }

    static class SaveFollowing extends DslCommand<Void> {
        final String user;
        final String other;

        public SaveFollowing(String user, String other) {
            this.user = user;
            this.other = other;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " for " + user + " to " + other;
        }
    }

}
