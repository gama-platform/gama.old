package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

import ummisco.gama.opengl.camera.ICamera;

public class Maths {
	
	static public Matrix4f createTransformationMatrix(Vector3f positions, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		Matrix4f tmpMatrix = new Matrix4f();
		matrix.setIdentity();
		
		// translation
		matrix.setTranslation(positions);
		
		// scale
		matrix.setScale(scale);
		
		// rotation
		tmpMatrix.rotX(rx);
		matrix.mul(tmpMatrix);
		tmpMatrix.rotY(ry);
		matrix.mul(tmpMatrix);
		tmpMatrix.rotZ(rz);
		matrix.mul(tmpMatrix);

		return matrix;
	}
	
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
	
	public static Matrix4f createViewMatrix(ICamera camera) {
		// see http://in2gpu.com/2015/05/17/view-matrix/
		Matrix4f viewMatrix = new Matrix4f();
		
		double[] fVect = new double[3]; // forward vector : direction vector of the camera.
		double[] sVect = new double[3]; // orthogonal vector : "right" or "sideways" vector.
		double[] vVect = new double[3]; // cross product between f and s.
		double[] pVect = new double[3]; // camera position.
		
		double sum = Math.pow(camera.getTarget().x - camera.getPosition().x,2)
				+ Math.pow(camera.getTarget().y - camera.getPosition().y,2)
				+ Math.pow(camera.getTarget().z - camera.getPosition().z,2);
		fVect[0] = -(camera.getTarget().x - camera.getPosition().x) / Math.sqrt(sum);
		fVect[1] = (camera.getTarget().y - camera.getPosition().y) / Math.sqrt(sum);
		fVect[2] = -(camera.getTarget().z - camera.getPosition().z) / Math.sqrt(sum);
		
		double[] crossProduct = CrossProduct(fVect,new double[]{camera.getOrientation().x,
				-camera.getOrientation().y,camera.getOrientation().z});
		sVect = Normalize(crossProduct);
		
		vVect = CrossProduct(sVect,fVect);
		
		pVect = new double[]{camera.getPosition().x,
				-camera.getPosition().y,camera.getPosition().z};
		
		viewMatrix.m00 = (float) sVect[0];
		viewMatrix.m01 = (float) sVect[1];
		viewMatrix.m02 = (float) sVect[2];
		viewMatrix.m03 = (float) -ScalarProduct(sVect,pVect);
		viewMatrix.m10 = (float) vVect[0];
		viewMatrix.m11 = (float) vVect[1];
		viewMatrix.m12 = (float) vVect[2];
		viewMatrix.m13 = (float) -ScalarProduct(vVect,pVect);
		viewMatrix.m20 = (float) fVect[0];
		viewMatrix.m21 = (float) fVect[1];
		viewMatrix.m22 = (float) fVect[2];
		viewMatrix.m23 = (float) -ScalarProduct(fVect,pVect);
		viewMatrix.m30 = (float) 0;
		viewMatrix.m31 = (float) 0;
		viewMatrix.m32 = (float) 0;
		viewMatrix.m33 = (float) 1;
		
		viewMatrix.transpose();
		
		return viewMatrix;
	}
	
	static public void glMultMatrixf(FloatBuffer a, FloatBuffer b, FloatBuffer d) {
		final int aP = a.position();
		final int bP = b.position();
		final int dP = d.position();
		for (int i = 0; i < 4; i++) {
			final float ai0=a.get(aP+i+0*4),  ai1=a.get(aP+i+1*4),  ai2=a.get(aP+i+2*4),  ai3=a.get(aP+i+3*4);
			d.put(dP+i+0*4 , ai0 * b.get(bP+0+0*4) + ai1 * b.get(bP+1+0*4) + ai2 * b.get(bP+2+0*4) + ai3 * b.get(bP+3+0*4) );
		 	d.put(dP+i+1*4 , ai0 * b.get(bP+0+1*4) + ai1 * b.get(bP+1+1*4) + ai2 * b.get(bP+2+1*4) + ai3 * b.get(bP+3+1*4) );
			d.put(dP+i+2*4 , ai0 * b.get(bP+0+2*4) + ai1 * b.get(bP+1+2*4) + ai2 * b.get(bP+2+2*4) + ai3 * b.get(bP+3+2*4) );
			d.put(dP+i+3*4 , ai0 * b.get(bP+0+3*4) + ai1 * b.get(bP+1+3*4) + ai2 * b.get(bP+2+3*4) + ai3 * b.get(bP+3+3*4) );
		}
	}

	static public float[] multiply(float[] a,float[] b){
		float[] tmp = new float[16];
		glMultMatrixf(FloatBuffer.wrap(a),FloatBuffer.wrap(b),FloatBuffer.wrap(tmp));
		return tmp;
	}

	static public float[] translate(float[] m,float x,float y,float z){
		float[] t = { 1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			x, y, z, 1.0f };
		return multiply(m, t);
	}

