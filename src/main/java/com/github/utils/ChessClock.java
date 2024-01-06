package com.github.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.UUID;

@Getter
@Setter
public class ChessClock {
    private long whiteTimeLeft;
    private long blackTimeLeft;
    private long lastUpdateTime;
    private Integer activePlayer = 0;

    public ChessClock(){

    }
    public ChessClock(long startTime){
        this.whiteTimeLeft = startTime;
        this.blackTimeLeft = startTime;
    }

    public void startClock(Integer startColor){
        this.activePlayer = startColor;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void setActivePlayer(Integer activePlayer) {
        // Aktualisieren Sie die Zeit des vorherigen Spielers, bevor Sie den aktiven Spieler wechseln
        updateClock();
        this.activePlayer = activePlayer;
        this.lastUpdateTime = System.currentTimeMillis(); // Setzen Sie die letzte Aktualisierungszeit zurück
    }

    public void updateClock() {
        long currentTime = System.currentTimeMillis();
        if (this.activePlayer == 0) {
            whiteTimeLeft -= currentTime - lastUpdateTime;
        } else if (this.activePlayer == 1) {
            blackTimeLeft -= currentTime - lastUpdateTime;
        }
        lastUpdateTime = currentTime;
    }

    public boolean isTimeOut(Integer color) {
        if (color == 0) {
            return whiteTimeLeft <= 0;
        } else {
            return blackTimeLeft <= 0;
        }
    }
}
