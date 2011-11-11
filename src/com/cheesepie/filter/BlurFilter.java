package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BlurFilter implements IFilter {

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		int height   = bmp.getHeight();
		int width    = bmp.getWidth();
		int range    = (width > height) ? (int) (height / 2 * 0.9) : (int) (width / 2 * 0.9);
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);

		int pixel = 2;
		for(int i = 0; i < width; ++i) {
			for(int j = 0; j < height; ++j) {
				int r, g, b;
				float sumR = 0.0f;
				float sumG = 0.0f;
				float sumB = 0.0f;

				for(int ii = -pixel; ii <= pixel; ii++) {
					for(int jj = -pixel; jj <= pixel; jj++) {

						if((i + ii) < 0 || width <= (i + ii) ||
						   (j + jj) < 0 || height <= (j + jj)) {
							continue;
						}

						int bitmapColor = pixels[(i + ii) + (j + jj) * width];

						r = Color.red(bitmapColor);
						g = Color.green(bitmapColor);
						b = Color.blue(bitmapColor);

						sumR += (float) r;
						sumG += (float) g;
						sumB += (float) b;
					}
				}

				int rr = (int) (sumR / Math.pow((1 + 2 * pixel), 2));
				int gg = (int) (sumG / Math.pow((1 + 2 * pixel), 2));
				int bb = (int) (sumB / Math.pow((1 + 2 * pixel), 2));

				if (range <= 0 ||
					!(i < (width / 2 + range) && (width / 2 - range) < i &&
					    j < (height / 2 + range) && (height / 2 - range) < j)){
					pixels[( i + j * width)] = Color.rgb(rr, gg, bb);
				}
			}
		}

		bmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmp;
	}

}
