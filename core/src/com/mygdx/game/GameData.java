package com.mygdx.game;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1;

    private final int MAX_SCORES = 10;
    public long[] highScores;
    public String[] names;

    private long tentativeScore;

    public GameData(){
        highScores = new long[MAX_SCORES];
        names = new String[MAX_SCORES];
    }

    //sets up an empty highscores table
    public void init(){
        highScores[0]=1000;
        names[0] = "XD0";

        highScores[1]=900;
        names[1] = "XD1";

        highScores[2]=800;
        names[2] = "XD2";

        highScores[3]=700;
        names[3] = "XD3";

        highScores[4]=600;
        names[4] = "XD4";

        highScores[5]=500;
        names[5] = "XD5";

        highScores[6]=400;
        names[6] = "XD6";

        highScores[7]=300;
        names[7] = "XD7";

        highScores[8]=200;
        names[8] = "XD8";

        highScores[9]=100;
        names[9] = "XD9";
        sortHighScores();
    }

    public long[] getHighScores(){
        return highScores;
    }

    public String[] getNames(){
        return names;
    }

    public long getTentativeScore(){
        return tentativeScore;
    }

    public void setTentativeScore(int i){
        tentativeScore = i;
    }
    public boolean isHighScore(long score){
        return score > highScores[MAX_SCORES-1];
    }

    public void addHighScore(long newScore, String name){
        if(isHighScore(newScore)){
            highScores[MAX_SCORES-1] = newScore;
            names[MAX_SCORES-1]=name;
            sortHighScores();
        }
    }

    public void sortHighScores() {
        for (int i = 0; i < MAX_SCORES-1; i++)
            for (int j = 0; j < MAX_SCORES-i-1; j++)
                if (highScores[j] < highScores[j+1])
                {
                    // swap arr[j+1] and arr[j]
                    long temp = highScores[j];
                    highScores[j] = highScores[j+1];
                    highScores[j+1] = temp;

                    String tempString = names[j];
                    names[j] = names[j+1];
                    names[j+1] = tempString;
                }
        }
}
