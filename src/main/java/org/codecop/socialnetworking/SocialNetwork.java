package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Free<DslCommand, ?> app = app();
        System.err.println(app);
        Interpret.it(app);
    }

    static Free<DslCommand, ?> app() {
        Free<DslCommand, BufferedReader> init = // 
                InMemoryOps.initDatabase(). // IO
                        flatMap(named("openInput", ignore -> InputOps.openInput())); // IO

        return processInput(init);
    }

    static Free<DslCommand, ?> processInput(Free<DslCommand, BufferedReader> inputCmd) {
        Function<BufferedReader, Free<DslCommand, String>> readLineF = named("readLine", InputOps::readLine);
        Free<DslCommand, Free<DslCommand, String>> readLineCmd = inputCmd.mapF(readLineF); // IO

        Function<String, Free<DslCommand, Command>> timeAndCreateCommandF = named("timeAndcreateCommand",
                line -> TimerOps.time(). // IO
                        mapF(named("createCommand", time -> new Command(line, time))));
        Function<Free<DslCommand, String>, Free<DslCommand, Free<DslCommand, Command>>> exec1 = named(
                "execTimeAndCreateCommand", uLineCmd -> uLineCmd.mapF(timeAndCreateCommandF));
        Free<DslCommand, Free<DslCommand, Free<DslCommand, Command>>> command = readLineCmd.mapF(exec1);

        Function<Command, Free<DslCommand, ?>> processCommandF = named("processCommand",
                commandCmd -> processCommand(inputCmd, commandCmd));

        Function<Free<DslCommand, Command>, Free<DslCommand, Free<DslCommand, ?>>> exec2 = named("map processCommand",
                uCommand -> uCommand.mapF(processCommandF));

        Free<DslCommand, ?> wtf = command.mapF(a -> a.mapF(exec2));
        return wtf;
    }

    private static Free<DslCommand, ?> processCommand(Free<DslCommand, BufferedReader> inputCmd, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.liftF(DslResult.nil());
        }

        Free<DslCommand, ?> result = Commands.handle(command);
        Free<DslCommand, ?> remaining = result //
                .flatMap(named("recurse processInput", ignore -> processInput(inputCmd)));

        return remaining;
    }

}
