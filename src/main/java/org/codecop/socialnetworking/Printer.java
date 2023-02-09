package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

class FreePrinter {

    public static Free<Void> println(Optional<String> text) {
        return new FreePrintln(text);
    }

    static class FreePrintln extends Free<Void> {
        final Optional<String> text;

        public FreePrintln(Optional<String> text) {
            this.text = text;
        }
    }

}
