package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Free<DslCommand, ?> app = app();

        System.err.println(app);
        Ex.rethrowIoException(() -> new DslVisitor().matchCommand(app));
    }

    static Free<DslCommand, ?> app() {
        Free<DslCommand, BufferedReader> init = initIO();
        return processInput(init);
    }

    static Free<DslCommand, BufferedReader> initIO() {
        return InMemoryOps.initDatabase(). // IO
                flatMap(named("openInput", ignore -> InputOps.openInput())); // IO
    }

    static Free<DslCommand, ?> processInput(Free<DslCommand, BufferedReader> input) {
        Free<DslCommand, Free<DslCommand, String>> lineRead = readLineFrom(input);
        Free<DslCommand, Free<DslCommand, Free<DslCommand, Command>>> command = createCommand(lineRead);
        Free<DslCommand, ?> executed = executeCommand(command, input);
        return executed;
    }

    static Free<DslCommand, Free<DslCommand, String>> readLineFrom(Free<DslCommand, BufferedReader> input) {
        Function<BufferedReader, Free<DslCommand, String>> readLine = named("readLine", InputOps::readLine); // IO
        return input.mapF(readLine);
    }

    static Free<DslCommand, Free<DslCommand, Free<DslCommand, Command>>> createCommand(
            Free<DslCommand, Free<DslCommand, String>> lineRead) {

        Function<String, Free<DslCommand, Command>> timedAndCreateCommand = //
                named("getTimedAndCreateCommand", line -> TimerOps.time(). // IO
                        mapF(named("create Command", time -> new Command(line, time))));

        Function<Free<DslCommand, String>, Free<DslCommand, Free<DslCommand, Command>>> createCommandOnInnerDsl = //
                named("execGetTimedAndCreateCommand", nested -> nested.mapF(timedAndCreateCommand));

        return lineRead.mapF(createCommandOnInnerDsl);
    }

    static Free<DslCommand, ?> executeCommand(Free<DslCommand, Free<DslCommand, Free<DslCommand, Command>>> command,
            Free<DslCommand, BufferedReader> input) {

        Function<Command, Free<DslCommand, ?>> runCommand = named("processCommand", c -> processCommand(input, c));

        Function<Free<DslCommand, Command>, Free<DslCommand, Free<DslCommand, ?>>> runCommandOnInnerDsl = //
                named("map processCommand", nested -> nested.mapF(runCommand));

        Function<Free<DslCommand, Free<DslCommand, Command>>, Free<DslCommand, Free<DslCommand, Free<DslCommand, ?>>>> runCommandOnInnerInnerDsl = // 
                named("map map processCommand", nested -> nested.mapF(runCommandOnInnerDsl));

        return command.mapF(runCommandOnInnerInnerDsl);
    }

    private static Free<DslCommand, ?> processCommand(Free<DslCommand, BufferedReader> input, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.liftF(DslResult.nil());
        }

        Free<DslCommand, ?> result = Commands.handle(command);
        Free<DslCommand, ?> remaining = result.flatMap(named("recurse processInput", ignore -> processInput(input)));
        return remaining;
    }

}
