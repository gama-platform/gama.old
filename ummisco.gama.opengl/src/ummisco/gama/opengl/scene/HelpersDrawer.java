package ummisco.gama.opengl.scene;

import java.awt.Color;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.JOGLRenderer;

public class HelpersDrawer {

	final JOGLRenderer renderer;

	public HelpersDrawer(final JOGLRenderer renderer) {
		this.renderer = renderer;
	}

	public void drawROIHelper(final GL2 gl, final Envelope3D envelope) {
		if (envelope == null || gl == null)
			return;
		final GamaPoint pos = envelope.centre();
		final double width = envelope.getWidth();
		final double height = envelope.getHeight();
		final double z = Math.max(2, renderer.getMaxEnvDim() / 100);
		final GLUT glut = renderer.getGlut();
		try {
			gl.glPushMatrix();
			gl.glTranslated(pos.x, pos.y, pos.z);
			gl.glScaled(width, height, z);
			renderer.setCurrentColor(0, 0.5, 0, 0.15);
			glut.glutSolidCube(0.9999f);
			renderer.setCurrentColor(Color.gray, 1);
			glut.glutWireCube(1f);
		} finally {
			gl.glPopMatrix();
		}
	}

	public void drawRotationHelper(final GL2 gl, final GamaPoint pos, final double distance) {
		// TODO
		if (gl == null) { return; }
		final int slices = GamaPreferences.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;
		final double radius = distance / 400;
		final GLUT glut = renderer.getGlut();
		try {
			gl.glPushMatrix();
			gl.glTranslated(pos.x, pos.y, pos.z);
			renderer.setCurrentColor(Color.gray, 1);
			glut.glutSolidSphere(5.0 * radius, slices, stacks);
			renderer.setCurrentColor(Color.gray, 0.1);
			glut.glutSolidSphere(49.0 * radius, slices, stacks);
			renderer.setCurrentColor(Color.gray, 1);
			glut.glutWireSphere(50.0 * radius, slices, stacks);
		} finally {
			gl.glPopMatrix();
		}
	}
}
