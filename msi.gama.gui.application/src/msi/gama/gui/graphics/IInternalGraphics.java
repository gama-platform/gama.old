/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
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

/**
 * An adapter that allows uDig and plugin writers to write to AWT components and images or SWT
 * Drawable objects by using this common interface.
 * 
 * @author jeichar
 */
public interface IInternalGraphics {

	public static final AffineTransform AFFINE_TRANSFORM = new AffineTransform();

	/**
	 * Line drawing style for solid lines (value is 1).
	 */
	public static final int LINE_SOLID = 1;

	/**
	 * Line drawing style for dashed lines (value is 2).
	 */
	public static final int LINE_DASH = 2;

	/**
	 * Line drawing style for dotted lines (value is 3).
	 */
	public static final int LINE_DOT = 3;

	/**
	 * Line drawing style for alternating dash-dot lines (value is 4).
	 */
	public static final int LINE_DASHDOT = 4;

	/**
	 * Line drawing style for dash-dot-dot lines (value is 5).
	 */
	public static final int LINE_DASHDOTDOT = 5;
	/**
	 * Line drawing style for solid line with rounded ends lines (value is 6).
	 */
	public static final int LINE_SOLID_ROUNDED = 6;

	/** <code>ALIGN_TOP</code> field used to align text */
	public static final int ALIGN_TOP = 1;
	/** <code>ALIGN_MIDDLE</code> field used to align text */
	public static final int ALIGN_MIDDLE = 0;
	/** <code>ALIGN_BOTTOM</code> field used to align text */
	public static final int ALIGN_BOTTOM = -1;
	/** <code>ALIGN_LEFT</code> field used to align text */
	public static final int ALIGN_LEFT = -1;
	/** <code>ALIGN_RIGHT</code> field used to align text */
	public static final int ALIGN_RIGHT = 1;

	/**
	 * Fills the interior of the path with the forground color.
	 * 
	 * @param path the path to fill.
	 */
	public void fillPath(Path path);

	/**
	 * Fills the interior of a <code>Shape</code> using the foreground color, clip & transform.
	 * <p>
	 * Reference description from Graphics2d: <bq> Fills the interior of a <code>Shape</code> using
	 * the settings of the <code>Graphics2D</code> context. The rendering attributes applied include
	 * the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>, and <code>Composite</code>.
	 * 
	 * @param s the <code>Shape</code> to be filled <bq>
	 *            </p>
	 * @param s the <code>Shape</code> to be rendered
	 * @see fill
	 */
	public void fill(Shape s);

	/**
	 * Fills an Oval
	 * 
	 * @param x the starting x coordinate
	 * @param y the starting y coordinate
	 * @param width the width of the Oval.
	 * @param height the height of the Oval.
	 */
	public void fillOval(int x, int y, int width, int height);

	/**
	 * Fills a rectangle.
	 * 
	 * @param x the starting x coordinate
	 * @param y the starting y coordinate
	 * @param width the width of the rectangle.
	 * @param height the height of the rectangle.
	 */
	public void fillRect(int x, int y, int width, int height);

	/**
	 * Fills the specified rectangle with the background color.
	 * 
	 * @param x The starting corner's x-coordinate.
	 * @param y The starting corner's y-coordinate.
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public void clearRect(int x, int y, int width, int height);

	/**
	 * Draws the outline of the path using the color, clip and transform.
	 * 
	 * @path the path to draw.
	 */
	public void drawPath(Path path);

	/**
	 * Draws the outline of <code>shape</code> using the color, clip & transform.
	 * <p>
	 * Reference description from Graphics2d: <bq> Strokes the outline of a <code>Shape</code> using
	 * the settings of the current <code>Graphics2D</code> context. The rendering attributes applied
	 * include the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>,
	 * <code>Composite</code> and <code>Stroke</code> attributes. <bq>
	 * </p>
	 * 
	 * @param s the <code>Shape</code> to be rendered
	 * @see fill
	 */
	public void draw(Shape s);

	/**
	 * Draws a rectangle - only the boundary.
	 * 
	 * @param x the starting x coordinate
	 * @param y the starting y coordinate
	 * @param width the width of the rectangle.
	 * @param height the height of the rectangle.
	 */
	public void drawRect(int x, int y, int width, int height);

	/**
	 * Draws a line from x1,y1 to x2,y2
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1, int x2, int y2);

	/**
	 * Draws an Oval - only the boundary
	 * 
	 * @param x the starting x coordinate
	 * @param y the starting y coordinate
	 * @param width the width of the Oval.
	 * @param height the height of the Oval.
	 */
	public void drawOval(int x, int y, int width, int height);

