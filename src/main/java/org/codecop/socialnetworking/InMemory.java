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

interface DbOps<OUTPUT> extends Generic<OUTPUT> {

    class Init implements DbOps<Void> {
    }

    class Query<T> implements DbOps<T> {
        // used for QueryMessages, QueryWallUsers
        final Class<T> type;
        final String user;

        public Query(Class<T> type, String user) {
            this.type = type;
            this.user = user;
        }
    }

    class Save<T> implements DbOps<Void> {
        // used for SaveUser, SaveFollowing
        final Class<T> type;
        // key/id is missing in this example
        final T value;

        public Save(Class<T> type, T value) {
            this.type = type;
            this.value = value;
        }
    }

}

//class DbMonad<T, A> implements Free<DbOps<T>, T, A> {
//    // type DBMonad[A] = Free[DBOps, A]
//}

class DbOpsImplFoo {
    static Free<DbOps<Void>, Void, Void> /*DbMonad<Void>*/ init() {
        return Free.<DbOps<Void>, Void, Void>liftM(new DbOps.Init());
    }

    static <T> Free<DbOps<T>, T, T> /*DbMonad<T>*/ query(Class<T> type, String user) {
        return Free.<DbOps<T>, T, T>liftM(new DbOps.Query<>(type, user));
    }

    static <T> Free<DbOps<Void>, Void, T> /*DbMonad<Void>*/ save(Class<T> type, T value) {
        return Free.<DbOps<Void>, Void, T>liftM(new DbOps.Save<>(type, value));
    }
}

class FreeInMemory {

    static AstNode<Void> initDatabase() {
        return new FreeInitDatabase();
    }

    static class FreeInitDatabase extends AstNode<Void> {
    }

    static AstNode<Messages> queryMessagesFor(String user) {
        return new FreeQueryMessages(user);
    }

    static class FreeQueryMessages extends AstNode<Messages> {
        final String user;

        public FreeQueryMessages(String user) {
            this.user = user;
        }
    }

    static AstNode<Void> save(Message message) {
        return new FreeSaveMessages(message);
    }

    static class FreeSaveMessages extends AstNode<Void> {
        final Message message;

        public FreeSaveMessages(Message message) {
            this.message = message;
        }
    }

    static AstNode<WallUsers> queryWallUsersFor(String user) {
        return new FreeQueryWall(user);
    }

    static class FreeQueryWall extends AstNode<WallUsers> {
        final String user;

        public FreeQueryWall(String user) {
            this.user = user;
        }
    }

    static AstNode<Void> saveFollowingFor(String user, String other) {
        return new FreeSaveFollowing(user, other);
    }

    static class FreeSaveFollowing extends AstNode<Void> {
        final String user;
        final String other;

        public FreeSaveFollowing(String user, String other) {
            this.user = user;
            this.other = other;
        }
    }

}
