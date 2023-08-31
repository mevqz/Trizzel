package com.gammery.trizzel.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gammery.trizzel.model.Piece;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class PieceFrame {

    private Piece piece;
    private Texture frame;
    private Vector2 dimension;
    private Vector2 framePosition;
    private Vector2 piecePosition;
    private static final float PIECE_SCALE = 0.5f;

    public PieceFrame(Texture bg, float x, float y) {
        frame = bg;

        framePosition = new Vector2(x, y);
        dimension = new Vector2(1f, 2.25f);
        piecePosition = new Vector2(framePosition.x + ((dimension.x - PIECE_SCALE) / 2),
                framePosition.y + ((dimension.y - PIECE_SCALE*3) / 2));
    }

    public void setPiece(Piece inPiece) {
        piece = inPiece;
    }

    public void update(float deltaTime) {

    }

    public void reset() {
        piece = null;
    }


    public void render(SpriteBatch batch) {

        batch.draw(frame,
                framePosition.x, framePosition.y,
                0, 0,
                dimension.x, dimension.y,
                1f, 1f,
                0,
                0, 0,
                frame.getWidth(), frame.getHeight(),
                false, false);

        if (piece != null)
            piece.renderAt(batch, piecePosition.x, piecePosition.y, PIECE_SCALE);

    }
}