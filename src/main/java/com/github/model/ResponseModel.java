package com.github.model;

import com.github.engine.models.MoveInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract model for responses
 */
@AllArgsConstructor
@Getter
@Setter
public abstract class ResponseModel {
    private Integer id;
}
