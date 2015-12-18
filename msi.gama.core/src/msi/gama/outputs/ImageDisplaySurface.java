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
import java.awt.image.*;
import java.io.*;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.display.*;
import msi.gama.outputs.layers.ILayerMouseListener;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;
import msi.gaml.types.Types;

@display("image")
public class ImageDisplaySurface implements IDisplaySurface {

	private final LayeredDisplayOutput output;
	private final boolean needsUpdate = true;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width = 500, height = 500;
	private IGraphics displayGraphics;
	ILayerManager manager;
	public static String snapshotFolder = "/tmp/";
	protected IScope scope;
	private final LayeredDisplayData data;
	private boolean disposed;

	public ImageDisplaySurface(final Object ... args) {
		output = (LayeredDisplayOutput) args[0];
		data = output.getData();

	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void outputReloaded() {
		this.scope = output.getScope().copy();
		scope.disableErrorReporting();
		if ( manager == null ) {
			manager = new LayerManager(this, output);
		} else {
			manager.outputChanged();
		}

	}

	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
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

		final String file =
			snapshotFolder + "/" + GAMA.getModel().getName() + "_display_" + scope.getClock().getCycle() + ".png";
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
	public boolean resizeImage(final int newWidth, final int newHeight, final boolean force) {
		if ( !force && width == newWidth && height == newHeight ) { return false; }
		this.width = newWidth;
		this.height = newHeight;
		final Image copy = buffImage;
		createBuffImage();
		if ( GAMA.isPaused() ) {
			updateDisplay(true);
		} else {
			g2.drawImage(copy, 0, 0, newWidth, newHeight, null);
		}
		copy.flush();
		return true;
	}

	@Override
	public void updateDisplay(final boolean force) {
		if ( needsUpdate || force ) {
			drawAllDisplays();
		}
	}

	private void drawAllDisplays() {
		if ( displayGraphics == null ) { return; }
		displayGraphics.fillBackground(data.getBackgroundColor(), 1);
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
		if ( disposed ) { return; }
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

	//
	// @Override
	// public void canBeUpdated(final boolean ok) {
	// needsUpdate = ok;
	// }

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

	// /**
	// * @see msi.gama.common.interfaces.IDisplaySurface#getImageWidth()
	// */
	// @Override
	// public int getImageWidth() {
	// return width;
	// }
	//
	// /**
	// * @see msi.gama.common.interfaces.IDisplaySurface#getImageHeight()
	// */
	// @Override
	// public int getImageHeight() {
	// return height;
	// }

	// /**
	// * @see msi.gama.common.interfaces.IDisplaySurface#getOriginX()
	// */
	// @Override
	// public int getOriginX() {
	// return 0;
	// }
	//
	// /**
	// * @see msi.gama.common.interfaces.IDisplaySurface#getOriginY()
	// */
	// @Override
	// public int getOriginY() {
	// return 0;
	// }

	@Override
	public void addMouseListener(final ILayerMouseListener e) {}

	@Override
	public double getEnvWidth() {
		return data.getEnvWidth();
	}

	@Override
	public double getEnvHeight() {
		return data.getEnvHeight();
	}

	@Override
	public double getDisplayWidth() {
		return width;
	}

	@Override
	public double getDisplayHeight() {
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
	 * Method removeMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeMouseListener(final ILayerMouseListener e) {}

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
	public IList<IAgent> selectAgent(final int xc, final int yc) {
		IList<IAgent> result = GamaListFactory.create(Types.AGENT);
		final List<ILayer> layers = getManager().getLayersIntersecting(xc, yc);
		for ( ILayer layer : layers ) {
			if ( layer.isSelectable() ) {
				Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
				if ( !agents.isEmpty() ) {
					result.addAll(agents);
				}
			}
		}
		return result;
	}

	/**
	 * Method getOutput()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public IDisplayOutput getOutput() {
		return output;
	}

	/**
	 * Method waitForUpdateAndRun()
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {
		r.run();
	}

	/**
	 * Method getData()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getData()
	 */
	@Override
	public LayeredDisplayData getData() {
		return data;
	}

	/**
	 * Method setSWTMenuManager()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSWTMenuManager(java.lang.Object)
	 */
	@Override
	public void setSWTMenuManager(final Object displaySurfaceMenu) {}

	/**
	 * Method layersChanged()
	 * @see msi.gama.common.interfaces.IDisplaySurface#layersChanged()
	 */
	@Override
	public void layersChanged() {}

	/**
	 * Method changed()
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(msi.gama.outputs.LayeredDisplayData.Changes, boolean)
	 */
	@Override
	public void changed(final Changes property, final boolean value) {}

	/**
	 * Method acquireLock()
	 * @see msi.gama.common.interfaces.IDisplaySurface#acquireLock()
	 */
	@Override
	public void acquireLock() {}

	/**
	 * Method releaseLock()
	 * @see msi.gama.common.interfaces.IDisplaySurface#releaseLock()
	 */
	@Override
	public void releaseLock() {}

}