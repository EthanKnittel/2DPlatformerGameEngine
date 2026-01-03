package com.EthanKnittel.score;

import com.EthanKnittel.Evolving;

public class ScoreManager implements Evolving {
    public static ScoreManager instance;

    private int score;
    private float timeSurvived;

    private int pointsPerSecond = 0; // nombre de points gagné par secondes par défaut
    private float scoreAccumulator= 0f;

    public ScoreManager(){
        this.score = 0;
        this.timeSurvived = 0;
        instance = this;
    }

    public void addScore(int score){
        if (score > 0) {
            this.score += score;
        }
    }

    public void update(float deltaTime) {
        timeSurvived += deltaTime;

        scoreAccumulator += deltaTime;
        if (scoreAccumulator >= 1.0f) {
            score += pointsPerSecond;
            scoreAccumulator = 0f;
        }
    }

    public int getScore() {
        return score;
    }
    public float getTimeSurvived() {
        return timeSurvived;
    }

    public String getFormattedTime(){
        int minutes = (int) timeSurvived/60;
        int seconds = (int) timeSurvived%60;

        String minuteString;
        String secondString;

        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else  {
            secondString = "" + seconds;
        }

        return minuteString + ":" + secondString;
    }

    public void reset(){
        score = 0;
        timeSurvived = 0;
        scoreAccumulator = 0f;
    }

    public int getPointsPerSecond() {
        return pointsPerSecond;
    }

    public void setPointsPerSecond(int pointsPerSecond) {
        if (pointsPerSecond >= 0) {
            this.pointsPerSecond = pointsPerSecond;
        }
    }
}
