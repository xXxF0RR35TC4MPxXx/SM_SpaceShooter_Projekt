package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PlayerExplosion {
    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;
    private Rectangle boundingBox;

    PlayerExplosion(Texture texture, Rectangle boundingBox, float totalAnimationTime){
        this.boundingBox = boundingBox;
        //split texture
        TextureRegion[][] textureRegion2D = TextureRegion.split(texture, 200, 200);

        //convert texture to 1D array
        TextureRegion[] textureRegion1D = new TextureRegion[5];

        for(int i=0;i<5;i++){
            textureRegion1D[i] = textureRegion2D[0][i];

        }

        explosionAnimation = new Animation<TextureRegion>(totalAnimationTime/5, textureRegion1D);
        explosionTimer=0;
    }

    public void update(float deltaTime){
        explosionTimer+=deltaTime;
    }

    public void draw(SpriteBatch batch){
        batch.draw(explosionAnimation.getKeyFrame(explosionTimer),
                boundingBox.x,
                boundingBox.y,
                boundingBox.width,
                boundingBox.height);
    }

    public boolean isFinished(){
        return explosionAnimation.isAnimationFinished(explosionTimer);
    }

}
