package org.codecop.socialnetworking;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Parsing and executing. Not from domain. Non pure actions are wrapped in Free Monads.
 */
public class Commands {

    static Free<DomainOps, Void> handle(Command command) {
        return post(command).orElse( //
               read(command).orElse( //
               wall(command).orElse( //
               following(command).orElse( //
               unknown(command)))));
    }

    static Optional<Free<DomainOps, Void>> post(Command command) {
        if (isPost(command)) {

            Message message = parsePostMessage(command);
            Free<DomainOps, Void> savedMessage = InMemoryOps.save(message); // IO

            return Optional.of(savedMessage);
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

    public static Optional<Free<DomainOps, Void>> read(Command command) {
        if (isRead(command)) {

            String user = parseReadUser(command);
            Free<DomainOps, Messages> messages = InMemoryOps.queryMessagesFor(user); // IO
            Free<DomainOps, Optional<String>> texts = messages.map(Messages::texts);
            Free<DomainOps, Void> printedTexts = texts.flatMap(PrinterOps::println); // IO

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

    public static Optional<Free<DomainOps, Void>> wall(Command command) {
        if (isWall(command)) {

            String user = parseWallUser(command);
            Free<DomainOps, WallUsers> wallUsers = InMemoryOps.queryWallUsersFor(user); // IO
            Free<DomainOps, Messages> allMessages = wallUsers.flatMap(Commands::queryMessagesForAllUsers); // mixed
            Free<DomainOps, Optional<String>> allTexts = allMessages.map(Messages::usersWithTexts);
            Free<DomainOps, Void> printedTexts = allTexts.flatMap(PrinterOps::println); // IO

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

    private static Free<DomainOps, Messages> queryMessagesForAllUsers(WallUsers wallUsers) {
        Stream<String> users = wallUsers.users();
        Stream<Free<DomainOps, Messages>> messages = users.map(InMemoryOps::queryMessagesFor); // IO
        return reduce(messages);
    }

    private static Free<DomainOps, Messages> reduce(Stream<Free<DomainOps, Messages>> messages) {
        Free<DomainOps, Messages> initial = Free.pure(Messages.empty());
        return messages.reduce(initial, (a, b) -> a.join(b, Messages::join));
    }

    public static Optional<Free<DomainOps, Void>> following(Command command) {
        if (isFollowing(command)) {

            Following following = parseFollowing(command);
            Free<DomainOps, Void> saved = InMemoryOps.saveFollowingFor(following.user, following.other); // IO

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

    public static Free<DomainOps, Void> unknown(Command command) {
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

    @Override
    public String toString() {
        // debugging
        return "User Command \"" + line + "\" at " + atTime;
    }
}
