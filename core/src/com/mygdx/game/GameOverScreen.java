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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.graalvm.compiler.replacements.Log;

import java.lang.Object;
import java.util.Locale;

import sun.jvm.hotspot.gc.shared.Space;

public class GameOverScreen implements Screen {
    private char[] newName;
    private int currentChar;
    FileHandle baseFileHandle = Gdx.files.internal("I18NStrings");
    I18NBundle localizationBundle;
    String local = java.util.Locale.getDefault().getLanguage();
    public float time_from_last_choice_change = 0;
    public float max_time_from_choice_change = 1f;
    SpaceGame game;
    public SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private TextureAtlas textureAtlas;
    private TextureRegion[] backgrounds;
    public float[] backgroundOffsets = {0,0};
    public float backgroundMaxScrollingSpeed;
    private int score =0;
    BitmapFont font;
    Stage stage;
    Skin skin;
    float hudVerticalMargin, hudLeftX, hudRightX,
            hudCenterX, hudSectionWidth;
    int choice_number = -1;
    TextField txtUserName;
    String username="---";
    public GameOverScreen(SpaceGame game, int score){
        Gdx.app.log("LOCALE",local);
        this.score = score;
        newName = new char[] {'A', 'A', 'A'};
        currentChar = 0;
        if(local == "pl"){

            Locale locale = new Locale("pl");
            localizationBundle = I18NBundle.createBundle(baseFileHandle, locale);
        }
        else localizationBundle = I18NBundle.createBundle(baseFileHandle);
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
        hudRightX = Constants.Menu_Width*2/3 - hudLeftX;
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
                        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
                        {
                            choice_number--;
                            if(choice_number<0) choice_number=4;

                        }


                        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
                        {
                            choice_number++;
                            if(choice_number>4) choice_number=0;
                        }
                    }

                    break;
                // desktop specific code
                case Desktop:
                    if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
                    {
                        choice_number--;
                        if(choice_number<0) choice_number=4;
                    }


                    if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
                    {
                        choice_number++;
                        if(choice_number>4) choice_number=0;
                    }

