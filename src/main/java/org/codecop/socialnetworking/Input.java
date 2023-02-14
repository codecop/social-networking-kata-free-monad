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
        return in.readLine();
    }
}

interface InputOps {

    static DslCommand<BufferedReader> initInput() {
        return new InitStdIn();
    }

    static class InitStdIn extends DslCommand<BufferedReader> {
    }

    static DslCommand<String> readLine(BufferedReader in) {
        return new ReadStdIn(in);
    }

    static class ReadStdIn extends DslCommand<String> {
        final BufferedReader in;

        public ReadStdIn(BufferedReader in) {
            this.in = in;
        }
    }
}
