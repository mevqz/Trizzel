package com.gammery.trizzel.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.gammery.trizzel.model.Block;
import com.gammery.trizzel.model.Level;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class Assets implements Disposable, AssetErrorListener {
	
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	
	public AssetBlocks2 blocks2;
	public AssetSounds sounds;
	public Sprite bgBoard;
	public Texture frmHoldPiece;
	public Texture frmNextPiece;
	public Skin skinLibGDX;
	public Skin skinGame;

	public Label.LabelStyle lblStyleSmall;
	public Label.LabelStyle lblStyleMedium;
	public Label.LabelStyle lblStyleBig;
	public Label.LabelStyle lblStyleBigBig;


	private Assets() {}

	public void init(AssetManager newAssetManager) {

		initLabelStyles();

		assetManager = newAssetManager;
		assetManager.setErrorListener(this);
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// load sounds/musics
		assetManager.load("sounds/combo01.wav", Sound.class);
		assetManager.load("sounds/combo02.wav", Sound.class);
		assetManager.load("sounds/combo03.wav", Sound.class);
		assetManager.load("sounds/main_music.mp3", Music.class);
		
		assetManager.finishLoading();
		
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}
		
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		for (Texture texture : atlas.getTextures()) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		blocks2 = new AssetBlocks2(atlas);
		sounds = new AssetSounds(assetManager);
		frmHoldPiece = new Texture("images/frameHold.png");
		frmNextPiece = new Texture("images/frameNext.png");
		frmHoldPiece.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frmNextPiece.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		skinLibGDX = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
		skinGame = new Skin(Gdx.files.internal(Constants.SKIN_GAME_UI), 
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		
		TextureAtlas at = skinGame.getAtlas();
		for (Texture texture : at.getTextures()) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		at = skinLibGDX.getAtlas();
		for (Texture texture : at.getTextures()) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	
	}


	private void initLabelStyles() {

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		float ratio = height / 640f;
		float baseSize = 28;
		int mediumSize = (int) (ratio * baseSize);
		int smallSize = (int) (mediumSize * 0.75f);
		int bigSize = (int) (mediumSize * 1.25f);
		int bigbigSize = (int) (mediumSize * 1.50f);

		FreeTypeFontGenerator generator =
				new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		// Char set reducido para optimizar
		parameter.characters = Constants.CHAR_SET;

		// Medium
		parameter.size = mediumSize;
		BitmapFont fontNormal = generator.generateFont(parameter);
		lblStyleMedium = new Label.LabelStyle(fontNormal, Color.WHITE);

		// Small
		parameter.size = smallSize;
		BitmapFont fontSmall = generator.generateFont(parameter);
		lblStyleSmall = new Label.LabelStyle(fontSmall, Color.WHITE);

		// Big
		parameter.size = bigSize;
		BitmapFont fontBig = generator.generateFont(parameter);
		lblStyleBig = new Label.LabelStyle(fontBig, Color.WHITE);

		// Big Big
		parameter.size = bigbigSize;
		BitmapFont fontBigBig = generator.generateFont(parameter);
		lblStyleBigBig = new Label.LabelStyle(fontBigBig, Color.WHITE);

		generator.dispose();
	}

	
	@Override
	public void dispose() {
		assetManager.dispose();
		skinGame.dispose();
		skinLibGDX.dispose();
		Gdx.app.debug(TAG, "disposed");
	}

 
	@Override
	public void error (AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "couldn't load asset: " + asset);
	}	

	
	public class AssetsFonts {
		public BitmapFont fontNormal;
		public BitmapFont fontBig;
	}
	
	
	public class AssetSounds {
		
		public final Sound combo1;
		public final Sound combo2;
		public final Sound combo3;
		public final Music music;
		
		public AssetSounds(AssetManager am) {
			combo1 = am.get("sounds/combo01.wav", Sound.class);
			combo2 = am.get("sounds/combo02.wav", Sound.class);
			combo3 = am.get("sounds/combo03.wav", Sound.class);
			music = am.get("sounds/main_music.mp3", Music.class);

		}

	}


	public class AssetBlocks2 {

		public final AtlasRegion[][] blocks = new AtlasRegion[Block.TOTAL+1][Level.MAX_LOCK_AMOUNT+1];
		public final AtlasRegion wildcard;
		public final AtlasRegion[] bombs = new AtlasRegion[Constants.MAXS_BOMBS+1];
		
		public AssetBlocks2(TextureAtlas atlas) {
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.BLUE][i] = atlas.findRegion("blue"+i);
			}
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.RED][i] = atlas.findRegion("red"+i);
			}
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.ORANGE][i] = atlas.findRegion("orange"+i);
			}
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.GREEN][i] = atlas.findRegion("green"+i);
			}
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.YELLOW][i] = atlas.findRegion("yellow"+i);
			}
			for (int i = 0; i < blocks[0].length; i++) {
				blocks[Block.VIOLET][i] = atlas.findRegion("violet"+i);
			}

			for (int i = 0; i < bombs.length; i++) {
				bombs[i] = atlas.findRegion("bomb"+i);
			}

			wildcard = atlas.findRegion("wildcard");
		}
		
		public AtlasRegion get(int color, int locks) {
			AtlasRegion atlasRegion = null;
			switch (color) {
				case Block.BLUE:
				case Block.GREEN:
				case Block.ORANGE:
				case Block.VIOLET:
				case Block.YELLOW:
				case Block.RED:
					atlasRegion = blocks[color][locks]; 
				    break;
				case Block.BOMB:
					atlasRegion = bombs[locks]; 
				    break;
				case Block.WILDCARD:
					atlasRegion = wildcard; 
				    break;
			}

			return atlasRegion;
		}

	}

}
