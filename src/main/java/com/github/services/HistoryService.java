package com.github.services;

import com.github.engine.models.MoveInfo;
import com.github.entity.HistoryEntity;
import com.github.repository.HistoryRepository;
import com.github.repository.RedisGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public HistoryService(HistoryRepository redisHistoryRepository, RedisTemplate<String, String> redisTemplate) {
        this.historyRepository = redisHistoryRepository;
        this.redisTemplate = redisTemplate;
    }


    public void addArrayToList(String key, String[] array) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        for (String item : array) {
            listOps.rightPush(key, item);
        }
    }

    public void addAllArrayToList(String key, String[] array) {
        redisTemplate.opsForList().rightPushAll(key, array);
    }

    public HistoryEntity createHistory(String gameId) {
        HistoryEntity historyEntity = new HistoryEntity(gameId, new ArrayList<>());
        historyRepository.save(historyEntity);
        return historyEntity;
    }

    public Optional<HistoryEntity> getHistoryForGame(String gameId){
        return historyRepository.findById(gameId);
    }

    public HistoryEntity saveHistory(HistoryEntity history) {
        historyRepository.save(history);
        return history;
    }

    public HistoryEntity addMoveToHistory(String gameId, MoveInfo moveInfo) {
        HistoryEntity historyEntity = historyRepository.findById(gameId).orElse(null);
        if (historyEntity == null) {
            historyEntity = this.createHistory(gameId);
        }
        HashMap<Date, MoveInfo> moveMap = new HashMap<>();
        Date date = new Date();
        moveMap.put(date, moveInfo);
        historyEntity.getMoveHistory().add(moveMap);
        historyRepository.save(historyEntity);
        return historyEntity;
    }
}
