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
package msi.gama.gui.displays;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISymbol;

public class ImageDisplaySurface implements IDisplaySurface {

	private boolean needsUpdate = true;
	private double widthHeightConstraint = 1.0;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width, height;
	private IGraphics displayGraphics;
	protected Color bgColor = Color.black;
	IDisplayManager manager;

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double,
	 *      msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void initialize(final double w, final double h, final IDisplayOutput output) {
		outputChanged(w, h, output);
	}

	@Override
	public IDisplayManager getManager() {
		return manager;
	}

	@Override
	public void outputChanged(final double env_width, final double env_height,
		final IDisplayOutput output) {
		widthHeightConstraint = env_height / env_width;
		if ( output == null ) { return; }
		bgColor = output.getBackgroundColor();
		if ( manager == null ) {
			manager = new DisplayManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				manager.addDisplay(DisplayManager.createDisplay((IDisplayLayer) layer, env_width,
					env_height, displayGraphics));
			}
		} else {
			manager.updateEnvDimensions(env_width, env_height);
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
			displayGraphics = GuiUtils.newGraphics(newWidth, newHeight);
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

	@Override
	public void updateDisplay() {
		if ( needsUpdate ) {
			drawAllDisplays();
		}
	}

	private void drawAllDisplays() {
		if ( displayGraphics == null ) { return; }
		displayGraphics.fill(bgColor, 1);
		manager.drawDisplaysOn(displayGraphics);
	}

	private void createBuffImage() {
		buffImage = ImageUtils.createCompatibleImage(width, height);
		g2 = (Graphics2D) buffImage.getGraphics();
		displayGraphics.setGraphics(g2);
	}

	private void paint() {
		if ( buffImage == null ) {
			createBuffImage();
			drawAllDisplays();
		}
	}

	@Override
	public void dispose() {
		if ( g2 != null ) {
			g2.dispose();
		}
		if ( manager != null ) {
			manager.dispose();
		}
	}

	@Override
	public BufferedImage getImage() {
		paint();
		return buffImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomIn(msi.gama.gui.views.IGamaView)
	 */
	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomOut(msi.gama.gui.views.IGamaView)
	 */
	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomFit(msi.gama.gui.views.IGamaView)
	 */
	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub

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
	public void focusOn(final IShape geometry, final IDisplay display) {
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
	public void setBackgroundColor(final Color c) {
		bgColor = c;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setPaused(boolean)
	 */
	@Override
	public void setPaused(final boolean b) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#isPaused()
	 */
	@Override
	public boolean isPaused() {
		return false;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSynchronized(boolean)
	 */
	@Override
	public void setSynchronized(final boolean checked) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setQualityRendering(boolean)
	 */
	@Override
	public void setQualityRendering(final boolean quality) {
		displayGraphics.setQualityRendering(quality);
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setAutoSave(boolean)
	 */
	@Override
	public void setAutoSave(final boolean autosave) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSnapshotFileName(java.lang.String)
	 */
	@Override
	public void setSnapshotFileName(final String string) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setNavigator(java.lang.Object)
	 */
	@Override
	public void setNavigator(final Object swtNavigationPanel) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImageWidth()
	 */
	@Override
	public int getImageWidth() {
		return width;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImageHeight()
	 */
	@Override
	public int getImageHeight() {
		return height;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setOrigin(int, int)
	 */
	@Override
	public void setOrigin(final int i, final int j) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginX()
	 */
	@Override
	public int getOriginX() {
		return 0;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginY()
	 */
	@Override
	public int getOriginY() {
		return 0;
	}

}
