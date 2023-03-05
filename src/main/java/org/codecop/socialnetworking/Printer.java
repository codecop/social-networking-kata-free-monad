package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

interface PrinterOps {

    public static Free<DomainOps, Void> println(Optional<String> text) {
        return Free.liftM(new Println(text));
    }

    static class Println extends DomainOps {
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
