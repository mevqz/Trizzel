package com.gammery.trizzel.view.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class SplashFade implements SplashAnimation {

    private Texture texture;
    private float posX, posY;
    private float duration;
    private float progress;
    private Vector2 scale, dimension;

    public SplashFade(Texture txre, float inDuration) {
        texture = txre;
        texture = new Texture("levelUp.png");
        duration = inDuration;
        dimension = new Vector2(6.84f, 1);
        scale = new Vector2(1, 1);
    }


    @Override
    public SplashAnimation init(float x, float y) {
        progress = 0;
        posX = x;
        posY = y;
        return this;
    }

    @Override
    public void render(SpriteBatch batch)
    {
        float alpha = Interpolation.fade.apply(progress * 2 / duration);
        if (progress > 0.5f)
            alpha = 1 - alpha;

        batch.setColor(1, 1, 1, alpha);

        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void update(float dt) {
        progress += dt;
    }

    @Override
    public boolean isCompleted() {
        return progress >= duration;
    }

}
