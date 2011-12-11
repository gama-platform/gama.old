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
package msi.gama.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class ImageCache {

	// TODO UCdetector: Remove unused code:
	// /**
	// * Creates a rotated version of the input image.
	// *
	// * @param c The component to get properties useful for painting, e.g. the foreground
	// * or background color.
	// * @param icon the image to be rotated.
	// * @param rotatedAngle the rotated angle, in degree, clockwise. It could be any double
	// * but we will mod it with 360 before using it.
	// *
	// * @return the image after rotating.
	// */
	// public BufferedImage createRotatedImage(final BufferedImage icon,
	// final int rotatedAngle) {
	// // convert rotatedAngle to a value from 0 to 360
	// int originalAngle = rotatedAngle % 360;
	// if ( rotatedAngle != 0 && originalAngle == 0 ) {
	// originalAngle = 360;
	// }
	//
	// // convert originalAngle to a value from 0 to 90
	// int angle = originalAngle % 90;
	// if ( originalAngle != 0.0 && angle == 0.0 ) {
	// angle = 90;
	// }
	//
	// double radian = angle * GamaMath.toRad;
	//
	// int iw = icon.getWidth();
	// int ih = icon.getHeight();
	// int w;
	// int h;
	//
	// if ( originalAngle >= 0 && originalAngle <= 90 || originalAngle > 180
	// && originalAngle <= 270 ) {
	// w = (int) (iw * GamaMath.sin(DEGREE_90 - radian) + ih * GamaMath.sin(radian));
	// h = (int) (iw * GamaMath.sin(radian) + ih * GamaMath.sin(DEGREE_90 - radian));
	// } else {
	// w = (int) (ih * GamaMath.sin(DEGREE_90 - radian) + iw * GamaMath.sin(radian));
	// h = (int) (ih * GamaMath.sin(radian) + iw * GamaMath.sin(DEGREE_90 - radian));
	// }
	// BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	// Graphics2D g2d = image.createGraphics();
	//
	// // calculate the center of the icon.
	// int cx = iw / 2;
	// int cy = ih / 2;
	//
	// // move the graphics center point to the center of the icon.
	// g2d.translate(w / 2, h / 2);
	//
	// // rotate the graphcis about the center point of the icon
	// g2d.rotate(Math.toRadians(originalAngle));
	//
	// g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	// g2d.drawImage(icon, -cx, -cy, null);
	// g2d.dispose();
	// return image;
	// }

	private final Map<String, BufferedImage[]> cache;

	private static final int POSITIONS = 360;

	private static final int ANGLE_INCREMENT = 360 / POSITIONS;

	// private final static double DEGREE_90 = 90.0 * Math.PI / 180.0;

	public ImageCache() {
		cache = new HashMap();
	}

	public boolean contains(final String s) {
		return cache.containsKey(s);
	}

	public void add(final String s, final BufferedImage image) {
		add(s, image, 0); // No rotations for the moment
		// for ( int i = 0; i < POSITIONS; i++ ) {
		// add(s, createRotatedImage(image, i * ANGLE_INCREMENT), i);
		// }
	}

	private void add(final String s, final BufferedImage image, final int position) {
		// OutputManager.debug("Creating rotated images of " + s + " at "
		// + position * ANGLE_INCREMENT);
		if ( !cache.containsKey(s) ) {
			cache.put(s, new BufferedImage[POSITIONS]);
		}
		BufferedImage[] map = cache.get(s);
		map[position] = toCompatibleImage(image);
	}

	public static BufferedImage createCompatibleImage(final int width, final int height) {
		GraphicsConfiguration gfx_config =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		BufferedImage new_image = gfx_config.createCompatibleImage(width, height);
		new_image.setAccelerationPriority(1f);
		return new_image;
	}

	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		// obtain the current system graphical settings
		GraphicsConfiguration gfx_config =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system settings, simply return
		 * it
		 */
		if ( image.getColorModel().equals(gfx_config.getColorModel()) ) { return image; }

		// image is not optimized, so create a new image that is
		BufferedImage new_image =
			gfx_config.createCompatibleImage(image.getWidth(), image.getHeight(),
				image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image;
	}

	public BufferedImage get(final String s) {
		return get(s, 0);
	}

	private BufferedImage get(final String s, final int angle) {
		BufferedImage[] map = cache.get(s);
		if ( map == null ) { return null; }
		int position =
			MathUtils.round((double) (angle % (360 - ANGLE_INCREMENT)) / ANGLE_INCREMENT);
		return map[position];
	}
}