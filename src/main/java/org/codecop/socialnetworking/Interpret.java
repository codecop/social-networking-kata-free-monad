package org.codecop.socialnetworking;

import java.util.Objects;

/**
 * Evaluate the tree
 */
public class Interpret {

    public static Object it(Unrestricted<DslCommand<Void>> uCommand) {
        Objects.requireNonNull(uCommand);
        DslVisitor v = new DslVisitor();
        return v.matchCommand(uCommand.transformable);
        // TODO return uCommand.map(i::matchCommand);
    }

    public static DslCommand<?> evalCommand(DslCommand<?> dslCommand) {
        DslVisitor v = new DslVisitor();
        return DslResult.of(v.matchCommand(dslCommand));
    }

}
