package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

public class Maths {
	
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
