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
public class Interpret {

    public static Object it(Unrestricted<DslCommand<Void>> uCommand) {
        Objects.requireNonNull(uCommand);
        Interpret i = new Interpret();
        return i.matchCommand(uCommand.transformable);
        // TODO return uCommand.map(i::matchCommand);
    }

    public static DslCommand evalCommand(DslCommand dslCommand) {
        Interpret i = new Interpret();
        return DslResult.of(i.matchCommand(dslCommand));
    }

    private Object matchCommand(DslCommand dslCommand) {
        // InMemory
        if (dslCommand instanceof InitDatabase) {
            return handle((InitDatabase) dslCommand);
        }
        if (dslCommand instanceof QueryMessages) {
            return handle((QueryMessages) dslCommand);
        }
        if (dslCommand instanceof QueryWall) {
            return handle((QueryWall) dslCommand);
        }
        if (dslCommand instanceof SaveFollowing) {
            return handle((SaveFollowing) dslCommand);
        }
        if (dslCommand instanceof SaveMessages) {
            return handle((SaveMessages) dslCommand);
        }

        // Input
        if (dslCommand instanceof InitStdIn) {
            return handle((InitStdIn) dslCommand);
        }
        if (dslCommand instanceof ReadStdIn) {
            return handle((ReadStdIn) dslCommand);
        }

        // Print
        if (dslCommand instanceof Println) {
            return handle((Println) dslCommand);
        }

        // Timer
        if (dslCommand instanceof Time) {
            return handle((Time) dslCommand);
        }

        // ---

        if (dslCommand instanceof DslResult) {
            return handle((DslResult) dslCommand);
        }

        throw new IllegalArgumentException(dslCommand.getClass().getName());
    }

    public Void handle(InitDatabase f) {
        InMemory.initDatabase();
        return null;
    }

    public Messages handle(QueryMessages f) {
        return InMemory.queryMessagesFor(f.user);
    }

    public WallUsers handle(QueryWall f) {
        return InMemory.queryWallUsersFor(f.user);
    }

    public Void handle(SaveFollowing f) {
        InMemory.saveFollowingFor(f.user, f.other);
        return null;
    }

    public Void handle(SaveMessages f) {
        InMemory.save(f.message);
        return null;
    }

    public BufferedReader handle(InitStdIn f) {
        return Input.initInput();
    }

    public String handle(ReadStdIn f) {
        try {
            return Input.readLine(f.in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Void handle(Println f) {
        Printer.println(f.text);
        return null;
    }

    public Long handle(Time f) {
        return Timer.time();
    }

    public Object handle(DslResult f) {
        return f.value;
    }

}
