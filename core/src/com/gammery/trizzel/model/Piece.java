package com.gammery.trizzel.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Piece {

	private float dropVelocity;
	// board position (slot)
	private int posY = -1;
	private int posX = -1;
	private Block[] blocks;
	private boolean isBomb;

	public void interpolate(float alpha) {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].interpolate(alpha);
		}
	}

	public void slowDropVelocity(float factor) {
		dropVelocity *= factor;
	}

	public void reset() {
		posY = -1;
		posX = -1;
		setFallingVelocity(0);
	}

	public Piece(Block[] newBlocks) {
		blocks = newBlocks;
	}

	public void setFallingVelocity(float velocity) {
		dropVelocity = velocity;
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].fallWithPiece(velocity);
		}
	}

	public Block getBlock(int idx) {
		return blocks[idx];
	}

	public void update(float deltaTime) {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].update(deltaTime);
		}
	}

	public int getHeight() {
		return blocks.length;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int y) {
		posY = y;
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].setPositionY(posY+i);
		}
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int x) {
		posX = x;
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].setPositionX(posX);
		}
	}

	public void rotate() {
		if (getHeight() == 1) return;

		Block firstBlock = blocks[0];
		for (int i = 0; i < blocks.length - 1; i++) {
			blocks[i] = blocks[i+1];
			float distance = (dropVelocity * Constants.PIECE_ROTATE_TIME) + (-1.0f);
			float tsSpeed = distance / Constants.PIECE_ROTATE_TIME;
			blocks[i].rotateSliding(tsSpeed, -1);
		}
		blocks[blocks.length - 1] = firstBlock;

		float distance = (dropVelocity * Constants.PIECE_ROTATE_TIME) + (blocks.length - 1);
		float tsSpeed = distance / Constants.PIECE_ROTATE_TIME;
		firstBlock.rotateSliding(tsSpeed, blocks.length - 1);
	}

	public void render(SpriteBatch batch) {
		for (int i = 0; i < getHeight(); i++) {
			blocks[i].render(batch);
		}
	}

	public void renderAt(SpriteBatch batch, float x, float y, float scale) {
		for (int i = 0; i < getHeight(); i++) {
			blocks[i].renderAt(batch, x, y+(i*scale), scale);
		}
	}


	public void touchGround() {
		for (int i = 0; i < getHeight(); i++) {
			blocks[i].stand(posY+i);
			blocks[i].colliding();
		}
	}

	public String debug() {
		String s = "posX(" + posX + ")  // posY(" + posY + ")  // ";
		return s;
	}

	public void turnIntoBomb() {
		isBomb = true;
		for (int i = 0; i < getHeight(); i++) {
			blocks[i].turnIntoBomb();
		}
	}

	public boolean isBomb() {
		return isBomb;
	}

}
