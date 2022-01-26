package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Locale;
import com.mygdx.game.Constants;

public class MainMenuScreen implements Screen {
    FileHandle baseFileHandle = Gdx.files.internal("I18NStrings");
    I18NBundle localizationBundle;
    String local = java.util.Locale.getDefault().getLanguage();
    Save save;
    SpaceGame game;
    public SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private TextureAtlas textureAtlas;
    private TextureRegion[] backgrounds;
    public float[] backgroundOffsets = {0,0};
    public float backgroundMaxScrollingSpeed;
    public float time_from_last_choice_change = 0;
    public float max_time_from_choice_change = 1f;

    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCenterX, hudSectionWidth;
    int choice_number = -1;
    public MainMenuScreen(SpaceGame game){
        //Gdx.app.log("LOCALE",local);
        if(local == "pl"){

            Locale locale = new Locale("pl");
            localizationBundle = I18NBundle.createBundle(baseFileHandle, locale);
        }
        else localizationBundle = I18NBundle.createBundle(baseFileHandle);
        this.game = game;
        save=new Save();
        save.load();
        //Save.load();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.Menu_Width, Constants.Menu_Height, camera);

        textureAtlas = new TextureAtlas("SpacePack.atlas");

        backgrounds = new TextureRegion[2];
        backgrounds[0] = textureAtlas.findRegion("8bitbackground");
        backgrounds[1] = textureAtlas.findRegion("8bitbackground2");

        backgroundMaxScrollingSpeed = (float)(Constants.Menu_Height) / 12;
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("retrofont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        //scale the font to fit the world
        font.getData().setScale(0.08f);


        //calculate the hud margins, etc.
        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftX = hudVerticalMargin;
        hudRightX =Constants.Menu_Width*2/3 - hudLeftX;
        hudCenterX = Constants.Menu_Width/3;
        hudSectionWidth = hudCenterX;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {

        batch.begin();
        renderBackground(deltaTime);
        renderText(deltaTime);
        batch.end();
    }
    private void menu_choice(float deltaTime){
        if(time_from_last_choice_change > Constants.max_time_from_choice_change){
        switch(Gdx.app.getType()) {
            case Android:
                int rotation = Gdx.input.getRotation();
                if((Gdx.input.getNativeOrientation() == Input.Orientation.Portrait && (rotation == 90 || rotation == 270)) || //First case, the normal phone
                        (Gdx.input.getNativeOrientation() == Input.Orientation.Landscape && (rotation == 0 || rotation == 180))) //Second case, the landscape device
                {
                    if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
                    {
                        choice_number--;
                        if(choice_number<0) choice_number=4;
                    }


                    if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN))
                    {
                        choice_number++;
                        if(choice_number>4) choice_number=0;
                    }
                }
                time_from_last_choice_change =0;
                break;
            // desktop specific code
            case Desktop:
                if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
                {
                    choice_number--;
                    if(choice_number<0) choice_number=4;
                }


                if(Gdx.input.isKeyPressed(Input.Keys.DOWN)|| Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN))
                {
                    choice_number++;
                    if(choice_number>4) choice_number=0;
                }
                time_from_last_choice_change=0;
                break;
        }
        }
    }

    private void renderText(float deltaTime){
        //top row
            time_from_last_choice_change+=deltaTime;
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.UP) ||
                    Gdx.input.isKeyPressed(Input.Keys.LEFT) ||Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                    || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)|| Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
                menu_choice(deltaTime);
            font.setColor(Color.CYAN);

            //print TITLE
            font.draw(batch, localizationBundle.get("app_name"), hudCenterX, Constants.MenuRow5Y, hudSectionWidth, Align.center, false);
            font.setColor(Color.WHITE);

            //print PLAY
            if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zerodwa && Gdx.input.getY() < Gdx.graphics.getHeight() * Constants.zerocztery
            && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.MinMenuRow2X && Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.MaxMenuRow2X)||choice_number==0) {
                font.setColor(Color.YELLOW);
                choice_number=0;
                font.draw(batch, localizationBundle.get("play"), hudCenterX, Constants.MenuRow4Y, hudSectionWidth, Align.center, false);
                if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.BUTTON_A))
                {
                    this.dispose();
                    this.hide();
                    game.setScreen(new GameScreen(batch, game, 0, 3, 3));
                }
                font.setColor(Color.WHITE);
            }else {font.draw(batch, localizationBundle.get("play"), hudCenterX, Constants.MenuRow4Y, hudSectionWidth, Align.center, false); }

            //print RANKINGS
             if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zerocztery && Gdx.input.getY() < Gdx.graphics.getHeight() * Constants.zerosześć
                     && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.zerotrzypięć && Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.zerosześćpięć)||choice_number==1) {
                font.setColor(Color.YELLOW);
                font.draw(batch, localizationBundle.get("ranking"), hudCenterX, Constants.MenuRow3Y, hudSectionWidth, Align.center, false);
                 choice_number=1;
                if(Gdx.input.isTouched()|| Gdx.input.isKeyPressed(Input.Keys.ENTER)|| Gdx.input.isKeyPressed(Input.Keys.BUTTON_A)) {
                     this.dispose();
                     this.hide();
                     Gdx.app.log("menu", "Powinien wejsc do rankingu");
                     game.setScreen(new RankingScreen(game));
                 }

                font.setColor(Color.WHITE);
            }else{font.draw(batch, localizationBundle.get("ranking"), hudCenterX, Constants.MenuRow3Y, hudSectionWidth, Align.center, false); }


             //print OPTIONS
             if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zerosześć && Gdx.input.getY() < Gdx.graphics.getHeight() *Constants.MinMenuRow1Y
                     && Gdx.input.getX() > Gdx.graphics.getWidth()* Constants.zerocztery&& Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.MaxMenuRow2X)||choice_number==2) {
                font.setColor(Color.YELLOW);choice_number=2;
                font.draw(batch, localizationBundle.get("options"), hudCenterX, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);
                 if(Gdx.input.isTouched()|| Gdx.input.isKeyPressed(Input.Keys.ENTER)|| Gdx.input.isKeyPressed(Input.Keys.BUTTON_A)) {
                     this.dispose();
                 }
                font.setColor(Color.WHITE);
            } else{
                 font.draw(batch, localizationBundle.get("options"), hudCenterX, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);
             }

             //print ABOUT
             if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.MinMenuRow1Y && Gdx.input.getY() < Gdx.graphics.getHeight()
                     && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.MinMenuRow2X && Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.MaxMenuRow2X)||choice_number==3)
             {
                font.setColor(Color.YELLOW);choice_number=3;
                font.draw(batch, localizationBundle.get("about"), hudCenterX, Constants.MenuRow1Y, hudSectionWidth, Align.center, false);
                 if(Gdx.input.isTouched()|| Gdx.input.isKeyPressed(Input.Keys.ENTER)|| Gdx.input.isKeyPressed(Input.Keys.BUTTON_A))
                 {
                     this.dispose();
                     this.hide();
                     game.setScreen(new AboutScreen(game));

                 }
                font.setColor(Color.WHITE);
            }else{font.draw(batch, localizationBundle.get("about"), hudCenterX, Constants.MenuRow1Y, hudSectionWidth, Align.center, false);}

        //print EXIT
        if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.MinMenuRow1Y && Gdx.input.getY() < Gdx.graphics.getHeight()
                && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.MinMenuRow1X && Gdx.input.getX() < Gdx.graphics.getWidth()*1f)||choice_number==4)
        {
            font.setColor(Color.YELLOW);choice_number=4;
            font.draw(batch, localizationBundle.get("exit"), hudRightX, Constants.MenuRow1Y, hudSectionWidth, Align.right, false);
            if(Gdx.input.isTouched()|| Gdx.input.isKeyPressed(Input.Keys.ENTER)|| Gdx.input.isKeyPressed(Input.Keys.BUTTON_A))
            {
                this.dispose();
                Gdx.app.exit();
            }
            font.setColor(Color.WHITE);
        }else{font.draw(batch, localizationBundle.get("exit"), hudRightX, Constants.MenuRow1Y, hudSectionWidth, Align.right, false);}

    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    private void renderBackground(float deltaTime){
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed;

        for(int layer = 0; layer < backgroundOffsets.length; layer++)
        {
            if(backgroundOffsets[layer] > Constants.Menu_Height)
            {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], Constants.Menu_Width, Constants.Menu_Height);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer]+Constants.Menu_Height, Constants.Menu_Width, Constants.Menu_Height);
        }
    }






    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
