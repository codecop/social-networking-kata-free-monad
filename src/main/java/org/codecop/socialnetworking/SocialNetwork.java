package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;

public class SocialNetwork {

    public static void main(String[] args) throws IOException {
        Interpret.it(app());
    }

    static AstNode<Void> app() {
        return FreeInMemory.initDatabase(). // io
                flatMap(ignore -> FreeInput.initInput()). // io
                flatMap(SocialNetwork::processInput);
    }

    static AstNode<Void> processInput(BufferedReader in) {
        AstNode<Command> command = //
            FreeInput.readLine(in). // io
            flatMap(line -> FreeTimer.time(). // io
                            map(time -> new Command(line, time)));
        
        return command.flatMap(c -> processCommand(in, c));
    }

    static AstNode<Void> processCommand(BufferedReader in, Command command) {
        if ("quit".equalsIgnoreCase(command.line)) {
            return AstNode.nil();
        }
        return Commands.handle(command). //
                flatMap(ignore -> processInput(in));
    }

}
