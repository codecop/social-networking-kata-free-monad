package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Unrestricted<DslCommand<Void>> app = app();
        System.err.println(app);
        Interpret.it(app);
    }

    static Unrestricted<DslCommand<Void>> app() {
        Unrestricted<DslCommand<BufferedReader>> init = // 
                InMemoryOps.initDatabase(). // IO
                flatMap(named("openInput", ignore -> InputOps.openInput())); // IO
        
        return processInput(init);
    }

    static Unrestricted<DslCommand<Void>> processInput(Unrestricted<DslCommand<BufferedReader>> inputCmd) {
        Function<BufferedReader, Unrestricted<DslCommand<String>>> f1 = named("readLine", InputOps::readLine);
        Unrestricted<DslCommand<Unrestricted<DslCommand<String>>>> readLineCmd = inputCmd.mapF(f1); // IO

        Function<DslCommand<String>, Unrestricted<DslCommand<Command>>> f2 = named("timeAndcreateCommand",
                lineCmd -> TimerOps.time(). // IO
                        flatMap(named("createCommand", timeCmd -> createCommand(timeCmd, lineCmd))));

        Function<Unrestricted<DslCommand<String>>, Unrestricted<DslCommand<Command>>> f3 = named(
                "flatmap timeAndcreateCommand", uLineCmd -> uLineCmd.flatMap(f2));
        Unrestricted<DslCommand<Unrestricted<DslCommand<Command>>>> command = readLineCmd.mapF(f3);

        Function<DslCommand<Command>, DslCommand<Unrestricted<DslCommand<Void>>>> f4 = named("processCommand",
                commandCmd -> processCommand(inputCmd, commandCmd));
        Function<Unrestricted<DslCommand<Command>>, Unrestricted<DslCommand<Unrestricted<DslCommand<Void>>>>> f5 = named(
                "map processCommand", uCommand -> uCommand.map(f4));
        Unrestricted wtf = command.mapF(f5);

        Unrestricted casts = wtf;
        return casts;
    }

    static Unrestricted<DslCommand<Command>> createCommand(DslCommand<Long> timeCmd, DslCommand<String> lineCmd) {
        DslCommand<Command> command = timeCmd.flatMap(time -> lineCmd.map(line -> new Command(line, time)));
        return Unrestricted.liftF(command);
    }

    static DslCommand<Unrestricted<DslCommand<Void>>> processCommand(Unrestricted<DslCommand<BufferedReader>> inputCmd,
            DslCommand<Command> commandCmd) {
        return commandCmd.map(command -> processCommand(inputCmd, command));
    }

    private static Unrestricted<DslCommand<Void>> processCommand(Unrestricted<DslCommand<BufferedReader>> inputCmd,
            Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Unrestricted.liftF(DslResult.nil());
        }

        Unrestricted<DslCommand<Void>> result = Commands.handle(command);
        Unrestricted<DslCommand<Void>> remaining = 
                result.flatMap(named("recurse processInput", ignore -> //
                        processInput(inputCmd)));
        
        return remaining;
    }

}
