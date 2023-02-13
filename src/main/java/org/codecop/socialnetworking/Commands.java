package org.codecop.socialnetworking;

import java.util.Optional;

/**
 * Parsing and executing. Not from domain. Non pure actions are Free Monads.
 */
public class Commands {

    static AstNode<Void> handle(Command command) {
        return post(command).orElse( //
               read(command).orElse( //
               wall(command).orElse( //
               following(command).orElse( //
               unknown(command)))));
    }

    static Optional<AstNode<Void>> post(Command command) {
        if (isPost(command)) {

            Message message = parsePostMessage(command);
            AstNode<Void> saved = FreeInMemory.save(message); // io

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

    public static Optional<AstNode<Void>> read(Command command) {
        if (isRead(command)) {

            String user = parseReadUser(command);
            AstNode<Void> printedTexts = FreeInMemory.queryMessagesFor(user). // io
                    map(Messages::texts). //
                    flatMap(FreePrinter::println); // io

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

    public static Optional<AstNode<Void>> wall(Command command) {
        if (isWall(command)) {

            String user = parseWallUser(command);
            AstNode<Void> printedTexts = FreeInMemory.queryWallUsersFor(user). // io
                    flatMap(Commands::queryMessagesForAllUsers). // mixed
                    map(Messages::usersWithTexts). //
                    flatMap(FreePrinter::println); // io

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

    private static AstNode<Messages> queryMessagesForAllUsers(WallUsers wallUsers) {
        return wallUsers.users(). //
                map(FreeInMemory::queryMessagesFor). // io
                // I have a Stream<Free<Messages>> -> Free<Messages>
                reduce(AstNode.of(Messages.empty()), new AstNode.Joiner<>());
    }

    public static Optional<AstNode<Void>> following(Command command) {
        if (isFollowing(command)) {

            Following following = parseFollowing(command);
            AstNode<Void> saved = FreeInMemory.saveFollowingFor(following.user, following.other); // io

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

    public static AstNode<Void> unknown(Command command) {
        return FreePrinter.println(Optional.of("Unknown command " + command.line));
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
