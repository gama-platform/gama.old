/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.graphics;

import java.awt.*;
import java.awt.Point;
import java.awt.geom.*;
import java.awt.image.*;
import msi.gama.util.MathUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * A Graphics object that wraps SWT's GC object
 * 
 * @author jeichar
 * @since 0.3
 */
public class InternalSWTGraphics implements IInternalGraphics {

	/** The <code>TRANSPARENT</code> color */
	public final static int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

	private Transform swtTransform;

	private GC gc = null;

	private Color fore = null;

	private Color back = null;

	private final Display display;

	private Font font = null;

	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param Image image
	 * @param display the display to use with the
	 * @param display The display object
	 */
	public InternalSWTGraphics(final Image image, final Display display) {
		this(new GC(image), display);

	}

	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param gc The GC object
	 * @param display The display object
	 */
	public InternalSWTGraphics(final GC gc, final Display display) {
		InternalGraphicsUtils.checkAccess();
		this.display = display;
		setGraphics(gc, display);
	}

	void setGraphics(final GC gg, final Display display) {
		if ( gc != null && !gc.isDisposed() ) {
			gc.dispose();
		}
		this.gc = gg;
		if ( back != null ) {
			back.dispose();
		}
		back = new Color(display, 255, 255, 255);
		gc.setBackground(back);
		gc.setAdvanced(true);
	}

	@Override
	public GC getGC() {
		return gc;
	}

	@Override
	public void dispose() {
		if ( fore != null ) {
			fore.dispose();
		}
		if ( back != null ) {
			back.dispose();
		}
		if ( swtTransform != null ) {
			swtTransform.dispose();
		}
		gc.dispose();
	}

