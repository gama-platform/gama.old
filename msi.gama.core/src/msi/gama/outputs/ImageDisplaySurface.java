/*******************************************************************************************************
 *
 * msi.gama.outputs.ImageDisplaySurface.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.display.AWTDisplayGraphics;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Files;

@display ("image")
public class ImageDisplaySurface implements IDisplaySurface {

	private final LayeredDisplayOutput output;
	// private final boolean needsUpdate = true;
	private BufferedImage buffImage = null;
	private Graphics2D g2 = null;
	private int width = 500, height = 500;
	private IGraphics displayGraphics;
	ILayerManager manager;
	public static String snapshotFolder = "/tmp/";
	protected IScope scope;
	private final LayeredDisplayData data;

	public ImageDisplaySurface(final Object... args) {
		output = (LayeredDisplayOutput) args[0];
		data = output.getData();

	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void outputReloaded() {
		this.scope = output.getScope().copy("in image surface of " + output.getName());
		if (!GamaPreferences.Runtime.ERRORS_IN_DISPLAYS.getValue()) {
			scope.disableErrorReporting();
		}
		if (manager == null) {
			manager = new LayerManager(this, output);
		} else {
			manager.outputChanged();
		}

	}

	@Override
	public IScope getScope() {
		return scope;
	}

	/**
	 * Save this surface into an image passed as a parameter
	 *
	 * @param actionScope
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
		// DataOutputStream os = null;
		try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
			ImageIO.write(image, "png", os);
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(getScope(), e, false);
		}
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	public boolean resizeImage(final int newWidth, final int newHeight, final boolean force) {
		if (!force && width == newWidth && height == newHeight) { return false; }
		this.width = newWidth;
		this.height = newHeight;
		final Image copy = buffImage;
		createBuffImage();
		if (getScope()!=null && getScope().isPaused()) {
			updateDisplay(true);
		} else {
			g2.drawImage(copy, 0, 0, newWidth, newHeight, null);
		}
		if(copy!=null)
			copy.flush();
		return true;
	}

	@Override
	public void updateDisplay(final boolean force) {
		// if ( needsUpdate || force ) {
		drawAllDisplays();
		// }
	}

	private void drawAllDisplays() {
		if (displayGraphics == null) { return; }
		displayGraphics.fillBackground(data.getBackgroundColor(), 1);
		manager.drawLayersOn(displayGraphics);
	}

	private void createBuffImage() {
		buffImage = ImageUtils.createCompatibleImage(width, height, false);
		g2 = (Graphics2D) buffImage.getGraphics();
		displayGraphics = new AWTDisplayGraphics((Graphics2D) buffImage.getGraphics());
		((AWTDisplayGraphics) displayGraphics).setGraphics2D((Graphics2D) buffImage.getGraphics());
		((AWTDisplayGraphics) displayGraphics).setUntranslatedGraphics2D((Graphics2D) buffImage.getGraphics());
		displayGraphics.setDisplaySurface(this);
	}

	private void paint() {
		if (buffImage == null) {
			createBuffImage();
		}
		drawAllDisplays();

	}

	@Override
	public void dispose() {
		if (g2 != null) {
			g2.dispose();
		}
		if (manager != null) {
			manager.dispose();
		}
		GAMA.releaseScope(scope);
	}

	@Override
	public BufferedImage getImage(final int w, final int h) {
		paint();
		return ImageUtils.resize(buffImage, w, h);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomIn(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomOut(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomFit(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#fireSelectionChanged(java.lang. Object)
	 */
	// @Override
	// public void fireSelectionChanged(final Object a) {
	// // TODO Auto-generated method stub
	//
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#focusOn(msi.gama.util.GamaGeometry, msi.gama.gui.displays.IDisplay)
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
	public void addListener(final IEventLayerListener e) {}

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

	// @Override
	// public void setZoomListener(final IZoomListener listener) {}
	//
	/**
	 * Method getModelCoordinates()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		return null;
	}

	/**
	 * Method followAgent()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {}

	/**
	 * Method getZoomLevel()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() {
		return 1.0;
	}

	/**
	 * Method setSize()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {
		resizeImage(x, y, false);
	}

	/**
	 * Method removeMouseListener()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeListener(final IEventLayerListener e) {}

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return Collections.EMPTY_LIST;
	}

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
		return GamaListFactory.EMPTY_LIST;
		// final IList<IAgent> result = GamaListFactory.create(Types.AGENT);
		// final List<ILayer> layers = getManager().getLayersIntersecting(xc,
		// yc);
		// for (final ILayer layer : layers) {
		// if (layer.isSelectable()) {
		// final Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
		// if (!agents.isEmpty()) {
		// result.addAll(agents);
		// }
		// }
		// }
		// return result;
	}

	/**
	 * Method getOutput()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public LayeredDisplayOutput getOutput() {
		return output;
	}

	/**
	 * Method waitForUpdateAndRun()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {
		r.run();
	}

	/**
	 * Method getData()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getData()
	 */
	@Override
	public LayeredDisplayData getData() {
		return data;
	}

	/**
	 * Method setSWTMenuManager()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSWTMenuManager(java.lang.Object)
	 */
	// @Override
	// public void setSWTMenuManager(final Object displaySurfaceMenu) {
	// }

	/**
	 * Method layersChanged()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#layersChanged()
	 */
	@Override
	public void layersChanged() {}

	/**
	 * Method changed()
	 *
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(msi.gama.outputs.LayeredDisplayData.Changes,
	 *      boolean)
	 */
	@Override
	public void changed(final Changes property, final Object value) {}

	/**
	 * Method getVisibleRegionForLayer()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getVisibleRegionForLayer(msi.gama.common.interfaces.ILayer)
	 */
	@Override
	public Envelope getVisibleRegionForLayer(final ILayer currentLayer) {
		return null;
	}

	/**
	 * Method getFPS()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getFPS()
	 */
	@Override
	public int getFPS() {
		return 0;
	}

	@Override
	public boolean isRealized() {
		return true;
	}

	/**
	 * Method isRendered()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#isRendered()
	 */
	@Override
	public boolean isRendered() {
		return true;
	}

	@Override
	public boolean isDisposed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesInfo()
	 */
	@Override
	public void getModelCoordinatesInfo(final StringBuilder sb) {}

	@Override
	public void dispatchKeyEvent(final char character) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispatchMouseEvent(final int swtEventType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMousePosition(final int x, final int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draggedTo(final int x, final int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAgentsAroundMouse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMenuManager(final Object displaySurfaceMenu) {
		// TODO Auto-generated method stub

	}

}