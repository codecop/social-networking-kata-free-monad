package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SocialNetwork {

    static class Message {
        String user;
        String text;
        long time;

        Message(String user, String text, long time) {
            this.user = user;
            this.text = text;
            this.time = time;
        }
    }

    public static void main(String[] args) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        Map<String, List<Message>> messagesByUser = new HashMap<>();
        Function<String, List<Message>> messagesFor = user -> messagesByUser.getOrDefault(user,
                new ArrayList<Message>());

        Map<String, List<String>> followingByUser = new HashMap<>();
        Function<String, List<String>> followingFor = user -> followingByUser.getOrDefault(user,
                new ArrayList<String>());

        long currentTime = 0;

        while (true) {
            String command = in.readLine().trim();
            currentTime++;

            if ("quit".equalsIgnoreCase(command)) {
                // do not close in
                return;
            }

            if (command.matches("\\w+\\s*->\\s*.*")) {
                String[] parts = command.split("\\s*->\\s*");
                String user = parts[0];
                String text = parts[1];

                Message message = new Message(user, text, currentTime);
                List<Message> messages = messagesFor.apply(user);
                messages.add(message);
                messagesByUser.put(user, messages);
                continue;
            }

            if (command.matches("\\w+")) {
                String user = command;
                messagesFor.apply(user).stream(). //
                        sorted(Comparator.comparing(m -> m.time)). //
                        map(m -> m.text). //
                        forEach(System.out::println);
                continue;
            }

            if (command.matches("\\w+\\s+wall")) {
                String user = command.split("\\s+")[0];

                List<String> timeLines = new ArrayList<>();
                timeLines.add(user);
                timeLines.addAll(followingFor.apply(user));

                timeLines.stream(). //
                        flatMap(name -> messagesFor.apply(name).stream()). //
                        sorted(Comparator.comparing(m -> m.time)). //
                        map(m -> m.user + " - " + m.text). //
                        forEach(System.out::println);
                continue;
            }

            if (command.matches("\\w+\\s+follows\\s+\\w+")) {
                String[] parts = command.split("\\s+");
                String user = parts[0];
                String other = parts[2];

                List<String> following = followingFor.apply(user);
                following.add(other);
                followingByUser.put(user, following);

                continue;
            }

            throw new IllegalArgumentException(command);
        }

    }

}
