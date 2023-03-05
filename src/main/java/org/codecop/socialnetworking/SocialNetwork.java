package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Free<DomainOps, ?> app = app();

        System.err.println("APP:\n" + Free.format(app) + "\n");
        Ex.rethrowIoException(() -> new DslVisitor().foldMap(app));
    }

    static Free<DomainOps, ?> app() {
        Free<DomainOps, BufferedReader> init = initIO();
        return processInput(init);
    }

    static Free<DomainOps, BufferedReader> initIO() {
        return InMemoryOps.initDatabase(). // IO
                flatMap(named("openInput", ignore -> InputOps.openInput())); // IO
    }

    static Free<DomainOps, ?> processInput(Free<DomainOps, BufferedReader> input) {
        Free<DomainOps, Free<DomainOps, String>> lineRead = readLineFrom(input);
        Free<DomainOps, Free<DomainOps, Free<DomainOps, Command>>> command = createCommand(lineRead);
        Free<DomainOps, ?> executed = executeCommand(command, input);
        return executed;
    }

    static Free<DomainOps, Free<DomainOps, String>> readLineFrom(Free<DomainOps, BufferedReader> input) {
        Function<BufferedReader, Free<DomainOps, String>> readLine = named("readLine", InputOps::readLine); // IO
        return input.map(readLine);
    }

    static Free<DomainOps, Free<DomainOps, Free<DomainOps, Command>>> createCommand(
            Free<DomainOps, Free<DomainOps, String>> lineRead) {

        Function<String, Free<DomainOps, Command>> timedAndCreateCommand = //
                named("getTimedAndCreateCommand", line -> TimerOps.time(). // IO
                        map(named("create Command", time -> new Command(line, time))));

        Function<Free<DomainOps, String>, Free<DomainOps, Free<DomainOps, Command>>> createCommandOnInnerDsl = //
                named("execGetTimedAndCreateCommand", nested -> nested.map(timedAndCreateCommand));

        return lineRead.map(createCommandOnInnerDsl);
    }

    static Free<DomainOps, ?> executeCommand(Free<DomainOps, Free<DomainOps, Free<DomainOps, Command>>> command,
            Free<DomainOps, BufferedReader> input) {

        Function<Command, Free<DomainOps, ?>> runCommand = named("processCommand", c -> processCommand(input, c));

        Function<Free<DomainOps, Command>, Free<DomainOps, Free<DomainOps, ?>>> runCommandOnInnerDsl = //
                named("map processCommand", nested -> nested.map(runCommand));

        Function<Free<DomainOps, Free<DomainOps, Command>>, Free<DomainOps, Free<DomainOps, Free<DomainOps, ?>>>> runCommandOnInnerInnerDsl = // 
                named("map map processCommand", nested -> nested.map(runCommandOnInnerDsl));

        return command.map(runCommandOnInnerInnerDsl);
    }

    private static Free<DomainOps, ?> processCommand(Free<DomainOps, BufferedReader> input, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.pure(null);
        }

        Free<DomainOps, ?> result = Commands.handle(command);
        Free<DomainOps, ?> remaining = result.flatMap(named("recurse processInput", ignore -> processInput(input)));
        return remaining;
    }

}
