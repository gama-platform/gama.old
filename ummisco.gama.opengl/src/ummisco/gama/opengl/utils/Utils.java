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

}
