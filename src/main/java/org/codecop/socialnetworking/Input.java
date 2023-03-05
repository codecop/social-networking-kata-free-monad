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

    static Free<DomainOps, BufferedReader> openInput() {
        return Free.liftM(new OpenStdIn());
    }

    static class OpenStdIn extends DomainOps {
    }

    static Free<DomainOps, String> readLine(BufferedReader in) {
        return Free.liftM(new ReadStdIn(in));
    }

    static class ReadStdIn extends DomainOps {
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
