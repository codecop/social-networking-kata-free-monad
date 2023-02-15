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
                flatMap(in -> SocialNetwork.processInput(in));
    }

    static Unrestricted<DslCommand<Void>> processInput(DslCommand<BufferedReader> inCmd) {
        DslCommand<Unrestricted<DslCommand<String>>> readLine = inCmd.map(InputOps::readLine); // io
        DslCommand<Unrestricted<DslCommand<Command>>> command = // 
                readLine.map(lineCmd -> //
                lineCmd.flatMap(line -> // 
                TimerOps.time(). // io
                        flatMap(tx -> foo(tx, line))));

        // return command.flatMap(c -> processCommand(inCmd, c));
    }
    
    static Unrestricted<DslCommand<Command>> foo(DslCommand<Long> t, DslCommand<String> line) {
        return Unrestricted.liftF(t.flatMap(t2 -> line.map(l -> new Command(l, t2))));
    }

    static Unrestricted<DslCommand<Void>> processCommand(BufferedReader in, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Unrestricted.liftF(DslCommand.nil());
        }
        return Commands.handle(command). //
                flatMap(ignore -> processInput(in));
    }

}
