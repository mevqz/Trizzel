package com.gammery.trizzel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.AudioManager;
import com.gammery.trizzel.utils.Constants;
import com.gammery.trizzel.view.PieceFrame;

import java.util.Iterator;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Board {
    
    enum Status {
        PIECE_FALLING, BLOCKS_EXPLODING, BLOCKS_FALLING,
        GAME_OVER, NONE_CURRENT_PIECE,
        ENDING,
    }

    public HUD hud = new HUD();

    private static final String TAG = Board.class.getSimpleName();
    private static final int MIN_CHAIN_BLOCK = 3;


    private float blocksExplodingTimeout;
    private float blocksFallingTimeout;
    private float gameEndingTimeout;
    private Piece currentPiece;
    private Block[][] board;
    private Score score;
    private PieceFactory pFactory = PieceFactory.getInstance();
    private float nextMoveDown;
    private float fallingTime;

    private Status status;
    private Array<Block> fallingBlocks = new Array<Block>();
    private Array<Block> removedBlocks = new Array<Block>();

    private Piece holdedPiece;
    private boolean canHold;
    private int chainComboCounter;
    private int bombsLeft;
    private int nextBomb;

    private PieceFrame frmNext = new PieceFrame(Assets.instance.frmNextPiece, 6f, 8);//6.25f, 6);
    private PieceFrame frmHold = new PieceFrame(Assets.instance.frmHoldPiece, -1.f, 8);//6.25f, 3);

    private boolean blocksFalling;
    private boolean gameEndingStarted;

    public Board(int width, int height)	{
        this(width, height, 0);
    }

    public Board(int width, int height, int trashLines) {

        score = new Score();
        board = new Block[height][width];

        if (trashLines > 0) createGarbageLines(trashLines);
    }


    public void init() {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                eraseSlot(i, j);
            }
        }

        status = Status.NONE_CURRENT_PIECE;
        currentPiece = null;
        score.reset();
        pFactory.reset();
        setPieceSettings();
        fallingBlocks.clear();
        removedBlocks.clear();
        //animations.clear();

        blocksExplodingTimeout = Constants.BLOCKS_EXPLODING_DELAY;
        blocksFallingTimeout = Constants.BLOCK_FALL_TIMEOUT;
        gameEndingTimeout = Constants.GAME_ENDING_TIMEOUT;
        chainComboCounter = 0;
        holdedPiece = null;
        canHold = true;

        AudioManager.instance.play(Assets.instance.sounds.music);

        hud.resetHud();

        bombsLeft = Constants.INITIAL_BOMBS;
        nextBomb = Constants.LEVELS_TO_GAIN_BOMB;

        frmNext.reset();
        frmHold.reset();

        blocksFalling = false;
        gameEndingStarted = false;
    }


    public boolean isGameOver() {
        return status == Status.GAME_OVER;
    }


    private void processGameOver(float deltaTime) {
        score.time -= deltaTime;
    }


    private void processGameEnding(float deltaTime) {
        score.time -= deltaTime;

        if (currentPiece != null) currentPiece.update(deltaTime);

        if (!gameEndingStarted) {
            gameEndingStarted = true;
        }

        gameEndingTimeout -= deltaTime;
        if (gameEndingTimeout <= 0) {
            status = Status.GAME_OVER;
        }
    }


    public void update(float deltaTime) {
        score.time += deltaTime;

        testBoardState();

        // Updating Blocks (in board)
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (isSlotOcuppy(i, j))
                    getBlock(i, j).update(deltaTime);
            }
        }

        Iterator<Block> iter = removedBlocks.iterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.isDeath()) {
                iter.remove();
            } else {
                block.update(deltaTime);
            }
        }

        switch (status) {

            case NONE_CURRENT_PIECE:
                processNoneCurrentPiece();
                break;

            case PIECE_FALLING:
                processPieceFalling(deltaTime);
                break;

            case BLOCKS_EXPLODING:
                processBlocksExploding(deltaTime);
                break;

            case BLOCKS_FALLING:
                processBlocksFalling(deltaTime);
                break;

            case ENDING:
                processGameEnding(deltaTime);
                break;

            case GAME_OVER:
                processGameOver(deltaTime);
                break;
        }
    }



    private void processPieceFalling(float deltaTime) {
        testBoardState();

        currentPiece.update(deltaTime);
        nextMoveDown -= deltaTime;
        if (nextMoveDown <= 0) {
            nextMoveDown += fallingTime;
            if (!moveDown() && status == Status.PIECE_FALLING) {
                status = Status.NONE_CURRENT_PIECE;
            }
        }
    }


    private void processNoneCurrentPiece() {
        chainComboCounter = 0;
        setAsFallingPiece(pFactory.next());
    }


    private void setAsFallingPiece(Piece piece)
    {
        piece.setFallingVelocity(-(1.0f / fallingTime));
        boolean drawResult = drawNewPiece(piece);
        if (drawResult) {
            nextMoveDown = 0;
            status = Status.PIECE_FALLING;
            frmNext.setPiece(pFactory.peekNext());
        } else {
            status = Status.ENDING;
        }
    }


    private void processBlocksExploding(float deltaTime) {
        blocksExplodingTimeout -= deltaTime;

        if (blocksExplodingTimeout <= 0) {
            blocksExplodingTimeout = Constants.BLOCKS_EXPLODING_DELAY;
            status = Status.BLOCKS_FALLING;
        }
    }


    private void processBlocksFalling(float deltaTime) {
        int emptySlotsCol = 0;

        for (int i = 0; i < getWidth() && !blocksFalling; i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (!isSlotOcuppy(j, i)) {

                    int emptySlots = 1;
                    while (j+emptySlots < getHeight() && !isSlotOcuppy(j+emptySlots, i)) {
                        emptySlots++;
                    }

                    emptySlotsCol += emptySlots;
                    for (int k = j + emptySlots; k < getHeight(); k++) {
                        if (isSlotOcuppy(k, i)) {
                            
                            setSlot(getBlock(k, i), k - emptySlots, i);
                            getBlock(k - emptySlots, i).fallAfterStand(-(emptySlotsCol / Constants.BLOCK_FALL_TIMEOUT));
                            eraseSlot(k, i);
                            fallingBlocks.add(getBlock(k - emptySlots, i));

                        }
                    }
                }
            }
            emptySlotsCol = 0;
        }

        if (fallingBlocks.size == 0) {
            status = Status.NONE_CURRENT_PIECE;
            return;
        }

        blocksFalling = true;
        blocksFallingTimeout -= deltaTime;

        if (blocksFallingTimeout <= 0) {
            blocksFallingTimeout = Constants.BLOCK_FALL_TIMEOUT;
            blocksFalling = false;

            for (Block block : fallingBlocks) {
                
                for (int i = 0; i < getHeight(); i++) {
                    for (int j = 0; j < getWidth(); j++) {
                        if (block == getBlock(i, j)) {
                            block.stand(i);
                        }
                    }
                }

            }
            fallingBlocks.clear();

            checkLines();
        }

    }


    public Score getScore() {
        return score;
    }

    public boolean hasPieceInPlay() {
        return currentPiece != null;
    }


    private int getHeight() {
        return board.length;
    }


    private int getWidth() {
        return board[0].length;
    }

    
    private void setSlot(Block block, int row, int col) {
        board[getHeight()-1 - row][col] = block;
    }


    public void eraseSlot(int row, int col) {
        setSlot(null, row, col);
    }


    public Block getBlock(int row, int col) {
        return board[getHeight()-1 - row][col];
    }


    public boolean isSlotOcuppy(int row, int col) {
        return getBlock(row, col) != null;
    }


    public boolean isSlotOcuppy(int row, int col, boolean checkBounds) {
        if (checkBounds && row < 0 || row >= getHeight() || col < 0 || col >= getWidth())
            return false;

        return getBlock(row, col) != null;
    }


    private void centerPiecePosition(Piece p) {
        p.setPosX(getWidth() / 2);
        p.setPosY(getHeight() - 1);
    }

    public boolean drawNewPiece(Piece piece) {
        if (currentPiece != null) {
            Gdx.app.debug(TAG, "drawNewPiece(): currentPiece != null");
        }

        centerPiecePosition(piece);
        boolean canBeDrawn = canBeDrawn(piece);
        if (canBeDrawn) currentPiece = piece;
        return canBeDrawn;
    }


    private boolean draw(Piece piece) {
        int posY = piece.getPosY();
        int posX = piece.getPosX();

        if (!canBeDrawn(piece)) {
            Gdx.app.debug(TAG, "draw() before return false #1 (cannotBeDraw)");
            return false;
        }

        for (int i = 0; i < piece.getHeight(); i++) {
            if (posY+i < getHeight()) {
                setSlot(piece.getBlock(i), posY+i, posX);
            } else {
                Gdx.app.debug(TAG, "draw() before return false #2");
                return false;
            }
        }

        return true;
    }


    public boolean moveDown() {
        if (currentPiece == null) {
            Gdx.app.debug(TAG, "Board.moveDown(): returns null (currentPiece == null)");
            return false;
        }

        if (canMoveDown(currentPiece)){
            currentPiece.setPosY(currentPiece.getPosY() - 1);
            return true;
        }

        pieceTouchGround();
        return false;
    }


    private void pieceTouchGround() {
        if (currentPiece.getBlock(0).getType() == Block.BOMB
                && isSlotOcuppy(currentPiece.getPosY() - 1, currentPiece.getPosX(), true)) {

            Block target = getBlock(currentPiece.getPosY() - 1, currentPiece.getPosX());
            for (int i = 0; i < getHeight(); i++) {
                for (int j = 0; j < getWidth(); j++) {
                    if (isSlotOcuppy(i, j) && getBlock(i, j).equals(target)) {
                        getBlock(i, j).implode();
                        removedBlocks.add(getBlock(i, j));
                        eraseSlot(i, j);
                    }
                }
            }

            status = Status.BLOCKS_EXPLODING;
            currentPiece.touchGround();
            currentPiece = null;
            return;

        }

        decrementLocks(currentPiece);
        currentPiece.touchGround();
        currentPiece = null;
        canHold = true;

        checkLines();
    }


    private boolean canMoveDown(Piece piece) {
        if (piece.getPosY()-1 < 0) return false;

        if (isSlotOcuppy(piece.getPosY()-1, piece.getPosX())) {
            return false;
        }

        return true;
    }


    public void moveLeft() {
        if (currentPiece == null) {
            Gdx.app.debug(TAG, "Board.moveLeft(): currentPiece == null");
            return;
        }

        if (canMoveLeft(currentPiece)){
            Gdx.app.debug(TAG, "moveLeft(): moviendo a la izq");
            currentPiece.setPosX(currentPiece.getPosX() - 1);
        } else {
            Gdx.app.debug(TAG, "p.X: " + currentPiece.getPosX());
        }
    }


    private boolean canMoveLeft(Piece piece) {
        int posY = piece.getPosY();
        int posX = piece.getPosX();

        if (posX - 1 < 0) return false;

        for (int i = 0; i < piece.getHeight(); i++) {
            if (isSlotOcuppy(posY+i, posX-1, true)) {
                return false;
            }
        }

        return true;
    }


    public void moveRight() {
        if (currentPiece == null) {
            Gdx.app.debug(TAG, "moveRight(): currentPiece == null");
            return;
        }

        if (canMoveRight(currentPiece)) {
            Gdx.app.debug(TAG, "moveRight(): moviendo a la dcha");
            currentPiece.setPosX(currentPiece.getPosX() + 1);
        }
    }


    private boolean canMoveRight(Piece piece) {
        int posY = piece.getPosY();
        int posX = piece.getPosX();

        if (posX + 1 >= getWidth()) return false;

        for (int i = 0; i < piece.getHeight(); i++) {
            if (isSlotOcuppy(posY+i, posX+1, true)) {
                return false;
            }
        }

        return true;
    }


    private boolean canBeDrawn(Piece piece) {
        return canBeDrawnAt(piece, piece.getPosX(), piece.getPosY(), false);
    }

    
    private boolean canBeDrawnAt(Piece piece, int posX, int posY, boolean checkBoardBounds) {
        
        if (checkBoardBounds && piece.getHeight() + posY > getHeight()) {
            return false;
        }

        if (posX < 0 || posX >= getWidth() || posY < 0 || posY >= getHeight()) {
            return false;
        }

        for (int i = 0; i < piece.getHeight(); i++) {
            if (posY+i < getHeight()) {
                if (isSlotOcuppy(posY+i, posX)) {
                    return false;
                }
            }
        }

        return true;
    }


    public void rotatePiece() {
        testBoardState();
        if (currentPiece == null) {
            Gdx.app.debug(TAG, "rotatePiece(): currentPiece == null");
            return;
        }

        currentPiece.rotate();
    }


    public void drawBoard() {
        System.out.println("\n\n");
        for (int i = getHeight()-1; i >= 0; i--){
            for (int j = 0; j < getWidth(); j++)
                System.out.print(" " + (isSlotOcuppy(i, j)? ""+getBlock(i,j).getType():"?") + " ");
            System.out.println("");
        }
    }

    private void createGarbageLines(int lines) { }

    com.badlogic.gdx.graphics.Texture cell = new com.badlogic.gdx.graphics.Texture("test.png");

    public void render(SpriteBatch batch) {
        testBoardState();

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                batch.draw(cell, j, i, 1, 1);
            }
        }

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (isSlotOcuppy(i, j)) {
                    getBlock(i, j).render(batch);
                }
            }
        }

        if (currentPiece != null) currentPiece.render(batch);

        for (Block block : removedBlocks) {
            block.render(batch);
        }

        frmNext.render(batch);
        frmHold.render(batch);

    }


    public boolean clickAt(int x, int y) {

        if (currentPiece == null || status != Status.PIECE_FALLING) {
            Gdx.app.debug(TAG, "clickAt(): returns false " +
                    "(currentPiece == null || status != Status.PIECE_FALLING)");
            return false;
        }

        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return false;
        }

        if (y > getHeight() - currentPiece.getHeight()) {
            y = getHeight() - currentPiece.getHeight();
        }

        if (canBeDrawnAt(currentPiece, x, y, true)) {
            int originY = currentPiece.getPosY();
            currentPiece.setPosX(x);
            currentPiece.setPosY(y);
            while (canMoveDown(currentPiece)) {
                currentPiece.setPosY(currentPiece.getPosY() - 1);
            }
            nextMoveDown = 0;
            score.droppedAt(originY);
        }

        return true;
    }


    public void checkLines() {

        boolean blocksDestroyed = false;
        int chainCounter;

        // Horizontal check
        chainCounter= 1;
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth() - (MIN_CHAIN_BLOCK - 1) ; j++) {
                if (!isSlotOcuppy(i, j))
                    continue;

                Block block = getBlock(i, j);
                while (j + chainCounter < getWidth() && isSlotOcuppy(i, j + chainCounter)
                        && getBlock(i, j + chainCounter).canChain(block)) {
                    chainCounter++;
                }
                if (chainCounter >= MIN_CHAIN_BLOCK) {
                    blocksDestroyed = true;
                    for (int x = 0; x < chainCounter; x++) {
                        Block b = getBlock(i, j + x);
                        b.explode();
                        if (!removedBlocks.contains(b, true)) {
                            removedBlocks.add(b);
                        } //else { crossChain = true; }  else {
                    }
                }
                j += chainCounter - 1;
                chainCounter = 1;
            }
        }

        // Vertical check
        chainCounter = 1;
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight() - (MIN_CHAIN_BLOCK - 1) ; j++) {
                if (!isSlotOcuppy(j, i))
                    continue;

                Block block = getBlock(j, i);
                while (j + chainCounter < getHeight() && isSlotOcuppy(j + chainCounter, i)
                        && getBlock(j + chainCounter, i).canChain(block)) {
                    chainCounter++;
                }
                if (chainCounter >= MIN_CHAIN_BLOCK) {
                    blocksDestroyed = true;
                    // chain from j to j+chainCounter
                    for (int x = 0; x < chainCounter; x++) {
                        Block b = getBlock(j + x, i);
                        b.explode();
                        if (!removedBlocks.contains(b, true)) {
                            removedBlocks.add(b);
                        } //else { crossChain = true; }
                    }
                }
                j += chainCounter - 1;
                chainCounter = 1;
            }
        }


        // Diagonal topLeft-to-bottomRight check
        chainCounter= 1;
        int xInit = 0;
        int yInit = getHeight() - (MIN_CHAIN_BLOCK - 1);

        while (true) {

            for (int k = 0; xInit + k < getWidth() && yInit + k < getHeight(); k++) {
                if (!isSlotOcuppy(yInit + k, xInit + k))
                    continue;

                Block block = getBlock(yInit + k, xInit + k);
                while (isSlotOcuppy(yInit + k + chainCounter, xInit + k + chainCounter, true)
                        && getBlock(yInit + k + chainCounter, xInit + k + chainCounter).canChain(block)) {
                    chainCounter++;
                }
                if (chainCounter >= MIN_CHAIN_BLOCK) {
                    blocksDestroyed = true;
                    // chain from j to j+chainCounter
                    for (int x = 0; x < chainCounter; x++) {
                        Block b = getBlock(yInit + k + x, xInit + k + x);
                        b.explode();
                        if (!removedBlocks.contains(b, true)) {
                            removedBlocks.add(b);
                        } //else { crossChain = true; }
                    }
                }
                k += chainCounter - 1;
                chainCounter = 1;
            }
            
            if (yInit == 0) {
                xInit++;
                if (xInit >= getWidth() - (MIN_CHAIN_BLOCK - 1)) {
                    break;
                }
            } else {
                yInit--;
            }
        }

        
        chainCounter= 1;
        xInit = getWidth() - 1;
        yInit = getHeight() - (MIN_CHAIN_BLOCK - 1);

        while (true) {

            for (int k = 0; xInit - k >= 0 && yInit + k < getHeight(); k++) {
                if (!isSlotOcuppy(yInit + k, xInit - k))
                    continue;

                Block block = getBlock(yInit + k, xInit - k);
                while (isSlotOcuppy(yInit + k + chainCounter, xInit - k - chainCounter, true)
                        && getBlock(yInit + k + chainCounter, xInit - k - chainCounter).canChain(block)) {
                    chainCounter++;
                }
                if (chainCounter >= MIN_CHAIN_BLOCK) {
                    blocksDestroyed = true;
                    // chain from j to j+chainCounter
                    for (int x = 0; x < chainCounter; x++) {
                        Block b = getBlock(yInit + k + x, xInit - k - x);
                        b.explode();
                        if (!removedBlocks.contains(b, true)) {
                            removedBlocks.add(b);
                        } //else { crossChain = true; }
                    }
                }
                k += chainCounter - 1;
                chainCounter = 1;
            }

            if (yInit == 0) {
                xInit--;
                if (xInit < MIN_CHAIN_BLOCK - 1 ) {
                    break;
                }
            } else {
                yInit--;
            }
        }

        if (blocksDestroyed) {
            for (Block b : removedBlocks) {
                for (int i = 0; i < getHeight(); i++) {
                    for (int j = 0; j < getWidth(); j++) {
                        if (getBlock(i, j) == b) {
                            eraseSlot(i, j);
                        }
                    }
                }
            }
            chainComboCounter++;
            boolean levelUp = score.blocksDestroyed(removedBlocks.size, chainComboCounter);
            if (levelUp) {
                levelUp();
            }

            if (removedBlocks.size > 0) {
                hud.setScore(score.score);
            }

            playSoundChainCombo(chainComboCounter);
            status = Status.BLOCKS_EXPLODING;

        } else {
            status = Status.NONE_CURRENT_PIECE;

            if (chainComboCounter > 1) {
                chainComboFinish(chainComboCounter);
            }
            chainComboCounter = 0;
        }
    }


    public void hold() {

        testBoardState();

        if (status != Status.PIECE_FALLING || !canHold) return;

        if (holdedPiece != null) {
            /* swap between current and holded */
            Piece tmp = currentPiece;
            currentPiece = holdedPiece;
            holdedPiece = tmp;

            setAsFallingPiece(currentPiece);
            canHold = false;
        } else {
            holdedPiece = currentPiece;
            status = Status.NONE_CURRENT_PIECE;
            canHold = false;
        }

        frmHold.setPiece(holdedPiece);
    }


    private void levelUp() {
        //animations.add(SplashAnimations.levelUp.init(1,8));
        hud.setLevel(score.level);
        hud.levelUpShow();
        nextBomb--;
        if (bombsLeft < Constants.MAXS_BOMBS && nextBomb == 0) {
            nextBomb = Constants.LEVELS_TO_GAIN_BOMB;
            bombsLeft++;
            hud.bombGained(bombsLeft);
        }
        setPieceSettings();
    }


    private void setPieceSettings() 	{
        pFactory.setLockAmount(Level.lockAmount(score.level));
        pFactory.setLockProbability(Level.lockProbability(score.level));
        fallingTime = Level.fallingTime(score.level);
    }


    private void playSoundChainCombo(int chainComboCounter) {
        Gdx.app.debug(TAG, "playSoundChainCombo: " + chainComboCounter);
        hud.chainComboFinish(chainComboCounter);
		float pitch = 1 + ((chainComboCounter-1) * 0.05f);
		if (pitch > 1.3f) {
            pitch = 1.3f;
        }
		AudioManager.instance.play(Assets.instance.sounds.combo1, 1f, pitch);
    }


    private void chainComboFinish(int chainComboCounter)  {
        Gdx.app.debug(TAG, "chainComboFinish(): comboCounter = " + chainComboCounter);
        //hud.chainComboFinish(chainComboCounter);
    }


    public void useBomb() {
        if (status != Status.PIECE_FALLING || bombsLeft <= 0 || currentPiece.isBomb()) {
            return;
        }

        bombsLeft--;
        currentPiece.turnIntoBomb();
        hud.bombUsed(bombsLeft);
    }


    private void decrementLocks(Piece p) {

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (isSlotOcuppy(i, j)) {
                    Block b = getBlock(i, j);

                    if (b != p.getBlock(0) && b != p.getBlock(1) && b != p.getBlock(2))
                        b.decrementLocksLeft();
                }
            }
        }
    }


    public void drop() {

        testBoardState();

        if (currentPiece == null) {
            Gdx.app.debug(TAG, "drop(): currentPiece == null");
            return;
        }

        while (canMoveDown(currentPiece)) {
            currentPiece.setPosY(currentPiece.getPosY() - 1);
        }
    }



    public void testBoardState() {
        switch (status) {
            case PIECE_FALLING:
                if (currentPiece == null) {
                    throw new IllegalStateException("Board.status == PIECE_FALLING && currentPiece == null");
                }
                break;
        }
    }
}
