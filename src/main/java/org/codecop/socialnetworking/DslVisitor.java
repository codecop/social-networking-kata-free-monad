package org.codecop.socialnetworking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.codecop.socialnetworking.InMemoryOps.InitDatabase;
import org.codecop.socialnetworking.InMemoryOps.QueryMessages;
import org.codecop.socialnetworking.InMemoryOps.QueryWall;
import org.codecop.socialnetworking.InMemoryOps.SaveFollowing;
import org.codecop.socialnetworking.InMemoryOps.SaveMessages;
import org.codecop.socialnetworking.InputOps.OpenStdIn;
import org.codecop.socialnetworking.InputOps.ReadStdIn;
import org.codecop.socialnetworking.PrinterOps.Println;
import org.codecop.socialnetworking.TimerOps.GetTime;
import org.codecop.socialnetworking.Unrestricted.UnrestrictedNode;

public class DslVisitor {

    public Object matchCommand(Unrestricted<?> u) {
        if (u instanceof UnrestrictedNode<?, ?>) {
            return handle((UnrestrictedNode) u);
        }

        return handle(u);
    }

    public Object handle(UnrestrictedNode u) {
        Object x = matchCommand(u.previous);
        System.err.println("evaluating " + u.toString());
        Unrestricted<DslCommand<?>> current = (Unrestricted<DslCommand<?>>) u.mapper.apply(DslResult.of(x));
        return matchCommand(current);
    }

    public Object handle(Unrestricted<?> u) {
        // TODO breaking encapsulation
        System.err.println("evaluating " + u.toString());
        return matchCommand((DslCommand<?>) u.transformable);
    }

    public Object matchCommand(DslCommand<?> dslCommand) {
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
        if (dslCommand instanceof OpenStdIn) {
            return handle((OpenStdIn) dslCommand);
        }
        if (dslCommand instanceof ReadStdIn) {
            return handle((ReadStdIn) dslCommand);
        }

        // Print
        if (dslCommand instanceof Println) {
            return handle((Println) dslCommand);
        }

        // Timer
        if (dslCommand instanceof GetTime) {
            return handle((GetTime) dslCommand);
        }

        // ---

        if (dslCommand instanceof DslResult) {
            return handle((DslResult<?>) dslCommand);
        }

        throw new IllegalArgumentException(dslCommand.getClass().getName());
    }

    private boolean init = false;

    public Void handle(@SuppressWarnings("unused") InitDatabase f) {
        // PROBLEM: This is a side effect. I must only run it once.
        if (!init) {
            init = true;
            InMemory.initDatabase();
        }
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

    private BufferedReader first;

    public BufferedReader handle(@SuppressWarnings("unused") OpenStdIn f) {
        // PROBLEM: This is not transparent, but a side effect. I must return the same all times.
        if (first == null) {
            first = Input.initInput();
        }
        return first;
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

    public Long handle(@SuppressWarnings("unused") GetTime f) {
        return Timer.time();
    }

    public <T> T handle(DslResult<T> f) {
        T value = f.value;
        if (value instanceof Unrestricted<?>) {
            System.err.print("nested ...");
            T result = (T) matchCommand((Unrestricted<?>) value);
            // System.err.println("XXX " + result + " XXX");
            return (T) Unrestricted.liftF(DslResult.of(result));
        }
        return value;
    }

}
