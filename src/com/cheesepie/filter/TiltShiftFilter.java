package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

public class TiltShiftFilter implements IFilter {

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		Bitmap copyBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(copyBmp);
		
		setContrastAndSaturation(canvas, copyBmp, 1, 2);
		setBlackEdge(canvas);
		
		BlurFilter bf = new BlurFilter();
		copyBmp = bf.doFilter(copyBmp);
		
		return copyBmp;
	}
	
	/**
	 * @param canvas
	 * @param bmp
	 * @param value
	 */
	private void setContrastAndSaturation(Canvas canvas, Bitmap bmp, int contrastValue, int saturationValue) {
		ColorMatrix concat = new ColorMatrix();
		
		int mc = contrastValue + 1;
		int co = Math.round(contrastValue * -128);
		float[] matrix = new float[] {
				mc, 0,  0,  0, co,
				0,  mc, 0,  0, co,
				0,  0,  mc, 0, co,
				0,  0,  0,  1, 0
		}; 
		ColorMatrix contrast = new ColorMatrix(matrix);
		concat.postConcat(contrast);
		
		float rl = (float) 0.212671;
		float gl = (float) 0.715160;
		float bl = (float) 0.072169;
		float sf = saturationValue;
		float nf = 1 - sf;
		float nr = rl * nf;
		float ng = gl * nf;
		float nb = bl * nf;
		float[] sMatrix = new float[] {
				nr + sf, ng,      nb,    0, 0,
				nr,      ng + sf, nb,    0, 0,
				nr,      ng,      nb+sf, 0, 0,
				0,       0,       0,     1, 0
		};
		ColorMatrix saturation = new ColorMatrix(sMatrix);
		concat.postConcat(saturation);
		
		ColorMatrixColorFilter cmxf = new ColorMatrixColorFilter(concat);
		
		Paint paint = new Paint();        
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
		paint.setColorFilter(cmxf);
		
		canvas.drawBitmap(bmp, 0, 0, paint);
	}
	
	/**
	 * @param canvas
	 */
	private void setBlackEdge(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		int w = width / 2;
		int h = height / 2;
		int[] colors = new int[] { 0x00000000, 0x00000000, 0xFF000000 };
        RectF rect = new RectF(0, 0, width, height);
        
        // create a paint with a RadialGradient
        float radius = (float) Math.sqrt(w * w + h * h);
        RadialGradient shader = new RadialGradient(w, h, radius, colors, null, TileMode.CLAMP);
        
        Paint paint = new Paint();        
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setShader(shader);
        
        // paint the rectangle with said gradient
        canvas.drawRect(rect, paint);
	}
}
