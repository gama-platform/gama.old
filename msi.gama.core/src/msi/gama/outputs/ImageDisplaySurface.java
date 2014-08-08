/*********************************************************************************************
 * 
 * 
 * 'ImageDisplaySurface.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.display.*;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Files;

@display("image")
public class ImageDisplaySurface implements IDisplaySurface {

	private boolean needsUpdate = true;
	private double widthHeightConstraint = 1.0;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width = 500, height = 500;
	private IGraphics displayGraphics;
	protected Color bgColor = Color.black;
	ILayerManager manager;
	private String snapshotFileName;
	public static String snapshotFolder = "/tmp/";
	public double envWidth, envHeight;
	protected IScope scope;

	public ImageDisplaySurface() {}

	public ImageDisplaySurface(final Object ... args) {}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void initialize(final IScope scope, final double w, final double h, final LayeredDisplayOutput output) {
		outputChanged(scope, w, h, output);
	}

	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public void save(final RenderedImage image) {
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(scope, e1, false);
			e1.printStackTrace();
			return;
		}

		final String file = snapshotFolder + "/" + snapshotFileName + scope.getClock().getCycle() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(getDisplayScope(), e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (final Exception ex) {
				final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(getDisplayScope(), e, false);
			}
		}
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	@Override
	public void outputChanged(final IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput output) {
		this.scope = scope.copy();
		scope.disableErrorReporting();
		widthHeightConstraint = env_height / env_width;
		envWidth = env_width;
		envHeight = env_height;
		if ( output == null ) { return; }
		bgColor = output.getBackgroundColor();
		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				manager.addLayer(AbstractLayer.createLayer(scope, (ILayerStatement) layer));
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
	public boolean resizeImage(final int newWidth, final int newHeight, final boolean force) {
		if ( !force && width == newWidth && height == newHeight ) { return false; }
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

	/*
	 * public void drawDisplaysWithoutRepainting() {
	 * if ( iGraphics == null ) { return; }
	 * ex[0] = null;
	 * iGraphics.fillBackground(bgColor, 1);
	 * manager.drawLayersOn(iGraphics);
	 * }
	 */

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
		GAMA.releaseScope(scope);
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
	public void focusOn(final IShape geometry) {
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
		save(buffImage);
	}

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
		return this.getImageWidth();
	}

	@Override
	public int getDisplayHeight() {
		return this.getHeight();
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
		resizeImage(x, y, false);
	}

	/**
	 * Method getQualityRendering()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getQualityRendering()
	 */
	@Override
	public boolean getQualityRendering() {
		return true;
	}

	/**
	 * Method removeMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeMouseListener(final MouseListener e) {}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		final double xScale = sizeInPixels.x / getEnvWidth();
		final double yScale = sizeInPixels.y / getEnvHeight();
		final int xInDisplay = xOnScreen - positionInPixels.x;
		final int yInDisplay = yOnScreen - positionInPixels.y;
		final double xInModel = xInDisplay / xScale;
		final double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
	}

	@Override
	public IList<IAgent> selectAgent(final int x, final int y) {
		int xc = x - getOriginX();
		int yc = y - getOriginY();
		IList<IAgent> result = new GamaList<IAgent>();
		final List<ILayer> layers = getManager().getLayersIntersecting(xc, yc);
		for ( ILayer layer : layers ) {
			Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
			if ( !agents.isEmpty() ) {
				result.addAll(agents);
			}
		}
		return result;
	}

}