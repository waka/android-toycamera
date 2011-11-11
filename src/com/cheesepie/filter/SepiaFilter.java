package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class SepiaFilter implements IFilter {

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		Bitmap copyBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
		int width = copyBmp.getWidth();
        int height = copyBmp.getHeight();

        int[] pixels = new int[width * height];
        copyBmp.getPixels(pixels, 0, width, 0, 0, width, height);
        
        int rr = 0, gg = 0, bb = 0, pixelColor = 0;
        float gray = 0.f;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
            	pixelColor = pixels[i + j * width];
                
                rr = Color.red(pixelColor);
                gg = Color.green(pixelColor);
                bb = Color.blue(pixelColor);
                gray = (rr * 0.298912f) + (gg * 0.586611f) + (bb * 0.114478f);
                rr = gg = bb = (int) gray;
                rr *= 0.9f;
                gg *= 0.7f;
                bb *= 0.4f;
                
                pixels[i + j * width] = Color.rgb(rr, gg, bb);
            }
        }
        copyBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return copyBmp;
	}
}
