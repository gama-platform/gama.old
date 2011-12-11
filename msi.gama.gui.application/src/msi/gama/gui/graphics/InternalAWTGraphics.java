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
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.awt.image.*;
import org.eclipse.swt.graphics.*;

public class InternalAWTGraphics implements IInternalGraphics {

	public Graphics2D g;

	/**
	 * Accept a DPI setting; fonts will be scaled based on this setting. defaults to 72 dpi.
	 */
	int dpi;

	public InternalAWTGraphics(final Graphics2D g) {
		this.g = g;
		g.setBackground(Color.WHITE);
		dpi = 72;
	}

	/**
	 * Construct a AWTGraphics with the indicated dpi
	 * 
	 * @param g
	 * @param dpi
	 */
	public InternalAWTGraphics(final Graphics2D g, final int dpi) {
		this.g = g;
		this.dpi = dpi;
		g.setBackground(Color.WHITE);

		if ( dpi != 72 ) {
			Font font = g.getFont();
			String name = font.getName();
			int style = font.getStyle();
			int size = font.getSize() * dpi / 72;
			g.setFont(new Font(name, style, size));
		}
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#draw(java.awt.Shape)
	 */
	@Override
	public void draw(final Shape s) {
		g.draw(s);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#draw(java.awt.Shape)
	 */
	@Override
	public void fill(final Shape s) {
		g.fill(s);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(final Color c) {
		g.setColor(c);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(final Color c) {
		g.setBackground(c);
	}

	/**
	 * Make use of the provided font.
	 * <p>
	 * Please note that the provided AWT Font makes use of a size in *points* (which are documented
	 * to be 72 DPI). Internally we adjust this size by the getDPI() value for this AWTGraphics.
	 * 
	 * @param f Font in 72 dpi
	 */
	@Override
	public void setFont(final Font f) {
		String name = f.getFamily();
		int style = f.getStyle();
		int size = f.getSize() * dpi / 72;

		Font font = new Font(name, style, size);
		g.setFont(font);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#setClip(java.awt.Rectangle)
	 */
	@Override
	public void setClip(final Rectangle r) {
		g.setClip(r);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#fillRect(int,
	 *      int, int, int)
	 */
	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		g.fillRect(x, y, width, height);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#translate(java.awt.Point)
	 */
	@Override
	public void translate(final Point offset) {
		g.setTransform(AffineTransform.getTranslateInstance(offset.x, offset.y));
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#clearRect(int,
	 *      int, int, int)
	 */
	@Override
	public void clearRect(final int x, final int y, final int width, final int height) {
		g.clearRect(x, y, width, height);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#drawImage(javax.media.jai.PlanarImage,
	 *      int, int)
	 */
	@Override
	public void drawImage(final RenderedImage image, final int x, final int y) {
		g.drawRenderedImage(image, AffineTransform.getTranslateInstance(x, y));
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#drawString(String,
	 *      int, int)
	 */
	@Override
	public void drawString(final String string, final int x, final int y, final int alignx,
		final int aligny) {
		Rectangle2D text = g.getFontMetrics().getStringBounds(string, g);
		int w = (int) text.getWidth();
		int h = (int) text.getHeight();

		int x2 = alignx == 0 ? x - w / 2 : alignx > 0 ? x - w : x;
		int y2 = aligny == 0 ? y + h / 2 : aligny > 0 ? y + h : y;
		g.drawString(string, x2, y2);
	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#setTransform(java.awt.geom.AffineTransform)
	 */
	@Override
	public void setTransform(final AffineTransform transform) {
		g.setTransform(transform);

	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
	 *      int, int)
	 */
	@Override
	public void drawImage(final Image image, final int x, final int y) {
		g.drawImage(image, x, y, null);
	}

	@Override
	public void drawImage(final Image image, final int dx1, final int dy1, final int dx2,
		final int dy2, final int sx1, final int sy1, final int sx2, final int sy2) {
		g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	@Override
	public int getFontHeight() {
		return g.getFontMetrics().getHeight();
	}

	@Override
	public int stringWidth(final String str) {
		return g.getFontMetrics().stringWidth(str);
	}

	@Override
	public int getFontAscent() {
		return g.getFontMetrics().getAscent();
	}

	@Override
	public Rectangle2D getStringBounds(final String str) {
		return g.getFontMetrics().getStringBounds(str, g);
	}

	/**
	 * Converts an SWT image to an AWT BufferedImage
	 * 
	 * @param swtImageData
	 * @return
	 * 
	 * @deprecated use {@link InternalGraphicsUtils}
	 */
	public static BufferedImage toAwtImage(final ImageData swtImageData) {
		return InternalGraphicsUtils.convertToAWT(swtImageData);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawImage(final org.eclipse.swt.graphics.Image image, final int dx1, final int dy1,
		final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2) {
		BufferedImage awtImage = InternalGraphicsUtils.convertToAWT(image.getImageData());
		drawImage(awtImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	@Override
	public void drawPath(final Path path) {
		PathData pathData = path.getPathData();
		float[] points = pathData.points;
		GeneralPath p = new GeneralPath();
		p.moveTo(points[0], points[1]);
		for ( int i = 2; i < points.length; i = i + 2 ) {
			p.lineTo(points[i], points[i + 1]);
		}
		draw(p);
	}

	@Override
	public void fillPath(final Path path) {
		PathData pathData = path.getPathData();
		float[] points = pathData.points;
		GeneralPath p = new GeneralPath();
		p.moveTo(points[0], points[1]);
		for ( int i = 2; i < points.length; i = i + 2 ) {
			p.lineTo(points[i], points[i + 1]);
		}
		fill(p);
	}

	@Override
	public void drawRect(final int x, final int y, final int width, final int height) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {
		g.drawOval(x, y, width, height);
	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
		g.fillOval(x, y, width, height);
	}

	@Override
	public void drawImage(final org.eclipse.swt.graphics.Image swtImage, final int x, final int y) {
		BufferedImage awtImage = InternalGraphicsUtils.convertToAWT(swtImage.getImageData());
		drawImage((Image) awtImage, x, y);

	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public void setClipBounds(final Rectangle newBounds) {
		g.setClip(newBounds);
	}

	@Override
	public Color getBackgroundColor() {
		return g.getBackground();
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width, final int height,
		final int arcWidth, final int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width, final int height,
		final int arcWidth, final int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void setLineDash(final int[] dash) {
		Stroke stroke = g.getStroke();
		if ( !(stroke instanceof BasicStroke) ) {
			stroke = new BasicStroke();
		}
		BasicStroke basicStroke = (BasicStroke) stroke;
		g.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(),
			basicStroke.getLineJoin(), basicStroke.getMiterLimit(), toFloatArray(dash), 0));
	}

	private float[] toFloatArray(final int[] dash) {
		float[] result = new float[dash.length];
		for ( int i = 0; i < result.length; i++ ) {
			result[i] = dash[i];
		}
		return result;
	}

	@Override
	public void setLineWidth(final int width) {
		Stroke stroke = g.getStroke();
		if ( !(stroke instanceof BasicStroke) ) {
			stroke = new BasicStroke();
		}
		BasicStroke basicStroke = (BasicStroke) stroke;
		g.setStroke(new BasicStroke(width, basicStroke.getEndCap(), basicStroke.getLineJoin(),
			basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase()));

	}

	/**
	 * @see msi.gama.gui.graphics.IInternalGraphics.udig.ui.graphics.ViewportGraphics#setStroke(int,
	 *      int)
	 */
	@Override
	public void setStroke(final int style, final int width) {
		switch (style) {
			case LINE_DASH: {
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
					10.0f, new float[] { width * 2.0f, width * 2.0f }, 0.0f));
				break;
			}
			case LINE_DASHDOT: {
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
					10.0f, new float[] { width * 2.0f, width * 2.0f, width * 1.0f, width * 2.0f },
					0.0f));
				break;
			}
			case LINE_DASHDOTDOT: {
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
					10.0f, new float[] { width * 2.0f, width * 2.0f, width * 1.0f, width * 2.0f,
						width * 1.0f, width * 2.0f }, 0.0f));
				break;
			}
			case LINE_DOT: {
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
					10.0f, new float[] { width * 1.0f, width * 2.0f }, 0.0f));
				break;
			}
			case LINE_SOLID: {
				g.setStroke(new BasicStroke(width));
				break;
			}

			case LINE_SOLID_ROUNDED: {
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				break;
			}
		}
	}

	@Override
	public int getDPI() {
		return dpi;
	}

	/**
	 * Set the viewport graphics to use the provided dpi.
	 * <p>
	 * Please note the DPI setting is only used to control font size.
	 */
	public void setDPI(final int dpi) {
		if ( this.dpi == dpi ) { return; }
		Font font = g.getFont();
		String name = font.getName();
		int style = font.getStyle();
		int size = font.getSize() * this.getDPI() / dpi;
		g.setFont(new Font(name, style, size));
		this.dpi = dpi;
	}

	@Override
	public void fillGradientRectangle(final int x, final int y, final int width, final int height,
		final Color startColor, final Color endColor, final boolean isVertical) {

		GradientPaint gradPaint = null;
		if ( isVertical ) {
			gradPaint = new GradientPaint(x, y, startColor, x, y + height, endColor);
		} else {
			gradPaint = new GradientPaint(x, y, startColor, x + width, y, endColor);
		}
		g.setPaint(gradPaint);
		g.fillRect(x, y, width, height);
	}

	/**
	 * @return null
	 */
	@Override
	public GC getGC() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.displays.ViewportGraphics#rotate(double, int, int)
	 */
	@Override
	public void rotate(final double d, final int i, final int j) {
		g.rotate(d, i, j);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.displays.ViewportGraphics#setComposite(java.awt.AlphaComposite)
	 */
	@Override
	public void setOpacity(final double alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	@Override
	public void drawImage(final Image img, final int x, final int y, final int width,
		final int height, final ImageObserver observer) {
		g.drawImage(img, x, y, width, height, observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.displays.ViewportGraphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(final String string, final int curX, final int curY) {
		g.drawString(string, curX, curY);
	}

	@Override
	public void erase(final Color bgColor, final int width, final int height) {
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
	}

}