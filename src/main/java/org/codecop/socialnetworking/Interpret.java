package org.codecop.socialnetworking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Evaluate the tree
 */
public class Interpret {

    public static Object it(Free<DslCommand, Void> uCommand) throws IOException {
        Objects.requireNonNull(uCommand);
        DslVisitor v = new DslVisitor();
        try {
            return v.matchCommand(uCommand);
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

}
