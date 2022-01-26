package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import com.mygdx.game.Constants;
public class GameScreen implements Screen{

    FileHandle baseFileHandle = Gdx.files.internal("I18NStrings");
    I18NBundle localizationBundle;
    String local = java.util.Locale.getDefault().getLanguage();

    //game states
    static final int GAME_RUNNING = 0;
    static final int GAME_PAUSED = 1;
    static final int GAME_OVER = 2;
    int state;
    //screen
    private Camera camera;
    private Viewport viewport;
    //private Preferences pref = Gdx.app.getPreferences("My Preferences");

    private SpaceGame game;
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;
    private LinkedList<PlayerExplosion> playerExplosionList;

    //graphics
    public SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private TextureRegion[] backgrounds;
    private Texture enemyExplosionTexture;
    private Texture playerExplosionTexture;
    private TextureRegion leftButton;
    private TextureRegion rightButton;
    private TextureRegion shotButton;

    private TextureRegion playerShipTextureRegion, enemyShipTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion,
            playerShieldTextureRegion, enemyShieldTextureRegion;

    //timing
    public float[] backgroundOffsets = {0,0};
    public float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 3f;
    private float enemySpawnTimer = 0f;
    private float invincibilityTimer =3f;
    private float maxInvincibilityTime = 3f;
    //world parameters




    private int score = 0;

    //HUD
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCenterX, hudRow1Y, hudRow2Y, hudRow3Y, hudSectionWidth;

    GameScreen(SpriteBatch batch, SpaceGame game, int score, int lives, int shield){
        this.game=game;

        state = GAME_RUNNING;
        if(local == "pl"){

            Locale locale = new Locale("pl");
            localizationBundle = I18NBundle.createBundle(baseFileHandle, locale);
        }
        else localizationBundle = I18NBundle.createBundle(baseFileHandle);
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);


        textureAtlas = new TextureAtlas("SpacePack.atlas");

        backgrounds = new TextureRegion[2];
        backgrounds[0] = textureAtlas.findRegion("8bitbackground");
        backgrounds[1] = textureAtlas.findRegion("8bitbackground2");
        backgroundMaxScrollingSpeed = (float)(Constants.WORLD_HEIGHT) / 12;

        leftButton = textureAtlas.findRegion("leftButton");
        rightButton = textureAtlas.findRegion("rightButton");
        shotButton = textureAtlas.findRegion("shotButton");



        playerShipTextureRegion = textureAtlas.findRegion("ship");
        enemyShipTextureRegion = textureAtlas.findRegion("alien1");
        playerLaserTextureRegion=textureAtlas.findRegion("playerBullet");
        enemyLaserTextureRegion=textureAtlas.findRegion("enemyBullet");
        playerShieldTextureRegion=textureAtlas.findRegion("Shield3");
        enemyShieldTextureRegion=textureAtlas.findRegion("Shield1");
        playerExplosionTexture = new Texture("playerExplosions.png");
        enemyExplosionTexture = new Texture("explosion.png");


        playerShip = new PlayerShip(400,
                Constants.playerShipWidth, Constants.playerShipHeight,
                80,80, 16f, 40f,
                1260, 0.5f, shield,
                playerShipTextureRegion, playerLaserTextureRegion, playerShieldTextureRegion);
    playerShip.lives = lives;
    playerShip.shield = shield;
    this.score = score;
        enemyShipList=new LinkedList<>();
        playerLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();
        playerExplosionList = new LinkedList<>();
        enemyLaserList= new LinkedList<>();
        this.batch = batch;

