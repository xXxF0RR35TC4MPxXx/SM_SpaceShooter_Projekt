package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class EnemyShip extends Ship{

    Vector2 directionVector;
    float timeSinceLastDirectionChange=0;
    float directionChangeFrequency=0.65f;


    public EnemyShip(float movementSpeed,
                     float xCenter, float yCenter,
                     float width, float height,
                     float laserWidth, float laserHeight,
                     float laserMovementSpeed, float timeBetweenShots, int shield,
                     TextureRegion shipTextureRegion, TextureRegion laserTextureRegion, TextureRegion shieldTextureRegion) {
        super(movementSpeed, xCenter, yCenter, width, height, laserWidth, laserHeight, laserMovementSpeed, shield, timeBetweenShots, shipTextureRegion, laserTextureRegion,shieldTextureRegion);
        directionVector = new Vector2(0, -1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector(){
        double bearing= SpaceGame.random.nextDouble()*6.283185;
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
        if(boundingBox.x==5){
            directionVector.x = Math.abs(directionVector.x);
        }
        if(boundingBox.x==182){
            directionVector.x = -directionVector.x;
        }
        if(boundingBox.y==5){
            directionVector.y = Math.abs(directionVector.y);
        }
        if(boundingBox.y==38){
            directionVector.y = -directionVector.y;
        }
    }




    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[1];
        laser[0] = new Laser(boundingBox.x+boundingBox.width*0.5f,boundingBox.y-laserHeight,
                laserWidth, laserHeight,laserMovementSpeed,laserTextureRegion);

        timeSinceLastShot =0;

        return laser;
    }

    @Override
    public void draw(Batch batch){
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if(shield >0){
            batch.draw(shieldTextureRegion, boundingBox.x-(boundingBox.width*0.25f), boundingBox.y-(boundingBox.height*0.25f), boundingBox.width*1.5f, boundingBox.height*1.5f);
        }

    }

    private static Random randomNumberGen = new Random();

    private float timeFromLastMovementChange =0;

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if(timeSinceLastDirectionChange> directionChangeFrequency){
            randomizeDirectionVector();
            timeSinceLastDirectionChange-=directionChangeFrequency;
        }
    }

}

