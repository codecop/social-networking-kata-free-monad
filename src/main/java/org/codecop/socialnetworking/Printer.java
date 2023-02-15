package org.codecop.socialnetworking;

import java.util.Optional;

public class Printer {

    public static void println(Optional<String> text) {
        text.ifPresent(System.out::println);
    }

}

interface PrinterOps {

    public static Unrestricted<DslCommand<Void>> println(Optional<String> text) {
        return Unrestricted.liftF(createPrintln(text));
    }

    static Println createPrintln(Optional<String> text) {
        return new Println(text);
    }

    public static Unrestricted<DslCommand<Void>> println(DslCommand<Optional<String>> text) {
        return Unrestricted.liftF(mapPrintln(text));
    }

    static DslCommand<Void> mapPrintln(DslCommand<Optional<String>> textCmd) {
        return textCmd.flatMap(PrinterOps::createPrintln);
    }

    static class Println extends DslCommand<Void> {
        final Optional<String> text;

        public Println(Optional<String> text) {
            this.text = text;
        }
    }

}
