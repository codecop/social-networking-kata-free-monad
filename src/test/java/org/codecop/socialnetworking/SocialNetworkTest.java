package org.codecop.socialnetworking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.codecop.base.CaptureSystemOutExtension;
import org.codecop.base.ProvideSystemInExtension;
import org.codecop.base.SystemIn;
import org.codecop.base.SystemOut;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProvideSystemInExtension.class)
@ExtendWith(CaptureSystemOutExtension.class)
class SocialNetworkTest {

    private void runSocialNetwork(Consumer<String> in) throws IOException {
        in.accept("quit");
        SocialNetwork.main(new String[0]);
    }

    @Test
    @Disabled
    void notDoingAnything(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {

        runSocialNetwork(in);

        assertEquals("", out.get());
    }

    @Test
    void postingOne(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Alice -> I love the weather today");
        in.accept("Alice");

        runSocialNetwork(in);

        assertEquals("I love the weather today\n", out.get());
    }

    @Test
    @Disabled
    void readingUnknownUser(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Bob");

        runSocialNetwork(in);

        assertEquals("", out.get());
    }

    @Test
    @Disabled
    void postingMoreWithBlanks(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Bob->Damn! We lost!");
        in.accept(" Bob ->Good game though. ");
        in.accept("Bob");

        runSocialNetwork(in);

        assertEquals("Damn! We lost!\nGood game though.\n", out.get());
    }

    @Test
    @Disabled
    void mixingPostingAndReading(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Alice -> M1");
        in.accept("Alice");
        in.accept("Alice -> M2");
        in.accept("Alice");

        runSocialNetwork(in);

        assertEquals("M1\nM1\nM2\n", out.get());
    }
    
    @Test
    @Disabled
    void wall(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Alice -> I love the weather today");
        in.accept("Alice wall");

        runSocialNetwork(in);

        assertEquals("Alice - I love the weather today\n", out.get());
    }

    @Test
    @Disabled
    void following(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Charlie -> I'm in New York today! Anyone want to have a coffee?");
        in.accept("Alice -> I love the weather today");
        in.accept("Charlie follows Alice");
        in.accept("Charlie wall");

        runSocialNetwork(in);

        assertEquals("Charlie - I'm in New York today! Anyone want to have a coffee?\n" //
                + "Alice - I love the weather today\n", //
                out.get());
    }

    @Test
    @Disabled
    void followingSortedByTime(@SystemIn Consumer<String> in, @SystemOut Supplier<String> out) throws IOException {
        in.accept("Alice -> I love the weather today");
        in.accept("Charlie -> I'm in New York today! Anyone want to have a coffee?");
        in.accept("Charlie follows Alice");
        in.accept("Charlie wall");

        runSocialNetwork(in);

        assertEquals("Alice - I love the weather today\n" //
                + "Charlie - I'm in New York today! Anyone want to have a coffee?\n", //
                out.get());
    }

}
