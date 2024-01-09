package com.github.model.debug;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class DebugMoveModel {
    private Integer id;
    private String move;
    private Integer promoteTo;
}
