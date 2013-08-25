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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Files;

@display("image")
public class ImageDisplaySurface implements IDisplaySurface {

	private boolean needsUpdate = true;
	private double widthHeightConstraint = 1.0;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width, height;
	private IGraphics displayGraphics;
	protected Color bgColor = Color.black;
	ILayerManager manager;
	private String snapshotFileName;
	public static String snapshotFolder = "/tmp/";
	public double envWidth, envHeight;

	public ImageDisplaySurface(final Object ... args) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void initialize(final double w, final double h, final LayeredDisplayOutput output) {
		outputChanged(w, h, output);
	}

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public void save(final IScope scope, final RenderedImage image) {
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(e1, false);
			e1.printStackTrace();
			return;
		}

		final String file = snapshotFolder + "/" + snapshotFileName + scope.getClock().getCycle() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (final Exception ex) {
				final GamaRuntimeException e = GamaRuntimeException.create(ex);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(e, false);
			}
		}
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		widthHeightConstraint = env_height / env_width;
		envWidth = env_width;
		envHeight = env_height;
		if ( output == null ) { return; }
		bgColor = output.getBackgroundColor();
		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width, env_height,
					displayGraphics));
			}
		} else {
			manager.outputChanged();
		}

	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		final int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	@Override
	public boolean resizeImage(final int newWidth, final int newHeight) {
		if ( width == newWidth && height == newHeight ) { return false; }
		this.width = newWidth;
		this.height = newHeight;
		final Image copy = buffImage;
		createBuffImage();
		if ( GAMA.isPaused() ) {
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

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	private void drawAllDisplays() {
		if ( displayGraphics == null ) { return; }
		displayGraphics.fillBackground(bgColor, 1);
		manager.drawLayersOn(displayGraphics);
	}

	private void createBuffImage() {
		buffImage = ImageUtils.createCompatibleImage(width, height);
		g2 = (Graphics2D) buffImage.getGraphics();
		displayGraphics = new AWTDisplayGraphics(this, (Graphics2D) buffImage.getGraphics());
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
	// @Override
	// public void fireSelectionChanged(final Object a) {
	// // TODO Auto-generated method stub
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IDisplaySurface#focusOn(msi.gama.util.GamaGeometry,
	 * msi.gama.gui.displays.IDisplay)
	 */
	@Override
	public void focusOn(final IShape geometry, final ILayer display) {
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
	public void setAutoSave(final boolean autosave, final int x, final int y) {}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {
		final IScope scope = GAMA.obtainNewScope();
		save(scope, buffImage);
		GAMA.releaseScope(scope);
	}

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

	@Override
	public int[] getHighlightColor() {
		return new int[] { 0, 0, 0 };
	}

	@Override
	public void setHighlightColor(final int[] rgb) {}

	/**
	 * This method does nothing for Image display
	 */
	@Override
	public void addShapeFile() {}

	//
	// @Override
	// public ILayerManager getLayerManager() {
	// return manager;
	// }

	@Override
	public void addMouseListener(final MouseListener e) {}

	@Override
	public void initOutput3D(final boolean output3d, final ILocation output3dNbCycles) {
		;
	}

	@Override
	public double getEnvWidth() {
		return envWidth;
	}

	@Override
	public double getEnvHeight() {
		return envHeight;
	}

	@Override
	public int getDisplayWidth() {
		return 0;
	}

	@Override
	public int getDisplayHeight() {
		return 0;
	}

	@Override
	public void setZoomListener(final IZoomListener listener) {}

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		return null;
	}

	/**
	 * Method isSynchronized()
	 * @see msi.gama.common.interfaces.IDisplaySurface#isSynchronized()
	 */
	@Override
	public boolean isSynchronized() {
		return false;
	}

	/**
	 * Method followAgent()
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {}

	/**
	 * Method getZoomLevel()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() {
		return 1.0;
	}

	/**
	 * Method setSize()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {
		resizeImage(x, y);
	}
}
