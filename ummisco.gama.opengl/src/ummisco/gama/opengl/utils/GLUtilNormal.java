/*********************************************************************************************
 * 
 * 
 * 'GLUtilNormal.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.awt.Color;
import ummisco.gama.opengl.JOGLRenderer;
import com.jogamp.opengl.*;

public class GLUtilNormal {

	// Calculate the normal, from three points on a surface
	public static double[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		// Step 1
		// build two vectors, one pointing from A to B, the other pointing from
		// A to C
		double[] vector1 = new double[3];
		double[] vector2 = new double[3];

		vector1[0] = pointB.x - pointA.x;
		vector2[0] = pointC.x - pointA.x;

		vector1[1] = pointB.y - pointA.y;
		vector2[1] = pointC.y - pointA.y;

		vector1[2] = pointB.z - pointA.z;
		vector2[2] = pointC.z - pointA.z;

		// Step 2
		// do the cross product of these two vectors to find the normal
		// of the surface

		double[] normal = new double[3];
		normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

		// Step 3
		// "normalise" the normal (make sure it has length of one)

		double total = 0.0d;
		for ( int i = 0; i < 3; i++ ) {
			total += normal[i] * normal[i];
		}
		double length = Math.sqrt(total);

		for ( int i = 0; i < 3; i++ ) {
			normal[i] /= length;
		}

		// done
		return normal;
	}

	public static Vertex GetCenter(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		Vertex center = new Vertex();
		center.x = (pointA.x + pointB.x + pointC.x) / 3;
		center.y = (pointA.y + pointB.y + pointC.y) / 3;
		center.z = (pointA.z + pointB.z + pointC.z) / 3;
		return center;
	}

	public static Vertex GetCenter(final Vertex[] vertices) {
		Vertex center = new Vertex();
		for ( int i = 0; i < vertices.length; i++ ) {
			center.x = center.x + vertices[i].x;
			center.y = center.y + vertices[i].y;
			center.z = center.z + vertices[i].z;
		}

		center.x = center.x / vertices.length;
		center.y = center.y / vertices.length;
		center.z = center.z / vertices.length;
		return center;
	}

	public static void HandleNormal(final Vertex[] vertices, final Color c, final double alpha, final int norm_dir,
		final JOGLRenderer renderer) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double[] normalmean = new double[3];
		for ( int i = 0; i < vertices.length - 2; i++ ) {
			double[] normal = CalculateNormal(vertices[i + 2], vertices[i + 1], vertices[i]);
			normalmean[0] = normalmean[0] + normal[0];
			normalmean[1] = normalmean[1] + normal[1];
			normalmean[2] = normalmean[2] + normal[2];
		}

		normalmean[0] = norm_dir * normalmean[0] / vertices.length;
		normalmean[1] = norm_dir * normalmean[1] / vertices.length;
		normalmean[2] = norm_dir * normalmean[2] / vertices.length;

		gl.glNormal3dv(normalmean, 0);

		normalmean[0] = renderer.getMaxEnvDim() / 20 * normalmean[0];
		normalmean[1] = renderer.getMaxEnvDim() / 20 * normalmean[1];
		normalmean[2] = renderer.getMaxEnvDim() / 20 * normalmean[2];

		if ( renderer.data.isDraw_norm() ) {
			Vertex center = GetCenter(vertices);
			gl.glBegin(GL.GL_LINES);
			gl.glColor3d(1.0, 0.0, 0.0);
			gl.glVertex3d(center.x, center.y, center.z);
			gl.glVertex3d(center.x + normalmean[0], center.y + normalmean[1], center.z + normalmean[2]);
			gl.glEnd();

			gl.glPointSize(2.0f);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex3d(center.x + normalmean[0], center.y + normalmean[1], center.z + normalmean[2]);
			gl.glEnd();

			if ( c != null ) {
				gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha * c.getAlpha() /
					255.0);
			}

		}

	}
	
	
	
}
