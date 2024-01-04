package com.github.model.debug;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class MoveDebugModel{
    private Integer id;
    private String move;
    private Integer promoteTo;
}
