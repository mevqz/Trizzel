package com.gammery.trizzel.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public interface ScreenTransition {

	public float getDuration();
	
	public void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha);
}