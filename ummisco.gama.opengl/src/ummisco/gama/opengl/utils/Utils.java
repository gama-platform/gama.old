/*********************************************************************************************
 *
 * 'Utils.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.util.Arrays;

public class Utils {

	public static float[] concatFloatArrays(final float[] l1, final float[] l2) {

		if (l1 == null)
			return l2;
		if (l2 == null)
			return l1;
		final float[] result = Arrays.copyOf(l1, l1.length + l2.length);
		System.arraycopy(l2, 0, result, l1.length, l2.length);
		return result;
	}

	public static int[] concatIntArrays(final int[] l1, final int[] l2) {
		if (l1 == null)
			return l2;
		if (l2 == null)
			return l1;
		final int[] result = Arrays.copyOf(l1, l1.length + l2.length);
		System.arraycopy(l2, 0, result, l1.length, l2.length);
		return result;
	}

	public static boolean isClockwise(final float[] vertices) {
		double sum = 0.0;
		for (int i = 0; i < vertices.length / 3; i++) {
			final float[] v1 = new float[] { vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2] };
			final float[] v2 = new float[] { vertices[(i + 1) % (vertices.length / 3) * 3],
					vertices[(i + 1) % (vertices.length / 3) * 3 + 1],
					vertices[(i + 1) % (vertices.length / 3) * 3 + 2] };
			sum += (v2[0] - v1[0]) * (v2[1] + v1[1]);
		}
		return sum > 0.0;
	}

}
