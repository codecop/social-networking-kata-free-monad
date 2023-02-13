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

class FreeInput {

    static AstNode<BufferedReader> initInput() {
        return new FreeInitStdIn();
    }

    static class FreeInitStdIn extends AstNode<BufferedReader> {
    }

    static AstNode<String> readLine(BufferedReader in) {
        return new FreeReadStdIn(in);
    }

    static class FreeReadStdIn extends AstNode<String> {
        final BufferedReader in;

        public FreeReadStdIn(BufferedReader in) {
            this.in = in;
        }
    }
}
