package com.gammery.trizzel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gammery.trizzel.TrizzelGame;
import com.gammery.trizzel.model.Board;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class GameScreen extends AbstractGameScreen {

	private static final String TAG = GameScreen.class.getName();
	private boolean paused;
	private Board board;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private InputProcessor inputManager;
	private Stage stage;
	private Label msgPause;
	private boolean gameOver;
	private float gameOverDelay;

	public GameScreen(DirectedGame game) {
		super(game);
	}

	private void buildStage() {

		gameOver = false;
		gameOverDelay = 2f;
		board = new Board(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		stage = new Stage();
		stage.setViewport(new StretchViewport(width, height));

		stage.addActor(buildStageContent(height));
		stage.getViewport().update(width, height, true);

		inputManager = new InputMultiplexer(
			// Gesture listener (for Android)
			new GestureDetector(42, 0.5f, 2, 0.15f, new TouchGestureListener()),
			// Keyboard listener (and Android Back button)
			new InputKeyAdapter(),
			// Pause button listener
			stage
		);
	}

	private Container btnPauseContainer(int height) {
		Container container = new Container();
		Button button = new Button(Assets.instance.skinGame, "btnPlay");
		button.setChecked(true);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				pauseGame();
			}
		});
		container.setActor(button);
		int size = height / 16;
		container.bottom().left().size(size);
		int padding = height / 64;
		container.padBottom(padding).padLeft(padding);
		return container;
	}


	private Container lblPauseContainer() {
		Container container = new Container();

		msgPause = new Label("PAUSED", Assets.instance.lblStyleBigBig);
		container.setActor(msgPause);
		msgPause.addAction(Actions.hide());

		container.center();
		return container;
	}


	private Stack buildStageContent(int height) {
		Stack stack = new Stack();
		stack.setFillParent(true);
		stack.add(lblPauseContainer());
		stack.add(btnPauseContainer(height));

		return stack;
	}


	private void pauseGame() {
		paused = !paused;
		if (paused) {
			msgPause.addAction(Actions.show());
		} else {
			msgPause.addAction(Actions.hide());
		}
	}


	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(Constants.BG_RED, Constants.BG_GREEN, Constants.BG_BLUE, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!paused) {
			if (deltaTime > 0.25f) {
				deltaTime = 0.25f;
			}

			board.update(deltaTime);

			if (isGameOver()) {
				gameOverDelay  -= deltaTime;
				if (!gameOver) {
					gameOver = true;
					TrizzelGame.googleServices.submitScore(board.getScore().score);
					board.hud.showGameOver(board.getScore());
				}

			}
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		board.render(batch);
		batch.end();
		board.hud.render(deltaTime);

		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / (float)height) * (float)width;
		float centerOffset = (camera.viewportWidth - Constants.BOARD_WIDTH) / 2;
		camera.position.set(camera.viewportWidth / 2 - centerOffset, Constants.VIEWPORT_HEIGHT / 2 - 0, 0);
		camera.update();
	}

	@Override
	public void show() {
		buildStage();

		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_HEIGHT, Constants.VIEWPORT_HEIGHT);
		board.init();

		Gdx.input.setCatchBackKey(true);
	}

	// dispose
	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
		stage.dispose();
		stage = null;
	}

	@Override
	public void pause() {
		paused = true;
	}

	// solo se invoca en android
	@Override
	public void resume() {
		super.resume();
		paused = false;
		msgPause.addAction(Actions.hide());
	}

	public void backToMainScreen() {
		Gdx.app.debug(TAG, "backToMainScreen()");
		//game.setScreen(new TrizzelScreen(game), TransitionFade.init(0.75f));
		game.setScreen(AbstractGameScreen.mainScreen);
	}

	public boolean isGameOver() {
		return board.isGameOver();// || timeLeft <= 0;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputManager;
	}


	// Each method returns a boolean indicating if the event should be handed
	// to the next listener (false to hand it to the next listener, true otherwise).
	private class TouchGestureListener extends GestureAdapter {
		private Vector3 touchCoord = new Vector3();

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			if (paused) return false;

			if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX < 0) {
	                	board.hold();
	                	return true;
                } else if (velocityX > 0) {
                		board.useBomb();
                		return true;
                }
	         } else {
		        	 if (velocityY != 0) {
		            	 board.rotatePiece();
		            	 return true;
	             }
	         }
       	 	return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (paused) return false;

			if (gameOverDelay <= 0) {
				backToMainScreen();
				return true;
			}

			Gdx.app.debug(TAG, "ScreenTouch:: " + Gdx.input.getX() + " ; "+Gdx.input.getY());
			touchCoord.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchCoord);

			if (touchCoord.x < 0) touchCoord.x = -1;
	        board.clickAt((int)touchCoord.x, (int)touchCoord.y);

			return false;
		}
	}


	// Each method returns a boolean in case you want to use this with
	// the InputMultiplexer to chain input processors.
	class InputKeyAdapter extends InputAdapter {
		// It will be called each frame before the call to render().
		@Override
		public boolean keyUp(int keyCode) {
			if (keyCode == Keys.LEFT) {
				board.moveLeft(); 		return true;
			} else if (keyCode == Keys.RIGHT) {
				board.moveRight(); 		return true;
			} else if (keyCode == Keys.UP) {
				board.rotatePiece(); 	return true;
			} else if (keyCode == Keys.P) {
				pauseGame();
				Gdx.app.debug(TAG, "paused?: " + paused);
				return true;
			} else if (keyCode == Keys.CONTROL_LEFT) {
				board.hold(); 			return true;
			} else if (keyCode == Keys.J) {
				board.useBomb(); 		return true;
			} else if (keyCode == Keys.BACK) {
				backToMainScreen();
			}

			return false;
		}
	}

}
