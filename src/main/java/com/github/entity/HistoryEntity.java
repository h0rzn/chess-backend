package com.github.entity;

import com.github.engine.models.MoveInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RedisHash("Game")
@AllArgsConstructor
public class HistoryEntity {

    @Getter
    @Id
    private String gameID;

   @Getter
   private ArrayList<HashMap<Date, MoveInfo>> moveHistory;


}
