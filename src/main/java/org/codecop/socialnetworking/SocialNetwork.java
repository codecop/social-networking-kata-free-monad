package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) {
        Interpret.it(app());
    }

    static Unrestricted<DslCommand<Void>> app() {
        return InMemoryOps.initDatabase(). // io
                flatMap(ignore -> InputOps.initInput()). // io
                flatMap(inputCmd -> SocialNetwork.processInput(inputCmd));
    }

    static DslCommand<Unrestricted<DslCommand<Void>>> processInput(DslCommand<BufferedReader> inputCmd) {
        DslCommand<Unrestricted<DslCommand<String>>> readLineCmd = inputCmd.map(InputOps::readLine); // IO
        DslCommand<Unrestricted<DslCommand<Command>>> command = // 
                readLineCmd.map(uLineCmd -> //
                    uLineCmd.flatMap(lineCmd -> // 
                        TimerOps.time(). // IO
                        flatMap(timeCmd -> createCommand(timeCmd, lineCmd))));
        return command.map(uCommand -> //
            uCommand.flatMap(commandCmd -> 
                processCommand(inputCmd, commandCmd)));
    }
    
    static Unrestricted<DslCommand<Command>> createCommand(DslCommand<Long> timeCmd, DslCommand<String> lineCmd) {
        DslCommand<Command> command = timeCmd.flatMap(time -> lineCmd.map(line -> new Command(line, time)));
        return Unrestricted.liftF(command);
    }

    static DslCommand<Unrestricted<DslCommand<Void>>> processCommand(DslCommand<BufferedReader> inputCmd, DslCommand<Command> commandCmd) {
        return commandCmd.map(command -> {
            if ("quit".equalsIgnoreCase(command.line)) {
                return Unrestricted.liftF(DslCommand.nil());
            }
            return x(inputCmd, command);
        });
    }

    private static DslCommand<Unrestricted<Object>> x(DslCommand<BufferedReader> inputCmd, Command command) {
        return Commands.handle(command). //
                flatMap(ignore -> processInput(inputCmd));
    }

}
