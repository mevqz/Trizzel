package com.gammery.trizzel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Block {

    private static final String TAG = Block.class.getName();

    enum Status {
        PIECE_FALLING, STANDING, EXPLODING, FADING, FALLING_AFTER_STANDING, COLLIDING, IMPLODE
    }

    // types
    public static final byte GREEN 		= 0;
    public static final byte RED 	 	= 1;
    public static final byte BLUE 		= 2;
    public static final byte ORANGE 	= 3;
    public static final byte YELLOW  	= 4;
    public static final byte VIOLET 	= 5;
    public static final byte BOMB 		= 6;
    public static final byte WILDCARD 	= 7;

    // amount of diff colors of blocks
    public static final byte TOTAL		= 6;


    private int locksLeft;
    private int type;
    private Vector2 position = new Vector2();
    private Status status;
    private float velocity;
    private float slideVelocity;
    private float slidePosition;
    private boolean slideAnimation;
    private float rotation;
    private Vector2 scale = new Vector2(1,1);
    private Vector2 origin = new Vector2(1,1);
    private float alphaColor = 1.0f;
    private float dyingTimeout;
    private boolean DEBUG_POSITION_SET;
    private TextureRegion textReg;
    private float collidingTimeout;
    private float collidingScale = 2;


    public Block(int newType) {
        type = newType;
        updateTexture();

        origin.x = 0.5f;
        origin.y = 0.5f;
    }


    public void decrementLocksLeft() {
        if (locksLeft > 0) {
            locksLeft--;
            updateTexture();
        }
    }


    public boolean isLocked() {
        return locksLeft > 0;
    }

    public void setLocks(int locks) {
        locksLeft = locks;
        updateTexture();
    }

    public boolean isStanding() {
        return status == Status.STANDING;
    }

    public void stand(int finalPositionY) {
        status = Status.STANDING;
        velocity = 0;
        position.y = finalPositionY;
        slideAnimation = false;
    }


    public void fallAfterStand(float newVelocity) {
        if (newVelocity > 0) {
            Gdx.app.error(TAG, "[ERROR] fallAfterStand: newVelocity > 0");
        }
        status = Status.FALLING_AFTER_STANDING;
        velocity = newVelocity;
    }

    public void fallWithPiece(float newVelocity) {
        if (newVelocity > 0) {
            Gdx.app.error(TAG, "[ERROR] setVelocity: newVelocity > 0");
        }
        status = Status.PIECE_FALLING;
        velocity = newVelocity;
    }


    public void explode() {
        status = Status.EXPLODING;
        dyingTimeout = Constants.BLOCK_EXPLODE_ANIM_DURATION;
    }



    public void colliding() {
        /*alphaColor = 0;
        collidingTimeout = Constants.COLLIDING_ANIM_DURATION;
        status = Status.COLLIDING;*/
    }


    public void fade() {
        status = Status.FADING;
        dyingTimeout = .5f;
    }


    public void implode() {
        status = Status.IMPLODE;
        dyingTimeout = .5f;
    }


    public boolean isDeath() {
        return dyingTimeout <= 0;
    }

    public int getType() {
        return type;
    }

    public void setPositionX(float x) {
        position.x = x;
    }

    public void setPositionY(float y) {
        position.y = y;
        DEBUG_POSITION_SET = true;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        DEBUG_POSITION_SET = true;
    }


    public void update(float dt) {

        switch (status) {
            case PIECE_FALLING:
            case FALLING_AFTER_STANDING:
                processFalling(dt);
                break;

            case EXPLODING:
                processExploding(dt);
                break;

            case FADING:
                processFading(dt);
                break;

            case IMPLODE:
                processImplode(dt);
                break;

            case COLLIDING:
                processColliding(dt);
                break;
        }
    }

    public void renderAt(SpriteBatch batch, float x, float y, float scale) {
        batch.draw(textReg, x, y, scale, scale);
    }


    public void render(SpriteBatch batch) {
        if (!DEBUG_POSITION_SET) Gdx.app.debug(TAG, "[ERROR] render: position is not set");

        float originalColor = batch.getPackedColor();
        
        if (status == Status.EXPLODING || status == Status.FADING || status == Status.IMPLODE) {// || status == Status.COLLIDING) {
            batch.setColor(1, 1, 1, alphaColor);
        }

        float posY = (slideAnimation ? slidePosition : position.y);
        
        if (status == Status.PIECE_FALLING) posY += 1;

        batch.draw(textReg, position.x, posY, origin.x, origin.y,
                1, 1, scale.x, scale.y, rotation);

        if (status == Status.COLLIDING) {
            batch.setColor(1, 1, 1, alphaColor);
            batch.draw(textReg, position.x, posY, origin.x, origin.y,
            		1, 1, collidingScale, collidingScale, rotation);
        }

        if (status == Status.EXPLODING || status == Status.COLLIDING || status == Status.FADING || status == Status.IMPLODE) {
            batch.setColor(1, 1, 1, 1);
        }
        batch.setColor(originalColor);
    }


    public boolean canChain(Object obj) {
        if (obj instanceof Block) {
            Block block = (Block) obj;
            return (getType() == block.getType() ||
                    //block.getType() == Block.WILDCARD;
                    getType() == Block.WILDCARD) &&
                    !isLocked() && !block.isLocked();
        }

        return false;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Block) {
            Block block = (Block) obj;
            return getType() == block.getType();
        }

        return false;
    }


    public void rotateSliding(float slideVelocity, int displacement) {
        slidePosition = position.y;
        position.y += displacement;
        this.slideVelocity = slideVelocity;
        slideAnimation = true;
    }


    public void turnIntoBomb() {
        type = BOMB;
        locksLeft = 0;
        updateTexture();
    }

    private void updateTexture() {
        textReg = Assets.instance.blocks2.get(type, locksLeft);
    }


    public void interpolate(float alpha) {
        //position = prevPosition.lerp(position, alpha);
        //position.x = (float) (currPosition.x * alpha + prevPosition.x * (1.0 - alpha));
        //lerpPosY = (float) (position.y * alpha + prevPosition.y * (1.0 - alpha));
    }

    private void processFalling(float dt) {
        position.y += velocity * dt;

        if (slideAnimation) { // rotation slide
            slidePosition += slideVelocity * dt;
            if (slideVelocity < 0) { // is going down
                if (slidePosition < position.y) slideAnimation = false;
            } else { // is going up
                if (slidePosition > position.y) slideAnimation = false;
            }
        }
    }

    private void processExploding(float dt) {
        rotation = (200 * dt + rotation) % 360; // 200º sexagesimals per second

        alphaColor -= dt / (Constants.BLOCK_EXPLODE_ANIM_DURATION + .05f);
        if (alphaColor < 0) alphaColor = 0;
        scale.x += dt * 3;	// 3 times per second
        scale.y += dt * 3;
        dyingTimeout -= dt;
    }


    private void processFading(float dt) {
        if (alphaColor > 0) alphaColor -= dt / .65f;
        dyingTimeout -= dt;
    }


    private void processImplode(float dt) {
        rotation = (180 * dt + rotation) % 360; // 180º sexagesimals per second

        alphaColor -= dt / (2 + .15f);
        scale.x -= dt * 2;
        scale.y -= dt * 2;
        dyingTimeout -= dt;
    }

    private void processColliding(float dt) {
        collidingTimeout -= dt;
        alphaColor += dt / Constants.COLLIDING_ANIM_DURATION;
        collidingScale -= dt * 2;
        if (collidingTimeout <= 0) status = Status.STANDING;
    }

}