        prepareHUD();
    }

    private void prepareHUD(){
        //create a bitmap font from font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("retrofont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 72;
        fontParameter.color = new Color(1,1,1,0.3f);
        font = fontGenerator.generateFont(fontParameter);

        //scale the font to fit the world
        font.getData().setScale(0.4f);


        //calculate the hud margins, etc.
        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftX = hudVerticalMargin;
        hudRightX = Constants.WORLD_WIDTH*2/3 - hudLeftX;
        hudCenterX = Constants.hudCenterX;
        hudRow1Y = Constants.WORLD_HEIGHT-hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudRow3Y = hudRow2Y - hudVerticalMargin*4 - font.getCapHeight();
        hudSectionWidth = Constants.hudCenterX;

    }

    @Override
    public void render(float deltaTime)
    {
        switch (state) {
            case GAME_RUNNING:
                batch.begin();
                if(score % 5000 ==0 && score!=0){
                    score+=500;
                    int temp_lives = playerShip.lives;
                    int temp_shields = playerShip.shield;
                    playerShip=null;
                    batch.end();
                    game.setScreen(new GameScreen(this.batch, game, score,temp_lives, temp_shields));
                    this.dispose();
                    this.hide();
                    return;
                }
                //scrolling background
                renderBackground(deltaTime);
                invincibilityTimer-=deltaTime;
                //detect input
                detectInput(deltaTime);
                playerShip.update(deltaTime);

                spawnEnemyShips(deltaTime);

                ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
                while(enemyShipListIterator.hasNext()){
                    EnemyShip enemyShip = enemyShipListIterator.next();
                    moveEnemy(enemyShip, deltaTime);
                    enemyShip.update(deltaTime);
                    enemyShip.draw(batch);

                }


                //player ship
                playerShip.draw(batch);



                //lasers
                renderEnemyLasers(deltaTime);

                //detect collisions between lasers and ships
                detectCollisions(deltaTime);

                //explosions
                updateAndRenderExplosions(deltaTime);

                //hud rendering
                updateAndRenderHUD();

                batch.end();
                break;
            case GAME_PAUSED:
                //updatePaused();
                break;
            case GAME_OVER:
                this.dispose();
                game.setScreen(new GameOverScreen(game, score));
                break;
        }

    }

    private void updateAndRenderHUD(){
        font.setColor(1,1,1,0.6f);
        //top row
        font.draw(batch, localizationBundle.get("score"), hudCenterX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, localizationBundle.get("shields"), hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, localizationBundle.get("lives"), hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);

        //render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudCenterX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);

        //pause button
        font.draw(batch, localizationBundle.get("pause"), hudRightX, hudRow3Y, hudSectionWidth, Align.right, false);

        //draw bottom controls
        batch.draw(leftButton, Constants.leftArrowX, Constants.gameButtonY, Constants.gameButtonWidthAndHeight,Constants.gameButtonWidthAndHeight);
        batch.draw(rightButton, Constants.rightArrowX, Constants.gameButtonY, Constants.gameButtonWidthAndHeight,Constants.gameButtonWidthAndHeight);
        batch.draw(shotButton, Constants.showButtonX, Constants.gameButtonY, Constants.gameButtonWidthAndHeight,Constants.gameButtonWidthAndHeight);

    }

    private void spawnEnemyShips(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns && enemyShipList.size() < 10)
        {
            enemyShipList.add(new EnemyShip(336,
                    SpaceGame.random.nextFloat()*(Constants.WORLD_WIDTH-10)+5, Constants.WORLD_HEIGHT-5,
                    Constants.enemyShipWidthAndHeight,Constants.enemyShipWidthAndHeight, Constants.enemyShipLaserWidth, Constants.enemyShipLaserHeight,
                    480, 1f, 1,
                    enemyShipTextureRegion, enemyLaserTextureRegion, enemyShieldTextureRegion));
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }

    }

    private void detectInput(float deltaTime)
    {
        //keyboard input
        float leftLimit, rightLimit, upLimit, downLimit;
        rightLimit = Constants.WORLD_WIDTH-playerShip.boundingBox.x - playerShip.boundingBox.width;
        leftLimit = -playerShip.boundingBox.x;
        int rotation = Gdx.input.getRotation();
        if((Gdx.input.getNativeOrientation() == Input.Orientation.Portrait && (rotation == 90 || rotation == 270)) || //First case, the normal phone
                (Gdx.input.getNativeOrientation() == Input.Orientation.Landscape && (rotation == 0 || rotation == 180))) //Second case, the landscape device
        {
            if((Gdx.input.isKeyPressed(Input.Keys.P) || Gdx.input.isKeyPressed(Input.Keys.BUTTON_START) ||
                    (Gdx.input.isTouched() && Gdx.input.getY() < Gdx.graphics.getHeight() * Constants.PauseMaxYMultiplier
                            && Gdx.input.getY()>Gdx.graphics.getHeight() * Constants.PauseMinYMultiplier
                            && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.PauseMaxXMultiplier
                            && Gdx.input.getX() < Gdx.graphics.getWidth())))
            {
                //pause game
            }




            if((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) ||
                    ((Gdx.input.isTouched(0) && Gdx.input.getY(0) < Gdx.graphics.getHeight() * Constants.lowerRowMaxY
                            && Gdx.input.getY(0)>Gdx.graphics.getHeight() * Constants.lowerRowMinY
                            && Gdx.input.getX(0) > Gdx.graphics.getWidth()*Constants.leftRowMinX
                            && Gdx.input.getX(0) < Gdx.graphics.getWidth()*Constants.leftRowMaxX) ||

                            (Gdx.input.isTouched(1) && Gdx.input.getY(1) < Gdx.graphics.getHeight() * Constants.lowerRowMaxY
                                    && Gdx.input.getY(1)>Gdx.graphics.getHeight() * Constants.lowerRowMinY
                                    && Gdx.input.getX(1) > Gdx.graphics.getWidth()*Constants.leftRowMinX
                                    && Gdx.input.getX(1) < Gdx.graphics.getWidth()*Constants.leftRowMaxX)))&& leftLimit < 0)
            {
                playerShip.translate(Math.max(-playerShip.movementSpeed*deltaTime, leftLimit),0f);
                //draw on-screen buttons

            }
            else playerShip.translate(0f,0f);

            if((Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)||
                    ((Gdx.input.isTouched(0) && Gdx.input.getY(0) < Gdx.graphics.getHeight() * Constants.lowerRowMaxY
                            && Gdx.input.getY(0)>Gdx.graphics.getHeight() * Constants.lowerRowMinY
                            && Gdx.input.getX(0) > Gdx.graphics.getWidth()*Constants.rightRowMinX
                            && Gdx.input.getX(0) < Gdx.graphics.getWidth()*Constants.rightRowMaxX) ||
                            (Gdx.input.isTouched(1) && Gdx.input.getY(1) < Gdx.graphics.getHeight() * Constants.lowerRowMaxY
                                    && Gdx.input.getY(1)>Gdx.graphics.getHeight() * Constants.lowerRowMinY
                                    && Gdx.input.getX(1) > Gdx.graphics.getWidth()*Constants.rightRowMinX
                                    && Gdx.input.getX(1) < Gdx.graphics.getWidth()*Constants.rightRowMaxX))) && rightLimit >0)
            {
                playerShip.translate(Math.min(+playerShip.movementSpeed*deltaTime, rightLimit),0f);
                //draw on-screen buttons

            }else playerShip.translate(0f,0f);
            //draw on-screen buttons
            if((Gdx.input.isKeyPressed(Input.Keys.X)
                    || Gdx.input.isKeyPressed(Input.Keys.BUTTON_X)
                    || ((Gdx.input.isTouched(0) && Gdx.input.getY(0) < Gdx.graphics.getHeight() *  Constants.shotButtonMaxYMultiplier
                    && Gdx.input.getY(0)>Gdx.graphics.getHeight() * Constants.shotButtonMinYMultiplier
                    && Gdx.input.getX(0) > Gdx.graphics.getWidth()*Constants.shotButtonMinXMultiplier
                    && Gdx.input.getX(0) < Gdx.graphics.getWidth()) || (Gdx.input.isTouched(1) && Gdx.input.getY(1) < Gdx.graphics.getHeight() *  Constants.shotButtonMaxYMultiplier
                    && Gdx.input.getY(1)>Gdx.graphics.getHeight() * Constants.shotButtonMinYMultiplier
                    && Gdx.input.getX(1) > Gdx.graphics.getWidth()*Constants.shotButtonMinXMultiplier
                    && Gdx.input.getX(1) < Gdx.graphics.getWidth()) ))
                    && TimeUtils.nanoTime() - playerShip.timeSinceLastShot > playerShip.timeBetweenShots)
            {
                Laser[] l = playerShip.fireLasers();
                for(Laser laser:l)
                {
                    playerLaserList.add(laser);
                }
                playerShip.timeSinceLastShot = TimeUtils.nanoTime();
            }
        }
    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime){
        float leftLimit, rightLimit, upLimit, downLimit;
        rightLimit = Constants.WORLD_WIDTH-enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        leftLimit = -enemyShip.boundingBox.x;
        upLimit = Constants.WORLD_HEIGHT-enemyShip.boundingBox.y-enemyShip.boundingBox.height;
        downLimit = (float)Constants.WORLD_HEIGHT/2-enemyShip.boundingBox.y;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if(xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if(yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);
    }

    private void detectCollisions(float deltaTime){
        //for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while(enemyShipListIterator.hasNext()){
                EnemyShip enemyShip = enemyShipListIterator.next();
                if(enemyShip.intersects(laser.boundingBox)){
                    //contact  with enemy ship
                    if(enemyShip.hit(laser)){
                        enemyShipListIterator.remove();
                        score+=100;
                        explosionList.add(new Explosion(enemyExplosionTexture,
                                new Rectangle(enemyShip.boundingBox),
                                0.7f));

                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        laserListIterator = enemyLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();

            if(playerShip.intersects(laser.boundingBox) && invincibilityTimer <=0f){
                //contact  with enemy laser
                if(playerShip.hit(laser)){
                    invincibilityTimer=maxInvincibilityTime;
                    playerExplosionList.add(new PlayerExplosion(playerExplosionTexture,
                            new Rectangle(playerShip.boundingBox.x-playerShip.boundingBox.width/2, playerShip.boundingBox.y-playerShip.boundingBox.height/2,
                                    playerShip.boundingBox.width*2, playerShip.boundingBox.height*2),
                            1f));
                    playerShip.lives--;
                    if(playerShip.lives>-1) playerShip.shield = 1;
                    else {state = GAME_OVER;}
                }
                laserListIterator.remove();
            }
        }

        //for each enemy laser, check whether it intersects a player ship
    }

    private void updateAndRenderExplosions(float deltaTime)
    {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while(explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()){
                explosionListIterator.remove();
            }
            else{
                explosion.draw(batch);
            }
        }
        ListIterator<PlayerExplosion> playerExplosionListIterator = playerExplosionList.listIterator();
        while(playerExplosionListIterator.hasNext()){
            PlayerExplosion explosion = playerExplosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()){
                playerExplosionListIterator.remove();
            }
            else{
                explosion.draw(batch);
            }
        }
    }


    private void renderEnemyLasers(float deltaTime){
        //create new lasers
        //enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while(enemyShipListIterator.hasNext()){
            EnemyShip enemyShip = enemyShipListIterator.next();
            if(enemyShip.canFireLaser()){
                Laser[] lasers = enemyShip.fireLasers();
                for(Laser laser:lasers){
                    enemyLaserList.add(laser);
                }
            }
        }


        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed*deltaTime;
            if(laser.boundingBox.y > Constants.WORLD_HEIGHT){
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed*deltaTime;
            if(laser.boundingBox.y + laser.boundingBox.height < 0){
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime){
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed;

        for(int layer = 0; layer < backgroundOffsets.length; layer++)
        {
            if(backgroundOffsets[layer] > Constants.WORLD_HEIGHT)
            {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer]+Constants.WORLD_HEIGHT, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        }
    }


    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public void hide(){}

    @Override
    public void dispose(){font.dispose();  game.dispose();

        enemyShipList=null;
        playerLaserList=null;
        enemyLaserList=null;
        explosionList=null;
        playerExplosionList=null;

        textureAtlas.dispose();
        backgrounds=null;
        enemyExplosionTexture.dispose();
        playerExplosionTexture.dispose();
          leftButton=null;
          rightButton=null;
          shotButton=null;
          playerShipTextureRegion=null; enemyShipTextureRegion=null;
                playerLaserTextureRegion=null; enemyLaserTextureRegion=null;
                playerShieldTextureRegion=null; enemyShieldTextureRegion=null;}


    @Override
    public void show(){

    }

}
