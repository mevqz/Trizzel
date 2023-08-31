package com.gammery.trizzel.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gammery.trizzel.GoogleServices;
import com.gammery.trizzel.TrizzelGame;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class AndroidLauncher extends AndroidApplication implements GoogleServices {

	private static final String TAG = AndroidLauncher.class.getSimpleName();

	private GameHelper gameHelper;
	private final static int requestCode = 1;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(true);
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInFailed(){ }

			@Override
			public void onSignInSucceeded(){ }
		};
		gameHelper.setup(gameHelperListener);


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TrizzelGame(this, new AndroidAdsService()), config);
	}

	@Override
	public void signIn() {

		Gdx.app.debug(TAG, "signIn()");
		try {
			runOnUiThread(new Runnable() 	{
				@Override
				public void run() 	{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e) {
			Gdx.app.log(TAG, "Log in failed: " + e.getMessage() + ".");
		}
	}


	@Override
	public void signOut() {

		Gdx.app.debug(TAG, "signOut()");

		try	{
			runOnUiThread(new Runnable()	{
				@Override
				public void run() {
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e) {
			Gdx.app.log(TAG, "Log out failed: " + e.getMessage() + ".");
		}
	}


	@Override
	public void rateGame() {
		Gdx.app.debug(TAG, "rateGame()");
	}

	@Override
	public void submitScore(long score) {

		Gdx.app.debug(TAG, "submitScore(" + score + ")");
		if (isSignedIn() == true) {
			Games.Leaderboards.submitScore(gameHelper.getApiClient(),
					getString(R.string.leaderboard_endless), score);
		}
	}


	@Override
	public void showScores() {

		Gdx.app.debug(TAG, "showScores()");

		if (isSignedIn() == true) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
					getString(R.string.leaderboard_endless)), requestCode);
		} else 	{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn() {
		Gdx.app.debug(TAG, "isSignedIn()");

		return gameHelper.isSignedIn();
	}


	@Override
	public void showInfoDialog() {
		MyDialogFragment.newInstance().show(getFragmentManager(), "MyDialogFragment");
	}


	@Override
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}
}