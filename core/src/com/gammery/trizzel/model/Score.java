package com.gammery.trizzel.model;

import com.badlogic.gdx.utils.Array;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Score {

	public int score;
	public int blocks;
	public int level;
	public int combos;
	public int largestComboChain;
	public float time;

	public Score() {
		reset();
	}

	public boolean blocksDestroyed(int destroyed, int chainComboCounter) 
	{
		blocks += destroyed;
		score += ((destroyed * 10) * chainComboCounter);
		int prevLevel = level;
		level = (int) (blocks / Level.blocksToLevelUp(level));
		return prevLevel != level;
	}
	
	public void reset() {
		score = 0;
		blocks = 0;
		level = 0;
		combos = 0;
		largestComboChain = 0;
		time = 0;
	}
	
	public void droppedAt(int posY) {
		//this.score = this.score + ((posY * posY * posY) / 40);	
	}
}