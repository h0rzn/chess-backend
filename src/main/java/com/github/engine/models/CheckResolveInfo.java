package com.github.engine.models;

public record CheckResolveInfo(
        boolean resolvable,
        long[] attack2Defend,
        long[] block2Defend
) {
}
