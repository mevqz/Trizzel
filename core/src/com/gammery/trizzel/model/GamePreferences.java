package com.gammery.trizzel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class GamePreferences {

    public static final GamePreferences instance = new GamePreferences();

    public boolean sound;
    public boolean rightHUD;
    public int gamesPlayed;

    private Preferences pref;

    private GamePreferences() {
        pref = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load()
    {
        sound = pref.getBoolean("sound", true);
        rightHUD = pref.getBoolean("rightHUD", true);
        gamesPlayed = pref.getInteger("gamesPlayed", 0);
    }

    public void save()
    {
        pref.putBoolean("sound", sound);
        pref.putBoolean("rightHUD", rightHUD);
        pref.putInteger("gamesPlayed", gamesPlayed);
        pref.flush();
    }

    public void toggleSound() {
        sound = !sound;
        save();
    }

    public boolean sound() {
        return sound;
    }
}
