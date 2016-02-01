/*********************************************************************************************
 *
 *
 * 'TessellCallBack.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.jts;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;

public class TessellCallBack implements GLUtessellatorCallback {

	// private GL2 gl;
	// private final GLU glu;

	public TessellCallBack(final GLU glu) {
		// this.glu = glu;
	}

	// GL2 gl() {
	// if ( gl == null ) {
	// gl = GLContext.getCurrentGL().getGL2();
	// }
	// return gl;
	// }

	@Override
	public void begin(final int type) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glBegin(type);
	}

	@Override
	public void end() {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glEnd();
	}

	@Override
	public void vertex(final Object vertexData) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double[] pointer;
		if ( vertexData instanceof double[] ) {
			pointer = (double[]) vertexData;
			if ( pointer.length == 6 ) {
				gl.glColor3dv(pointer, 3);
			}
			gl.glVertex3dv(pointer, 0);
		}

	}

	@Override
	public void vertexData(final Object vertexData, final Object polygonData) {}

	/*
	 * combineCallback is used to create a new vertex when edges intersect.
	 * coordinate location is trivial to calculate, but weight[4] may be
	 * used to average color, normal, or texture coordinate data. In this
	 * program, color is weighted.
	 */
	@Override
	public void combine(final double[] coords, final Object[] data, //
		final float[] weight, final Object[] outData) {
		double[] vertex = new double[6];
		int i;

		vertex[0] = coords[0];
		vertex[1] = coords[1];
		vertex[2] = coords[2];
		for ( i = 3; i < 6/* 7OutOfBounds from C! */; i++ ) {
			vertex[i] = weight[0] //
			* ((double[]) data[0])[i] + weight[1] * ((double[]) data[1])[i] + weight[2] * ((double[]) data[2])[i] +
				weight[3] * ((double[]) data[3])[i];
		}
		outData[0] = vertex;
	}

	@Override
	public void combineData(final double[] coords, final Object[] data, //
		final float[] weight, final Object[] outData, final Object polygonData) {}

	@Override
	public void error(final int errnum) {
		GLU glu = new GLU();
		String estring;
		estring = glu.gluErrorString(errnum);
		System.err.println("Tessellation Error: " + estring);
		// System.exit(0);s
	}

	@Override
	public void beginData(final int type, final Object polygonData) {}

	@Override
	public void endData(final Object polygonData) {}

	@Override
	public void edgeFlag(final boolean boundaryEdge) {}

	@Override
	public void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {}

	@Override
	public void errorData(final int errnum, final Object polygonData) {}
}// tessellCallBack