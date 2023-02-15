package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.codecop.socialnetworking.InMemoryOps.InitDatabase;
import org.codecop.socialnetworking.InMemoryOps.QueryMessages;
import org.codecop.socialnetworking.InMemoryOps.QueryWall;
import org.codecop.socialnetworking.InMemoryOps.SaveFollowing;
import org.codecop.socialnetworking.InMemoryOps.SaveMessages;
import org.codecop.socialnetworking.InputOps.InitStdIn;
import org.codecop.socialnetworking.InputOps.ReadStdIn;
import org.codecop.socialnetworking.PrinterOps.Println;
import org.codecop.socialnetworking.TimerOps.Time;

/**
 * Evaluate the tree
 */
public abstract class Interpret {

    public static Object it(Unrestricted<DslCommand<Void>> uCommand) {
        Objects.requireNonNull(uCommand);
        return uCommand.map(Interpret::matchCommand);
    }

    private static Object matchCommand(DslCommand dslCommand) {
        // InMemory
        if (dslCommand instanceof InitDatabase) {
            return handleInitDb((InitDatabase) dslCommand);
        }
        if (dslCommand instanceof QueryMessages) {
            return handleQueryMessages((QueryMessages) dslCommand);
        }
        if (dslCommand instanceof QueryWall) {
            return handleQueryWall((QueryWall) dslCommand);
        }
        if (dslCommand instanceof SaveFollowing) {
            return handleSaveFollowing((SaveFollowing) dslCommand);
        }
        if (dslCommand instanceof SaveMessages) {
            return handleSaveMessages((SaveMessages) dslCommand);
        }

        // Input
        if (dslCommand instanceof InitStdIn) {
            return handleInitInput((InitStdIn) dslCommand);
        }
        if (dslCommand instanceof ReadStdIn) {
            return handleReadLine((ReadStdIn) dslCommand);
        }

        // Print
        if (dslCommand instanceof Println) {
            return handlePrint((Println) dslCommand);
        }

        // Timer
        if (dslCommand instanceof Time) {
            return handleTime((Time) dslCommand);
        }

        // ---

        if (dslCommand instanceof DslResult) {
            return handleResult((DslResult) dslCommand);
        }

        throw new IllegalArgumentException(dslCommand.getClass().getName());
    }

    public static Void handleInitDb(InitDatabase f) {
        InMemory.initDatabase();
        return null;
    }

    public static Messages handleQueryMessages(QueryMessages f) {
        return InMemory.queryMessagesFor(f.user);
    }

    public static WallUsers handleQueryWall(QueryWall f) {
        return InMemory.queryWallUsersFor(f.user);
    }

    public static Void handleSaveFollowing(SaveFollowing f) {
        InMemory.saveFollowingFor(f.user, f.other);
        return null;
    }

    public static Void handleSaveMessages(SaveMessages f) {
        InMemory.save(f.message);
        return null;
    }

    public static BufferedReader handleInitInput(InitStdIn f) {
        return Input.initInput();
    }

    public static String handleReadLine(ReadStdIn f) {
        try {
            return Input.readLine(f.in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Void handlePrint(Println f) {
        Printer.println(f.text);
        return null;
    }

    public static Long handleTime(Time f) {
        return Timer.time();
    }

    public static Object handleResult(DslResult f) {
        return f.value;
    }

}
