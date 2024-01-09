package com.github.model;

import com.github.engine.models.MoveInfo;

/**
 * Wrapper for game response
 */
public record ResponseModelRecord(MoveInfo moveInfo, long whiteTimeLeft, long blackTimeLeft) {
}
