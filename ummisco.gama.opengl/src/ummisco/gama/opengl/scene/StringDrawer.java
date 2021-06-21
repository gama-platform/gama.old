/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.StringDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaFontType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.ui.utils.PlatformHelper;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class StringDrawer extends ObjectDrawer<StringObject> {
	private StringDrawerHelper builder;
	final static AffineTransform AT = AffineTransform.getScaleInstance(1.0, -1.0);
	final static FontRenderContext CONTEXT = new FontRenderContext(new AffineTransform(), true, true);

	public StringDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final StringObject s) {
		TextDrawingAttributes attributes = s.getAttributes();
		try {
			gl.pushMatrix();
			final AxisAngle rotation = attributes.getRotation();
			final GamaPoint p = attributes.getLocation();
			if (rotation != null) {
				gl.translateBy(p.x, p.y, p.z);
				final GamaPoint axis = rotation.getAxis();
				// AD Change to a negative rotation to fix Issue #1514
				gl.rotateBy(-rotation.getAngle(), axis.x, axis.y, axis.z);
				// Voids the location so as to make only one translation
				p.setLocation(0, 0, 0);
			}
			if (attributes.isPerspective()) {
				Font font = attributes.getFont();
				final int fontSize = PlatformHelper.scaleToHiDPI(Math.round(font.getSize()));
				if (fontSize != font.getSize()) { font = font.deriveFont((float) fontSize); }
				final float ratio = (float) gl.getRatios().y;
				final float scale = 1f / ratio;
				final Rectangle2D bounds = getBounds(s.getObject(), font);
				GamaPoint anchor = attributes.getAnchor();
				final double curX = p.x - bounds.getWidth() * scale * anchor.x;
				final double curY = p.y + bounds.getY() * scale * anchor.y;
				final double curZ = p.z /* + gl.getCurrentZTranslation() */;
				draw(s.getObject(), font, curX, curY, curZ, scale, attributes.getPrecision(), attributes.getDepth(),
						attributes.isEmpty(), attributes.getBorder());
			} else {
				int fontToUse = GLUT.BITMAP_HELVETICA_18;
				final Font f = s.getAttributes().getFont();
				if (f != null) {
					if (f.getSize() < 10) {
						fontToUse = GLUT.BITMAP_HELVETICA_10;
					} else if (f.getSize() < 16) { fontToUse = GLUT.BITMAP_HELVETICA_12; }
				}
				gl.rasterText(s.getObject(), fontToUse, p.x, p.y, p.z);
			}
		} finally {
			gl.popMatrix();
		}
	}

	public Rectangle2D getBounds(final String str, final Font font) {
		return font.createGlyphVector(CONTEXT, str).getOutline().getBounds2D();
	}

	public void draw(final String str, final Font f, final double xOff, final double yOff, final double zOff,
			final double scaleFactor, final double precision, final Double depth, final boolean wireframe,
			final GamaColor border) {
		Font font = f == null ? GamaFontType.DEFAULT_DISPLAY_FONT.getValue() : f;
		gl.push(GL2.GL_MODELVIEW);
		gl.translateBy(xOff, yOff, zOff);
		gl.scaleBy(scaleFactor, scaleFactor, scaleFactor);
		Shape shape = font.createGlyphVector(CONTEXT, str).getOutline();
		PathIterator pi = shape.getPathIterator(AT, precision);
		Rectangle2D bounds = shape.getBounds2D();
		getBuilder().resetWith(depth, wireframe, border, gl.isTextured(), bounds.getWidth(), bounds.getHeight());
		getBuilder().process(pi);
		getBuilder().drawOn(gl);
		gl.popMatrix();
	}

	@Override
	public void dispose() {}

	StringDrawerHelper getBuilder() {
		if (builder == null) { builder = new StringDrawerHelper(gl); }
		return builder;
	}

}