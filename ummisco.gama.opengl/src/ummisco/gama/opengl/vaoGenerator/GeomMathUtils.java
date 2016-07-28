package ummisco.gama.opengl.vaoGenerator;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.vividsolutions.jts.geom.Coordinate;

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
	
	static public DoubleBuffer getDoubleBuffer(Matrix4d matrix) {
		DoubleBuffer result = DoubleBuffer.allocate(16);
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
	
	static public double[] getDoubleArray(Matrix4d matrix) {
		double[] result = new double[16];
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
	
	static public Coordinate[] setTranslationToCoordArray(Coordinate[] coordinates, double x, double y, double z) {
		Coordinate[] result = new Coordinate[coordinates.length];
		for (int i = 0 ; i < coordinates.length ; i++) {
			result[i] = new Coordinate(coordinates[i].x + x,
					coordinates[i].y + y,
					coordinates[i].z + z);
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
	
	static public Coordinate[] setScalingToCoordArray(Coordinate[] coordinates, double x, double y, double z) {
		Coordinate[] result = new Coordinate[coordinates.length];
		for (int i = 0 ; i < coordinates.length ; i++) {
			result[i] = new Coordinate(coordinates[i].x * x,
					coordinates[i].y * y,
					coordinates[i].z * z);
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
	
	public static Matrix4f translateMatrix(float x, float y, float z, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		Matrix4f result = (Matrix4f) matrix.clone();
        result.m00 = matrix.m00;
        result.m01 = matrix.m01;
        result.m02 = matrix.m02;
        result.m03 = matrix.m03;
        result.m10 = matrix.m10;
        result.m11 = matrix.m11;
        result.m12 = matrix.m12;
        result.m13 = matrix.m13;
        result.m20 = matrix.m20;
        result.m21 = matrix.m21;
        result.m22 = matrix.m22;
        result.m23 = matrix.m23;
        result.m30 = matrix.m00 * x + matrix.m10 * y + matrix.m20 * z + matrix.m30;
        result.m31 = matrix.m01 * x + matrix.m11 * y + matrix.m21 * z + matrix.m31;
        result.m32 = matrix.m02 * x + matrix.m12 * y + matrix.m22 * z + matrix.m32;
        result.m33 = matrix.m03 * x + matrix.m13 * y + matrix.m23 * z + matrix.m33;
        return result;
	}
	
	public static Matrix4f rotateMatrix(float angle, Vector3f axis, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		return rotateMatrix(angle, axis.x, axis.y, axis.z, matrix);
	}
	
	public static Matrix4f rotateMatrix(float angle, float rotX, float rotY, float rotZ, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		Matrix4f result = (Matrix4f) matrix.clone();
				
		float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);
        float C = 1.0f - c;
        float xx = rotX * rotX, xy = rotX * rotY, xz = rotX * rotZ;
        float yy = rotY * rotY, yz = rotY * rotZ;
        float zz = rotZ * rotZ;
        float rm00 = xx * C + c;
        float rm01 = xy * C + rotZ * s;
        float rm02 = xz * C - rotY * s;
        float rm10 = xy * C - rotZ * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + rotX * s;
        float rm20 = xz * C + rotY * s;
        float rm21 = yz * C - rotX * s;
        float rm22 = zz * C + c;
        float nm00 = matrix.m00 * rm00 + matrix.m10 * rm01 + matrix.m20 * rm02;
        float nm01 = matrix.m01 * rm00 + matrix.m11 * rm01 + matrix.m21 * rm02;
        float nm02 = matrix.m02 * rm00 + matrix.m12 * rm01 + matrix.m22 * rm02;
        float nm03 = matrix.m03 * rm00 + matrix.m13 * rm01 + matrix.m23 * rm02;
        float nm10 = matrix.m00 * rm10 + matrix.m10 * rm11 + matrix.m20 * rm12;
        float nm11 = matrix.m01 * rm10 + matrix.m11 * rm11 + matrix.m21 * rm12;
        float nm12 = matrix.m02 * rm10 + matrix.m12 * rm11 + matrix.m22 * rm12;
        float nm13 = matrix.m03 * rm10 + matrix.m13 * rm11 + matrix.m23 * rm12;
        result.m20 = matrix.m00 * rm20 + matrix.m10 * rm21 + matrix.m20 * rm22;
        result.m21 = matrix.m01 * rm20 + matrix.m11 * rm21 + matrix.m21 * rm22;
        result.m22 = matrix.m02 * rm20 + matrix.m12 * rm21 + matrix.m22 * rm22;
        result.m23 = matrix.m03 * rm20 + matrix.m13 * rm21 + matrix.m23 * rm22;
        result.m00 = nm00;
        result.m01 = nm01;
        result.m02 = nm02;
        result.m03 = nm03;
        result.m10 = nm10;
        result.m11 = nm11;
        result.m12 = nm12;
        result.m13 = nm13;
        result.m30 = matrix.m30;
        result.m31 = matrix.m31;
        result.m32 = matrix.m32;
        result.m33 = matrix.m33;
        return result;
	}
	
	public static Matrix4f rotateMatrix(float rotX, float rotY, float rotZ, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		Matrix4f result = (Matrix4f) matrix.clone();
		
		float cosX = (float) Math.cos(rotX);
        float sinX = (float) Math.sin(rotX);
        float cosY = (float) Math.cos(rotY);
        float sinY = (float) Math.sin(rotY);
        float cosZ = (float) Math.cos(rotZ);
        float sinZ = (float) Math.sin(rotZ);
        float m_sinX = -sinX;
        float m_sinY = -sinY;
        float m_sinZ = -sinZ;

        // rotateX
        float nm11 = cosX;
        float nm12 = sinX;
        float nm21 = m_sinX;
        float nm22 = cosX;
        // rotateY
        float nm00 = cosY;
        float nm01 = nm21 * m_sinY;
        float nm02 = nm22 * m_sinY;
        result.m20 = sinY;
        result.m21 = nm21 * cosY;
        result.m22 = nm22 * cosY;
        result.m23 = 0.0f;
        // rotateZ
        result.m00 = nm00 * cosZ;
        result.m01 = nm01 * cosZ + nm11 * sinZ;
        result.m02 = nm02 * cosZ + nm12 * sinZ;
        result.m03 = 0.0f;
        result.m10 = nm00 * m_sinZ;
        result.m11 = nm01 * m_sinZ + nm11 * cosZ;
        result.m12 = nm02 * m_sinZ + nm12 * cosZ;
        result.m13 = 0.0f;
        // set last column to identity
        result.m30 = 0.0f;
        result.m31 = 0.0f;
        result.m32 = 0.0f;
        result.m33 = 1.0f;
        return result;
	}
	
	public static Matrix4f scaleMatrix(float scaleX, float scaleY, float scaleZ, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		Matrix4f result = (Matrix4f) matrix.clone();
        result.m00 = matrix.m00 * scaleX;
        result.m01 = matrix.m01 * scaleX;
        result.m02 = matrix.m02 * scaleX;
        result.m03 = matrix.m03 * scaleX;
        result.m10 = matrix.m10 * scaleY;
        result.m11 = matrix.m11 * scaleY;
        result.m12 = matrix.m12 * scaleY;
        result.m13 = matrix.m13 * scaleY;
        result.m20 = matrix.m20 * scaleZ;
        result.m21 = matrix.m21 * scaleZ;
        result.m22 = matrix.m22 * scaleZ;
        result.m23 = matrix.m23 * scaleZ;
        result.m30 = matrix.m30;
        result.m31 = matrix.m31;
        result.m32 = matrix.m32;
        result.m33 = matrix.m33;
        return result;
	}
	
	public static Matrix4f rotateMatrixAroundLocal(float[] quat, float ox, float oy, float oz, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		// quat : {x,y,z,w}
		Matrix4f result = (Matrix4f) matrix.clone();
        float dqx = quat[0] + quat[0];
        float dqy = quat[1] + quat[1];
        float dqz = quat[2] + quat[2];
        float q00 = dqx * quat[0];
        float q11 = dqy * quat[1];
        float q22 = dqz * quat[2];
        float q01 = dqx * quat[1];
        float q02 = dqx * quat[2];
        float q03 = dqx * quat[3];
        float q12 = dqy * quat[2];
        float q13 = dqy * quat[3];
        float q23 = dqz * quat[3];
        float lm00 = 1.0f - q11 - q22;
        float lm01 = q01 + q23;
        float lm02 = q02 - q13;
        float lm10 = q01 - q23;
        float lm11 = 1.0f - q22 - q00;
        float lm12 = q12 + q03;
        float lm20 = q02 + q13;
        float lm21 = q12 - q03;
        float lm22 = 1.0f - q11 - q00;
        float tm00 = matrix.m00 - ox * matrix.m03;
        float tm01 = matrix.m01 - oy * matrix.m03;
        float tm02 = matrix.m02 - oz * matrix.m03;
        float tm10 = matrix.m10 - ox * matrix.m13;
        float tm11 = matrix.m11 - oy * matrix.m13;
        float tm12 = matrix.m12 - oz * matrix.m13;
        float tm20 = matrix.m20 - ox * matrix.m23;
        float tm21 = matrix.m21 - oy * matrix.m23;
        float tm22 = matrix.m22 - oz * matrix.m23;
        float tm30 = matrix.m30 - ox * matrix.m33;
        float tm31 = matrix.m31 - oy * matrix.m33;
        float tm32 = matrix.m32 - oz * matrix.m33;
        result.m00 = lm00 * tm00 + lm10 * tm01 + lm20 * tm02 + ox * matrix.m03;
        result.m01 = lm01 * tm00 + lm11 * tm01 + lm21 * tm02 + oy * matrix.m03;
        result.m02 = lm02 * tm00 + lm12 * tm01 + lm22 * tm02 + oz * matrix.m03;
        result.m03 = matrix.m03;
        result.m10 = lm00 * tm10 + lm10 * tm11 + lm20 * tm12 + ox * matrix.m13;
        result.m11 = lm01 * tm10 + lm11 * tm11 + lm21 * tm12 + oy * matrix.m13;
        result.m12 = lm02 * tm10 + lm12 * tm11 + lm22 * tm12 + oz * matrix.m13;
        result.m13 = matrix.m13;
        result.m20 = lm00 * tm20 + lm10 * tm21 + lm20 * tm22 + ox * matrix.m23;
        result.m21 = lm01 * tm20 + lm11 * tm21 + lm21 * tm22 + oy * matrix.m23;
        result.m22 = lm02 * tm20 + lm12 * tm21 + lm22 * tm22 + oz * matrix.m23;
        result.m23 = matrix.m23;
        result.m30 = lm00 * tm30 + lm10 * tm31 + lm20 * tm32 + ox * matrix.m33;
        result.m31 = lm01 * tm30 + lm11 * tm31 + lm21 * tm32 + oy * matrix.m33;
        result.m32 = lm02 * tm30 + lm12 * tm31 + lm22 * tm32 + oz * matrix.m33;
        result.m33 = matrix.m33;
        return result;
    }
	
	public static Matrix4f rotateMatrixAround(float[] quat, float ox, float oy, float oz, Matrix4f matrix) {
		// cf https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		// quat : {x,y,z,w}
		Matrix4f result = (Matrix4f) matrix.clone();
        float dqx = quat[0] + quat[0];
        float dqy = quat[1] + quat[1];
        float dqz = quat[2] + quat[2];
        float q00 = dqx * quat[0];
        float q11 = dqy * quat[1];
        float q22 = dqz * quat[2];
        float q01 = dqx * quat[1];
        float q02 = dqx * quat[2];
        float q03 = dqx * quat[3];
        float q12 = dqy * quat[2];
        float q13 = dqy * quat[3];
        float q23 = dqz * quat[3];
        float rm00 = 1.0f - q11 - q22;
        float rm01 = q01 + q23;
        float rm02 = q02 - q13;
        float rm10 = q01 - q23;
        float rm11 = 1.0f - q22 - q00;
        float rm12 = q12 + q03;
        float rm20 = q02 + q13;
        float rm21 = q12 - q03;
        float rm22 = 1.0f - q11 - q00;
        float tm30 = matrix.m00 * ox + matrix.m10 * oy + matrix.m20 * oz + matrix.m30;
        float tm31 = matrix.m01 * ox + matrix.m11 * oy + matrix.m21 * oz + matrix.m31;
        float tm32 = matrix.m02 * ox + matrix.m12 * oy + matrix.m22 * oz + matrix.m32;
        float nm00 = matrix.m00 * rm00 + matrix.m10 * rm01 + matrix.m20 * rm02;
        float nm01 = matrix.m01 * rm00 + matrix.m11 * rm01 + matrix.m21 * rm02;
        float nm02 = matrix.m02 * rm00 + matrix.m12 * rm01 + matrix.m22 * rm02;
        float nm03 = matrix.m03 * rm00 + matrix.m13 * rm01 + matrix.m23 * rm02;
        float nm10 = matrix.m00 * rm10 + matrix.m10 * rm11 + matrix.m20 * rm12;
        float nm11 = matrix.m01 * rm10 + matrix.m11 * rm11 + matrix.m21 * rm12;
        float nm12 = matrix.m02 * rm10 + matrix.m12 * rm11 + matrix.m22 * rm12;
        float nm13 = matrix.m03 * rm10 + matrix.m13 * rm11 + matrix.m23 * rm12;
        result.m20 = matrix.m00 * rm20 + matrix.m10 * rm21 + matrix.m20 * rm22;
        result.m21 = matrix.m01 * rm20 + matrix.m11 * rm21 + matrix.m21 * rm22;
        result.m22 = matrix.m02 * rm20 + matrix.m12 * rm21 + matrix.m22 * rm22;
        result.m23 = matrix.m03 * rm20 + matrix.m13 * rm21 + matrix.m23 * rm22;
        result.m00 = nm00;
        result.m01 = nm01;
        result.m02 = nm02;
        result.m03 = nm03;
        result.m10 = nm10;
        result.m11 = nm11;
        result.m12 = nm12;
        result.m13 = nm13;
        result.m30 = -nm00 * ox - nm10 * oy - matrix.m20 * oz + tm30;
        result.m31 = -nm01 * ox - nm11 * oy - matrix.m21 * oz + tm31;
        result.m32 = -nm02 * ox - nm12 * oy - matrix.m22 * oz + tm32;
        result.m33 = matrix.m33;
        return result;
    }


}
