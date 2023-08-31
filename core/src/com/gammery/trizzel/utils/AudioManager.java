package com.gammery.trizzel.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gammery.trizzel.model.GamePreferences;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class AudioManager {

	public static final AudioManager instance = new AudioManager();
	private AudioManager() { }
	
	private Music playingMusic;
	
	
	public void play(Sound sound) {
		play(sound, 1);
	}
	
	public void play(Sound sound, float volume) {
		play(sound, volume, 1);
	}
	
	public void play(Sound sound, float volume, float pitch) {
		play(sound, volume, pitch, 0);		
	}
	
	public void play(Sound sound, float volume, float pitch, float pan) {
		if (!GamePreferences.instance.sound) return;
		sound.play(volume, pitch, pan);
	}

	public void play(Music music) {
		stopMusic();
		playingMusic = music;
		if (GamePreferences.instance.sound) {
			music.setLooping(true);
			music.setVolume(1);
			music.play();
		}
	}
	
	public void stopMusic() {
		if (playingMusic != null) playingMusic.stop();
	}
		
	
	public void onSettingsUpdated() {
		if (playingMusic == null) return;

		if (GamePreferences.instance.sound) {
			if (!playingMusic.isPlaying()) playingMusic.play();
		} else {
			playingMusic.pause();
		}
	}
	
}
