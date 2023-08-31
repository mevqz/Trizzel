package com.gammery.trizzel.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gammery.trizzel.screens.transitions.ScreenTransition;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public abstract class DirectedGame implements ApplicationListener {

	private boolean init;
	private FrameBuffer currFbo;
	private FrameBuffer nextFbo;
	private SpriteBatch batch;
	private AbstractGameScreen nextScreen;
	private AbstractGameScreen currScreen;
	private ScreenTransition screenTransition;
	private float t;

	public void setScreen(AbstractGameScreen screen) {
		setScreen(screen, null);
	}

	public void setScreen(AbstractGameScreen screen, ScreenTransition transition) {

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		if (!init) {
			currFbo = new FrameBuffer(Format.RGB888, w, h, false);
			nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
			batch = new SpriteBatch();
			init = true;
		}

		nextScreen = screen;
		nextScreen.show();
		nextScreen.resize(w, h);

		if (currScreen != null) {
			currScreen.pause();
		}

		nextScreen.pause();
		Gdx.input.setInputProcessor(null);
		screenTransition = transition;
		t = 0;
	}


	@Override
	public void render() {

		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1f/60f);

		if (nextScreen == null) {
			if (currScreen != null)
				currScreen.render(deltaTime);
		} else {

			float duration = 0;

			if (screenTransition != null) {
				duration = screenTransition.getDuration();
			}

			// update progress
			t = Math.min(t + deltaTime, duration);

			if (screenTransition == null || t >= duration) {
				if (currScreen != null)
					currScreen.hide();
				nextScreen.resume();

				Gdx.input.setInputProcessor(nextScreen.getInputProcessor());

				currScreen = nextScreen;
				nextScreen = null;
				screenTransition = null;
			} else {
				currFbo.begin();
				if (currScreen != null) {
					currScreen.render(deltaTime);
				}
				currFbo.end();
				nextFbo.begin();
				nextScreen.render(deltaTime);
				nextFbo.end();
				float alpha = t / duration;
				screenTransition.render(batch, currFbo.getColorBufferTexture(),
						nextFbo.getColorBufferTexture(), alpha);
			}
		}

	}


	@Override
	public void resize(int width, int height) {
		if (currScreen != null) {
			currScreen.resize(width, height);
		}
		if (nextScreen != null) {
			nextScreen.resize(width, height);
		}
	}


	@Override
	public void pause()	{
		if (currScreen != null) {
			currScreen.pause();
		}
	}

	@Override
	public void resume()	{
		if (currScreen != null) {
			currScreen.resume();
		}
	}

	@Override
	public void dispose() {
		if (currScreen != null) {
			currScreen.hide();
		}
		if (nextScreen != null) {
			nextScreen.hide();
		}

		if (init) {
			currFbo.dispose();
			currScreen = null;
			nextFbo.dispose();
			nextScreen = null;
			batch.dispose();
			init = false;
		}
	}
}
