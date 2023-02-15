package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.codecop.socialnetworking.DslCommand.DslCommandMapper;
import org.codecop.socialnetworking.DslCommand.DslCommandValue;
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

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object it(Unrestricted<DslCommand<?>> free) {
        Objects.requireNonNull(free);
        return free.map(dslCommand -> {
            matchCommand((DslCommand<?>)dslCommand);
        });

        //        if (dslCommand instanceof FreeFlatMapper) {
        //            FreeFlatMapper<Object, Object> f = (FreeFlatMapper) dslCommand;
        //            Object before = it(f.before);
        //            DslCommand<Object> current = f.mapper.apply(before);
        //            return matchCommand(current);
        //        }

    }

    private static Object matchCommand(DslCommand<?> dslCommand) {
        // InMemory
        if (dslCommand instanceof InitDatabase) {
            return handleInitDb();
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
            return handleInitInput();
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
            return handleTime();
        }

        // ---

        if (dslCommand instanceof DslCommandValue) {
            return handleValue((DslCommandValue<?>) dslCommand);
        }

        throw new IllegalArgumentException(dslCommand.getClass().getName());
    }

    private static Void handleInitDb() {
        InMemory.initDatabase();
        return null;
    }

    private static Messages handleQueryMessages(QueryMessages f) {
        return InMemory.queryMessagesFor(f.user);
    }

    private static WallUsers handleQueryWall(QueryWall f) {
        return InMemory.queryWallUsersFor(f.user);
    }

    private static Void handleSaveFollowing(SaveFollowing f) {
        InMemory.saveFollowingFor(f.user, f.other);
        return null;
    }

    private static Void handleSaveMessages(SaveMessages f) {
        InMemory.save(f.message);
        return null;
    }

    private static BufferedReader handleInitInput() {
        return Input.initInput();
    }

    private static String handleReadLine(ReadStdIn f) {
        try {
            return Input.readLine(f.in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Void handlePrint(Println f) {
        Printer.println(f.text);
        return null;
    }

    private static Long handleTime() {
        return Timer.time();
    }

    private static <T> T handleValue(DslCommandValue<T> f) {
        return f.value;
    }

}