	/**
	 * Draws a string. Alignment parameters specify where the string should be located relative to
	 * coordinate (x,y).
	 * 
	 * @param string The string to draw.
	 * @param x the x coordinate of the location where the of the string will be placed.
	 * @param y the y coordinate of the location where the of the string will be placed.
	 * @param alignx horizontal alignment, {@link #ALIGN_LEFT}, {@link #ALIGN_MIDDLE} or
	 *            {@link #ALIGN_RIGHT}
	 * @param aligny vertical alignment, {@link #ALIGN_BOTTOM}, {@link #ALIGN_MIDDLE} or
	 *            {@link #ALIGN_TOP}
	 */
	public void drawString(String string, int x, int y, int alignx, int aligny);

	/**
	 * Sets the foreground color to draw with.
	 * 
	 * @param c The new color.
	 */
	public void setColor(Color c);

	/**
	 * Sets the background color to draw with.
	 * 
	 * @param c The new color.
	 */
	public void setBackground(Color c);

	/**
	 * Sets the stroke color to draw with.
	 * 
	 * @param strokeStyle The style of line to draw.
	 * @param strokeWidth the width, in pixels, to draw lines with.
	 */
	public void setStroke(int strokeStyle, int strokeWidth);

	/**
	 * Sets the clip.
	 * 
	 * @param r the rectangle to clip to.
	 */
	public void setClip(Rectangle r);

	/**
	 * Sets the draw offset.
	 * 
	 * @param offset The amount the draw is offset in the graphics.
	 */
	public void translate(Point offset);

	/**
	 * Draws an image.
	 * 
	 * @param image The image to draw.
	 * @param x The x coordinate of the image top left corner of the image.
	 * @param y The y coordinate of the image top left corner of the image.
	 */
	public void drawImage(RenderedImage renderedImage, int x, int y);

	/**
	 * Draws an {@link Image}.
	 * 
	 * @param image The {@link Image} to draw.
	 * @param x The x coordinate of the image top left corner of the image.
	 * @param y The y coordinate of the image top left corner of the image.
	 */
	public void drawImage(Image awtImage, int x, int y);

	/**
	 * Draws a portion of the image to the target location on the viewport graphics.
	 * 
	 * @param image {@link Image} to draw
	 * @param dx1 - the x coordinate of the first corner of the destination rectangle.
	 * @param dy1 - the y coordinate of the first corner of the destination rectangle.
	 * @param dx2 - the x coordinate of the second corner of the destination rectangle.
	 * @param dy2 - the y coordinate of the second corner of the destination rectangle.
	 * @param sx1 - the x coordinate of the first corner of the source rectangle.
	 * @param sy1 - the y coordinate of the first corner of the source rectangle.
	 * @param sx2 - the x coordinate of the second corner of the source rectangle.
	 * @param sy2 - the y coordinate of the second corner of the source rectangle.
	 */
	public void drawImage(Image awtImage, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
		int sx2, int sy2);

	/**
	 * Copies a rectangular area from the source image into a (potentially different sized)
	 * rectangular area in the receiver. If the source and destination areas are of differing sizes,
	 * then the source area will be stretched or shrunk to fit the destination area as it is copied.
	 * The copy fails if any part of the source rectangle lies outside the bounds of the source
	 * image, or if any of the width or height arguments are negative.
	 * 
	 * @param image the source image
	 * @param dx1 - the x coordinate of the first corner of the destination rectangle.
	 * @param dy1 - the y coordinate of the first corner of the destination rectangle.
	 * @param dx2 - the x coordinate of the second corner of the destination rectangle.
	 * @param dy2 - the y coordinate of the second corner of the destination rectangle.
	 * @param sx1 - the x coordinate of the first corner of the source rectangle.
	 * @param sy1 - the y coordinate of the first corner of the source rectangle.
	 * @param sx2 - the x coordinate of the second corner of the source rectangle.
	 * @param sy2 - the y coordinate of the second corner of the source rectangle.
	 */
	public void drawImage(org.eclipse.swt.graphics.Image swtImage, int dx1, int dy1, int dx2,
		int dy2, int sx1, int sy1, int sx2, int sy2);

	/**
	 * Draws an {@link org.eclipse.swt.graphics.Image}.
	 * 
	 * @param image The {@link org.eclipse.swt.graphics.Image} to draw.
	 * @param x The x coordinate of the image top left corner of the image.
	 * @param y The y coordinate of the image top left corner of the image.
	 */
	public void drawImage(org.eclipse.swt.graphics.Image swtImage, int x, int y);

