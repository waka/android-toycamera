package com.cheesepie.simpletoycam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.cheesepie.simpletoycam.R;
import com.cheesepie.filter.IFilter;
import com.cheesepie.filter.Filters;
import com.cheesepie.util.FileUtil;

public class ImageProcessingTask extends AsyncTask<Bitmap, Integer, Bitmap> {
	
	private Activity uiActivity;
	private Object objLock = new Object();

	public ImageProcessingTask(Context context) {
		super();
		this.uiActivity = (Activity) context;
	}
	
	@Override
    protected void onPreExecute() {
		uiActivity.setProgressBarVisibility(true);
		uiActivity.setProgressBarIndeterminate(true);
    }
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Bitmap doInBackground(Bitmap... bmp) {
		Bitmap sourceBmp = bmp[0];
		// get preference
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(uiActivity);
		String filterPreference = sharedPreferences.getString("filter", "default");
		
		// Set filter
		IFilter filter = Filters.DEFAULT.get();
		for (Filters f : Filters.values()) {
			if (f.toString().equals(filterPreference)) {
				filter = f.get();
				break;
			}
		}
		// 複数スレッドで同時に処理されないように保護する
		synchronized (objLock) {
			if (this.isCancelled()) {
                return sourceBmp;
            }
		    Bitmap filteredBmp = filter.doFilter(sourceBmp);
		    sourceBmp = null;
		    return filteredBmp;
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Bitmap bmp) {
		uiActivity.setProgressBarVisibility(false);
		uiActivity.setProgressBarIndeterminate(false);
		
		// Check directory to save
		String dir = Environment.getExternalStorageDirectory() + "/DCIM/EasyToyCam/";
        FileUtil.createDirectory(dir);
		
		String fileName = FileUtil.createFileName("jpg");
		FileUtil.writeAsBitmap(bmp, dir, fileName);
		FileUtil.addGallery(uiActivity, dir, fileName);
	}
}
