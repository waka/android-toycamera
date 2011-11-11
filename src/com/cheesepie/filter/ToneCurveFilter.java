package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.cheesepie.image.Curve;

public class ToneCurveFilter implements IFilter {
	
	private int[] rTable, gTable, bTable;
	
	public void setCurves(Curve rCurve, Curve gCurve, Curve bCurve) {
		rTable = rCurve.makeTable();
        gTable = gCurve.makeTable();
        bTable = bCurve.makeTable();
	}

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		int height   = bmp.getHeight();
		int width    = bmp.getWidth();
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int pixelColor, j, i;
		int[] rgb;
		for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
            	pixelColor = pixels[i + j * width];
                rgb = adjustRGB(Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
                pixels[i + j * width] = Color.rgb(rgb[0], rgb[1], rgb[2]);
            }
		}

		bmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmp;
	}

	private int[] adjustRGB(int r, int g, int b) {
		r = rTable[r];
		g = gTable[g];
		b = bTable[b];
		return new int [] {r, g, b};
	}
}
