package org.codecop.socialnetworking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DomainTypesTest {

    @Nested
    class JoinerTest {

        @Test
        void joinerJoins() {
            Joiner<Messages> joiner = new Joiner<>(Messages.empty());

            List<Messages> messages = Arrays.asList( //
                    new Messages(Arrays.asList(new Message("A", "Hi1", 1), new Message("A", "Hi3", 3))), //
                    new Messages(Arrays.asList(new Message("B", "Hello", 2))));

            Messages joined = messages.stream().reduce(Messages.empty(), (ignore, b) -> joiner.apply(b));
            assertEquals("A - Hi1\nB - Hello\nA - Hi3", joined.usersWithTexts().get());
        }

    }

}
