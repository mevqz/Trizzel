package com.gammery.trizzel.desktop;

import com.badlogic.gdx.Gdx;
import com.gammery.trizzel.GoogleServices;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class DesktopGoogleServices implements GoogleServices {

	private static final String TAG = DesktopGoogleServices.class.getSimpleName();
	
	@Override
	public void signIn() {
		Gdx.app.debug(TAG, "signIn()");		
	}

	@Override
	public void signOut() {
		Gdx.app.debug(TAG, "signOut()");	
	}

	@Override
	public void rateGame() {
		Gdx.app.debug(TAG, "rateGame()");	
	}

	@Override
	public void submitScore(long score) {
		Gdx.app.debug(TAG, "submitScore("+score+")");	
	}

	@Override
	public void showScores() {
		Gdx.app.debug(TAG, "showScores()");	
	}

	@Override
	public boolean isSignedIn() {
		Gdx.app.debug(TAG, "isSignedIn()");	
		return false;
	}

	@Override
	public void showInfoDialog() {
		Gdx.app.debug(TAG, "showInfoDialog()");
	}
}
