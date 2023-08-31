package com.gammery.trizzel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gammery.trizzel.model.GamePreferences;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class InfoScreen extends AbstractGameScreen {

    private static final String TAG = InfoScreen.class.getName();
    private boolean debugEnabled = true;
    private static final float DEBUG_REBUILD_STAGE = 3.0f;
    private float debugRebuildStage = DEBUG_REBUILD_STAGE;

    private Stage stage;
    private int width;
    private int height;
    private InputProcessor inputManager;

    public InfoScreen(DirectedGame game) {
        super(game);
    }

    
    private void rebuildStage() {

        if (stage == null) stage = new Stage();

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        stage.clear();

        stage.setViewport(new StretchViewport(width, height));

        Stack stack = new Stack();
        stack.setFillParent(true);
        stage.addActor(stack);

        stack.add(middlePanel());

        inputManager = new InputMultiplexer(new InputKeyAdapter(), stage);
    }

    private Table middlePanel() {
        Table layer = new Table();
        if (debugEnabled) layer.debug();
        Label label = null;
        String content = "Author: Matias E. Vazquez (matiasevqz@gmail.com)\n"
                       + "Icons: \"Sound on/off\", \"Play\" and \"Pause\" by "
                       + "Egor Rumyantsev (www.flaticon.com/authors/egor-rumyantsev) "
                       + "from www.flaticon.com licensed by Creative Commons BY 3.0\n"
                       + "Music: ...\n"
                       + "Sound: ...\n";

        label = new Label(content, Assets.instance.lblStyleSmall);
        label.setWrap(true);

        layer.add(label);

        return layer;
    }


    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(Constants.BG_RED, Constants.BG_GREEN, Constants.BG_BLUE, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (debugEnabled) {
            debugRebuildStage -= deltaTime;
            if (debugRebuildStage <= 0) {
                debugRebuildStage = DEBUG_REBUILD_STAGE;
                Gdx.app.debug(TAG, "Rebuild Stage");
                rebuildStage();
                stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            }
        }

        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        Gdx.app.log("RESOLUTION: ", "Width,Height: " + width + ", " + height);
        Gdx.app.log("RESOLUTION: ", stage.getViewport().getClass().getName());
    }


    @Override
    public void show() {
        GamePreferences.instance.load();
        Gdx.app.debug(TAG, "show()");
        Gdx.input.setCatchBackKey(true);
        rebuildStage();
    }


    @Override
    public void hide() {
        Gdx.app.debug(TAG, "hide()");
        stage.dispose();
        stage = null;
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause() {
        Gdx.app.debug(TAG, "pause()");
    }


    @Override
    public InputProcessor getInputProcessor() {
        return inputManager;//stage;
    }


    public void backToMainScreen() {
        Gdx.app.debug(TAG, "backToMainScreen()");
        game.setScreen(AbstractGameScreen.mainScreen);
    }

    // Each method returns a boolean in case you want to use this with
    // the InputMultiplexer to chain input processors.
    class InputKeyAdapter extends InputAdapter {
        // It will be called each frame before the call to render().
        @Override
        public boolean keyUp(int keyCode) {
            if (keyCode == Input.Keys.BACK) {
                backToMainScreen();
                return true;
            }
            return false;
        }
    }

}