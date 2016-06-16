package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ummisco.gama.opengl.camera.ICamera;

public class Maths {
	
	static public Matrix4f createTransformationMatrix(Vector3f positions, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		Matrix4f tmpMatrix = new Matrix4f();
		matrix.setIdentity();
		
		// translation
		matrix.setTranslation(positions);
//		tmpMatrix.setTranslation(new Vector3f(-50,50,0));
//		matrix.mul(tmpMatrix);
		
		// scale
		matrix.setScale(scale);
//		matrix.mul(tmpMatrix);
		
		// rotation
		tmpMatrix.rotX(rx);
		matrix.mul(tmpMatrix);
		tmpMatrix.rotY(ry);
		matrix.mul(tmpMatrix);
		tmpMatrix.rotZ(rz);
		matrix.mul(tmpMatrix);
		
//		tmpMatrix.setTranslation(new Vector3f(50,-50,0));
//		matrix.mul(tmpMatrix);

		return matrix;
	}
	
	public static double[] CrossProduct(final double[] vect1, final double[] vect2) {
		final double[] result = new double[3];
		result[0] = vect1[1] * vect2[2] - vect1[2] * vect2[1];
		result[1] = vect1[2] * vect2[0] - vect1[0] * vect2[2];
		result[2] = vect1[0] * vect2[1] - vect1[1] * vect2[0];
		return result;
	}
	
	public static double ScalarProduct(final double[] vect1, final double[] vect2) {
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
		
//		viewMatrix.m00 = (float) sVect[0];
//		viewMatrix.m01 = (float) vVect[0];
//		viewMatrix.m02 = (float) fVect[0];
//		viewMatrix.m03 = (float) pVect[0];
//		viewMatrix.m10 = (float) sVect[1];
//		viewMatrix.m11 = (float) vVect[1];
//		viewMatrix.m12 = (float) fVect[1];
//		viewMatrix.m13 = (float) pVect[1];
//		viewMatrix.m20 = (float) sVect[2];
//		viewMatrix.m21 = (float) vVect[2];
//		viewMatrix.m22 = (float) fVect[2];
//		viewMatrix.m23 = (float) pVect[2];
//		viewMatrix.m30 = (float) 0;
//		viewMatrix.m31 = (float) 0;
//		viewMatrix.m32 = (float) 0;
//		viewMatrix.m33 = (float) 1;
		
		viewMatrix.transpose();
		
		
//		Matrix4f viewMatrix = new Matrix4f();
//		Matrix4f tmpMatrix = new Matrix4f();
//		viewMatrix.setIdentity();
//		
//		// rotate
//		tmpMatrix.rotX((float) camera.getPitch());
//		viewMatrix.mul(tmpMatrix);
//		tmpMatrix.rotY((float) camera.getYaw());
//		viewMatrix.mul(tmpMatrix);
//		tmpMatrix.rotZ((float) camera.getRoll());
//		viewMatrix.mul(tmpMatrix);
//		
//		// translate
//		Vector3f cameraPos = new Vector3f((float)camera.getPosition().getX(),(float)camera.getPosition().getY(),(float)camera.getPosition().getZ());
//		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
////		Vector3f negativeCameraPos = new Vector3f(0,0,0);
//		viewMatrix.setTranslation(negativeCameraPos);
//		
//		viewMatrix.transpose();
		
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
}
