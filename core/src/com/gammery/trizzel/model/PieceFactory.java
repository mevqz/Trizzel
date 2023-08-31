package com.gammery.trizzel.model;

import java.util.Random;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class PieceFactory {

	private long seed;
	private Random random;
	private Piece next;
	private int locksAmount;
	private int lockProbability;	// percentage
	private static PieceFactory pFactory = new PieceFactory();

	private static boolean TEST_CREATE = false;

	public static PieceFactory getInstance() {
		return pFactory;
	}

	private PieceFactory() {
		reset();
	}

	public void setLockAmount(int n) {
		locksAmount = n;
	}

	public void setLockProbability(int n) {
		lockProbability = n;
	}

	public void reset() {
		reset(true);
	}

	public void reset(boolean playNewMatch) {
		if (playNewMatch) {
			seed = new Random().nextLong();
		}
		random = new Random(seed);
		next = null;
		locksAmount = 0;
		lockProbability = 0;
	}


	public long getSeed() {
		return seed;
	}


	public Piece next() {

		Piece tmp = next;
		if (tmp == null) {
			tmp = TEST_CREATE ? testCreate() : create();
		}
		next = TEST_CREATE ? testCreate() : create();
		return tmp;
	}


	public Piece peekNext() {
		return next;
	}


	private Piece create() {
		Block[] blocks = new Block[3];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] =	new Block(random.nextInt(Block.TOTAL));
			if (locksAmount > 0 && random.nextInt(100) < lockProbability) {
				blocks[i].setLocks(locksAmount);
			}
		}
		return new Piece(blocks);
	}


	private Piece testCreate() {
		Block[] blocks = new Block[3];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = new Block(random.nextInt(1));
			if (locksAmount > 0 && random.nextInt(100) < lockProbability) {
				blocks[i].setLocks(locksAmount);
			}
			//blocks[2] = new Block(0);
		}
		return new Piece(blocks);
	}

}
