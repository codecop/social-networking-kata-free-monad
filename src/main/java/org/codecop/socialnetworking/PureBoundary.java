package org.codecop.socialnetworking;

import java.util.Optional;

/**
 * Pure parsing results. Not from domain but pure. Just parse the strings.
 */
public class PureBoundary {

    static Optional<ReadCommand> reading(String command) {
        if (ReadCommand.is(command)) {
            ReadCommand c = new ReadCommand(command);
            return Optional.of(c);
        }
        return Optional.empty();
    }

    static Optional<PostCommand> posting(String command, long currentTime) {
        if (PostCommand.is(command)) {
            PostCommand c = new PostCommand(command, currentTime);
            return Optional.of(c);
        }
        return Optional.empty();
    }

    static Optional<WallCommand> wall(String command) {
        if (WallCommand.is(command)) {
            WallCommand c = new WallCommand(command);
            return Optional.of(c);
        }
        return Optional.empty();
    }

    static Optional<FollowingCommand> following(String command) {
        if (FollowingCommand.is(command)) {
            FollowingCommand c = new FollowingCommand(command);
            return Optional.of(c);
        }
        return Optional.empty();
    }

}

class ReadCommand {

    final String user;

    public ReadCommand(String command) {
        user = command;
    }

    static boolean is(String command) {
        return command.matches("\\w+");
    }
}

class PostCommand {

    final Message message;

    public PostCommand(String command, long currentTime) {
        String[] parts = command.split("\\s*->\\s*");
        String user = parts[0];
        String text = parts[1];
        message = new Message(user, text, currentTime);
    }

    static boolean is(String command) {
        return command.matches("\\w+\\s*->\\s*.*");
    }
}

class WallCommand {

    final String user;

    public WallCommand(String command) {
        user = command.split("\\s+")[0];
    }

    static boolean is(String command) {
        return command.matches("\\w+\\s+wall");
    }
}

class FollowingCommand {

    final String user;
    final String other;

    public FollowingCommand(String command) {
        String[] parts = command.split("\\s+");
        user = parts[0];
        other = parts[2];
    }

    static boolean is(String command) {
        return command.matches("\\w+\\s+follows\\s+\\w+");
    }
}