	/**
	 * Modifies the graphics so that further draws us minX,minY as the origin and maxX and maxY as
	 * the width and height of the display area.
	 * 
	 * @param minX The x-coord that will be used as the origin.
	 * @param minY The y-coord that will be used as the origin.
	 * @param width The width that will be used to draws.
	 * @param height The height that will be used to draws.
	 */
	public void setTransform(AffineTransform transform);

	/**
	 * Gets the height of the current font TODO at some point maybe this could be broken out to
	 * getFontMetrics(), and a create FontMetrics object that maps between SWT and AWT.
	 * 
	 * @return the height of the current font
	 */
	public int getFontHeight();

	/**
	 * Returns the length in pixels of the given string, or -1 if this operation is not available.
	 * 
	 * @param str
	 * @return
	 */
	public int stringWidth(String str);

	/**
	 * Gets the <code>ascent</code> of the current font, which is the distance the font rises above
	 * its <code>baseline</code>.
	 * 
	 * @return
	 */
	public int getFontAscent();

	/**
	 * Returns the bounds of a String. Does not expand tabs or newlines
	 * 
	 * @param str
	 * @return
	 */
	public Rectangle2D getStringBounds(String str);

	public AffineTransform getTransform();

	/**
	 * Disposes of any resources the graphics might be hanging on to.
	 */
	public void dispose();

	/**
	 * Gets the area that can be drawn in.
	 * 
	 * @return the area that can be drawn in.
	 */
	Shape getClip();

	/**
	 * Sets the clip area.
	 * 
	 * @param newBounds new clip area
	 */
	void setClipBounds(Rectangle newBounds);

	/**
	 * Gets the current Color value
	 * @return the current Color value
	 */
	Color getColor();

	/**
	 * Gets the current background color value
	 * @return the current background color value
	 */
	Color getBackgroundColor();

	/**
	 * Draws a round cornered rectangle
	 * 
	 * @param x the x component of the upper left corner
	 * @param y the y component of the upper left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param arcWidth the horizontal diameter of the arc at the four corners.
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	/**
	 * Fills a round cornered rectangle using the foreground color
	 * 
	 * @param x the x component of the upper left corner
	 * @param y the y component of the upper left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param arcWidth the horizontal diameter of the arc at the four corners.
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	/**
	 * Sets the line width of the graphics (in pixels).
	 * <p>
	 * If you need to make your drawing device independent please consider making use of the DPI
	 * setting.
	 * 
	 * <pre>
	 * <code>
	 * g.setLineWith( width ); // BEFORE
	 * g.setLineWith( (width * g.getDPI()) / 72 ); // AFTER 
	 * </code>
	 * </pre>
	 * 
	 * Where 72 is chosen because that is what a Java image resolutions is assumed to be by default.
	 * 
	 * @param the new line width
	 */
	public void setLineWidth(int width);

	/**
	 * Sets the line dash pattern
	 * 
	 * @param dash the pattern of dashes.
	 */
	public void setLineDash(int[] dash);

	/**
	 * Sets the font; size is taken to be in DPI.
	 * <p>
	 * Please note this is a change in behaviour over AWT (where size is documented to be in 72 dpi.
	 * They are changing their mind in later versions of java but we cannot wait for them to get
	 * there act together.
	 * <p>
	 * @param the font new font
	 */
	public void setFont(Font font);

	/**
	 * Get the dots per inch, used to scale fonts, but you can scale anything you want based on this
	 * value (for example a scalebar).
	 * 
	 * @param dpi
	 */
	public int getDPI();

	/**
	 * Fills a rectangle using a gradient paint
	 * 
	 * @param x the x component of the upper left corner
	 * @param y the y component of the upper left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param startColor the first color used in the gradient paint
	 * @param endColor the last color used in the gradient paint
	 * @param isVertical orientation of the gradient
	 */
	public void fillGradientRectangle(int x, int y, int width, int height, Color startColor,
		Color endColor, boolean isVertical);

	/**
	 * Gets the SWT graphics if applicable. May return null if no SWT graphics.
	 * 
	 * @return SWT graphics or null
	 */
	public GC getGC();

	public void rotate(double d, int i, int j);

	public void setOpacity(double alpha);

	public void drawImage(Image img, int curX, int curY, int curWidth, int curHeight,
		ImageObserver object);

	public void drawString(String string, int curX, int curY);

	public void erase(Color bgColor, int width, int height);

}