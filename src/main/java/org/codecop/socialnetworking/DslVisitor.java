package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.util.Objects;

import org.codecop.socialnetworking.Free.FreeFlatMapped;
import org.codecop.socialnetworking.Free.FreeValue;
import org.codecop.socialnetworking.InMemoryOps.InitDatabase;
import org.codecop.socialnetworking.InMemoryOps.QueryMessages;
import org.codecop.socialnetworking.InMemoryOps.QueryWall;
import org.codecop.socialnetworking.InMemoryOps.SaveFollowing;
import org.codecop.socialnetworking.InMemoryOps.SaveMessages;
import org.codecop.socialnetworking.InputOps.OpenStdIn;
import org.codecop.socialnetworking.InputOps.ReadStdIn;
import org.codecop.socialnetworking.PrinterOps.Println;
import org.codecop.socialnetworking.TimerOps.GetTime;

public class DslVisitor {

    public Object handleFree(Free<DslCommand, ?> free) {
        Objects.requireNonNull(free);

        if (free instanceof FreeFlatMapped) {
            return handle((FreeFlatMapped<DslCommand, ?, DslCommand, ?>) free);
        }
        if (free instanceof FreeValue) {
            return handle((FreeValue<DslCommand, ?>) free);
        }

        throw new IllegalArgumentException(free.getClass().getName());
    }

    public Object handle(FreeFlatMapped<DslCommand, ?, DslCommand, ?> free) {
        Object previous = handleFree(free.previous);
        System.err.println("evaluating " + free.toString());
        Free<DslCommand, ?> current = free.mapper.apply(DslResult.of(previous));
        return handleFree(current);
    }

    public Object handle(FreeValue<DslCommand, ?> free) {
        System.err.println("evaluating " + free.toString());
        return handleCommand(free.transformable);
    }

    public Object handleCommand(DslCommand command) {
        // InMemory
        if (command instanceof InitDatabase) {
            return handle((InitDatabase) command);
        }
        if (command instanceof QueryMessages) {
            return handle((QueryMessages) command);
        }
        if (command instanceof QueryWall) {
            return handle((QueryWall) command);
        }
        if (command instanceof SaveFollowing) {
            return handle((SaveFollowing) command);
        }
        if (command instanceof SaveMessages) {
            return handle((SaveMessages) command);
        }

        // Input
        if (command instanceof OpenStdIn) {
            return handle((OpenStdIn) command);
        }
        if (command instanceof ReadStdIn) {
            return handle((ReadStdIn) command);
        }

        // Print
        if (command instanceof Println) {
            return handle((Println) command);
        }

        // Timer
        if (command instanceof GetTime) {
            return handle((GetTime) command);
        }

        if (command instanceof DslResult) {
            return handle((DslResult) command);
        }

        throw new IllegalArgumentException(command.getClass().getName());
    }

    private boolean initDatabase = false;

    public Void handle(@SuppressWarnings("unused") InitDatabase f) {
        // PROBLEM: This is a side effect. I must only run it once.
        if (!initDatabase) {
            initDatabase = true;
            InMemory.initDatabase();
        }
        return null;
    }

    public Messages handle(QueryMessages command) {
        return InMemory.queryMessagesFor(command.user);
    }

    public WallUsers handle(QueryWall command) {
        return InMemory.queryWallUsersFor(command.user);
    }

    public Void handle(SaveFollowing command) {
        InMemory.saveFollowingFor(command.user, command.other);
        return null;
    }

    public Void handle(SaveMessages command) {
        InMemory.save(command.message);
        return null;
    }

    private BufferedReader firstOpenStdIn;

    public BufferedReader handle(@SuppressWarnings("unused") OpenStdIn command) {
        // PROBLEM: This is not transparent, but a side effect. I must return the same all times.
        if (firstOpenStdIn == null) {
            firstOpenStdIn = Input.initInput();
        }
        return firstOpenStdIn;
    }

    public String handle(ReadStdIn command) {
        return Ex.uncheckIoException(() -> Input.readLine(command.in));
    }

    public Void handle(Println command) {
        Printer.println(command.text);
        return null;
    }

    public Long handle(@SuppressWarnings("unused") GetTime command) {
        return Timer.time();
    }

    public /*<T>*/ Object handle(DslResult command) {
        Object value = command.value;
        if (value instanceof Free) {
            System.err.print("nested ...");
            Object result = handleFree((Free<DslCommand, ?>) value);
            return Free.liftF(DslResult.of(result));
        }
        return value;
    }

}
