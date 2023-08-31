package com.gammery.trizzel.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.gammery.trizzel.TrizzelGame;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class DesktopLauncher 
{
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;
	
	public static void main (String[] arg) 
	{
		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/blocks",
				"images", "newVersDiscardtrizzel.pack");
			
			settings.maxWidth = 2048;
			settings.maxHeight = 2048;
			TexturePacker.process(settings, "assets-raw/images-ui", 
				"images", "trizzel-ui.pack");
		}
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Trizzel";
		config.vSyncEnabled = true;
		config.width = 360;
		config.height = 592;
				
		new LwjglApplication(new TrizzelGame(new DesktopGoogleServices(), new DesktopAdsService()), config);
	}
}
