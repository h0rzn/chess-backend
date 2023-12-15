package com.github.engine.models;

// CheckResolveInfo represents the result
// of a check resolve procedure
public record CheckResolveInfo(
        // indicates if a given check situation
        // is resolvable or not
        boolean resolvable,
        // stores attack destinations (pieces that threaten
        // the players king) of a piece set
        long[] attack2Defend,
        // stores squares that are reachable for a group of
        // pieces that can block a check route
        long[] block2Defend
) {
}
