package ummisco.gama.opengl.jts;

import java.awt.Color;
import java.math.BigInteger;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.Pie3DObject;
import msi.gama.util.GamaColor;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.vividsolutions.jts.geom.Polygon;

public class Pie3DDrawer extends JTSDrawer {

	public Pie3DDrawer(final JOGLRenderer gLRender) {
		super(gLRender);
		// TODO Auto-generated constructor stub
	}

	public void _draw(final Pie3DObject geometry) {
		switch (geometry.type) {
			case HEMISPHERE:
				drawHemiSphereChart(geometry);
				break;
			case PIESPHERE:
				drawPieSphere(geometry);
				break;
			case PIESPHEREWITHDYNAMICALCOLOR:
				drawPieSphere(geometry);
				break;
			case PACMAN:
				drawPacMan(geometry);
				break;
			case ANTISLICE:
				drawPac(geometry);
				break;
			case SLICE:
				drawMan(geometry);
				break;
			default:
		}
	}

	public void drawHemiSphereChart(final Pie3DObject g) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			setColor(c, g.getAlpha());
		}
		GLU myGlu = renderer.getGlu();
		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !renderer.data.isTriangulation() ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;

		gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
		if ( g.ratio.get(0) > 0 ) {
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, new double[] { 1.0, 0, 0, -(1 - 2 * g.ratio.get(0)) * g.height / 2 },
				0);
			myGlu.gluSphere(quad, g.height, slices, stacks);
		} else {
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0,
				new double[] { -1.0, 0, 0, (1 - 2 * -g.ratio.get(0)) * g.height / 2 }, 0);
			myGlu.gluSphere(quad, g.height, slices, stacks);
		}

		gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
	}

	public void drawPieSphere(final Pie3DObject g) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		GamaColor curColor;

		Double curRatio = 0.0;
		int curIndex = 0;

		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			setColor(c, g.getAlpha());
		}
		GLU myGlu = renderer.getGlu();

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !renderer.data.isTriangulation() ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;

		for ( Double curR : g.ratio ) {

			gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE1);

			int n = g.ratio.size();
			int k = n / 4;

			// create 3 BigInteger objects
			BigInteger bi1, bi2, bi3;

			// assign values to bi1, bi2
			bi1 = new BigInteger("" + k);
			bi2 = new BigInteger("" + n);

			// assign gcd of bi1, bi2 to bi3
			bi3 = bi1.gcd(bi2);

			while (bi3.doubleValue() > 1.0) {
				k = k + 1;
				bi1 = new BigInteger("" + k);
				bi3 = bi1.gcd(bi2);
			}
			if ( g.colors.size() == 0 || g.colors == null ) {
				curColor = new GamaColor(Color.getHSBColor((float) k / (float) n * curIndex, 1.0f, 1.0f), 1.0);
			} else {
				curColor = g.colors.get(curIndex);
			}

			setColor(curColor, g.getAlpha());

			if ( curR <= 0.5 ) {
				gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0,
					new double[] { -Math.sin(2 * curRatio * Math.PI), Math.cos(2 * curRatio * Math.PI), 0, 0 }, 0);
				gl.glClipPlane(
					GL2ES1.GL_CLIP_PLANE1,
					new double[] { Math.sin(2 * (curRatio + curR) * Math.PI),
						-Math.cos(2 * (curRatio + curR) * Math.PI), 0, 0 }, 0);
				myGlu.gluSphere(quad, g.height, slices, stacks);
			} else {
				gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0,
					new double[] { -Math.sin(2 * curRatio * Math.PI), Math.cos(2 * curRatio * Math.PI), 0, 0 }, 0);
				gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1,
					new double[] { Math.sin(2 * (curRatio + 0.5) * Math.PI), -Math.cos(2 * (curRatio + 0.5) * Math.PI),
						0, 0 }, 0);
				myGlu.gluSphere(quad, g.height, slices, stacks);
				gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0,
					new double[] { -Math.sin(2 * (curRatio + 0.5) * Math.PI), Math.cos(2 * (curRatio + 0.5) * Math.PI),
						0, 0 }, 0);
				gl.glClipPlane(
					GL2ES1.GL_CLIP_PLANE1,
					new double[] { Math.sin(2 * (curRatio + curR) * Math.PI),
						-Math.cos(2 * (curRatio + curR) * Math.PI), 0, 0 }, 0);
				myGlu.gluSphere(quad, g.height, slices, stacks);
			}

			gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
			gl.glDisable(GL2ES1.GL_CLIP_PLANE1);

			curRatio = curRatio + curR;
			curIndex = curIndex + 1;
		}
	}

	public void drawPacMan(final Pie3DObject g) {
		drawPac(g);
	}

	public void drawPac(final Pie3DObject g) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			setColor(c, g.getAlpha());
		}
		GLU myGlu = renderer.getGlu();

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !renderer.data.isTriangulation() ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;

		gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
		gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, new double[] { 0, 1, 0, 0 }, 0);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1,
			new double[] { -Math.sin(g.ratio.get(0) * Math.PI), Math.cos(g.ratio.get(0) * Math.PI), 0, 0 }, 0);
		myGlu.gluSphere(quad, g.height, slices, stacks);

		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, new double[] { 0, -1, 0, 0 }, 0);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1,
			new double[] { -Math.sin(g.ratio.get(0) * Math.PI), -Math.cos(g.ratio.get(0) * Math.PI), 0, 0 }, 0);
		myGlu.gluSphere(quad, g.height, slices, stacks);

		gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
		gl.glDisable(GL2ES1.GL_CLIP_PLANE1);
	}

	public void drawMan(final Pie3DObject g) {
		Pie3DObject g2 = (Pie3DObject) g.clone();
		g2.ratio.set(0, g.ratio.get(0) + 1);
		drawPac(g2);
	}

}
