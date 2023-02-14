package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

interface PrinterOps {

    public static DslCommand<Void> println(Optional<String> text) {
        return new Println(text);
    }

    static class Println extends DslCommand<Void> {
        final Optional<String> text;

        public Println(Optional<String> text) {
            this.text = text;
        }
    }

}
