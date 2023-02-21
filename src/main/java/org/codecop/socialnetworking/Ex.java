package org.codecop.socialnetworking;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Exception helper for lambda and streams.
 */
public class Ex {

    @FunctionalInterface
    interface IoSupplier<T> {
        T get() throws IOException;
    }

    public static <T> T uncheckIoException(IoSupplier<T> object) {
        try {
            return object.get();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void rethrowIoException(Runnable block) throws IOException {
        try {
            block.run();
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }

    }

}
