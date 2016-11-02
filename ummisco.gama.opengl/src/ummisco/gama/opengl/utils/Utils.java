/*********************************************************************************************
 *
 * 'Utils.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

public class Utils {
	
	public static float[] concatFloatArrays(float[] l1, float[] l2) {
		if (l1 == null) return l2;
		if (l2 == null) return l1;
		float[] result = new float[l1.length+l2.length];
		for (int i = 0 ; i < l1.length ; i++) {
			result[i] = l1[i];
		}
		for (int i = 0 ; i < l2.length ; i++) {
			result[i+l1.length] = l2[i];
		}
		return result;
	}
	
	public static int[] concatIntArrays(int[] l1, int[] l2) {
		if (l1 == null) return l2;
		if (l2 == null) return l1;
		int[] result = new int[l1.length+l2.length];
		for (int i = 0 ; i < l1.length ; i++) {
			result[i] = l1[i];
		}
		for (int i = 0 ; i < l2.length ; i++) {
			result[i+l1.length] = l2[i];
		}
		return result;
	}
	
	public static boolean isClockwise(final float[] vertices) {
		double sum = 0.0;
		for (int i = 0; i < vertices.length/3; i++) {
			final float[] v1 = new float[]{vertices[i*3],vertices[i*3+1],vertices[i*3+2]};
			final float[] v2 = new float[]{vertices[((i + 1) % (vertices.length/3))*3],vertices[((i + 1) % (vertices.length/3))*3+1],vertices[((i + 1) % (vertices.length/3))*3+2]};
			sum += (v2[0] - v1[0]) * (v2[1] + v1[1]);
		}
		return sum > 0.0;
	}

}
