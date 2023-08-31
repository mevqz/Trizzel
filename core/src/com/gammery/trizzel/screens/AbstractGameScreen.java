package com.gammery.trizzel.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.gammery.trizzel.utils.Assets;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public abstract class AbstractGameScreen implements Screen 
{
	protected DirectedGame game;
	
	public static TrizzelScreen mainScreen;
	public static GameScreen gameScreen;
	public static ScoreScreen scoreScreen;
	public static InfoScreen infoScreen;
	
	public AbstractGameScreen(DirectedGame newGame) 	{
		game = newGame;
	}
	
	public abstract void render(float deltaTime);
	
	public abstract void resize(int width, int height);
	
	public abstract void show();
	
	public abstract void hide();
	
	public abstract void pause();
	
	public void resume() {
		Assets.instance.init(new AssetManager());
	}
	
	public void dispose() {
		Assets.instance.dispose();
	}
	
	public abstract InputProcessor getInputProcessor();
	
	
}
