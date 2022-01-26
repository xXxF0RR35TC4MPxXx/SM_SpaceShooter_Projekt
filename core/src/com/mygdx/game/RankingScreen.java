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

import org.graalvm.compiler.replacements.Log;

import java.lang.Object;
import java.util.Locale;

import sun.jvm.hotspot.gc.shared.Space;

public class RankingScreen implements Screen {
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
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX,
            hudCenterX, hudSectionWidth;
    int choice_number = -1;
    public RankingScreen(SpaceGame game){
        Gdx.app.log("LOCALE",local);
        if(local == "pl") {

            Locale locale = new Locale("pl");
            localizationBundle = I18NBundle.createBundle(baseFileHandle, locale);
        }
        else localizationBundle = I18NBundle.createBundle(baseFileHandle);

        Gdx.app.log("RankingScreen", "Pomyslnie przeszedl dalej, po Save.load()");
        this.game = game;
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
        fontParameter.size = 32;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        //scale the font to fit the world
        font.getData().setScale(0.08f);


        //calculate the hud margins, etc.
        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftX = hudVerticalMargin;
        hudRightX = Constants.Menu_Width*2/3 - hudLeftX;
        hudCenterX = Constants.Menu_Width/3;
        hudSectionWidth = hudCenterX;
        save=new Save();
        save.load();
        highScores=save.gd.highScores;
        names = save.gd.names;
        Gdx.app.log("koniec", "DOTARLEM DO KONCA!");
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

    private long[] highScores;
    private String[] names;

    private void renderText(float deltaTime){
        //top row
        time_from_last_choice_change+=deltaTime;
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.BUTTON_A) || Gdx.input.isTouched()){
            //this.dispose();
            this.hide();
            game.setScreen(new MainMenuScreen(game));

        }
        font.setColor(Color.WHITE);

        for(int i=0;i<10;i++){
            font.draw(batch, String.format(
                    "%2d. %7s %s", i+1, highScores[i], names[i]
            ), hudCenterX, Constants.Menu_Height*(10-i)/10-4f, hudSectionWidth, Align.center, false);

        }




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
