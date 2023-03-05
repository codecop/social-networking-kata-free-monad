package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.util.Objects;

import org.codecop.socialnetworking.Free.FreeFlatMap;
import org.codecop.socialnetworking.Free.FreePure;
import org.codecop.socialnetworking.Free.FreeSuspend;
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

    public Object foldMap(Free<DomainOps, ?> free) {
        Objects.requireNonNull(free);

        if (free instanceof FreeFlatMap) {
            return handle((FreeFlatMap<DomainOps, ?, DomainOps, ?>) free);
        }
        if (free instanceof FreeSuspend) {
            return handle((FreeSuspend<DomainOps, ?>) free);
        }
        if (free instanceof FreePure) {
            return handle((FreePure<DomainOps, ?>) free);
        }

        throw new IllegalArgumentException(free.getClass().getName());
    }

    public Object handle(FreeFlatMap free) {
        Object previous = foldMap(free.previous);
        System.err.println("evaluating " + free.toString());
        Free<DomainOps, ?> current = (Free<DomainOps, ?>) free.mapper.apply(previous);
        return foldMap(current);
    }

    public Object handle(FreeSuspend<DomainOps, ?> free) {
        System.err.println("evaluating " + free.toString());
        return foldMap(free.ops);
    }

    public Object handle(FreePure<DomainOps, ?> free) {
        System.err.println("evaluating " + free.toString());
        return free.value;
    }

    public Object foldMap(DomainOps command) {
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

//    public /*<T>*/ Object handle(DslResult command) {
//        Object value = command.value;
//        if (value instanceof Free) {
//            System.err.print("nested ...");
//            Object result = foldMap((Free<DomainOps, ?>) value);
//            return Free.liftM(DslResult.of(result));
//        }
//        return value;
//    }

}
