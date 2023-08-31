package com.gammery.trizzel.view.animations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

// ALT NAME: SpriteAnimation
public interface SplashAnimation {

    public SplashAnimation init(float x, float y);

    public void render(SpriteBatch batch);

    public void update(float dt);

    public boolean isCompleted();

}
