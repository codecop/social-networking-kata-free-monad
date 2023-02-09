package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Interpret.it(app());
    }

    static Free<Void> app() {
        return FreeInMemory.initDatabase(). // io
                flatMap(ignore -> FreeInput.initInput()). // io
                flatMap(SocialNetwork::processInput);
    }

    static Free<Void> processInput(BufferedReader in) {
        Free<Command> command = //
            FreeInput.readLine(in). // io
            flatMap(line -> FreeTimer.time(). // io
                            map(time -> new Command(line, time)));
        
        return command.flatMap(c -> processCommand(in, c));
    }

    static Free<Void> processCommand(BufferedReader in, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return Free.nil();
        }
        return Commands.handle(command). //
                flatMap(ignore -> processInput(in));
    }

}
