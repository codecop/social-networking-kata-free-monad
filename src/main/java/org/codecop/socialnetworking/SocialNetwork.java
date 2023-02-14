package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Interpret.it(app());
    }

    static Unrestricted<DslCommand<Void>, Void> app() {
        return InMemoryOps.initDatabase(). // io
                flatMap(ignore -> InputOps.initInput()). // io
                flatMap(SocialNetwork::processInput);
    }

    static Unrestricted<DslCommand<Void>, Void> processInput(BufferedReader in) {
        Unrestricted<DslCommand<Command>, Command> command = //
            InputOps.readLine(in). // io
            flatMap(line -> TimerOps.time(). // io
                            map(time -> new Command(line, time)));
        
        return command.flatMap(c -> processCommand(in, c));
    }

    static Unrestricted<DslCommand<Void>, Void> processCommand(BufferedReader in, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Unrestricted.liftF(DslCommand.nil());
        }
        return Commands.handle(command). //
                flatMap(ignore -> processInput(in));
    }

}
