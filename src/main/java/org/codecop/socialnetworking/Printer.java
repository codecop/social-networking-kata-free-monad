package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

interface PrinterOps {

    public static Free<DslCommand, Void> println(Optional<String> text) {
        return Free.liftF(new Println(text));
    }

    static class Println extends DslCommand {
        final Optional<String> text;

        public Println(Optional<String> text) {
            this.text = text;
        }

        @Override
        public String toString() {
            // debugging
            return super.toString() + " of " + text;
        }
    }

}
