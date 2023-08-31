package com.gammery.trizzel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gammery.trizzel.utils.Assets;
import com.gammery.trizzel.utils.Constants;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class HUD {

	private static final String TAG = HUD.class.getSimpleName();

	private Stage stage;
	private Label hudScore;
	private Label hudLevel;
	private Table layerScoreNLevel;
	private Label msgLevelUp;
	private Label msgCombo;
	private Image hudBombs;
	private Label msgGameOver;

	private int msgComboPosY = (int)(Gdx.graphics.getHeight()*.5);
	private int msgLevelUpPosY = (int)(Gdx.graphics.getHeight()*.65);
	private float msgSlideY = (Gdx.graphics.getHeight()/12);

	private Container c = new Container();

	public HUD() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		hudScore = new Label("", Assets.instance.lblStyleMedium);
		hudLevel = new Label("", Assets.instance.lblStyleMedium);
		msgLevelUp = new Label(Constants.MSG_LEVEL_UP, Assets.instance.lblStyleBig);
		msgGameOver = new Label("GAME OVER", Assets.instance.lblStyleBigBig);
		msgCombo = new Label("", Assets.instance.lblStyleMedium);

		stage = new Stage();
		stage.setViewport(new StretchViewport(width, height));
		Stack stack = new Stack();
		stack.setFillParent(true);
		stage.addActor(stack);

		stack.add(addLevelNScore());
		stack.add(addNotificationMessages());
		stack.add(addHudBomb(height));

		alignRight();
		stage.getViewport().update(width, height, true);

		resetHud();
	}


	private Container addHudBomb(int height) {
		hudBombs = new Image(Assets.instance.blocks2.get(Block.BOMB, Constants.INITIAL_BOMBS));
		c.setActor(hudBombs);
		float size = height/14;
		c.size(size);
		float leftPadding = height / 64;
		c.top().left().pad(leftPadding);
		return c;
	}


	public void bombUsed(int bombsLeft) {
		hudBombs = new Image(Assets.instance.blocks2.get(Block.BOMB, bombsLeft));
		c.setActor(hudBombs);
		hudBombs.addAction(Actions.sequence(
			Actions.scaleTo(1.75f, 1.75f, .25f, Interpolation.swingOut),
			Actions.scaleTo(1.f, 1.f, .25f, Interpolation.swingOut)
		));
	}

	public void bombGained(int bombsLeft) {
		hudBombs = new Image(Assets.instance.blocks2.get(Block.BOMB, bombsLeft));
		c.setActor(hudBombs);
		hudBombs.addAction(Actions.sequence(
			Actions.scaleTo(1.5f, 1.5f, .25f, Interpolation.swingOut),
			Actions.scaleTo(1.f, 1.f, .25f, Interpolation.swingOut)
		));
	}

	private Table addNotificationMessages() {
		Table table = new Table();

		table.add(msgLevelUp).center().row();
		msgLevelUp.addAction(Actions.fadeOut(0));

		table.add(msgCombo).center().row();
		msgCombo.addAction(Actions.fadeOut(0));

		table.add(msgGameOver).center();
		msgGameOver.addAction(Actions.fadeOut(0));

		return table;
	}


	public void levelUpShow() {
		msgLevelUp.setY(msgLevelUpPosY);
		Vector2 v = new Vector2();
		msgLevelUp.stageToLocalCoordinates(v);
		Gdx.app.debug(TAG, "stageToLocalCoordinates: " + v);
		v = new Vector2();
		msgLevelUp.screenToLocalCoordinates(v);
		Gdx.app.debug(TAG, "screenToLocalCoordinates: " + v);
		msgLevelUp.clearActions();

		msgLevelUp.addAction(Actions.parallel(
			Actions.fadeIn(0),
			Actions.fadeOut(2.5f, Interpolation.fade),
			Actions.moveBy(0.f, msgSlideY, 2.5f)
		));
	}

	public void showGameOver(Score score) {

		msgGameOver.setY(msgLevelUpPosY);
		msgGameOver.addAction(Actions.parallel(
			Actions.fadeOut(0),
			Actions.fadeIn(2.5f, Interpolation.fade)
		));
	}


	public void chainComboFinish(int n) {

		if (n == 1) {
			return;
		}

		msgCombo.setText(String.format(Constants.MSG_COMBO, n));

		msgCombo.setY(msgComboPosY);
		msgCombo.clearActions();
		msgCombo.addAction(Actions.parallel(
				Actions.fadeIn(0),
				Actions.fadeOut(1.35f, Interpolation.fade),
				Actions.moveBy(0.f, msgSlideY, 1.35f)
			));
	}

	public void chainComboFinish2(int n) {
		String s = "Combo x" + n + "!";

		msgCombo.setText(s);
		msgCombo.addAction(
				Actions.sequence(
					Actions.show(),
					Actions.fadeIn(0.f),
					Actions.parallel(
						Actions.fadeOut(3.f, Interpolation.fade)
					)
				)
			);
	}

	private Table addLevelNScore() {
		layerScoreNLevel = new Table();
	
		layerScoreNLevel.add(hudLevel).top().row();
		layerScoreNLevel.add(hudScore).top().row();
	
		layerScoreNLevel.top();
		return layerScoreNLevel;
	}

	public void alignLeft() {
		//align(Align.bottom | Align.left);
		layerScoreNLevel.left();
	}
	public void alignRight() {
		//align(Align.bottom | Align.right);
		layerScoreNLevel.right();
	}

	private void align(int alignment) {
		hudScore.setAlignment(alignment);
		hudLevel.setAlignment(alignment);
	}

	public void setScore(int value) {
		String s = String.format("%06d", value);
		hudScore.setText(String.format("%06d", value));
	}

	public void setLevel(int cur) {
		hudLevel.setText("LEVEL "+cur);
	}

	public void render(float deltaTime) 	{
		stage.act(deltaTime);
		stage.draw();
	}

	public void resetHud() {
		setLevel(0);
		setScore(0);
	}
}
