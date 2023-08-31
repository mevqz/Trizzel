package com.gammery.trizzel;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.gammery.trizzel.screens.AbstractGameScreen;
import com.gammery.trizzel.screens.DirectedGame;
import com.gammery.trizzel.screens.GameScreen;
import com.gammery.trizzel.screens.InfoScreen;
import com.gammery.trizzel.screens.ScoreScreen;
import com.gammery.trizzel.screens.TrizzelScreen;
import com.gammery.trizzel.utils.Assets;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class TrizzelGame extends DirectedGame {
	
	public static GoogleServices googleServices;
	
	public static AdsService adsService;
	
	public TrizzelGame(GoogleServices googleServices, AdsService adsService) {
		TrizzelGame.googleServices = googleServices;
		TrizzelGame.adsService = adsService;
	}
	
	
	@Override
	public void create () {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Assets.instance.init(new AssetManager());
		
		AbstractGameScreen.mainScreen = new TrizzelScreen(this);
		AbstractGameScreen.gameScreen = new GameScreen(this);
		AbstractGameScreen.scoreScreen = new ScoreScreen(this);
		AbstractGameScreen.infoScreen = new InfoScreen(this);

		setScreen(AbstractGameScreen.mainScreen);
	}


}
