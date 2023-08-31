package com.gammery.trizzel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gammery.trizzel.TrizzelGame;
import com.gammery.trizzel.model.GamePreferences;
import com.gammery.trizzel.model.Score;
import com.gammery.trizzel.screens.transitions.ScreenTransition;
import com.gammery.trizzel.screens.transitions.FadeTransition;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class ScoreScreen extends AbstractGameScreen {
	
	private static final String TAG = ScoreScreen.class.getName();
	private boolean debugEnabled = false;
	private static final float DEBUG_REBUILD_STAGE = 3.0f;
	private float debugRebuildStage = DEBUG_REBUILD_STAGE;
	
	private Stage stage;
	private int width;
	private int height;
	private Score score;
	private InputProcessor inputManager;

	public ScoreScreen(DirectedGame game) {
		super(game);
	}
	
	public void setScore(Score score) {
		this.score = score;
	}
	
	private void rebuildStage()	{

		if (stage == null) stage = new Stage();
				
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		stage.clear();
		
		stage.setViewport(new StretchViewport(width, height));

		Stack stack = new Stack();
		stack.setFillParent(true);
		stage.addActor(stack);
		
		stack.add(middlePanel2());
		
		inputManager = new InputMultiplexer(new InputKeyAdapter(), stage);
	}

	private Table background() {
		Table layer = new Table();
		if (debugEnabled) layer.debug();
		Image img = new Image(Assets.instance.skinGame, "bgViolet");
		layer.add(img);
		return layer;
	}
	
	private Table title() {
		byte[] bytesBanner = TrizzelGame.adsService.getBanner(1);
		Pixmap pixmap = new Pixmap(bytesBanner, 0, bytesBanner.length);
		Texture texture = new Texture(pixmap);
		
		Table layer = new Table();
		if (debugEnabled) layer.debug();

		Image img = new Image(texture);
		layer.add(img).width(width*.95f).height(height*0.25f);
		layer.top().padTop(width/8);
		return layer;
	}
	
	
	private Table middlePanel() {
		Table layer = new Table();
		if (debugEnabled) layer.debug();
		Label label = null;
		label = new Label("Score: " + score.score, Assets.instance.skinLibGDX);
		layer.add(label).width(width/2).height((width/2)/5.09f).row();
		label = new Label("Level: " + score.level, Assets.instance.skinLibGDX);
		layer.add(label).width(width/2).height((width/2)/5.09f).row();
		label = new Label("Blocks: " + score.blocks, Assets.instance.skinLibGDX);
		layer.add(label).width(width/2).height((width/2)/5.09f).row();
		int h = (int) score.time / 3600;
		int min = (int) score.time / 60;
		int seg = (int) score.time % 60;
		String time = String.format("%02d:%02d:%02d", h, min, seg);
		label = new Label("Time: " + time, Assets.instance.skinLibGDX);
		layer.add(label).width(width/2).height((width/2)/5.09f).row();
		
		Button btnContinue = new Button(Assets.instance.skinGame, "btnPlay");
		layer.add(btnContinue).width(width/2).height((width/2)/5.09f);
		btnContinue.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnContinue");
				ScreenTransition transition = FadeTransition.init(0.75f);
				game.setScreen(AbstractGameScreen.mainScreen, transition);
			}
		});

		return layer;
	}
	
	private Table bottomPanel()	{
		Table layer = new Table();
		if (debugEnabled) layer.debug();
		int btnSize = width/6;
		
		Button btnScores = new Button(Assets.instance.skinGame, "btnLeaderboard");
		layer.add(btnScores).size(btnSize).expandX().padLeft(width/6);
		btnScores.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnScores (ChangeEvent)");
			}
		});
		
		final Button btnSound = new Button(Assets.instance.skinGame, "btnSound");
		btnSound.setChecked(GamePreferences.instance.sound);
		layer.add(btnSound).size(btnSize).expandX();
		btnSound.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnSound (ChangeEvent) // status: " + !btnSound.isChecked());
				GamePreferences.instance.sound = !GamePreferences.instance.sound; //!btnSound.isChecked();
				GamePreferences.instance.save();
			}
		});
		
		layer.add(new Button(Assets.instance.skinGame, "btnHelp")).size(btnSize).expandX().padRight(width/6);
		layer.bottom();
		layer.padBottom(width/6);
		return layer;
	}
	
	
	@Override
	public void render(float deltaTime) 	{
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

		if (Gdx.input.isTouched()) {
			backToMainScreen();
		}
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
		return inputManager;
	}
	
	
	private Table middlePanel2() {

		Table layer = new Table();
		
		if (debugEnabled) {
			layer.debug();
		}
		
		layer.row().row();
		Label label = null;
		label = new Label("Score: " + score.score, Assets.instance.lblStyleBig);
		layer.add(label).width(width/2).bottom().center().row();
		label = new Label("Level: " + score.level, Assets.instance.lblStyleBig);
		layer.add(label).width(width/2).bottom().center().row();
		label = new Label("Blocks: " + score.blocks, Assets.instance.lblStyleBig);
		layer.add(label).width(width/2).bottom().center().row();
		int h = (int) score.time / 3600;
		int min = (int) score.time / 60;
		int seg = (int) score.time % 60;
		String time = String.format("%02d:%02d:%02d", h, min, seg);
		label = new Label("Time: " + time, Assets.instance.lblStyleBig);
		layer.add(label).width(width/2).bottom().center().row();
		
		
		Button btnContinue = new Button(Assets.instance.skinGame, "btnPlay");
		btnContinue.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnContinue");
				ScreenTransition transition = FadeTransition.init(0.75f);
				game.setScreen(AbstractGameScreen.mainScreen, transition);
			}
		});
		
		layer.row().row();
		label = new Label("(TAP TO CONTINUE)", Assets.instance.lblStyleBig);
		layer.add(label).width(width/2).bottom().center().row();


		return layer;
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
			if (keyCode == Keys.BACK) { 
				backToMainScreen();
				return true;
			}
			return false;
		}
	}
}