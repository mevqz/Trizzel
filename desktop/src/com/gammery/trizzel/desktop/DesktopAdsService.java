package com.gammery.trizzel.desktop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.gammery.trizzel.AdsService;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class DesktopAdsService implements AdsService {

	@Override
	public byte[] getBanner(int gameId) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		URL toDownload = null;

	    try {
	    		toDownload = new URL("http://172.16.1.5/gammery/image.png");
	        byte[] chunk = new byte[4096];
	        int bytesRead;
	        InputStream stream = toDownload.openStream();

	        while ((bytesRead = stream.read(chunk)) > 0) {
	            outputStream.write(chunk, 0, bytesRead);
	        }
	    } catch (MalformedURLException e1) {
	    		e1.printStackTrace();	
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }

	    return outputStream.toByteArray();
	}

}
