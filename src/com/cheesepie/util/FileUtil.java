package com.cheesepie.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.Log;

public class FileUtil {
	/**
	 * @return
	 */
	public static String createFileName(String ext) {
		long dateTaken = System.currentTimeMillis();
		return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString() + "." + ext;
	}
	
	public static void createDirectory(String path) {
		File dir = new File(path);
        if (!dir.exists()) {
        	dir.mkdirs();
        	Log.d("FileUtil", dir.toString() + " create");
        }
	}
	
	/**
	 * @param filePath
	 * @param bmp
	 */
	public static void writeAsBitmap(Bitmap bmp, String dir, String fileName) {
		try {
			FileOutputStream out = new FileOutputStream(dir + fileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (IOException e) {
			Log.e("File", "File::writeAsBitmap() : Fail to write image data to file.");
		}
	}
	
	/**
	 * ギャラリーに登録する
	 * @param context
	 * @param dir
	 * @param fileName
	 */
	public static void addGallery(Context context, String dir, String fileName) {
		long nDate = System.currentTimeMillis();
		ContentValues values = new ContentValues();

		values.put(Images.Media.TITLE, fileName);
		values.put(Images.Media.DISPLAY_NAME, fileName);
		values.put(Images.Media.DATE_TAKEN, nDate);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(Images.Media.DATA, dir + fileName);

		ContentResolver	contentResolver = context.getContentResolver();
		contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
	}
}
