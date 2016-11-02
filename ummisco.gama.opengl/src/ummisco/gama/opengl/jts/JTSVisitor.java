/*********************************************************************************************
 *
 * 'JTSVisitor.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.jts;

import com.jogamp.opengl.*;
import com.vividsolutions.jts.geom.*;

public class JTSVisitor implements CoordinateSequenceFilter {

	public JTSVisitor() {}

	@Override
	public void filter(final CoordinateSequence seq, final int i) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( Double.isNaN(seq.getCoordinate(i).z) == true ) {
			gl.glVertex3f((float) seq.getX(i % seq.size()), (float) -seq.getY(i % seq.size()), 0.0f);
			gl.glVertex3f((float) seq.getX((i + 1) % seq.size()), (float) -seq.getY((i + 1) % seq.size()), 0.0f);
		} else {
			gl.glVertex3f((float) seq.getX(i % seq.size()), (float) -seq.getY(i % seq.size()),
				(float) seq.getCoordinate(i % seq.size()).z);
			gl.glVertex3f((float) seq.getX((i + 1) % seq.size()), (float) -seq.getY((i + 1) % seq.size()),
				(float) seq.getCoordinate((i + 1) % seq.size()).z);
		}

	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeometryChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
