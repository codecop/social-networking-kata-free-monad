package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.NamedFunction.named;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Free<DomainOps, ?> app = app();

        System.err.println("APP:\n" + Free.format(app) + "\n");
        Ex.rethrowIoException(() -> new DslVisitor().foldMap(app));
    }

    static Free<DomainOps, ?> app() {
        Free<DomainOps, BufferedReader> init = InMemoryOps.initDatabase(). // IO
                flatMap(named("openInput", ignore -> InputOps.openInput()));
        return processInput(init);
    }

    static Free<DomainOps, Void> processInput(Free<DomainOps, BufferedReader> input) {
        Free<DomainOps, String> lineRead = readLineFrom(input);
        Free<DomainOps, Command> command = createCommand(lineRead);
        Free<DomainOps, Void> executed = executeCommand(command, input);
        return executed;
    }

    static Free<DomainOps, String> readLineFrom(Free<DomainOps, BufferedReader> input) {
        return input.flatMap(named("readLine", InputOps::readLine)); // IO
    }

    static Free<DomainOps, Command> createCommand(Free<DomainOps, String> lineRead) {
        return lineRead.flatMap(named("getTimedAndCreateCommand", line -> TimerOps.time(). // IO
                map(named("create Command", time -> new Command(line, time)))));
    }

    static Free<DomainOps, Void> executeCommand(Free<DomainOps, Command> command, Free<DomainOps, BufferedReader> input) {
        return command.flatMap(named("processCommand", c -> processCommand(input, c)));
    }

    private static Free<DomainOps, Void> processCommand(Free<DomainOps, BufferedReader> input, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.<DomainOps, Void>pure(null);
        }

        Free<DomainOps, Void> result = Commands.handle(command);
        Free<DomainOps, Void> remaining = result.flatMap(named("recurse processInput", ignore -> processInput(input)));
        return remaining;
    }

}
