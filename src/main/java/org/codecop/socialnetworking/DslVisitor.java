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

public class DslVisitor {

    public Void handle(@SuppressWarnings("unused") InitDatabase f) {
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

    public BufferedReader handle(@SuppressWarnings("unused") OpenStdIn f) {
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

    public Long handle(@SuppressWarnings("unused") GetTime f) {
        return Timer.time();
    }

    public <T> T handle(DslResult<T> f) {
        return f.value;
    }

}
