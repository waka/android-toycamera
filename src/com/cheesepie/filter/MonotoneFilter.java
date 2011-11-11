package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MonotoneFilter implements IFilter {

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		Bitmap copyBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
		int width = copyBmp.getWidth();
        int height = copyBmp.getHeight();
        
        int[] pixels = new int[width * height];
        copyBmp.getPixels(pixels, 0, width, 0, 0, width, height);

        int pixelColor = 0, y = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                pixelColor = pixels[i + j * width];
                y = (int) (0.299 * Color.red(pixelColor) +
                        0.587 * Color.green(pixelColor) +
                        0.114 * Color.blue(pixelColor));
                pixels[i + j * width] = Color.rgb(y, y, y);
            }
        }
        copyBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return copyBmp;
	}

}
