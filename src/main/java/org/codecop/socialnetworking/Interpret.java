package org.codecop.socialnetworking;

import java.util.Objects;

/**
 * Evaluate the tree
 */
public class Interpret {

    public static Object it(Unrestricted<DslCommand<Void>> uCommand) {
        Objects.requireNonNull(uCommand);
        DslVisitor v = new DslVisitor();
        return uCommand.run(v);
    }

}
