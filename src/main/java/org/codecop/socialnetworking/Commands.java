package org.codecop.socialnetworking;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Parsing and executing. Not from domain. Non pure actions are Free Monads.
 */
public class Commands {

    static Unrestricted<DslCommand<Void>> handle(Command command) {
        return post(command).orElse( //
                read(command).orElse( //
                        wall(command).orElse( //
                                following(command).orElse( //
                                        unknown(command)))));
    }

    static Optional<Unrestricted<DslCommand<Void>>> post(Command command) {
        if (isPost(command)) {

            Message message = parsePostMessage(command);
            Unrestricted<DslCommand<Void>> saved = InMemoryOps.save(message); // io

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

    public static Optional<Unrestricted<DslCommand<Void>>> read(Command command) {
        if (isRead(command)) {

            String user = parseReadUser(command);
            Unrestricted<DslCommand<Messages>> messagesCmd = InMemoryOps.queryMessagesFor(user); // IO
            Unrestricted<DslCommand<Optional<String>>> textsCmd = messagesCmd.mapF(Messages::texts); // <Messages, Optional<String>>
            Unrestricted<DslCommand<Void>> printedTexts = textsCmd.flatMap(PrinterOps::println); // IO

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

    public static Optional<Unrestricted<DslCommand<Void>>> wall(Command command) {
        if (isWall(command)) {

            String user = parseWallUser(command);
            Unrestricted<DslCommand<WallUsers>> uWallUsersCmd = InMemoryOps.queryWallUsersFor(user); // IO
            Unrestricted<DslCommand<Unrestricted<DslCommand<Messages>>>> messagesCmd = uWallUsersCmd
                    .map(Commands::queryMessagesForAllUsers); // mixed
            Unrestricted<DslCommand<Unrestricted<DslCommand<Optional<String>>>>> textCmd = messagesCmd
                    .<Unrestricted<DslCommand<Messages>>, Unrestricted<DslCommand<Optional<String>>>>mapF(
                            m -> m.<Messages, Optional<String>>mapF(Messages::usersWithTexts));
            Unrestricted<DslCommand<Unrestricted<DslCommand<Void>>>> printedTexts = textCmd
                    .<Unrestricted<DslCommand<Optional<String>>>, Unrestricted<DslCommand<Void>>>mapF(
                            m -> m.flatMap(PrinterOps::println)); // IO

            Unrestricted casts = printedTexts;
            return Optional.of(casts);
        }
        return Optional.empty();
    }

    private static boolean isWall(Command command) {
        return command.line.matches("\\w+\\s+wall");
    }

    private static String parseWallUser(Command command) {
        return command.line.split("\\s+")[0];
    }

    private static DslCommand<Unrestricted<DslCommand<Messages>>> queryMessagesForAllUsers(
            DslCommand<WallUsers> wallUsersCmd) {
        return wallUsersCmd.map(wallUsers -> {
            Stream<String> users = wallUsers.users();
            Stream<Unrestricted<DslCommand<Messages>>> messages = users.map(InMemoryOps::queryMessagesFor); // IO
            return reduce(messages);
        });
    }

    private static Unrestricted<DslCommand<Messages>> reduce(Stream<Unrestricted<DslCommand<Messages>>> messages) {
        Unrestricted<DslCommand<Messages>> initial = Unrestricted.liftF(DslResult.of(Messages.empty()));
        return messages.reduce(initial, (a, b) -> join(a, b));
    }

    private static Unrestricted<DslCommand<Messages>> join(Unrestricted<DslCommand<Messages>> ua,
            Unrestricted<DslCommand<Messages>> ub) {
        return ub.flatMap(bs -> ua.map(as -> join(as, bs)));
    }

    private static DslCommand<Messages> join(DslCommand<Messages> as, DslCommand<Messages> bs) {
        return as.flatMap(a -> bs.map(b -> a.join(b)));
    }

    public static Optional<Unrestricted<DslCommand<Void>>> following(Command command) {
        if (isFollowing(command)) {

            Following following = parseFollowing(command);
            Unrestricted<DslCommand<Void>> saved = InMemoryOps.saveFollowingFor(following.user, following.other); // io

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

    public static Unrestricted<DslCommand<Void>> unknown(Command command) {
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
