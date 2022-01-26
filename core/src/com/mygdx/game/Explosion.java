package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {
    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;
    private Rectangle boundingBox;

    Explosion(Texture texture, Rectangle boundingBox, float totalAnimationTime){
        this.boundingBox = boundingBox;
        //split texture
        TextureRegion[][] textureRegion2D = TextureRegion.split(texture, 111, 111);

        //convert texture to 1D array
        TextureRegion[] textureRegion1D = new TextureRegion[6];

        for(int i=0;i<6;i++){
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
