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
	
	
//	static public void glMultMatrixf(FloatBuffer a, FloatBuffer b, FloatBuffer d) {
//		final int aP = a.position();
//		final int bP = b.position();
//		final int dP = d.position();
//		for (int i = 0; i < 4; i++) {
//			final float ai0=a.get(aP+i+0*4),  ai1=a.get(aP+i+1*4),  ai2=a.get(aP+i+2*4),  ai3=a.get(aP+i+3*4);
//			d.put(dP+i+0*4 , ai0 * b.get(bP+0+0*4) + ai1 * b.get(bP+1+0*4) + ai2 * b.get(bP+2+0*4) + ai3 * b.get(bP+3+0*4) );
//		 	d.put(dP+i+1*4 , ai0 * b.get(bP+0+1*4) + ai1 * b.get(bP+1+1*4) + ai2 * b.get(bP+2+1*4) + ai3 * b.get(bP+3+1*4) );
//			d.put(dP+i+2*4 , ai0 * b.get(bP+0+2*4) + ai1 * b.get(bP+1+2*4) + ai2 * b.get(bP+2+2*4) + ai3 * b.get(bP+3+2*4) );
//			d.put(dP+i+3*4 , ai0 * b.get(bP+0+3*4) + ai1 * b.get(bP+1+3*4) + ai2 * b.get(bP+2+3*4) + ai3 * b.get(bP+3+3*4) );
//		}
//	}
//
//	static public float[] multiply(float[] a,float[] b){
//		float[] tmp = new float[16];
//		glMultMatrixf(FloatBuffer.wrap(a),FloatBuffer.wrap(b),FloatBuffer.wrap(tmp));
//		return tmp;
//	}
//
//	static public float[] translate(float[] m,float x,float y,float z){
//		float[] t = { 1.0f, 0.0f, 0.0f, 0.0f,
//			0.0f, 1.0f, 0.0f, 0.0f,
//			0.0f, 0.0f, 1.0f, 0.0f,
//			x, y, z, 1.0f };
//		return multiply(m, t);
//	}
//
//	static public float[] rotate(float[] m,float a,float x,float y,float z){
//		float s, c;
//		s = (float)Math.sin(Math.toRadians(a));
//		c = (float)Math.cos(Math.toRadians(a));
//		float[] r = {
//			x * x * (1.0f - c) + c,     y * x * (1.0f - c) + z * s, x * z * (1.0f - c) - y * s, 0.0f,
//			x * y * (1.0f - c) - z * s, y * y * (1.0f - c) + c,     y * z * (1.0f - c) + x * s, 0.0f,
//			x * z * (1.0f - c) + y * s, y * z * (1.0f - c) - x * s, z * z * (1.0f - c) + c,     0.0f,
//			0.0f, 0.0f, 0.0f, 1.0f 
//			};
//		return multiply(m, r);
//	}
}
