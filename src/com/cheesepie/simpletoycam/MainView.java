package com.cheesepie.simpletoycam;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.cheesepie.simpletoycam.R;
import com.cheesepie.util.ImageUtil;

public class MainView extends ViewGroup implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private int mOrientation = 0;
    private boolean autoFocus = false;
    private boolean hasTouch = false;
    
    public MainView(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    /**
     * @param enabled
     */
    public void setAutoFocus(boolean enabled) {
    	autoFocus = enabled;
    }
    
    /*
     * @return
     */
    public boolean getAutoFocus() {
    	return autoFocus;
    }
    
    /**
     * @param camera
     */
    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
        	mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        }
    }
    
    /**
     * Dispose camera.
     */
    public void disposeCamera() {
    	mCamera.setPreviewCallback(null);
    	mCamera.release();
        setCamera(null);
    }
    
    /**
     * Set orientation from portrait / landscape.
     */
    public void setCameraDisplayOrientation() {
	     CameraInfo info = new CameraInfo();
	     Camera.getCameraInfo(0, info);
	     int rotation = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     mCamera.setDisplayOrientation(result);
	     mOrientation = result;
	 }

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
	}
	
	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height / previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width / previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2,
						width, (height + scaledChildHeight) / 2);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera != null) {
		    try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                Log.e("MainView", "Failed by setPreviewDisplay()");
            }
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mCamera.stopPreview();
		mCamera.setPreviewCallback(new NoShutterCallback(this));
		setCameraDisplayOrientation();
		
		// Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
	}
	
	/**
	 * @param sizes
	 * @param w
	 * @param h
	 * @return
	 */
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//タッチイベント取得
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getAutoFocus()) {
				mCamera.autoFocus(_pfnAutoFocusCallback);
			}
		}
		return true;
	}
	
	/**
     * @param enabled
     */
    public void setHasTouch(boolean enabled) {
    	hasTouch = enabled;
    }
    
    /*
     * @return
     */
    public boolean getHasTouch() {
    	return hasTouch;
    }
	
	private Camera.AutoFocusCallback _pfnAutoFocusCallback = new AutoFocusCallback();
	private final class AutoFocusCallback implements Camera.AutoFocusCallback {
		public void onAutoFocus(boolean success, Camera camera) {
			if (!getHasTouch()) {
				setHasTouch(true);
			}
		}
	}
	
	private final class NoShutterCallback implements Camera.PreviewCallback {
		private View view;
		public NoShutterCallback(View mView) {
			this.view = mView;
		}
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (!getHasTouch()) {
				return;
			} else {
				setHasTouch(false);
			}

			Size size = mCamera.getParameters().getPreviewSize();
			int[] rgb = new int[(size.width * size.height)]; // ARGB8888の画素の配列
			ImageUtil.decodeYUV420SP(rgb, data, size.width, size.height);
			
			Matrix matrix = new Matrix();
		 	matrix.postRotate(mOrientation);
			Bitmap bmp = Bitmap.createBitmap(rgb, size.width, size.height, Bitmap.Config.ARGB_8888);
			Bitmap drawn = Bitmap.createBitmap(bmp, 0, 0, size.width, size.height, matrix, true);
			
			// タスクの生成
	        ImageProcessingTask imageProcessingTask = new ImageProcessingTask(view.getContext());
			imageProcessingTask.execute(drawn);
		}
	}
}