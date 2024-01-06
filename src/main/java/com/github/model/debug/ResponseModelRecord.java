package com.github.model.debug;

import com.github.engine.models.MoveInfo;

public record ResponseModelRecord(MoveInfo moveInfo, long whiteTimeLeft, long blackTimeLeft) {
}
