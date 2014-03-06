package msi.gama.jogl.utils.JTSGeometryOpenGLDrawer;

import javax.media.opengl.GL;
import com.vividsolutions.jts.geom.*;

public class JTSVisitor implements CoordinateSequenceFilter {

	private final GL myGl;

	public JTSVisitor(final GL gl) {
		myGl = gl;
	}

	@Override
	public void filter(final CoordinateSequence seq, final int i) {
		// TODO Auto-generated method stub

		if ( Double.isNaN(seq.getCoordinate(i).z) == true ) {
			myGl.glVertex3f((float) seq.getX(i % seq.size()), (float) -seq.getY(i % seq.size()), 0.0f);
			myGl.glVertex3f((float) seq.getX((i + 1) % seq.size()), (float) -seq.getY((i + 1) % seq.size()), 0.0f);
		} else {
			myGl.glVertex3f((float) seq.getX(i % seq.size()), (float) -seq.getY(i % seq.size()),
				(float) seq.getCoordinate(i % seq.size()).z);
			myGl.glVertex3f((float) seq.getX((i + 1) % seq.size()), (float) -seq.getY((i + 1) % seq.size()),
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
