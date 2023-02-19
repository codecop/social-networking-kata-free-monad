package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A side effect, impure.
 */
public class Input {

    static BufferedReader initInput() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    static String readLine(BufferedReader in) throws IOException {
        return debug(in.readLine());
    }

    private static String debug(String line) {
        // debugging
        // System.err.println("READING ... " + line);
        return line;
    }
}

interface InputOps {

    static Unrestricted<DslCommand<BufferedReader>> openInput() {
        return Unrestricted.liftF(new OpenStdIn());
    }

    static class OpenStdIn extends DslCommand<BufferedReader> {
    }

    static Unrestricted<DslCommand<String>> readLine(BufferedReader in) {
        return Unrestricted.liftF(new ReadStdIn(in));
    }

    static class ReadStdIn extends DslCommand<String> {
        final BufferedReader in;

        public ReadStdIn(BufferedReader in) {
            this.in = in;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " with " + in;
        }
    }
}
