/*********************************************************************************************
 *
 * 'GeomMathUtils.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.vividsolutions.jts.geom.Coordinate;

//import Jama.Matrix;

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
		return vect1[0] * vect2[0] + vect1[1] * vect2[1] + vect1[2] * vect2[2];
	}

	public static float ScalarProduct(final float[] vect1, final float[] vect2) {
		return vect1[0] * vect2[0] + vect1[1] * vect2[1] + vect1[2] * vect2[2];
	}

	public static double[] Normalize(final double[] vect) {
		final double[] result = new double[vect.length];
		double sum = 0;
		for (int i = 0; i < vect.length; i++) {
			sum += Math.pow(vect[i], 2);
		}
		for (int i = 0; i < vect.length; i++) {
			result[i] = vect[i] / Math.sqrt(sum);
		}
		return result;
	}

	public static float[] Normalize(final float[] vect) {
		final float[] result = new float[vect.length];
		float sum = 0;
		for (int i = 0; i < vect.length; i++) {
			sum += Math.pow(vect[i], 2);
		}
		for (int i = 0; i < vect.length; i++) {
			result[i] = (float) (vect[i] / Math.sqrt(sum));
		}
		return result;
	}

	static public FloatBuffer getFloatBuffer(final Matrix4f matrix) {
		final FloatBuffer result = FloatBuffer.allocate(16);
		result.put(0, matrix.m00);
		result.put(1, matrix.m01);
		result.put(2, matrix.m02);
		result.put(3, matrix.m03);
		result.put(4, matrix.m10);
		result.put(5, matrix.m11);
		result.put(6, matrix.m12);
		result.put(7, matrix.m13);
		result.put(8, matrix.m20);
		result.put(9, matrix.m21);
		result.put(10, matrix.m22);
		result.put(11, matrix.m23);
		result.put(12, matrix.m30);
		result.put(13, matrix.m31);
		result.put(14, matrix.m32);
		result.put(15, matrix.m33);
		return result;
	}

	static public DoubleBuffer getDoubleBuffer(final Matrix4d matrix) {
		final DoubleBuffer result = DoubleBuffer.allocate(16);
		result.put(0, matrix.m00);
		result.put(1, matrix.m01);
		result.put(2, matrix.m02);
		result.put(3, matrix.m03);
		result.put(4, matrix.m10);
		result.put(5, matrix.m11);
		result.put(6, matrix.m12);
		result.put(7, matrix.m13);
		result.put(8, matrix.m20);
		result.put(9, matrix.m21);
		result.put(10, matrix.m22);
		result.put(11, matrix.m23);
		result.put(12, matrix.m30);
		result.put(13, matrix.m31);
		result.put(14, matrix.m32);
		result.put(15, matrix.m33);
		return result;
	}

	static public float[] getFloatArray(final Matrix4f matrix) {
		final float[] result = new float[16];
		result[0] = matrix.m00;
		result[1] = matrix.m01;
		result[2] = matrix.m02;
		result[3] = matrix.m03;
		result[4] = matrix.m10;
		result[5] = matrix.m11;
		result[6] = matrix.m12;
		result[7] = matrix.m13;
		result[8] = matrix.m20;
		result[9] = matrix.m21;
		result[10] = matrix.m22;
		result[11] = matrix.m23;
		result[12] = matrix.m30;
		result[13] = matrix.m31;
		result[14] = matrix.m32;
		result[15] = matrix.m33;
		return result;
	}

	static public double[] getDoubleArray(final Matrix4d matrix) {
		final double[] result = new double[16];
		result[0] = matrix.m00;
		result[1] = matrix.m01;
		result[2] = matrix.m02;
		result[3] = matrix.m03;
		result[4] = matrix.m10;
		result[5] = matrix.m11;
		result[6] = matrix.m12;
		result[7] = matrix.m13;
		result[8] = matrix.m20;
		result[9] = matrix.m21;
		result[10] = matrix.m22;
		result[11] = matrix.m23;
		result[12] = matrix.m30;
		result[13] = matrix.m31;
		result[14] = matrix.m32;
		result[15] = matrix.m33;
		return result;
	}

	static public float[] setTranslationToVertex(final float[] coordinates, final float x, final float y,
			final float z) {
		final float[] result = new float[coordinates.length];
		final int vertexNb = coordinates.length / 3;
		for (int i = 0; i < vertexNb; i++) {
			result[3 * i] = coordinates[i * 3] + x;
			result[3 * i + 1] = coordinates[i * 3 + 1] + y;
			result[3 * i + 2] = coordinates[i * 3 + 2] + z;
		}
		return result;
	}

	static public Coordinate[] setTranslationToCoordArray(final Coordinate[] coordinates, final double x,
			final double y, final double z) {
		final Coordinate[] result = new Coordinate[coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			result[i] = new Coordinate(coordinates[i].x + x, coordinates[i].y + y, coordinates[i].z + z);
		}
		return result;
	}

	static public float[] setRotationToVertex(final float[] coordinates, final float a, final float x, final float y,
			final float z) {
		final float[] result = new float[coordinates.length];
		final int vertexNb = coordinates.length / 3;
		for (int i = 0; i < vertexNb; i++) {
			// get the result of the rotation
			final double[] tmpResult = QuaternionRotate(
					new double[] { coordinates[i * 3], coordinates[i * 3 + 1], coordinates[i * 3 + 2] },
					new double[] { x, y, z, a });
			result[3 * i] = (float) tmpResult[0];
			result[3 * i + 1] = (float) tmpResult[1];
			result[3 * i + 2] = (float) tmpResult[2];
		}
		return result;
	}

	static public float[] setScalingToVertex(final float[] coordinates, final float x, final float y, final float z) {
		final float[] result = new float[coordinates.length];
		final int vertexNb = coordinates.length / 3;
		for (int i = 0; i < vertexNb; i++) {
			result[3 * i] = coordinates[3 * i] * x;
			result[3 * i + 1] = coordinates[3 * i + 1] * y;
			result[3 * i + 2] = coordinates[3 * i + 2] * z;
		}
		return result;
	}

	static public Coordinate[] setScalingToCoordArray(final Coordinate[] coordinates, final double x, final double y,
			final double z) {
		final Coordinate[] result = new Coordinate[coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			result[i] = new Coordinate(coordinates[i].x * x, coordinates[i].y * y, coordinates[i].z * z);
		}
		return result;
	}

	public static double[] QuaternionRotate(final double[] initVector3, final double[] quaternionRotation) {
		final double[] result = new double[3];
		// a, b, c are the normalized composants of the axis.
		final double[] axis = new double[] { quaternionRotation[0], quaternionRotation[1], quaternionRotation[2] };
		final double angle = quaternionRotation[3];
		final double a = axis[0] / Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
		final double b = -axis[1] / Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
		final double c = axis[2] / Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
		// x, y, z are the initial position of the light.
		final double x = initVector3[0];
		final double y = initVector3[1];
		final double z = initVector3[2];

		result[0] = x * (Math.cos(angle) + a * a * (1 - Math.cos(angle)))
				+ y * (a * b * (1 - Math.cos(angle)) - c * Math.sin(angle))
				+ z * (a * c * (1 - Math.cos(angle)) + b * Math.sin(angle));
		result[1] = x * (a * b * (1 - Math.cos(angle)) + c * Math.sin(angle))
				+ y * (Math.cos(angle) + b * b * (1 - Math.cos(angle)))
				+ z * (b * c * (1 - Math.cos(angle)) - a * Math.sin(angle));
		result[2] = x * (a * c * (1 - Math.cos(angle)) - b * Math.sin(angle))
				+ y * (b * c * (1 - Math.cos(angle)) + a * Math.sin(angle))
				+ z * (Math.cos(angle) + c * c * (1 - Math.cos(angle)));
		return result;
	}

	public static Matrix4f translateMatrix(final float x, final float y, final float z, final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		final Matrix4f result = (Matrix4f) matrix.clone();
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

	public static Matrix4f rotateMatrix(final float angle, final Vector3f axis, final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		return rotateMatrix(angle, axis.x, axis.y, axis.z, matrix);
	}

	public static Matrix4f rotateMatrix(final float angle, final float rotX, final float rotY, final float rotZ,
			final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		final Matrix4f result = (Matrix4f) matrix.clone();

		final float s = (float) Math.sin(angle);
		final float c = (float) Math.cos(angle);
		final float C = 1.0f - c;
		final float xx = rotX * rotX, xy = rotX * rotY, xz = rotX * rotZ;
		final float yy = rotY * rotY, yz = rotY * rotZ;
		final float zz = rotZ * rotZ;
		final float rm00 = xx * C + c;
		final float rm01 = xy * C + rotZ * s;
		final float rm02 = xz * C - rotY * s;
		final float rm10 = xy * C - rotZ * s;
		final float rm11 = yy * C + c;
		final float rm12 = yz * C + rotX * s;
		final float rm20 = xz * C + rotY * s;
		final float rm21 = yz * C - rotX * s;
		final float rm22 = zz * C + c;
		final float nm00 = matrix.m00 * rm00 + matrix.m10 * rm01 + matrix.m20 * rm02;
		final float nm01 = matrix.m01 * rm00 + matrix.m11 * rm01 + matrix.m21 * rm02;
		final float nm02 = matrix.m02 * rm00 + matrix.m12 * rm01 + matrix.m22 * rm02;
		final float nm03 = matrix.m03 * rm00 + matrix.m13 * rm01 + matrix.m23 * rm02;
		final float nm10 = matrix.m00 * rm10 + matrix.m10 * rm11 + matrix.m20 * rm12;
		final float nm11 = matrix.m01 * rm10 + matrix.m11 * rm11 + matrix.m21 * rm12;
		final float nm12 = matrix.m02 * rm10 + matrix.m12 * rm11 + matrix.m22 * rm12;
		final float nm13 = matrix.m03 * rm10 + matrix.m13 * rm11 + matrix.m23 * rm12;
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

	public static Matrix4f rotateMatrix(final float rotX, final float rotY, final float rotZ, final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		final Matrix4f result = (Matrix4f) matrix.clone();

		final float cosX = (float) Math.cos(rotX);
		final float sinX = (float) Math.sin(rotX);
		final float cosY = (float) Math.cos(rotY);
		final float sinY = (float) Math.sin(rotY);
		final float cosZ = (float) Math.cos(rotZ);
		final float sinZ = (float) Math.sin(rotZ);
		final float m_sinX = -sinX;
		final float m_sinY = -sinY;
		final float m_sinZ = -sinZ;

		// rotateX
		final float nm11 = cosX;
		final float nm12 = sinX;
		final float nm21 = m_sinX;
		final float nm22 = cosX;
		// rotateY
		final float nm00 = cosY;
		final float nm01 = nm21 * m_sinY;
		final float nm02 = nm22 * m_sinY;
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

	public static Matrix4f scaleMatrix(final float scaleX, final float scaleY, final float scaleZ,
			final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		final Matrix4f result = (Matrix4f) matrix.clone();
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

	public static Matrix4f rotateMatrixAroundLocal(final float[] quat, final float ox, final float oy, final float oz,
			final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		// quat : {x,y,z,w}
		final Matrix4f result = (Matrix4f) matrix.clone();
		final float dqx = quat[0] + quat[0];
		final float dqy = quat[1] + quat[1];
		final float dqz = quat[2] + quat[2];
		final float q00 = dqx * quat[0];
		final float q11 = dqy * quat[1];
		final float q22 = dqz * quat[2];
		final float q01 = dqx * quat[1];
		final float q02 = dqx * quat[2];
		final float q03 = dqx * quat[3];
		final float q12 = dqy * quat[2];
		final float q13 = dqy * quat[3];
		final float q23 = dqz * quat[3];
		final float lm00 = 1.0f - q11 - q22;
		final float lm01 = q01 + q23;
		final float lm02 = q02 - q13;
		final float lm10 = q01 - q23;
		final float lm11 = 1.0f - q22 - q00;
		final float lm12 = q12 + q03;
		final float lm20 = q02 + q13;
		final float lm21 = q12 - q03;
		final float lm22 = 1.0f - q11 - q00;
		final float tm00 = matrix.m00 - ox * matrix.m03;
		final float tm01 = matrix.m01 - oy * matrix.m03;
		final float tm02 = matrix.m02 - oz * matrix.m03;
		final float tm10 = matrix.m10 - ox * matrix.m13;
		final float tm11 = matrix.m11 - oy * matrix.m13;
		final float tm12 = matrix.m12 - oz * matrix.m13;
		final float tm20 = matrix.m20 - ox * matrix.m23;
		final float tm21 = matrix.m21 - oy * matrix.m23;
		final float tm22 = matrix.m22 - oz * matrix.m23;
		final float tm30 = matrix.m30 - ox * matrix.m33;
		final float tm31 = matrix.m31 - oy * matrix.m33;
		final float tm32 = matrix.m32 - oz * matrix.m33;
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

	public static Matrix4f rotateMatrixAround(final float[] quat, final float ox, final float oy, final float oz,
			final Matrix4f matrix) {
		// cf
		// https://github.com/JOML-CI/JOML/blob/master/src/org/joml/Matrix4f.java
		// quat : {x,y,z,w}
		final Matrix4f result = (Matrix4f) matrix.clone();
		final float dqx = quat[0] + quat[0];
		final float dqy = quat[1] + quat[1];
		final float dqz = quat[2] + quat[2];
		final float q00 = dqx * quat[0];
		final float q11 = dqy * quat[1];
		final float q22 = dqz * quat[2];
		final float q01 = dqx * quat[1];
		final float q02 = dqx * quat[2];
		final float q03 = dqx * quat[3];
		final float q12 = dqy * quat[2];
		final float q13 = dqy * quat[3];
		final float q23 = dqz * quat[3];
		final float rm00 = 1.0f - q11 - q22;
		final float rm01 = q01 + q23;
		final float rm02 = q02 - q13;
		final float rm10 = q01 - q23;
		final float rm11 = 1.0f - q22 - q00;
		final float rm12 = q12 + q03;
		final float rm20 = q02 + q13;
		final float rm21 = q12 - q03;
		final float rm22 = 1.0f - q11 - q00;
		final float tm30 = matrix.m00 * ox + matrix.m10 * oy + matrix.m20 * oz + matrix.m30;
		final float tm31 = matrix.m01 * ox + matrix.m11 * oy + matrix.m21 * oz + matrix.m31;
		final float tm32 = matrix.m02 * ox + matrix.m12 * oy + matrix.m22 * oz + matrix.m32;
		final float nm00 = matrix.m00 * rm00 + matrix.m10 * rm01 + matrix.m20 * rm02;
		final float nm01 = matrix.m01 * rm00 + matrix.m11 * rm01 + matrix.m21 * rm02;
		final float nm02 = matrix.m02 * rm00 + matrix.m12 * rm01 + matrix.m22 * rm02;
		final float nm03 = matrix.m03 * rm00 + matrix.m13 * rm01 + matrix.m23 * rm02;
		final float nm10 = matrix.m00 * rm10 + matrix.m10 * rm11 + matrix.m20 * rm12;
		final float nm11 = matrix.m01 * rm10 + matrix.m11 * rm11 + matrix.m21 * rm12;
		final float nm12 = matrix.m02 * rm10 + matrix.m12 * rm11 + matrix.m22 * rm12;
		final float nm13 = matrix.m03 * rm10 + matrix.m13 * rm11 + matrix.m23 * rm12;
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

	public static Matrix4f getHomographyf(final double[][] srcPoints, final double[][] dstPoints) {
		// see :
		// http://math.stackexchange.com/questions/296794/finding-the-transform-matrix-from-4-projected-points-with-javascript

		final double sx1 = srcPoints[0][0];
		final double sy1 = srcPoints[0][1];
		final double sx2 = srcPoints[1][0];
		final double sy2 = srcPoints[1][1];
		final double sx3 = srcPoints[2][0];
		final double sy3 = srcPoints[2][1];
		final double sx4 = srcPoints[3][0];
		final double sy4 = srcPoints[3][1];

		final double dx1 = dstPoints[0][0];
		final double dy1 = dstPoints[0][1];
		final double dx2 = dstPoints[1][0];
		final double dy2 = dstPoints[1][1];
		final double dx3 = dstPoints[2][0];
		final double dy3 = dstPoints[2][1];
		final double dx4 = dstPoints[3][0];
		final double dy4 = dstPoints[3][1];

		// 1) resolve the following equation for src and dst :
		// | x1 x2 x3 | | lamda | | x4 |
		// | y1 y2 y3 | . | mu | = | y4 |
		// | 1 1 1 | | to | | 1 |

		// for src :

		final double src_to = ((sx4 - sx1) * (sy2 - sy1) - (sy4 - sy1) * (sx2 - sx1))
				/ ((sx3 - sx1) * (sy2 - sy1) - (sy3 - sy1) * (sx2 - sx1));
		// double src_to = ( (sy4 - sy1)*(sx2 - sx1) - (sx4 - sx1)*(sy2 - sy1) )
		// /
		// ( (sx2 - sx1)*(sy3 - sy1)+(sx3 - sx1)*(sy2 - sy1) );
		final double src_mu = (sx4 - sx1 - src_to * (sx3 - sx1)) / (sx2 - sx1);
		final double src_lamda = 1 - src_mu - src_to;

		// for dst :

		final double dst_to = ((dx4 - dx1) * (dy2 - dy1) - (dy4 - dy1) * (dx2 - dx1))
				/ ((dx3 - dx1) * (dy2 - dy1) - (dy3 - dy1) * (dx2 - dx1));
		// double dst_to = ( (dy4 - dy1)*(dx2 - dx1) - (dx4 - dx1)*(dy2 - dy1) )
		// /
		// ( (dx2 - dx1)*(dy3 - dy1)+(dx3 - dx1)*(dy2 - dy1) );
		final double dst_mu = (dx4 - dx1 - dst_to * (dx3 - dx1)) / (dx2 - dx1);
		final double dst_lamda = 1 - dst_mu - dst_to;

		// 2) scale the columns by the coefficients just computed :
		// | lamda*x1 mu*x2 to*x3 |
		// | lamda*y1 mu*y2 to*y3 |
		// | lamda mu to |

		final Matrix3f A = new Matrix3f();
		A.m00 = (float) (src_lamda * sx1);
		A.m01 = (float) (src_mu * sx2);
		A.m02 = (float) (src_to * sx3);
		A.m10 = (float) (src_lamda * sy1);
		A.m11 = (float) (src_mu * sy2);
		A.m12 = (float) (src_to * sy3);
		A.m20 = (float) src_lamda;
		A.m21 = (float) src_mu;
		A.m22 = (float) src_to;

		final Matrix3f B = new Matrix3f();
		B.m00 = (float) (dst_lamda * dx1);
		B.m01 = (float) (dst_mu * dx2);
		B.m02 = (float) (dst_to * dx3);
		B.m10 = (float) (dst_lamda * dy1);
		B.m11 = (float) (dst_mu * dy2);
		B.m12 = (float) (dst_to * dy3);
		B.m20 = (float) dst_lamda;
		B.m21 = (float) dst_mu;
		B.m22 = (float) dst_to;

		// 3) compute Ainvert

		final Matrix3f Ainv = (Matrix3f) A.clone();
		Ainv.invert();

		// 4) compute C = B . Ainvert

		final Matrix3f C = (Matrix3f) B.clone();
		C.mul(Ainv);

		// 5) compute the final matrix

		final Matrix4f Result = new Matrix4f();
		Result.m00 = C.m00;
		Result.m01 = C.m01;
		Result.m02 = C.m02;
		Result.m03 = 0;
		Result.m10 = C.m10;
		Result.m11 = C.m11;
		Result.m12 = C.m12;
		Result.m13 = 0;
		Result.m20 = C.m20;
		Result.m21 = C.m21;
		Result.m22 = C.m22;
		Result.m23 = 0;
		Result.m30 = 0;
		Result.m31 = 0;
		Result.m32 = 0;
		Result.m33 = 0;

		return Result;
	}

	public static Matrix4d getHomography(final double[][] srcPoints, final double[][] dstPoints) {
		// see :
		// http://math.stackexchange.com/questions/296794/finding-the-transform-matrix-from-4-projected-points-with-javascript

		final double sx1 = srcPoints[0][0];
		final double sy1 = srcPoints[0][1];
		final double sx2 = srcPoints[1][0];
		final double sy2 = srcPoints[1][1];
		final double sx3 = srcPoints[2][0];
		final double sy3 = srcPoints[2][1];
		final double sx4 = srcPoints[3][0];
		final double sy4 = srcPoints[3][1];

		final double dx1 = dstPoints[0][0];
		final double dy1 = dstPoints[0][1];
		final double dx2 = dstPoints[1][0];
		final double dy2 = dstPoints[1][1];
		final double dx3 = dstPoints[2][0];
		final double dy3 = dstPoints[2][1];
		final double dx4 = dstPoints[3][0];
		final double dy4 = dstPoints[3][1];

		// 1) resolve the following equation for src and dst :
		// | x1 x2 x3 | | lamda | | x4 |
		// | y1 y2 y3 | . | mu | = | y4 |
		// | 1 1 1 | | to | | 1 |

		// for src :

		final double src_to = ((sx4 - sx1) * (sy2 - sy1) - (sy4 - sy1) * (sx2 - sx1))
				/ ((sx3 - sx1) * (sy2 - sy1) - (sy3 - sy1) * (sx2 - sx1));
		// double src_to = ( (sy4 - sy1)*(sx2 - sx1) - (sx4 - sx1)*(sy2 - sy1) )
		// /
		// ( (sx2 - sx1)*(sy3 - sy1)+(sx3 - sx1)*(sy2 - sy1) );
		final double src_mu = (sx4 - sx1 - src_to * (sx3 - sx1)) / (sx2 - sx1);
		final double src_lamda = 1 - src_mu - src_to;

		// for dst :

		final double dst_to = ((dx4 - dx1) * (dy2 - dy1) - (dy4 - dy1) * (dx2 - dx1))
				/ ((dx3 - dx1) * (dy2 - dy1) - (dy3 - dy1) * (dx2 - dx1));
		// double dst_to = ( (dy4 - dy1)*(dx2 - dx1) - (dx4 - dx1)*(dy2 - dy1) )
		// /
		// ( (dx2 - dx1)*(dy3 - dy1)+(dx3 - dx1)*(dy2 - dy1) );
		final double dst_mu = (dx4 - dx1 - dst_to * (dx3 - dx1)) / (dx2 - dx1);
		final double dst_lamda = 1 - dst_mu - dst_to;

		// 2) scale the columns by the coefficients just computed :
		// | lamda*x1 mu*x2 to*x3 |
		// | lamda*y1 mu*y2 to*y3 |
		// | lamda mu to |

		final Matrix3d A = new Matrix3d();
		A.m00 = src_lamda * sx1;
		A.m01 = src_mu * sx2;
		A.m02 = src_to * sx3;
		A.m10 = src_lamda * sy1;
		A.m11 = src_mu * sy2;
		A.m12 = src_to * sy3;
		A.m20 = src_lamda;
		A.m21 = src_mu;
		A.m22 = src_to;

		final Matrix3d B = new Matrix3d();
		B.m00 = dst_lamda * dx1;
		B.m01 = dst_mu * dx2;
		B.m02 = dst_to * dx3;
		B.m10 = dst_lamda * dy1;
		B.m11 = dst_mu * dy2;
		B.m12 = dst_to * dy3;
		B.m20 = dst_lamda;
		B.m21 = dst_mu;
		B.m22 = dst_to;

		// 3) compute Ainvert

		final Matrix3d Ainv = (Matrix3d) A.clone();
		Ainv.invert();

		// 4) compute C = B . Ainvert

		final Matrix3d C = (Matrix3d) B.clone();
		C.mul(Ainv);

		// 5) compute the final matrix

		final Matrix4d Result = new Matrix4d();
		Result.m00 = C.m00;
		Result.m01 = C.m01;
		Result.m02 = 0;
		Result.m03 = C.m02;
		Result.m10 = C.m10;
		Result.m11 = C.m11;
		Result.m12 = 0;
		Result.m13 = C.m12;
		Result.m20 = 0;
		Result.m21 = 0;
		Result.m22 = 1;
		Result.m23 = 0;
		Result.m30 = C.m20;
		Result.m31 = C.m21;
		Result.m32 = 0;
		Result.m33 = C.m22;

		return Result;
	}

	// public static Matrix4d getHomography2(double[][] srcPoints, double[][]
	// dstPoints) {
	// // see :
	// http://math.stackexchange.com/questions/494238/how-to-compute-homography-matrix-h-from-corresponding-points-2d-2d-planar-homog
	//
	// double sx1 = srcPoints[0][0];
	// double sy1 = srcPoints[0][1];
	// double sx2 = srcPoints[1][0];
	// double sy2 = srcPoints[1][1];
	// double sx3 = srcPoints[2][0];
	// double sy3 = srcPoints[2][1];
	// double sx4 = srcPoints[3][0];
	// double sy4 = srcPoints[3][1];
	//
	// double dx1 = dstPoints[0][0];
	// double dy1 = dstPoints[0][1];
	// double dx2 = dstPoints[1][0];
	// double dy2 = dstPoints[1][1];
	// double dx3 = dstPoints[2][0];
	// double dy3 = dstPoints[2][1];
	// double dx4 = dstPoints[3][0];
	// double dy4 = dstPoints[3][1];
	//
	// //Creating Arrays Representing Equations
	// double[][] lhsArray = {
	// {-sx1, -sy1, -1, 0, 0, 0, sx1*dx1, sy1*dx1},
	// {0, 0, 0, -dx1, -dy1, -1, sx1*dy1, sy1*dy1},
	// {-sx2, -sy2, -1, 0, 0, 0, sx2*dx2, sy2*dx2},
	// {0, 0, 0, -dx2, -dy2, -1, sx2*dy2, sy2*dy2},
	// {-sx3, -sy3, -1, 0, 0, 0, sx3*dx3, sy3*dx3},
	// {0, 0, 0, -dx3, -dy3, -1, sx3*dy3, sy3*dy3},
	// {-sx4, -sy4, -1, 0, 0, 0, sx4*dx4, sy4*dx4},
	// {0, 0, 0, -dx4, -dy4, -1, sx4*dy4, sy4*dy4}};
	// double[] rhsArray = {dx1, dy1, dx2, dy2, dx3, dy3, dx4, dy4};
	// //Creating Matrix Objects with arrays
	// Matrix lhs = new Matrix(lhsArray);
	// Matrix rhs = new Matrix(rhsArray, 8);
	// //Calculate Solved Matrix
	// Matrix ans = lhs.solve(rhs);
	//
	// Matrix4d Result = new Matrix4d();
	// Result.m00 = ans.get(0, 0);
	// Result.m01 = ans.get(1, 0);
	// Result.m02 = 0;
	// Result.m03 = ans.get(2, 0);
	// Result.m10 = ans.get(3, 0);
	// Result.m11 = ans.get(4, 0);
	// Result.m12 = 0;
	// Result.m13 = ans.get(5, 0);
	// Result.m20 = 0;
	// Result.m21 = 0;
	// Result.m22 = 1;
	// Result.m23 = 0;
	// Result.m30 = ans.get(6, 0);
	// Result.m31 = ans.get(7, 0);
	// Result.m32 = 0;
	// Result.m33 = 1;
	//
	// return Result;
	// }

	public static Matrix4d getHomography3(final double[][] srcPoints, final double[][] dstPoints) {

		// see :
		// http://math.stackexchange.com/questions/494238/how-to-compute-homography-matrix-h-from-corresponding-points-2d-2d-planar-homog

		final double sx1 = srcPoints[0][0];
		final double sy1 = srcPoints[0][1];
		final double sx2 = srcPoints[1][0];
		final double sy2 = srcPoints[1][1];
		final double sx3 = srcPoints[2][0];
		final double sy3 = srcPoints[2][1];
		final double sx4 = srcPoints[3][0];
		final double sy4 = srcPoints[3][1];

		final double dx1 = dstPoints[0][0];
		final double dy1 = dstPoints[0][1];
		final double dx2 = dstPoints[1][0];
		final double dy2 = dstPoints[1][1];
		final double dx3 = dstPoints[2][0];
		final double dy3 = dstPoints[2][1];
		final double dx4 = dstPoints[3][0];
		final double dy4 = dstPoints[3][1];

		final RealMatrix coefficients = new Array2DRowRealMatrix(new double[][] {
				{ -sx1, -sy1, -1, 0, 0, 0, sx1 * dx1, sy1 * dx1 }, { 0, 0, 0, -dx1, -dy1, -1, sx1 * dy1, sy1 * dy1 },
				{ -sx2, -sy2, -1, 0, 0, 0, sx2 * dx2, sy2 * dx2 }, { 0, 0, 0, -dx2, -dy2, -1, sx2 * dy2, sy2 * dy2 },
				{ -sx3, -sy3, -1, 0, 0, 0, sx3 * dx3, sy3 * dx3 }, { 0, 0, 0, -dx3, -dy3, -1, sx3 * dy3, sy3 * dy3 },
				{ -sx4, -sy4, -1, 0, 0, 0, sx4 * dx4, sy4 * dx4 }, { 0, 0, 0, -dx4, -dy4, -1, sx4 * dy4, sy4 * dy4 } },
				false);
		final DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();

		final RealVector constants = new ArrayRealVector(new double[] { dx1, dy1, dx2, dy2, dx3, dy3, dx4, dy4 },
				false);
		final RealVector solution = solver.solve(constants);

		final Matrix4d Result = new Matrix4d();
		Result.m00 = solution.getEntry(0);
		Result.m01 = solution.getEntry(1);
		Result.m02 = 0;
		Result.m03 = solution.getEntry(2);
		Result.m10 = solution.getEntry(3);
		Result.m11 = solution.getEntry(4);
		Result.m12 = 0;
		Result.m13 = solution.getEntry(5);
		Result.m20 = 0;
		Result.m21 = 0;
		Result.m22 = 1;
		Result.m23 = 0;
		Result.m30 = solution.getEntry(6);
		Result.m31 = solution.getEntry(7);
		Result.m32 = 0;
		Result.m33 = 1;

		return Result;

	}

}
