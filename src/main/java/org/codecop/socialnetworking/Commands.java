package org.codecop.socialnetworking;

import java.util.Optional;

/**
 * Parsing and executing. Not from domain. Non pure actions are Free Monads.
 */
public class Commands {

    static Unrestricted<DslCommand<Void>, Void> handle(Command command) {
        return post(command).orElse( //
               read(command).orElse( //
               wall(command).orElse( //
               following(command).orElse( //
               unknown(command)))));
    }

    static Optional<Unrestricted<DslCommand<Void>, Void>> post(Command command) {
        if (isPost(command)) {

            Message message = parsePostMessage(command);
            Unrestricted<DslCommand<Void>, Void> saved = InMemoryOps.save(message); // io

            return Optional.of(saved);
        }
        return Optional.empty();
    }

    private static boolean isPost(Command command) {
        return command.line.matches("\\w+\\s*->\\s*.*");
    }

    private static Message parsePostMessage(Command command) {
        String[] parts = command.line.split("\\s*->\\s*");
        String user = parts[0];
        String text = parts[1];
        return new Message(user, text, command.atTime);
    }

    public static Optional<Unrestricted<DslCommand<Void>, Void>> read(Command command) {
        if (isRead(command)) {

            String user = parseReadUser(command);
            Unrestricted<DslCommand<Void>, Void> printedTexts = InMemoryOps.queryMessagesFor(user). // io
                    map(Messages::texts). //
                    flatMap(PrinterOps::println); // io

            return Optional.of(printedTexts);
        }
        return Optional.empty();
    }

    private static boolean isRead(Command command) {
        return command.line.matches("\\w+") && !command.line.equalsIgnoreCase("quit");
    }

    private static String parseReadUser(Command command) {
        return command.line;
    }

    public static Optional<Unrestricted<DslCommand<Void>, Void>> wall(Command command) {
        if (isWall(command)) {

            String user = parseWallUser(command);
            Unrestricted<DslCommand<Void>, Void> printedTexts = InMemoryOps.queryWallUsersFor(user). // io
                    flatMap(Commands::queryMessagesForAllUsers). // mixed
                    map(Messages::usersWithTexts). //
                    flatMap(PrinterOps::println); // io

            return Optional.of(printedTexts);
        }
        return Optional.empty();
    }

    private static boolean isWall(Command command) {
        return command.line.matches("\\w+\\s+wall");
    }

    private static String parseWallUser(Command command) {
        return command.line.split("\\s+")[0];
    }

    private static Unrestricted<DslCommand<Messages>, Messages> queryMessagesForAllUsers(WallUsers wallUsers) {
        return wallUsers.users(). //
                map(InMemoryOps::queryMessagesFor). // io
                // I have a Stream<Free<Messages>> -> Free<Messages>
                reduce(DslCommand.of(Messages.empty()), new Unrestricted.Joiner<>());
    }

    public static Optional<Unrestricted<DslCommand<Void>, Void>> following(Command command) {
        if (isFollowing(command)) {

            Following following = parseFollowing(command);
            Unrestricted<DslCommand<Void>, Void> saved = InMemoryOps.saveFollowingFor(following.user, following.other); // io

            return Optional.of(saved);
        }
        return Optional.empty();
    }

    private static boolean isFollowing(Command command) {
        return command.line.matches("\\w+\\s+follows\\s+\\w+");
    }

    private static Following parseFollowing(Command command) {
        String[] parts = command.line.split("\\s+");
        String user = parts[0];
        String other = parts[2];
        return new Following(user, other);
    }

    private static class Following {
        final String user;
        final String other;

        public Following(String user, String other) {
            this.user = user;
            this.other = other;
        }
    }

    public static Unrestricted<DslCommand<Void>, Void> unknown(Command command) {
        return PrinterOps.println(Optional.of("Unknown command " + command.line));
    }

}

class Command {
    final String line;
    final Long atTime;

    public Command(String line, Long atTime) {
        this.line = line.trim();
        this.atTime = atTime;
    }
}
