package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.io.BufferedReader;
import java.io.IOException;

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
        
        // return init.flatMap(named(inputCmd -> SocialNetwork.processInput(inputCmd), "processInput"));
        return processInput(init);
    }

    static Unrestricted<DslCommand<Void>> processInput(Unrestricted<DslCommand<BufferedReader>> inputCmd) {
        Unrestricted<DslCommand<Unrestricted<DslCommand<String>>>> readLineCmd = 
                inputCmd.mapF(named("readLine", InputOps::readLine)); // IO

        Unrestricted casts = readLineCmd;
        return casts;
        
//        DslCommand<Unrestricted<DslCommand<Command>>> command = // 
//                readLineCmd.map(uLineCmd -> //
//                    uLineCmd.flatMap(lineCmd -> // 
//                        TimerOps.time(). // IO
//                        flatMap(timeCmd -> createCommand(timeCmd, lineCmd))));
//        DslCommand<Unrestricted<DslCommand<Unrestricted<DslCommand<Void>>>>> wtf = //
//                command.map(uCommand -> //
//                    uCommand.map(commandCmd -> 
//                        processCommand(inputCmd, commandCmd)));
//
//        Unrestricted casts = Unrestricted.liftF(wtf);
//        return casts;
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
//        Unrestricted<DslCommand<Void>> result = Commands.handle(command);
//        Unrestricted<DslCommand<Void>> remaining = 
//                result.flatMap(ignore -> //
//                        processInput(inputCmd));
//        
//        return remaining;
        return null;
    }

}
