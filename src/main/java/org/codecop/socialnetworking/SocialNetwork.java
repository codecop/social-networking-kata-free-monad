package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Unrestricted<DslCommand<Void>> app = app();
        System.err.println(app);
        Interpret.it(app);
    }

    static Unrestricted<DslCommand<Void>> app() {
        return InMemoryOps.initDatabase(). // io
                flatMap(ignore -> InputOps.openInput()). // io
                flatMap(inputCmd -> SocialNetwork.processInput(inputCmd));
    }

    static Unrestricted<DslCommand<Void>> processInput(DslCommand<BufferedReader> inputCmd) {
        DslCommand<Unrestricted<DslCommand<String>>> readLineCmd = inputCmd.map(InputOps::readLine); // IO
        DslCommand<Unrestricted<DslCommand<Command>>> command = // 
                readLineCmd.map(uLineCmd -> //
                    uLineCmd.flatMap(lineCmd -> // 
                        TimerOps.time(). // IO
                        flatMap(timeCmd -> createCommand(timeCmd, lineCmd))));
        DslCommand<Unrestricted<DslCommand<Unrestricted<DslCommand<Void>>>>> wtf = //
                command.map(uCommand -> //
                    uCommand.map(commandCmd -> 
                        processCommand(inputCmd, commandCmd)));

        Unrestricted casts = Unrestricted.liftF(wtf);
        return casts;
    }
    
    static Unrestricted<DslCommand<Command>> createCommand(DslCommand<Long> timeCmd, DslCommand<String> lineCmd) {
        DslCommand<Command> command = timeCmd.flatMap(time -> lineCmd.map(line -> new Command(line, time)));
        return Unrestricted.liftF(command);
    }

    static DslCommand<Unrestricted<DslCommand<Void>>> processCommand(DslCommand<BufferedReader> inputCmd, DslCommand<Command> commandCmd) {
        return commandCmd.map(command -> processCommand(inputCmd, command));
    }

    private static Unrestricted<DslCommand<Void>> processCommand(DslCommand<BufferedReader> inputCmd, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Unrestricted.liftF(DslResult.nil());
        }
        Unrestricted<DslCommand<Void>> result = Commands.handle(command);
        Unrestricted<DslCommand<Void>> remaining = 
                result.flatMap(ignore -> //
                        processInput(inputCmd));
        
        return remaining;
    }

}
