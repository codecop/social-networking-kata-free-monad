package org.codecop.socialnetworking;

import java.io.IOException;
import java.util.Objects;

import org.codecop.socialnetworking.DslCommand.FreeFlatMapper;
import org.codecop.socialnetworking.DslCommand.FreeMapper;
import org.codecop.socialnetworking.DslCommand.FreeValue;
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object it(DslCommand<?> free) throws IOException {
        Objects.requireNonNull(free);

        // InMemory
        if (free instanceof InitDatabase) {
            InMemory.initDatabase();
            return null;
        }
        if (free instanceof QueryMessages) {
            QueryMessages f = (QueryMessages) free;
            return InMemory.queryMessagesFor(f.user);
        }
        if (free instanceof QueryWall) {
            QueryWall f = (QueryWall) free;
            return InMemory.queryWallUsersFor(f.user);
        }
        if (free instanceof SaveFollowing) {
            SaveFollowing f = (SaveFollowing) free;
            InMemory.saveFollowingFor(f.user, f.other);
            return null;
        }
        if (free instanceof SaveMessages) {
            SaveMessages f = (SaveMessages) free;
            InMemory.save(f.message);
            return null;
        }

        // Input
        if (free instanceof InitStdIn) {
            return Input.initInput();
        }
        if (free instanceof ReadStdIn) {
            ReadStdIn f = (ReadStdIn) free;
            return Input.readLine(f.in);
        }

        // Print
        if (free instanceof Println) {
            Println f = (Println) free;
            Printer.println(f.text);
            return null;
        }

        // Timer
        if (free instanceof Time) {
            return Timer.time();
        }

        // ---

        if (free instanceof FreeValue) {
            FreeValue<?> f = (FreeValue<?>) free;
            return f.value;
        }

        if (free instanceof FreeMapper) {
            FreeMapper<Object, Object> f = (FreeMapper) free;
            Object before = it(f.before);
            return f.mapper.apply(before);
        }
        if (free instanceof FreeFlatMapper) {
            FreeFlatMapper<Object, Object> f = (FreeFlatMapper) free;
            Object before = it(f.before);
            DslCommand<Object> current = f.mapper.apply(before);
            return it(current);
        }

        throw new IllegalArgumentException(free.getClass().getName());
    }

}