                    break;
            }
        }
    }

    private void renderText(float deltaTime){
        //top row
        time_from_last_choice_change+=deltaTime;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ||Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            menu_choice(deltaTime);

        font.setColor(Color.RED);
        //print TITLE
        font.draw(batch, localizationBundle.get("game_over"), hudCenterX, Constants.MenuRow5Y, hudSectionWidth, Align.center, false);
        font.setColor(Color.WHITE);

        font.draw(batch, localizationBundle.format("your_score", score), hudCenterX, Constants.MenuRow4Y, hudSectionWidth, Align.center, false);
        font.draw(batch, localizationBundle.get("enter_your_name"), hudCenterX, Constants.MenuRow3Y, hudSectionWidth, Align.center, false);



        if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zeroosiempięć && Gdx.input.getY() < Gdx.graphics.getHeight()
                && Gdx.input.getX() > 0 && Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.zero475)||choice_number==3) {
            font.setColor(Color.YELLOW);
            choice_number = 3;
            font.draw(batch, localizationBundle.get("Leave_to_menu"), hudLeftX, Constants.MenuRow1Y, hudSectionWidth, Align.left, false);
            if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zeroosiempięć && Gdx.input.getY() < Gdx.graphics.getHeight()
                    && Gdx.input.getX() > 0 && Gdx.input.getX() < Gdx.graphics.getWidth()*Constants.zero475) || Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.BUTTON_A)) {
                //this.dispose();
                this.hide();
                game.setScreen(new MainMenuScreen(game));
            }
            font.setColor(Color.WHITE);
        }else font.draw(batch, localizationBundle.get("Leave_to_menu"), hudLeftX, Constants.MenuRow1Y, hudSectionWidth, Align.left, false);

        if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zeroosiempięć && Gdx.input.getY() < Gdx.graphics.getHeight()
                && Gdx.input.getX() < Gdx.graphics.getWidth() && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.zeropięcdwapięć)||choice_number==4) {
            font.setColor(Color.YELLOW);
            choice_number = 4;
            font.draw(batch, localizationBundle.get("submit_score"), hudRightX, Constants.MenuRow1Y, hudSectionWidth, Align.right, false);
            if ((Gdx.input.getY() > Gdx.graphics.getHeight() * Constants.zeroosiempięć && Gdx.input.getY() < Gdx.graphics.getHeight()
                    && Gdx.input.getX() < Gdx.graphics.getWidth() && Gdx.input.getX() > Gdx.graphics.getWidth()*Constants.zeropięcdwapięć&& Gdx.input.isTouched()) || Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.BUTTON_A)) {
                Save save = new Save(); save.load(); save.gd.addHighScore(score, String.valueOf(newName)); save.save();
                this.dispose();
                this.hide();
                game.setScreen(new RankingScreen(game));
            }
            font.setColor(Color.WHITE);
        }else font.draw(batch, localizationBundle.get("submit_score"), hudRightX, Constants.MenuRow1Y, hudSectionWidth, Align.right, false);


        //pierwsza litera
        if ((Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                && Gdx.input.getX() > Gdx.graphics.getWidth()*0.3142f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.3912f)||choice_number==0) {
            font.setColor(Color.YELLOW);
            choice_number = 0;
            font.draw(batch, Character.toString(newName[0]), hudCenterX-30, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);
            if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.getY() < Gdx.graphics.getHeight()*0.67f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.3142f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.3912f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                time_from_last_choice_change =0;
                if(newName[0]==' ') newName[0]='Z';
                else {
                    newName[0]--;
                    if(newName[0] < 'A'){
                        newName[0] = ' ';
                    }
                }

            }
            else if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || Gdx.input.isKeyPressed(Input.Keys.DOWN) || (Gdx.input.getY() > Gdx.graphics.getHeight()*0.776f && Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.3142f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.3912f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                time_from_last_choice_change =0;
                if(newName[0]==' ') newName[0]='A';
                else {
                    newName[0]++;
                    if(newName[0] > 'Z'){
                        newName[0] = ' ';
                    }
                }
            }
            font.setColor(Color.WHITE);
        }else font.draw(batch, Character.toString(newName[0]), hudCenterX-30, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);

        //druga litera
        if ((Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                && Gdx.input.getX() > Gdx.graphics.getWidth()*0.46f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.556f)||choice_number==1) {
            font.setColor(Color.YELLOW);
            choice_number = 1;
            font.draw(batch, Character.toString(newName[1]), hudCenterX, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);
            if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.getY() < Gdx.graphics.getHeight()*0.67f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.46f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.556f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                if(newName[1]==' ') newName[1]='Z';
                else {
                    newName[1]--;
                    if(newName[1] < 'A'){
                        newName[1] = ' ';
                    }
                }time_from_last_choice_change =0;
            }
            else if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || Gdx.input.isKeyPressed(Input.Keys.DOWN) || (Gdx.input.getY() > Gdx.graphics.getHeight()*0.776f && Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.46f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.556f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                if(newName[1]==' ') newName[1]='A';
                else {
                    newName[1]++;
                    if(newName[1] > 'Z'){
                        newName[1] = ' ';
                    }
                }time_from_last_choice_change =0;
            }
            font.setColor(Color.WHITE);
        }else font.draw(batch, Character.toString(newName[1]), hudCenterX, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);


        //trzecia litera
        if ((Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                && Gdx.input.getX() > Gdx.graphics.getWidth()*0.595f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.718f)||choice_number==2) {
            font.setColor(Color.YELLOW);
            choice_number = 2;
            font.draw(batch, Character.toString(newName[2]), hudCenterX+30, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);
            if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.getY() < Gdx.graphics.getHeight()*0.67f && Gdx.input.getY() > Gdx.graphics.getHeight()*0.559f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.595f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.718f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                if(newName[2]==' ') newName[2]='Z';
                else {
                    newName[2]--;
                    if(newName[2] < 'A'){
                        newName[2] = ' ';
                    }
                }time_from_last_choice_change =0;
            }
            else if ((Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || Gdx.input.isKeyPressed(Input.Keys.DOWN) || (Gdx.input.getY() > Gdx.graphics.getHeight()*0.776f && Gdx.input.getY() < Gdx.graphics.getHeight()*0.844f
                    && Gdx.input.getX() > Gdx.graphics.getWidth()*0.595f && Gdx.input.getX() < Gdx.graphics.getWidth()*0.718f && Gdx.input.isTouched())) && time_from_last_choice_change > Constants.max_time_from_choice_change) {
                if(newName[2]==' ') newName[2]='A';
                else {
                    newName[2]++;
                    if(newName[2] > 'Z'){
                        newName[2] = ' ';
                    }
                }time_from_last_choice_change =0;
            }
            font.setColor(Color.WHITE);
        }else font.draw(batch, Character.toString(newName[2]), hudCenterX+30, Constants.MenuRow2Y, hudSectionWidth, Align.center, false);





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
