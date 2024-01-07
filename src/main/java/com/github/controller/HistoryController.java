package com.github.controller;

import com.github.entity.GameEntity;
import com.github.entity.HistoryEntity;
import com.github.model.GameModel;
import com.github.services.GameService;
import com.github.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class HistoryController {

    private HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<HistoryEntity> getHistoryByGameId(@PathVariable("id") String id){
        Optional<HistoryEntity> history = historyService.getHistoryForGame(id);
        return history.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
