package com.gammery.trizzel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gammery.trizzel.TrizzelGame;
import com.gammery.trizzel.model.GamePreferences;
import com.gammery.trizzel.screens.transitions.FadeTransition;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.AudioManager;
import com.gammery.trizzel.utils.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class TrizzelScreen extends AbstractGameScreen {

	private static final String TAG = TrizzelScreen.class.getName();
	private boolean debugEnabled = false;
	private static final float DEBUG_REBUILD_STAGE = 3.0f;
	private float debugRebuildStage = DEBUG_REBUILD_STAGE;

	private Stage stage;
	private int width;
	private int height;

	public TrizzelScreen(DirectedGame game) {
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

		Gdx.gl.glClearColor(Constants.BG_RED, Constants.BG_GREEN, Constants.BG_BLUE, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stack.add(title(height));
		stack.add(bottomPanel());

	}


	private Table title(int height) {
		Table layer = new Table();
		if (debugEnabled) layer.debug();

		float ratio = height/1184f;

		Image titleLeft = new Image(Assets.instance.skinGame.getDrawable("title_left"));
		titleLeft.setOrigin(titleLeft.getWidth()/2, titleLeft.getHeight()/2);
		layer.add(titleLeft).size(190*ratio,386*ratio);

		Image titleRight = new Image(Assets.instance.skinGame.getDrawable("title_right"));
		titleRight.setOrigin(titleRight.getWidth()/2, titleRight.getHeight()/2);
		layer.add(titleRight).size(476*ratio, 120*ratio);

		titleLeft.addAction(sequence(
			//Actions.moveTo(900, 135),
			//Actions.moveBy(25, 210, 0.5f, Interpolation.swingOut)
			Actions.moveBy(height/47f, height/5.6f, 0.5f, Interpolation.swingOut)
		));

		titleRight.addAction(sequence(
			//Actions.moveTo(335, 900),
			//Actions.delay(0.25f),
			//Actions.moveBy(-25, 170, 0.5f, Interpolation.swingOut)
			Actions.moveBy(-height/47f, height/7, 0.5f, Interpolation.swingOut)
		));

		return layer;
	}


	private Table bottomPanel() {

		Table layer = new Table();
		if (debugEnabled) layer.debug();
		int btnSize = height/10;

		Button.ButtonStyle bs1 = new Button.ButtonStyle();
		bs1.down = Assets.instance.skinGame.getDrawable("btnPlay");
		bs1.up = Assets.instance.skinGame.getDrawable("btnPlay");
		final Button btnPlay = new Button(bs1);
		layer.add(btnPlay).size(btnSize);
		btnPlay.addListener(new ChangeListener() {
			@Override public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnPlay (ChangeEvent)");
				btnPlay.addAction(Actions.sequence(
					Actions.scaleTo(1.25f, 1.25f, .15f, Interpolation.swingOut),
					Actions.scaleTo(1.f, 1.f, .15f, Interpolation.swingOut),
					Actions.run(new Runnable() {
						@Override public void run() {
							game.setScreen(AbstractGameScreen.gameScreen, FadeTransition.init(0.75f));
							//game.setScreen(AbstractGameScreen.gameScreen,
									//SlideTransition.init(1.25f, SlideTransition.LEFT, false, Interpolation.bounceOut));
						}
					})
				));
			}
		});

		btnPlay.setVisible(false);


		final Button btnScores = new Button(Assets.instance.skinGame, "btnScores");

		layer.add(btnScores).size(btnSize);
		btnScores.addListener(new ChangeListener() {
			@Override public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnScores (ChangeEvent)");
				btnScores.addAction(Actions.sequence(
					//Actions.scaleTo(.75f, .75f, .15f, Interpolation.bounceIn),
					//Actions.scaleTo(1.f, 1.f, .15f, Interpolation.bounceOut),
					Actions.scaleTo(1.25f, 1.25f, .15f, Interpolation.swingOut),
					Actions.scaleTo(1.f, 1.f, .15f, Interpolation.swingOut),
					Actions.run(new Runnable() {
						@Override public void run() {
							if (TrizzelGame.googleServices.isSignedIn()) {
								TrizzelGame.googleServices.showScores();
							} else {
								TrizzelGame.googleServices.signIn();
								Gdx.app.debug(TAG, "googleServices.isSignedIn(): FALSE");
							}
						}
					})
				));
			}
		});

		btnScores.setVisible(false);

		final Button btnSound = new Button(Assets.instance.skinGame, "btnSound");
		Gdx.app.debug(TAG, "btnSound : " + GamePreferences.instance.sound);
		btnSound.setChecked(GamePreferences.instance.sound);
		layer.add(btnSound).size(btnSize);//.expandX();
		btnSound.addListener(new ChangeListener() {
			@Override public void changed(ChangeEvent event, Actor actor) {
				btnSound.addAction(Actions.sequence(
					Actions.scaleTo(1.25f, 1.25f, .15f, Interpolation.swingOut),
					Actions.scaleTo(1.f, 1.f, .15f, Interpolation.swingOut)
				));
				Gdx.app.debug(TAG, "Button clicked: btnSound (ChangeEvent) // status: " + !btnSound.isChecked());
				GamePreferences.instance.sound = !GamePreferences.instance.sound; //!btnSound.isChecked();
				GamePreferences.instance.save();
				//AudioManager.instance.onSettingsUpdated();
			}
		});

		btnSound.setVisible(false);


		final Button btnHowToPlay = new Button(Assets.instance.skinGame, "btnInfo");

		layer.add(btnHowToPlay).size(btnSize);
		btnHowToPlay.addListener(new ChangeListener() {
			@Override public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug(TAG, "Button clicked: btnHowToPlay (ChangeEvent)");
				btnHowToPlay.addAction(Actions.sequence(
					Actions.scaleTo(1.25f, 1.25f, .15f, Interpolation.swingOut),
					Actions.scaleTo(1.f, 1.f, .15f, Interpolation.swingOut),
					Actions.run(new Runnable() {
						@Override public void run() {
							//game.setScreen(AbstractGameScreen.infoScreen, FadeTransition.init(0.75f));
							TrizzelGame.googleServices.showInfoDialog();
						}
					})
				));
			}
		});

		btnHowToPlay.setVisible(false);

		layer.bottom();
		layer.padBottom(height/10);

		btnPlay.setOrigin(btnSize/2, btnSize/2);
		btnScores.setOrigin(btnSize/2, btnSize/2);
		btnSound.setOrigin(btnSize/2, btnSize/2);
		btnHowToPlay.setOrigin(btnSize/2, btnSize/2);

		float actionMoveToY = 0;
		float actionMoveToX = (width / 2) - (btnSize/2);
		float actionPadBottom = (btnSize/4f);
		float actionVerticalSpace = (btnSize/8f);


		btnPlay.setTransform(true);
		actionMoveToY = actionPadBottom+(actionVerticalSpace*3)+(btnSize*3);
		btnPlay.addAction(Actions.sequence(
			Actions.scaleTo(0, 0),
			Actions.fadeOut(0),
			Actions.delay(.85f),
			Actions.show(),
			Actions.parallel(
				Actions.moveTo(actionMoveToX, actionMoveToY, 0.5f, Interpolation.swingOut),
				Actions.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
				Actions.alpha(1.0f, 0.5f))
		));

		btnSound.setTransform(true);
		actionMoveToY = actionPadBottom+(actionVerticalSpace*2)+(btnSize*2);
		btnSound.addAction(Actions.sequence(
				Actions.scaleTo(0, 0),
				Actions.fadeOut(0),
				Actions.delay(1.f),
				Actions.show(),
				Actions.parallel(
					Actions.moveTo(actionMoveToX, actionMoveToY, 0.5f, Interpolation.swingOut),
					Actions.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
					Actions.alpha(1.0f, 0.5f))
		));

		btnHowToPlay.setTransform(true);
		actionMoveToY = actionPadBottom+(actionVerticalSpace)+btnSize;
		btnHowToPlay.addAction(Actions.sequence(
				Actions.scaleTo(0, 0),
				Actions.fadeOut(0),
				Actions.delay(1.15f),
				Actions.show(),
				Actions.parallel(
					Actions.moveTo(actionMoveToX, actionMoveToY, 0.5f, Interpolation.swingOut),
					Actions.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
					Actions.alpha(1.0f, 0.5f))
		));

		btnScores.setTransform(true);
		actionMoveToY = actionPadBottom;
		btnScores.addAction(Actions.sequence(
			Actions.scaleTo(0, 0),
			Actions.fadeOut(0),
			Actions.delay(1.3f),
			Actions.show(),
			Actions.parallel(
				Actions.moveTo(actionMoveToX, actionMoveToY, 0.5f, Interpolation.swingOut),
				Actions.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
				Actions.alpha(1.0f, 0.5f))
		));

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

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		GamePreferences.instance.load();
		AudioManager.instance.stopMusic();
		rebuildStage();
	}

	@Override
	public void hide() {
		stage.dispose();
		stage = null;
	}

	@Override
	public void pause() {
		Gdx.app.debug(TAG, "pause()");
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}


	private Table middlePanel2() {
		FreeTypeFontGenerator generator =
				new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = 44;

			BitmapFont fontBig = generator.generateFont(parameter);
			LabelStyle labelStyleBig = new LabelStyle(fontBig, Color.WHITE);
			generator.dispose();

		Table layer = new Table();
		if (debugEnabled) layer.debug();
		layer.row().row();
		Label label = null;

		label = new Label("Score: " + 12323, labelStyleBig);
		layer.add(label).width(width/2).bottom().row();
		label = new Label("Level: " + 12, labelStyleBig);
		layer.add(label).width(width/2).bottom().row();
		label = new Label("Blocks: " + 343, labelStyleBig);
		layer.add(label).width(width/2).bottom().row();
		label = new Label("Time: 00:05:56", labelStyleBig);
		layer.add(label).width(width/2).bottom().row();


		return layer;
	}
}
