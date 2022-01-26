package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerShip extends Ship{

    int lives;

    public PlayerShip(float movementSpeed,
                      float xCenter, float yCenter,
                      float width, float height,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots, int shield,
                      TextureRegion shipTextureRegion, TextureRegion laserTextureRegion,TextureRegion shieldTextureRegion) {
        super(movementSpeed, xCenter, yCenter, width, height, laserWidth, laserHeight, laserMovementSpeed, shield, timeBetweenShots, shipTextureRegion, laserTextureRegion, shieldTextureRegion);
        lives = 3;
        this.timeBetweenShots = 250000000;
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[1];
        laser[0] = new Laser(boundingBox.x+boundingBox.width*0.6f,boundingBox.y+boundingBox.height*1f,
                laserWidth, laserHeight,laserMovementSpeed,laserTextureRegion);

        timeSinceLastShot =0;

        return laser;
    }
}
