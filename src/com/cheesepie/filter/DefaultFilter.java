package com.cheesepie.filter;

import android.graphics.Bitmap;

public class DefaultFilter implements IFilter {

	@Override
	public Bitmap doFilter(Bitmap bmp) {
		return bmp;
	}

}