	@Override
	public void drawPath(final Path path) {
		gc.drawPath(path);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
	 */
	@Override
	public void draw(final Shape s) {
		// GUI.debug("draw Shape");
		Path path = InternalGraphicsUtils.convertToPath(s, display);
		if ( path != null ) {
			gc.drawPath(path);
			path.dispose();
		}

	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
	 */
	@Override
	public void fill(final Shape s) {
		// GUI.debug("fill Shape");
		Color tmp = prepareForFill();
		Path path = InternalGraphicsUtils.convertToPath(s, display);
		gc.fillPath(path);
		path.dispose();
		gc.setBackground(tmp);
	}

	private Color prepareForFill() {
		// GUI.debug("prepare for fill");
		Color tmp = gc.getBackground();
		if ( fore == null ) {
			gc.setBackground(gc.getForeground());
		} else {
			gc.setBackground(fore);
		}
		return tmp;
	}

	@Override
	public void fillPath(final Path path) {
		// GUI.debug("fill Path");
		Color tmp = prepareForFill();
		gc.fillPath(path);
		gc.setBackground(tmp);
	}

	@Override
	public void drawRect(final int x, final int y, final int width, final int height) {
		// GUI.debug("draw Rect");
		Color tmp = prepareForFill();
		gc.drawRectangle(x, y, width, height);
		gc.setBackground(tmp);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#fillRect(int,
	 *      int, int, int)
	 */
	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		// GUI.debug("fill Rect");
		Color tmp = prepareForFill();
		gc.fillRectangle(new Rectangle(x, y, width, height));
		gc.setBackground(tmp);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(final java.awt.Color c) {
		// GUI.debug("set Color");
		Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
		gc.setForeground(color);
		gc.setAlpha(c.getAlpha());
		if ( fore != null ) {
			fore.dispose();
		}
		fore = color;
	}

	/**
	 * This is hard because - background doesn't mean what we think it means.
	 * 
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(final java.awt.Color c) {
		// GUI.debug("set Background");
		Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
		gc.setBackground(color);
		if ( back != null ) {
			back.dispose();
		}
		back = color;
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#setStroke(int,
	 *      int)
	 */
	@Override
	public void setStroke(final int style, final int width) {
		// GUI.debug("set Stroke");
		gc.setLineWidth(width);
		switch (style) {
			case LINE_DASH: {
				gc.setLineStyle(SWT.LINE_DASH);
				break;
			}
			case LINE_DASHDOT: {
				gc.setLineStyle(SWT.LINE_DASHDOT);
				break;
			}
			case LINE_DASHDOTDOT: {
				gc.setLineStyle(SWT.LINE_DASHDOTDOT);
				break;
			}
			case LINE_DOT: {
				gc.setLineStyle(SWT.LINE_DOT);
				break;
			}
			case LINE_SOLID: {
				gc.setLineStyle(SWT.LINE_SOLID);
				break;
			}

			case LINE_SOLID_ROUNDED: {
				gc.setLineCap(SWT.CAP_ROUND);
				gc.setLineJoin(SWT.JOIN_ROUND);
				gc.setLineStyle(SWT.LINE_SOLID);
				break;
			}
			default: {
				gc.setLineStyle(SWT.LINE_SOLID);
				break;
			}
		}
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#setClip(java.awt.Rectangle)
	 */
	@Override
	public void setClip(final java.awt.Rectangle r) {
		// GUI.debug("set Clip");
		gc.setClipping(r.x, r.y, r.width, r.height);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.project.render.ViewportGraphics#translate(java.awt.Point)
	 */
	@Override
	public void translate(final Point offset) {
		// GUI.debug("translate Point");
		if ( swtTransform == null ) {
			swtTransform = new Transform(display);
		}
		swtTransform.translate(offset.x, offset.y);
		gc.setTransform(swtTransform);
	}

	@Override
	public void clearRect(final int x, final int y, final int width, final int height) {
		// GUI.debug("clear Rect");
		gc.fillRectangle(x, y, width, height);
	}

	@Override
	public void drawString(final String string, final int x, final int y, final int alignx,
		final int aligny) {
		// GUI.debug("draw String");
		InternalGraphicsUtils.checkAccess();
		org.eclipse.swt.graphics.Point text = gc.stringExtent(string);
		int w = text.x;
		int h = text.y;

		int x2 = alignx == 0 ? x - w / 2 : alignx > 0 ? x - w : x;
		int y2 = aligny == 0 ? y + h / 2 : aligny > 0 ? y + h : y;

		gc.drawString(string, x2, y2, true);
	}

	@Override
	public void setTransform(final AffineTransform transform) {
		// GUI.debug("set Transform");
		double[] matrix = new double[6];
		transform.getMatrix(matrix);
		if ( swtTransform == null ) {
			swtTransform =
				new Transform(display, (float) matrix[0], (float) matrix[1], (float) matrix[2],
					(float) matrix[3], (float) matrix[4], (float) matrix[5]);
		} else {
			swtTransform.setElements((float) matrix[0], (float) matrix[1], (float) matrix[2],
				(float) matrix[3], (float) matrix[4], (float) matrix[5]);
		}

		gc.setTransform(swtTransform);
	}

	@Override
	public int getFontHeight() {
		return gc.getFontMetrics().getHeight();
	}

	@Override
	public int stringWidth(final String str) {
		return gc.getFontMetrics().getAverageCharWidth() * str.length();
		// return -1;
	}

	@Override
	public int getFontAscent() {
		return gc.getFontMetrics().getAscent();
	}

	@Override
	public Rectangle2D getStringBounds(final String str) {
		org.eclipse.swt.graphics.Point extent = gc.textExtent(str);
		return new java.awt.Rectangle(0, 0, extent.x, extent.y);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		// GUI.debug("draw Line");
		gc.drawLine(x1, y1, x2, y2);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
	 *      int, int) Current version can only draw Image if the image is an RenderedImage
	 */
	@Override
	public void drawImage(final java.awt.Image awtImage, final int x, final int y) {
		// GUI.debug("draw Image x y ");
		RenderedImage rimage = (RenderedImage) awtImage;
		drawImage(rimage, x, y);
	}

	@Override
	public void drawImage(final java.awt.Image rimage, final int dx1, final int dy1, final int dx2,
		final int dy2, final int sx1, final int sy1, final int sx2, final int sy2) {
		// GUI.debug("draw RenderedImage dx1 dx2... ");
		Image swtImage = null;
		swtImage = InternalGraphicsUtils.toSwt(rimage);

		// if ( rimage instanceof BufferedImage ) {
		// swtImage = InternalGraphicsUtils.convertToSWTImage((BufferedImage) rimage);
		// } else {
		// swtImage = InternalGraphicsUtils.createSWTImage(rimage);
		// }
		if ( swtImage != null ) {
			// gc.drawImage(swtImage, sx1, sy1);
			gc.drawImage(swtImage, sx1, sy1, Math.abs(sx2 - sx1), Math.abs(sy2 - sy1), dx1, dy1,
				Math.abs(dx2 - dx1), Math.abs(dy2 - dy1));
			swtImage.dispose();
		}

	}

	@Override
	public void drawImage(final Image swtImage, final int dx1, final int dy1, final int dx2,
		final int dy2, final int sx1, final int sy1, final int sx2, final int sy2) {
		// GUI.debug("draw SWT Image dx1 dx2 ...");
		gc.drawImage(swtImage, sx1, sy1, Math.abs(sx2 - sx1), Math.abs(sy2 - sy1), dx1, dy1,
			Math.abs(dx2 - dx1), Math.abs(dy2 - dy1));

	}

	@Override
	public void drawImage(final Image swtImage, final int x, final int y) {
		// GUI.debug("draw SWT Image xy");
		gc.drawImage(swtImage, x, y);
	}

	@Override
	public AffineTransform getTransform() {
		if ( swtTransform == null ) { return AFFINE_TRANSFORM; }
		float[] matrix = new float[6];
		swtTransform.getElements(matrix);
		return new AffineTransform(matrix);
	}

	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {
		// GUI.debug("draw Oval");
		gc.drawOval(x, y, width, height);
	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
		// GUI.debug("fill Oval");
		gc.fillOval(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		Rectangle clipping = gc.getClipping();
		return new java.awt.Rectangle(clipping.x, clipping.y, clipping.width, clipping.height);
	}

	@Override
	public void setClipBounds(final java.awt.Rectangle newBounds) {
		// GUI.debug("set ClipBounds");
		gc.setClipping(new Rectangle(newBounds.x, newBounds.y, newBounds.width, newBounds.height));
	}

	@Override
	public java.awt.Color getBackgroundColor() {
		return InternalGraphicsUtils.swtColor2awtColor(gc, gc.getBackground());
	}

	@Override
	public java.awt.Color getColor() {
		return InternalGraphicsUtils.swtColor2awtColor(gc, gc.getForeground());
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width, final int height,
		final int arcWidth, final int arcHeight) {
		// GUI.debug("draw RoundRect");
		gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width, final int height,
		final int arcWidth, final int arcHeight) {
		// GUI.debug("fill RoundRect");
		Color tmp = prepareForFill();
		gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
		gc.setBackground(tmp);
	}

	@Override
	public void setLineDash(final int[] dash) {
		// GUI.debug("set LineDash");
		gc.setLineDash(dash);
	}

	@Override
	public void setLineWidth(final int width) {
		// GUI.debug("set LineWidth");
		gc.setLineWidth(width);
	}

	@Override
	public void setFont(final java.awt.Font f) {
		// GUI.debug("set Font");
		Font swtFont;

		int size = f.getSize() * getDPI() / 72;
		int style = InternalGraphicsUtils.toFontStyle(f);

		swtFont = new Font(gc.getDevice(), f.getFamily(), size, style);
		if ( font != null ) {
			font.dispose();
		}
		font = swtFont;
		gc.setFont(font);
	}

	@Override
	public int getDPI() {
		return gc.getDevice().getDPI().y;
	}

	@Override
	public void fillGradientRectangle(final int x, final int y, final int width, final int height,
		final java.awt.Color startColor, final java.awt.Color endColor, final boolean isVertical) {
		// GUI.debug("fill GradientRectangle");
		Color color1 =
			new Color(display, startColor.getRed(), startColor.getGreen(), startColor.getBlue());
		Color color2 =
			new Color(display, endColor.getRed(), endColor.getGreen(), endColor.getBlue());
		gc.setForeground(color1);
		gc.setBackground(color2);

		gc.fillGradientRectangle(x, y, width, height, isVertical);
		color1.dispose();
		color2.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.displays.ViewportGraphics#rotate(double, int, int)
	 */
	@Override
	public void rotate(final double d, final int i, final int j) {
		// GUI.debug("rotate");
		// gc.setTransform(null);
		if ( swtTransform == null ) {
			swtTransform = new Transform(display);
		} else {
			swtTransform.identity();
		}
		swtTransform.translate(i, j);
		swtTransform.rotate((float) (d * MathUtils.toDeg));
		swtTransform.translate(-i, -j);
		gc.setTransform(swtTransform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.displays.ViewportGraphics#setComposite(java.awt.AlphaComposite)
	 */
	@Override
	public void setOpacity(final double alpha) {
		// GUI.debug("set Opacity");
		int opacity = (int) (alpha * 255);
		gc.setAlpha(opacity);
	}

	@Override
	public void drawImage(final java.awt.Image img, final int curX, final int curY,
		final int curWidth, final int curHeight, final ImageObserver object) {
		// GUI.debug("draw Image (observer)");
		this.drawImage(img, curX, curY, curX + curWidth, curY + curHeight, 0, 0,
			img.getHeight(object), img.getWidth(object));
	}

	@Override
	public void drawString(final String string, final int curX, final int curY) {
		// GUI.debug("draw String x y");
		this.drawString(string, curX, curY, ALIGN_LEFT, ALIGN_BOTTOM);
	}

	@Override
	public void erase(final java.awt.Color bgColor, final int width, final int height) {
		// GUI.debug("erase");
		setColor(bgColor);
		// gc.fillRectangle(0, 0, width, height);
	}

	@Override
	public void drawImage(final RenderedImage renderedImage, final int x, final int y) {
		if ( renderedImage instanceof java.awt.Image ) {
			drawImage((java.awt.Image) renderedImage, x, y);
		}

	}
}
