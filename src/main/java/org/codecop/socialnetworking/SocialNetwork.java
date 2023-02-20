package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Free<DslCommand, Void> app = app();
        System.err.println(app);
        Interpret.it(app);
    }

    static Free<DslCommand, Void> app() {
        Free<DslCommand, BufferedReader> init = // 
                InMemoryOps.initDatabase(). // IO
                flatMap(named("openInput", ignore -> InputOps.openInput())); // IO
        
        return processInput(init);
    }

    static Free<DslCommand, Void> processInput(Free<DslCommand, BufferedReader> inputCmd) {
        Function<BufferedReader, Free<DslCommand, String>> f1 = named("readLine", InputOps::readLine);
        Free<DslCommand, Free<DslCommand, String>> readLineCmd = inputCmd.mapF(f1); // IO

        Function<DslCommand, String>, Free<DslCommand<Command>>> f2 = named("timeAndcreateCommand",
                lineCmd -> TimerOps.time(). // IO
                        flatMap(named("createCommand", timeCmd -> createCommand(timeCmd, lineCmd))));

        Function<Free<DslCommand<String>>, Free<DslCommand<Command>>> f3 = named(
                "flatmap timeAndcreateCommand", uLineCmd -> uLineCmd.flatMap(f2));
        Free<DslCommand<Free<DslCommand<Command>>>> command = readLineCmd.mapF(f3);

        Function<DslCommand<Command>, DslCommand<Free<DslCommand, Void>>> f4 = named("processCommand",
                commandCmd -> processCommand(inputCmd, commandCmd));
        Function<Free<DslCommand<Command>>, Free<DslCommand<Free<DslCommand, Void>>>> f5 = named(
                "map processCommand", uCommand -> uCommand.map(f4));
        Free wtf = command.mapF(f5);

        Free casts = wtf;
        return casts;
    }

    static Free<DslCommand, Command> createCommand(DslCommand<Long> timeCmd, DslCommand<String> lineCmd) {
        DslCommand<Command> command = timeCmd.flatMap(time -> lineCmd.map(line -> new Command(line, time)));
        return Free.liftF(command);
    }

    static DslCommand<Free<DslCommand, Void>> processCommand(Free<DslCommand, BufferedReader> inputCmd,
            DslCommand<Command> commandCmd) {
        return commandCmd.map(command -> processCommand(inputCmd, command));
    }

    private static Free<DslCommand, ?> processCommand(Free<DslCommand, BufferedReader> inputCmd,
            Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.liftF(DslResult.nil());
        }

        Free<DslCommand, ?> result = Commands.handle(command);
        Free<DslCommand, ?> remaining = 
                result.flatMap(named("recurse processInput", ignore -> //
                        processInput(inputCmd)));
        
        return remaining;
    }

}
