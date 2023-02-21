package org.codecop.socialnetworking;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Parsing and executing. Not from domain. Non pure actions are Free Monads.
 */
public class Commands {

    static Free<DslCommand, ?> handle(Command command) {
        return post(command).orElse( //
               read(command).orElse( //
               wall(command).orElse( //
               following(command).orElse( //
               unknown(command)))));
    }

    static Optional<Free<DslCommand, ?>> post(Command command) {
        if (isPost(command)) {

            Message message = parsePostMessage(command);
            Free<DslCommand, Void> saved = InMemoryOps.save(message); // IO

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

    public static Optional<Free<DslCommand, ?>> read(Command command) {
        if (isRead(command)) {

            String user = parseReadUser(command);
            Free<DslCommand, Messages> messagesCmd = InMemoryOps.queryMessagesFor(user); // IO
            Free<DslCommand, Optional<String>> textsCmd = messagesCmd.mapF(Messages::texts);
            Free<DslCommand, Free<DslCommand, Void>> printedTexts = textsCmd.mapF(PrinterOps::println); // IO

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

    public static Optional<Free<DslCommand, ?>> wall(Command command) {
        if (isWall(command)) {

            String user = parseWallUser(command);
            Free<DslCommand, WallUsers> uWallUsersCmd = InMemoryOps.queryWallUsersFor(user); // IO
            Free<DslCommand, Free<DslCommand, Messages>> messagesCmd = uWallUsersCmd
                    .mapF(Commands::queryMessagesForAllUsers); // mixed
            Free<DslCommand, Free<DslCommand, Optional<String>>> textCmd = messagesCmd
                    .mapF(m -> m.mapF(Messages::usersWithTexts));
            Free<DslCommand, Free<DslCommand, Free<DslCommand, Void>>> printedTexts = textCmd
                    .mapF(m -> m.mapF(PrinterOps::println)); // IO

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

    private static Free<DslCommand, Messages> queryMessagesForAllUsers(WallUsers wallUsers) {
        Stream<String> users = wallUsers.users();
        Stream<Free<DslCommand, Messages>> messages = users.map(InMemoryOps::queryMessagesFor); // IO
        return reduce(messages);
    }

    private static Free<DslCommand, Messages> reduce(Stream<Free<DslCommand, Messages>> messages) {
        Free<DslCommand, Messages> initial = Free.liftF(DslResult.of(Messages.empty()));
        return messages.reduce(initial, Commands::join);
    }

//    private static Free<DslCommand, Messages> join1(Free<DslCommand, Messages> fma, Free<DslCommand, Messages> fmb) {
//        return fma.mapF(ma -> {
//            return fmb.mapF(mb -> {
//                return ma.join(mb);
//            });
//        });
//    }

    private static Free<DslCommand, Messages> join(Free<DslCommand, Messages> ua, Free<DslCommand, Messages> ub) {
        return ub.flatMap(bs -> ua.map(as -> {
            return as.flatMap(a -> bs.map(b -> ((Messages)a).join((Messages)b)));
        }));
    }

    public static Optional<Free<DslCommand, ?>> following(Command command) {
        if (isFollowing(command)) {

            Following following = parseFollowing(command);
            Free<DslCommand, Void> saved = InMemoryOps.saveFollowingFor(following.user, following.other); // IO

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

    public static Free<DslCommand, ?> unknown(Command command) {
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
        return "User Command \"" + line + "\" at " + atTime;
    }
}
