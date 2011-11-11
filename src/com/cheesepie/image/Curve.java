package com.cheesepie.image;

public class Curve {

	public float[] x;
	public float[] y;
	
	public Curve(float[] x, float[] y) {
		this.x = x;
		this.y = y;
	}

	public int[] makeTable() {
		int numKnots = x.length;
		float[] nx = new float[numKnots+2];
		float[] ny = new float[numKnots+2];
		System.arraycopy(x, 0, nx, 1, numKnots);
		System.arraycopy(y, 0, ny, 1, numKnots);
		nx[0] = nx[1];
		ny[0] = ny[1];
		nx[numKnots+1] = nx[numKnots];
		ny[numKnots+1] = ny[numKnots];

		int[] table = new int[256];
		for (int i = 0; i < 1024; i++) {
			float f = i / 1024.0f;
			int x = (int) (255 * spline(f, nx.length, nx) + 0.5f);
			int y = (int) (255 * spline(f, nx.length, ny) + 0.5f);
			x = clamp(x, 0, 255);
			y = clamp(y, 0, 255);
			table[x] = y;
		}
		return table;
	}
	
	// Catmull-Rom splines
	private final static float m00 = -0.5f;
	private final static float m01 =  1.5f;
	private final static float m02 = -1.5f;
	private final static float m03 =  0.5f;
	private final static float m10 =  1.0f;
	private final static float m11 = -2.5f;
	private final static float m12 =  2.0f;
	private final static float m13 = -0.5f;
	private final static float m20 = -0.5f;
	private final static float m21 =  0.0f;
	private final static float m22 =  0.5f;
	private final static float m23 =  0.0f;
	private final static float m30 =  0.0f;
	private final static float m31 =  1.0f;
	private final static float m32 =  0.0f;
	private final static float m33 =  0.0f;
	
	/**
	 * Compute a Catmull-Rom spline.
	 * @param x the input parameter
	 * @param numKnots the number of knots in the spline
	 * @param knots the array of knots
	 * @return the spline value
	 */
	public float spline(float x, int numKnots, float[] knots) {
		int span;
		int numSpans = numKnots - 3;
		float k0, k1, k2, k3;
		float c0, c1, c2, c3;
		
		if (numSpans < 1) {
			throw new IllegalArgumentException("Too few knots in spline");
		}
		x = clamp(x, 0, 1) * numSpans;
		span = (int) x;
		if (span > numKnots - 4)
			span = numKnots - 4;
		x -= span;

		k0 = knots[span];
		k1 = knots[span + 1];
		k2 = knots[span + 2];
		k3 = knots[span + 3];
		
		c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
		c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
		c1 = m20*k0 + m21*k1 + m22*k2 + m23*k3;
		c0 = m30*k0 + m31*k1 + m32*k2 + m33*k3;
		
		return ((c3 * x + c2) * x + c1) * x + c0;
	}
	
	/**
	 * Clamp a value to an interval.
	 * @param a the lower clamp threshold
	 * @param b the upper clamp threshold
	 * @param x the input parameter
	 * @return the clamped value
	 */
	private float clamp(float x, float a, float b) {
		return (x < a) ? a : (x > b) ? b : x;
	}
	
	/**
	 * Clamp a value to an interval.
	 * @param a the lower clamp threshold
	 * @param b the upper clamp threshold
	 * @param x the input parameter
	 * @return the clamped value
	 */
	private static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}
}
