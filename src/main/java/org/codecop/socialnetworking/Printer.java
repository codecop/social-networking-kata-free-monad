package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

interface PrinterOps {

    public static Free<DslCommand<Void>> println(Optional<String> text) {
        return Free.liftF(createPrintln(text));
    }

    static Println createPrintln(Optional<String> text) {
        return new Println(text);
    }

    public static Free<DslCommand<Void>> println(DslCommand<Optional<String>> text) {
        return Free.liftF(mapPrintln(text));
    }

    static DslCommand<Void> mapPrintln(DslCommand<Optional<String>> textCmd) {
        return textCmd.flatMap(PrinterOps::createPrintln);
    }

    static class Println extends DslCommand<Void> {
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
