package ummisco.gama.opengl.vaoGenerator;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

public class GeomMathUtils {
	
	public static double[] CrossProduct(final double[] vect1, final double[] vect2) {
		final double[] result = new double[3];
		result[0] = vect1[1] * vect2[2] - vect1[2] * vect2[1];
		result[1] = vect1[2] * vect2[0] - vect1[0] * vect2[2];
		result[2] = vect1[0] * vect2[1] - vect1[1] * vect2[0];
		return result;
	}
	
	public static float[] CrossProduct(final float[] vect1, final float[] vect2) {
		final float[] result = new float[3];
		result[0] = vect1[1] * vect2[2] - vect1[2] * vect2[1];
		result[1] = vect1[2] * vect2[0] - vect1[0] * vect2[2];
		result[2] = vect1[0] * vect2[1] - vect1[1] * vect2[0];
		return result;
	}
	
	public static double ScalarProduct(final double[] vect1, final double[] vect2) {
		return vect1[0]*vect2[0]+vect1[1]*vect2[1]+vect1[2]*vect2[2];
	}
	
	public static float ScalarProduct(final float[] vect1, final float[] vect2) {
		return vect1[0]*vect2[0]+vect1[1]*vect2[1]+vect1[2]*vect2[2];
	}
	
	public static double[] Normalize(final double[] vect) {
		double[] result = new double[vect.length];
		double sum = 0;
		for (int i = 0; i < vect.length ; i++) {
		    sum += Math.pow(vect[i], 2);
		}
		for (int i = 0; i < vect.length ; i++) {
		    result[i] = vect[i] / Math.sqrt(sum);
		}
		return result;
	}
	
	public static float[] Normalize(final float[] vect) {
		float[] result = new float[vect.length];
		float sum = 0;
		for (int i = 0; i < vect.length ; i++) {
		    sum += Math.pow(vect[i], 2);
		}
		for (int i = 0; i < vect.length ; i++) {
		    result[i] = (float) (vect[i] / Math.sqrt(sum));
		}
		return result;
	}
	
	static public FloatBuffer getFloatBuffer(Matrix4f matrix) {
		FloatBuffer result = FloatBuffer.allocate(16);
		result.put(0,matrix.m00);
		result.put(1,matrix.m01);
		result.put(2,matrix.m02);
		result.put(3,matrix.m03);
		result.put(4,matrix.m10);
		result.put(5,matrix.m11);
		result.put(6,matrix.m12);
		result.put(7,matrix.m13);
		result.put(8,matrix.m20);
		result.put(9,matrix.m21);
		result.put(10,matrix.m22);
		result.put(11,matrix.m23);
		result.put(12,matrix.m30);
		result.put(13,matrix.m31);
		result.put(14,matrix.m32);
		result.put(15,matrix.m33);
		return result;
	}
	
	static public float[] getFloatArray(Matrix4f matrix) {
		float[] result = new float[16];
		result[0]=matrix.m00;
		result[1]=matrix.m01;
		result[2]=matrix.m02;
		result[3]=matrix.m03;
		result[4]=matrix.m10;
		result[5]=matrix.m11;
		result[6]=matrix.m12;
		result[7]=matrix.m13;
		result[8]=matrix.m20;
		result[9]=matrix.m21;
		result[10]=matrix.m22;
		result[11]=matrix.m23;
		result[12]=matrix.m30;
		result[13]=matrix.m31;
		result[14]=matrix.m32;
		result[15]=matrix.m33;
		return result;
	}
	
	static public float[] setTranslationToVertex(float[] coordinates, float x, float y, float z) {
		float[] result = new float[coordinates.length];
		int vertexNb = coordinates.length/3;
		for (int i = 0 ; i < vertexNb ; i++) {
			result[3*i] = coordinates[i*3] + x;
			result[3*i+1] = coordinates[i*3+1] + y;
			result[3*i+2] = coordinates[i*3+2] + z;
		}
		return result;
	}
	
	static public float[] setRotationToVertex(float[] coordinates, float a, float x, float y, float z) {
		float[] result = new float[coordinates.length];
		int vertexNb = coordinates.length/3;
		for (int i = 0 ; i < vertexNb ; i++) {
			// get the result of the rotation
			double[] tmpResult = QuaternionRotate(new double[]{coordinates[i*3],coordinates[i*3+1],coordinates[i*3+2]},
					new double[]{x,y,z,a});
			result[3*i] = (float) tmpResult[0];
			result[3*i+1] = (float) tmpResult[1];
			result[3*i+2] = (float) tmpResult[2];
		}
		return result;
	}
	
	static public float[] setScalingToVertex(float[] coordinates, float x, float y, float z) {
		float[] result = new float[coordinates.length];
		int vertexNb = coordinates.length/3;
		for (int i = 0 ; i < vertexNb ; i++) {
			result[3*i] = (float) coordinates[3*i]*x;
			result[3*i+1] = (float) coordinates[3*i+1]*y;
			result[3*i+2] = (float) coordinates[3*i+2]*z;
		}
		return result;
	}
	
	public static double[] QuaternionRotate(final double[] initVector3, final double[] quaternionRotation) {
		double[] result = new double[3];
		// a, b, c are the normalized composants of the axis.
		double[] axis = new double[] {quaternionRotation[0],quaternionRotation[1],quaternionRotation[2]};
		double angle = quaternionRotation[3];
		double a = axis[0]/Math.sqrt(axis[0]*axis[0]+axis[1]*axis[1]+axis[2]*axis[2]);
		double b = -axis[1]/Math.sqrt(axis[0]*axis[0]+axis[1]*axis[1]+axis[2]*axis[2]);
		double c = axis[2]/Math.sqrt(axis[0]*axis[0]+axis[1]*axis[1]+axis[2]*axis[2]);
		// x, y, z are the initial position of the light.
		double x = initVector3[0];
		double y = initVector3[1];
		double z = initVector3[2];
		
		result[0] = x * (Math.cos(angle) + a*a * (1 - Math.cos(angle)))
				+ y * (a*b * (1-Math.cos(angle)) - c * Math.sin(angle))
				+ z * (a*c * (1-Math.cos(angle)) + b * Math.sin(angle));
		result[1] = x * (a*b * (1-Math.cos(angle)) + c * Math.sin(angle))
				+ y * (Math.cos(angle) + b*b * (1 - Math.cos(angle)))
				+ z * (b*c * (1 - Math.cos(angle)) - a * Math.sin(angle));
		result[2] = x * (a*c * (1 - Math.cos(angle)) - b * Math.sin(angle))
				+ y * (b*c * (1 - Math.cos(angle)) + a * Math.sin(angle))
				+ z * (Math.cos(angle) + c*c * (1 - Math.cos(angle)));
		return result;
	}

}
