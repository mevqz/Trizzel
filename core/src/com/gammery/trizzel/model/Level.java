package com.gammery.trizzel.model;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Level {

	private final static int[] lockAmount = { 0, 0, 0, 2, 2, 2, 3 };
	
	public static final int MAX_LOCK_AMOUNT;
	
	static {
		
		int max = lockAmount[0];
		for (int val : lockAmount) {
			if (val > max) max = val;
		}
		
		MAX_LOCK_AMOUNT = max;
	}
		
	
	private final static int[] lockProbability = { 0, 0, 0, 15, 16, 17 }; //, 18, 19, 20 };
	
	// blocks required to level up
	private final static int[] goal = { 25 };
	
	private final static float[] fallingTime = 
		{ .75f, .70f, .65f, .60f, .55f, .50f, .48f, .46f, .44f, .42f, .40f, .38f, .36f, .33f };
	
	public static int lockAmount(int idx) {
		if (idx >= lockAmount.length) return lockAmount[lockAmount.length-1];
		return lockAmount[idx];
	}
	
	public static int lockProbability(int idx) {
		if (idx >= lockProbability.length) return lockProbability[lockProbability.length-1];
		return lockProbability[idx];
	}
	
	public static int goal(int idx) {
		if (idx >= goal.length) return goal[goal.length-1];
		return goal[idx];
	}
	
	public static float fallingTime(int idx) {
		if (idx >= fallingTime.length) return fallingTime[fallingTime.length-1];
		return fallingTime[idx];
	}
	
	public static int blocksToLevelUp(int idx) {
		return goal(idx);
	}
	
}