package com.gammery.trizzel;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public interface GoogleServices {

	public void signIn();

	public void signOut();

	public void rateGame();

	public void submitScore(long score);

	public void showScores();

	public boolean isSignedIn();

	public void showInfoDialog();

}
