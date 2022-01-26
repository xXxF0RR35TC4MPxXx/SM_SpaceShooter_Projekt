package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Save {
    public GameData gd;
    public Save(){
        gd= new GameData();
    }

    public void save(){
        try {String temp="";
            FileHandle file = Gdx.files.local("scores.txt");
            for(int i = 0; i < 10; i++) {
                if(i!=9)
                 temp += gd.getNames()[i] + "," + gd.getHighScores()[i] + "\n";
                else temp += gd.getNames()[i] + "," + gd.getHighScores()[i];
            }file.writeString(temp, false);
        }catch(Exception e){
            e.printStackTrace();
            Gdx.app.log("jakisError", "Wyjątek przy zapisie");
            Gdx.app.exit();
        }
    }
    public void load()
    {
        try{
            if(!saveFileExists()){
                Gdx.app.log("exists", "Twierdzi, że nie istnieje!");
                init();
                return;
            }
            FileHandle file = Gdx.files.local("scores.txt");
            String temp = file.readString();
            String jedenWynik[] = temp.split("\\n");
            String scoreZPliku="";
            String nameZPliku="";
            gd.sortHighScores();
            for(int i = 0; i < 10; i++) {
                String[] result = jedenWynik[i].split(",");
                scoreZPliku = result[1];
                nameZPliku = result[0];
                gd.highScores[i] = Long.parseLong(scoreZPliku);
                gd.names[i] = nameZPliku;
                Gdx.app.log("score", String.valueOf(gd.highScores[i]) + " - " + gd.names[i]);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            Gdx.app.log("TAG", "WYWALA PRZY LOADZIE");
            Gdx.app.exit();
        }
    }
    public boolean saveFileExists(){
        FileHandle file = Gdx.files.local("scores.txt");
        return file.exists();
    }
    public void init(){
        gd.init();
        this.save();
    }
}
