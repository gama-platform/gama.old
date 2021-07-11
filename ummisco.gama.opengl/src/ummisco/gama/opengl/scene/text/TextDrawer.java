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
package ummisco.gama.opengl.scene.text;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.ui.utils.PlatformHelper;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class TextDrawer extends ObjectDrawer<StringObject> {
	private final TextDrawerHelper builder = new TextDrawerHelper();
	final static AffineTransform AT = AffineTransform.getScaleInstance(1.0, -1.0);
	final static FontRenderContext CONTEXT = new FontRenderContext(new AffineTransform(), true, true);

	public TextDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final StringObject s) {
		TextDrawingAttributes attributes = s.getAttributes();
		final AxisAngle rotation = attributes.getRotation();
		final GamaPoint p = attributes.getLocation();
		gl.pushMatrix();
		if (rotation != null) {
			gl.translateBy(p.x, p.y, p.z);
			final GamaPoint axis = rotation.getAxis();
			// AD Change to a negative rotation to fix Issue #1514
			gl.rotateBy(-rotation.getAngle(), axis.x, axis.y, axis.z);
			// Voids the location so as to make only one translation
			p.setLocation(0, 0, 0);
		}
		if (!attributes.isPerspective()) {
			drawBitmap(s.getObject(), attributes, p);
		} else {
			Font font = attributes.getFont();
			final int fontSize = PlatformHelper.scaleToHiDPI(Math.round(font.getSize()));
			if (fontSize != font.getSize()) { font = font.deriveFont((float) fontSize); }
			Shape shape = font.createGlyphVector(CONTEXT, s.getObject()).getOutline();
			final Rectangle2D bounds = shape.getBounds2D();
			GamaPoint anchor = attributes.getAnchor();
			final float scale = 1f / (float) gl.getRatios().y;
			gl.translateBy(p.x - bounds.getWidth() * scale * anchor.x, p.y + bounds.getY() * scale * anchor.y, p.z);
			gl.scaleBy(scale, scale, scale);
			builder.init(attributes.getDepth(), attributes.isEmpty(), attributes.getBorder(), gl.isTextured(), bounds);
			builder.process(shape.getPathIterator(AT, attributes.getPrecision()));
			builder.drawOn(gl);
		}
		gl.popMatrix();
	}

	private void drawBitmap(final String object, final TextDrawingAttributes attributes, final GamaPoint p) {
		int fontToUse = GLUT.BITMAP_HELVETICA_18;
		final Font f = attributes.getFont();
		if (f != null) {
			if (f.getSize() < 10) {
				fontToUse = GLUT.BITMAP_HELVETICA_10;
			} else if (f.getSize() < 16) { fontToUse = GLUT.BITMAP_HELVETICA_12; }
		}
		gl.pushMatrix();
		gl.rasterText(object, fontToUse, p.x, p.y, p.z);
		gl.popMatrix();
	}

	@Override
	public void dispose() {}

}