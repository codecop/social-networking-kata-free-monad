package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        InMemory.initDatabase(); // io
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = in.readLine(); // io
            long currentTime = Timer.time(); // io

            String command = line.trim();
            if ("quit".equalsIgnoreCase(command)) {
                break;
            }

            Optional<PostCommand> p = PureBoundary.posting(command, currentTime); // pure
            p.ifPresent(c -> InMemory.save(c.message)); // io

            Optional<ReadCommand> r = PureBoundary.reading(command); // pure
            r.ifPresent(c -> InMemory.queryMessagesFor(c.user). // io
                             texts(). // pure
                             forEach(System.out::println)); // io

            Optional<WallCommand> w = PureBoundary.wall(command); // pure
            w.ifPresent(c -> InMemory.queryWallUsersFor(c.user). // io
                               users(). // pure
                               flatMap(name -> InMemory.queryMessagesFor(name).stream()). // io
                               sorted().map(Message::getUserWithText). // pure
                               forEach(System.out::println)); // io

            Optional<FollowingCommand> f = PureBoundary.following(command); // pure
            f.ifPresent(c -> InMemory.saveFollowingFor(c.user, c.other)); // io
        }

    }

}
