package com.cheesepie.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

/**
 * 顔認識してモザイクをかけたBitmapを返す
 * @author yo_waka
 *
 */
public class FaceMosaicFilter implements IFilter {
	
    @Override
	public Bitmap doFilter(Bitmap bmp) {
    	//BitmapFactory.Options options = new BitmapFactory.Options();
		//options.inSampleSize = 8;
		Bitmap copyBmp = bmp.copy(Bitmap.Config.RGB_565, true);
		
    	FaceDetector.Face[] faces = new FaceDetector.Face[3];
    	FaceDetector detector = new FaceDetector(
    		    copyBmp.getWidth(),
    		    copyBmp.getHeight(),
    		    faces.length);
    	int numFaces = detector.findFaces(copyBmp, faces); // 顔認識実行
    	if (numFaces == 0) {
    		return bmp;
    	}
    	
		for (int i = 0; i < numFaces; i++) {
			Face face = faces[i];
			PointF midPoint = new PointF(0, 0);
			face.getMidPoint(midPoint);
			float eyesDistance = face.eyesDistance();
			int width  = (int) eyesDistance * 2;
			int height = (int) eyesDistance * 2;
			int pixels[] = new int[width * height];
			copyBmp.getPixels(pixels, 0,
                    width,
                    (int) midPoint.x - (int) eyesDistance,
                    (int) midPoint.y - (int) eyesDistance,
                    width,
                    height);
			
			int dot = 24; // モザイクのかかり方（大きくすると粗くなる）
			int rr = 0, gg = 0, bb = 0, dotColor = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// ドットの中の平均値を使う
		            for (int k = 0; k < dot; k++) {
		            	for (int l = 0; l < dot; l++) {
		            		dotColor = copyBmp.getPixel(
		            				((int) midPoint.x - (int) eyesDistance) + x + k -dot / 2,
                                    ((int) midPoint.y - (int) eyesDistance) + y + l -dot / 2);
		            		rr += Color.red(dotColor);
		            		gg += Color.green(dotColor);
		            		bb += Color.blue(dotColor);
		            	}
		            }
		            rr = rr / (dot * dot);
		            gg = gg / (dot * dot);
		            bb = bb / (dot * dot);
		            
		            pixels[x + y * width] = Color.rgb(rr, gg, bb);
				}
			}
			copyBmp.setPixels(pixels, 0,
                    width,
                    (int) midPoint.x - (int) eyesDistance,
                    (int) midPoint.y - (int) eyesDistance,
                    width,
                    height);
		}
    	
		return copyBmp;
	}
}
