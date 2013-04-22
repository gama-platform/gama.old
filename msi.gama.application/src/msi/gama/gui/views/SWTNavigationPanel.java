/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class SWTNavigationPanel extends Canvas implements PaintListener, MouseListener, MouseTrackListener,
	MouseMoveListener {

	private boolean mouseDown;
	private int squareX, squareY, squareW, squareH;
	private int imgWidth, imgHeight;
	private Image image;

	private final IDisplaySurface surface;

	SWTNavigationPanel(final Composite parent, final int style, final IDisplaySurface surface) {
		super(parent, style);
		this.surface = surface;
		surface.setNavigator(this);
		addPaintListener(this);
		addMouseListener(this);
		addMouseTrackListener(this);
		addMouseMoveListener(this);

	}

	/**
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	@Override
	public void paintControl(final PaintEvent e) {
		if ( surface.getImage() == null ) { return; }
		int[] dim = surface.computeBoundsFrom(this.getSize().x, this.getSize().y);
		imgWidth = dim[0];
		imgHeight = dim[1];
		BufferedImage awtImage = ImageUtils.createCompatibleImage(imgWidth, imgHeight);
		java.awt.Graphics2D gc = (java.awt.Graphics2D) awtImage.getGraphics();
		gc.drawImage(surface.getImage(), 0, 0, imgWidth, imgHeight, null);
		gc.dispose();
		if ( image != null ) {
			image.dispose();
		}
		image = new Image(SwtGui.getDisplay(), ImageUtils.convertToSWT(awtImage));
		if ( !mouseDown ) {
			updateSquare();
		}
		drawSquare(e.gc);
	}

	boolean isFullImageInPanel() {
		int ox = surface.getOriginX();
		int oy = surface.getOriginY();
		return ox >= 0 && ox + surface.getImageWidth() < surface.getWidth() && oy >= 0 &&
			oy + surface.getImageHeight() < surface.getHeight();
	}

	void drawSquare(final GC g) {
		g.drawImage(image, 0, 0);
		if ( isFullImageInPanel() ) { return; }
		g.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		g.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		g.setAlpha(175);
		g.drawRectangle(squareX, squareY, squareW, squareH);
		g.fillRectangle(0, 0, imgWidth, squareY);
		g.fillRectangle(0, squareY, squareX, imgHeight - squareY);
		g.fillRectangle(squareX + squareW, squareY, imgWidth - squareX - squareW, imgHeight - squareY);
		g.fillRectangle(squareX, squareY + squareH, squareW, imgHeight - squareY - squareH);
	}

	/**
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(final MouseEvent e) {
		if ( mouseDown ) {
			updateSquare(e.x, e.y);
			GC g = new GC(this);
			drawSquare(g);
			g.dispose();
		}
	}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseEnter(final MouseEvent e) {}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(final MouseEvent e) {}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseHover(final MouseEvent e) {}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent e) {}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(final MouseEvent e) {
		mouseDown = true;
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(final MouseEvent e) {
		mouseDown = false;
		moveShape(e.x, e.y);
	}

	private void moveShape(final int mouseX, final int mouseY) {
		int x = (int) (mouseX * (double) surface.getImageWidth() / getSize().x);
		int y = (int) (mouseY * (double) surface.getImageHeight() / getSize().y);
		surface.setOrigin(-(x - surface.getWidth() / 2), -(y - surface.getHeight() / 2));
		surface.forceUpdateDisplay();
	}

	public void updateSquare(final int mouseX, final int mouseY) {
		squareX = /* Math.max(0, */mouseX - squareW / 2/* ) */;
		squareY = /* Math.max(0, */mouseY - squareH / 2/* ) */;
	}

	public void updateSquare() {
		double wRatio = (double) imgWidth / surface.getImageWidth();
		double hRatio = (double) imgHeight / surface.getImageHeight();
		squareX = /* Math.max(0, */(int) (-surface.getOriginX() * wRatio)/* ) */;
		squareY = /* Math.max(0, */(int) (-surface.getOriginY() * hRatio)/* ) */;
		squareW = Math.min((int) (surface.getWidth() * wRatio), imgWidth - squareX);
		squareH = Math.min((int) (surface.getHeight() * hRatio), imgHeight - squareY);
	}
}
