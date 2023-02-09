package org.codecop.socialnetworking;

import java.io.IOException;
import java.util.Objects;

import org.codecop.socialnetworking.Free.FreeFlatMapper;
import org.codecop.socialnetworking.Free.FreeMapper;
import org.codecop.socialnetworking.Free.FreeValue;
import org.codecop.socialnetworking.FreeInMemory.FreeInitDatabase;
import org.codecop.socialnetworking.FreeInMemory.FreeQueryMessages;
import org.codecop.socialnetworking.FreeInMemory.FreeQueryWall;
import org.codecop.socialnetworking.FreeInMemory.FreeSaveFollowing;
import org.codecop.socialnetworking.FreeInMemory.FreeSaveMessages;
import org.codecop.socialnetworking.FreeInput.FreeInitStdIn;
import org.codecop.socialnetworking.FreeInput.FreeReadStdIn;
import org.codecop.socialnetworking.FreePrinter.FreePrintln;
import org.codecop.socialnetworking.FreeTimer.FreeTime;

/**
 * Evaluate the tree
 */
public abstract class Interpret {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object it(Free<?> free) throws IOException {
        Objects.requireNonNull(free);

        // InMemory
        if (free instanceof FreeInitDatabase) {
            InMemory.initDatabase();
            return null;
        }
        if (free instanceof FreeQueryMessages) {
            FreeQueryMessages f = (FreeQueryMessages) free;
            return InMemory.queryMessagesFor(f.user);
        }
        if (free instanceof FreeQueryWall) {
            FreeQueryWall f = (FreeQueryWall) free;
            return InMemory.queryWallUsersFor(f.user);
        }
        if (free instanceof FreeSaveFollowing) {
            FreeSaveFollowing f = (FreeSaveFollowing) free;
            InMemory.saveFollowingFor(f.user, f.other);
            return null;
        }
        if (free instanceof FreeSaveMessages) {
            FreeSaveMessages f = (FreeSaveMessages) free;
            InMemory.save(f.message);
            return null;
        }

        // Input
        if (free instanceof FreeInitStdIn) {
            return Input.initInput();
        }
        if (free instanceof FreeReadStdIn) {
            FreeReadStdIn f = (FreeReadStdIn) free;
            return Input.readLine(f.in);
        }

        // Print
        if (free instanceof FreePrintln) {
            FreePrintln f = (FreePrintln) free;
            Printer.println(f.text);
            return null;
        }

        // Timer
        if (free instanceof FreeTime) {
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
            Free<Object> current = f.mapper.apply(before);
            return it(current);
        }

        throw new IllegalArgumentException(free.getClass().getName());
    }

}
