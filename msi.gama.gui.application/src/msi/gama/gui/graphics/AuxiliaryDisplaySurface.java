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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.ImageCache;

public class AuxiliaryDisplaySurface extends JPanel implements ActionListener {

	private int imgWidth, imgHeight;
	private final AWTDisplaySurface surface;
	private BufferedImage image;
	Timer timer;
	IGraphics g;

	AuxiliaryDisplaySurface(final AWTDisplaySurface surface) {
		timer = new Timer(1000, this);
		timer.setInitialDelay(2000);
		timer.start();
		this.surface = surface;

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final MouseEvent e) {
				if ( SwingUtilities.isLeftMouseButton(e) ) {
					Point p = e.getPoint();
					int x = p.x * surface.bWidth / imgWidth;
					int y = p.y * surface.bHeight / imgHeight;
					surface.setOrigin(new Point(-(x - surface.getWidth() / 2), -(y - surface
						.getHeight() / 2)));
					surface.updateDisplay();
				}
			}
		});
	}

	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / surface.widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * surface.widthHeightConstraint) : vheight;
		return dim;
	}

	protected void computeWholeImage() {
		int[] dim = surface.computeBoundsFrom(getHeight(), getWidth());
		imgWidth = dim[0];
		imgHeight = dim[1];
		if ( image != null ) {
			image.flush();
		}
		image = ImageCache.createCompatibleImage(imgWidth, imgHeight);
		g = new AWTDisplayGraphics(image);
		g.setQualityRendering(false);
		try {
			surface.manager.drawDisplaysOn(g);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if ( image == null ) {
			computeWholeImage();
		}
		g.drawImage(image, 0, 0, null);
		g.setColor(Color.black);
		g.drawRect(1, 1, imgWidth - 2, imgHeight - 2);
		if ( surface.isFullImageInPanel() ) { return; }
		double wRatio = (double) imgWidth / surface.bWidth;
		double hRatio = (double) imgHeight / surface.bHeight;
		g.setColor(Color.white);
		g.drawRect((int) (-surface.origin.x * wRatio), (int) (-surface.origin.y * hRatio),
			(int) (surface.getWidth() * wRatio), (int) (surface.getHeight() * hRatio));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			computeWholeImage();
			repaint();
		} catch (Exception ex) {
			timer.stop();
		}
	}

	public void toggle(final boolean state) {
		if ( state ) {
			timer.start();
		} else {
			timer.stop();
		}
	}

}
