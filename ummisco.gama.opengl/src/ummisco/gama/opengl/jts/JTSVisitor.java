/*********************************************************************************************
 *
 * 'JTSVisitor.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.jts;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

import ummisco.gama.opengl.JOGLRenderer;

public class JTSVisitor implements CoordinateSequenceFilter {

	public JTSVisitor() {}

	@Override
	public void filter(final CoordinateSequence seq, final int i) {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		// final int ii1 = (i + 1) % seq.size();
		gl.glVertex3d(seq.getX(i), JOGLRenderer.Y_FLAG * seq.getY(i), seq.getOrdinate(i, 2));
		// gl.glVertex3d(seq.getX(ii1), JOGLRenderer.Y_FLAG * seq.getY(ii1), seq.getOrdinate(ii1, 2));
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean isGeometryChanged() {
		return false;
	}

}
