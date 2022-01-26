package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Ship {

    float movementSpeed;
    float laserWidth, laserHeight;
    float timeBetweenShots;
    float timeSinceLastShot =0;
    float laserMovementSpeed;
    int shield;
    Rectangle boundingBox;

    public Ship(float movementSpeed,
                float xCenter, float yCenter,
                float width, float height,
                float laserWidth, float laserHeight, float laserMovementSpeed, int shield,
                float timeBetweenShots,
                TextureRegion shipTextureRegion, TextureRegion laserTextureRegion, TextureRegion shieldTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.boundingBox = new Rectangle(xCenter - width/2, yCenter - height/2, width, height);

        this.laserWidth=laserWidth;
        this.laserHeight = laserHeight;
        this.timeBetweenShots = timeBetweenShots;
        this.laserMovementSpeed = laserMovementSpeed;
        this.shield = shield;
        this.shipTextureRegion = shipTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
    }

    public void update(float deltaTime){
        timeSinceLastShot += deltaTime;
    }

    public boolean canFireLaser(){
        return (timeSinceLastShot - timeBetweenShots >= 0);
    }

    public abstract Laser[] fireLasers();

    TextureRegion shipTextureRegion, laserTextureRegion, shieldTextureRegion;

    public boolean intersects(Rectangle laserRectangle){
        return boundingBox.overlaps(laserRectangle);

    }

    private TextureAtlas textureAtlas = new TextureAtlas("SpacePack.atlas");

    public boolean hit(Laser laser){
        if(shield > 0)
        {
            shield--;
            if(shield == 2)
            {
                shieldTextureRegion = textureAtlas.findRegion("Shield2");
                return false;
            }
            if(shield == 1)
            {
                shieldTextureRegion = textureAtlas.findRegion("Shield1");
                return false;
            }
            if(shield==0){
                return false;
            }
        }
        return true;
    }

    public void translate(float xChange, float yChange){
        boundingBox.setPosition(boundingBox.x+xChange, boundingBox.y+yChange);
    }

    public void draw(Batch batch){
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if(shield>0){
            batch.draw(shieldTextureRegion, boundingBox.x-(boundingBox.width/2), boundingBox.y-(boundingBox.height/2), boundingBox.width*2, boundingBox.height*2);
        }

    }
}