	static public float[] rotate(float[] m,float a,float x,float y,float z){
		float s, c;
		s = (float)Math.sin(Math.toRadians(a));
		c = (float)Math.cos(Math.toRadians(a));
		float[] r = {
			x * x * (1.0f - c) + c,     y * x * (1.0f - c) + z * s, x * z * (1.0f - c) - y * s, 0.0f,
			x * y * (1.0f - c) - z * s, y * y * (1.0f - c) + c,     y * z * (1.0f - c) + x * s, 0.0f,
			x * z * (1.0f - c) + y * s, y * z * (1.0f - c) - x * s, z * z * (1.0f - c) + c,     0.0f,
			0.0f, 0.0f, 0.0f, 1.0f 
			};
		return multiply(m, r);
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
	
	public static float[] getNormals(final float[] coordinates, final float[] idxBuffer) {
		
		float[] result = new float[coordinates.length];
		
		int vertexNb = coordinates.length / 3;
		
		for (int i = 0 ; i < vertexNb ; i++) {
			
			float xVal = 0;
			float yVal = 0;
			float zVal = 0;
			float sum = 0;
			// search the triangle where the vertex is
			for (int j = 0 ; j < idxBuffer.length ; j++) {
				if ( (int)idxBuffer[j] == i ) {
					boolean computeNormal = true;
					
					int positionInTriangle = j % 3;
					
					int idxOfPreviousTriangle = (int) ((positionInTriangle == 0) ? idxBuffer[j+2] : idxBuffer[j-1]);
					int idxOfNextTriangle = (int) ((positionInTriangle == 2) ? idxBuffer[j-2] : idxBuffer[j+1]);
					
					// check if the line from the current vertex to the previous/next vertex represents the diagonal of a rectangular face
					// we assume that indices of vertices for rectangular faces are all grouped 6 by 6
					int startIdxOfFace = (j/6)*6; // 7 -> 6, 8 -> 6.
					// we search first the number of occurences of each vertices in the index array (to determine which vertices compose the diagonal)
					HashMap<Integer,Integer> mapOccurencesIdx = new HashMap<Integer,Integer>();
					for (int k = 0 ; k < 6 ; k++) {
						if (mapOccurencesIdx.containsKey((int)idxBuffer[startIdxOfFace+k])) {
							mapOccurencesIdx.put((int) idxBuffer[startIdxOfFace+k], mapOccurencesIdx.get((int)idxBuffer[startIdxOfFace+k])+1);
						}
						else {
							mapOccurencesIdx.put((int) idxBuffer[startIdxOfFace+k], 1);
						}
					}
					// we check if the current vertex is in the diagonal (if its occurence is equal to 2).
//					if ((mapOccurencesIdx.containsKey((int)idxBuffer[j])) && (mapOccurencesIdx.get((int)idxBuffer[j]) == 2))
//					{
//						// if it is the case, we check if both of the previous and next vertices are not in the diagonal. 
//						if ((mapOccurencesIdx.containsKey((int)idxOfPreviousTriangle)) && (mapOccurencesIdx.get((int)idxOfPreviousTriangle) == 2)
//								|| (mapOccurencesIdx.containsKey((int)idxOfNextTriangle)) && (mapOccurencesIdx.get((int)idxOfNextTriangle) == 2))
//						{
//							// if it is the case, we do not compute this normal.
//							computeNormal = false;
//						}
//					}
					
					if (computeNormal) 
					{
						double[] firstVect = new double[] {
								coordinates[idxOfPreviousTriangle*3] - coordinates[(int) ((idxBuffer[j])*3)],
								coordinates[idxOfPreviousTriangle*3+1] - coordinates[(int) ((idxBuffer[j])*3)+1],
								coordinates[idxOfPreviousTriangle*3+2] - coordinates[(int) ((idxBuffer[j])*3)+2],
						};
						double[] secondVect = new double[] {
								coordinates[idxOfNextTriangle*3] - coordinates[(int) ((idxBuffer[j])*3)],
								coordinates[idxOfNextTriangle*3+1] - coordinates[(int) ((idxBuffer[j])*3)+1],
								coordinates[idxOfNextTriangle*3+2] - coordinates[(int) ((idxBuffer[j])*3)+2],
						};
						double[] vectProduct = CrossProduct(firstVect,secondVect);
						
						sum = (float) (vectProduct[0]*vectProduct[0] + vectProduct[1]*	vectProduct[1] + vectProduct[2]*vectProduct[2]);
						xVal += vectProduct[0] / Math.sqrt(sum);
						yVal += vectProduct[1] / Math.sqrt(sum);
						zVal += vectProduct[2] / Math.sqrt(sum);
					}
				}
			}
			sum = xVal*xVal + yVal*yVal + zVal*zVal;
			xVal = (float) (xVal / Math.sqrt(sum));
			yVal = (float) (yVal / Math.sqrt(sum));
			zVal = (float) (zVal / Math.sqrt(sum));
			
			result[3*i] = xVal;
			result[3*i+1] = yVal;
			result[3*i+2] = zVal;
		}
		
		return result;
	}
}
