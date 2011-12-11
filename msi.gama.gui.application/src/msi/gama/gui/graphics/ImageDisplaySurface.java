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
import java.awt.image.BufferedImage;
import java.util.List;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.layers.AbstractDisplayLayer;
import msi.gama.util.*;

public class ImageDisplaySurface implements IDisplaySurface {

	private boolean needsUpdate = true;
	private double widthHeightConstraint = 1.0;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width, height;
	private final GamaList<IDisplay> displays = new GamaList();
	private IGraphics displayGraphics;
	protected Color bgColor = Color.black;

	public ImageDisplaySurface(final double env_width, final double env_height) {
		outputChanged(env_width, env_height, null);
	}

	@Override
	public void outputChanged(final double env_width, final double env_height,
		final IDisplayOutput output) {
		widthHeightConstraint = env_height / env_width;
		if ( output == null ) { return; }
		bgColor = output.getBackgroundColor();
		final List<? extends ISymbol> layers = output.getChildren();
		for ( final ISymbol layer : layers ) {
			addDisplay(DisplayManager.createDisplay((AbstractDisplayLayer) layer, env_width,
				env_height, displayGraphics), layer.getName());
		}

	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	@Override
	public boolean resizeImage(final int newWidth, final int newHeight) {
		if ( width == newWidth && height == newHeight ) { return false; }
		this.width = newWidth;
		this.height = newHeight;
		Image copy = buffImage;
		if ( displayGraphics == null ) {
			displayGraphics = new AWTDisplayGraphics(newWidth, newHeight);
		} else {
			displayGraphics.setDisplayDimensions(newWidth, newHeight);
		}
		createBuffImage();
		if ( GAMA.getFrontmostSimulation().isPaused() ) {
			updateDisplay();
		} else {
			g2.drawImage(copy, 0, 0, newWidth, newHeight, null);
		}
		copy.flush();
		return true;
	}

	private void addDisplay(final IDisplay d, final String name) {
		displays.add(d);
	}

	@Override
	public void updateDisplay() {
		if ( needsUpdate ) {
			try {
				drawAllDisplays();
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
			}
		}
	}

	private void drawAllDisplays() throws GamaRuntimeException {
		if ( g2 == null ) { return; }
		g2.setColor(bgColor);
		g2.fillRect(0, 0, width, height);
		for ( int i = 0; i < displays.size(); i++ ) {
			final IDisplay dis = displays.get(i);
			dis.drawDisplay(displayGraphics);
		}
	}

	private void createBuffImage() {
		buffImage = ImageCache.createCompatibleImage(width, height);
		g2 = (Graphics2D) buffImage.getGraphics();
		displayGraphics.setGraphics(g2);
	}

	private void paint() {
		if ( buffImage == null ) {
			createBuffImage();
			try {
				drawAllDisplays();
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
			}
		}
	}

	@Override
	public void dispose() {
		if ( g2 != null ) {
			g2.dispose();
		}
		for ( IDisplay i : displays ) {
			i.dispose();
		}
		displays.clear();
	}

	@Override
	public BufferedImage getImage() {
		paint();
		return buffImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomIn(msi.gama.gui.application.views.IGamaView)
	 */
	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomOut(msi.gama.gui.application.views.IGamaView)
	 */
	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomFit(msi.gama.gui.application.views.IGamaView)
	 */
	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub

	}

	@Override
	public DisplayManager getManager() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#fireSelectionChanged(java.lang.Object)
	 */
	@Override
	public void fireSelectionChanged(final Object a) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#focusOn(msi.gama.util.GamaGeometry,
	 * msi.gama.gui.displays.IDisplay)
	 */
	@Override
	public void focusOn(final IGeometry geometry, final IDisplay display) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#canBeUpdated()
	 */
	@Override
	public boolean canBeUpdated() {
		return needsUpdate;
	}

	@Override
	public void canBeUpdated(final boolean ok) {
		needsUpdate = ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#setBackgroundColor(java.awt.Color)
	 */
	@Override
	public void setBackgroundColor(final Color background) {
		// TODO Auto-generated method stub

	}

}
