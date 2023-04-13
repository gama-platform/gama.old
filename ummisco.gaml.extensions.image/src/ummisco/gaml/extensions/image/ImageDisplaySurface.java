/*******************************************************************************************************
 *
 * ImageDisplaySurface.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.display.AWTDisplayGraphics;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ImageDisplaySurface.
 */

/**
 * The Class ImageDisplaySurface.
 */
@display (
		value = IKeyword.IMAGE)
@doc ("A display used to save the graphical representations of agents into image files")
public class ImageDisplaySurface implements IDisplaySurface {

	/** The null point. */
	static GamaPoint NULL_POINT = new GamaPoint.Immutable();

	/** The output. */
	private final LayeredDisplayOutput output;

	/** The buff image. */
	private GamaImage bufferImage = null;

	/** The height. */
	private int width = 500, height = 500;

	/** The display graphics. */
	private IGraphics displayGraphics;

	/** The manager. */
	ILayerManager manager;

	/** The scope. */
	protected IGraphicsScope scope;

	/** The data. */
	private final LayeredDisplayData data;

	/**
	 * Instantiates a new image display surface.
	 *
	 * @param args
	 *            the args
	 */
	public ImageDisplaySurface(final Object... args) {
		output = (LayeredDisplayOutput) args[0];
		DEBUG.LOG("Image Display Surface created for simulation " + output.getScope().getSimulation());
		data = output.getData();
		resizeImage(width, height, true);

	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public void outputReloaded() {
		this.scope = output.getScope().copyForGraphics("in image surface of " + output.getName());
		if (!GamaPreferences.Runtime.ERRORS_IN_DISPLAYS.getValue()) { scope.disableErrorReporting(); }
		if (manager == null) {
			manager = new LayerManager(this, output);
		} else {
			manager.outputChanged();
		}

	}

	@Override
	public IGraphicsScope getScope() { return scope; }

	@Override
	public ILayerManager getManager() { return manager; }

	/**
	 * Resize image.
	 *
	 * @param newWidth
	 *            the new width
	 * @param newHeight
	 *            the new height
	 * @param force
	 *            the force
	 * @return true, if successful
	 */
	public boolean resizeImage(final int newWidth, final int newHeight, final boolean force) {
		if (!force && width == newWidth && height == newHeight) return false;
		this.width = newWidth;
		this.height = newHeight;
		final Image copy = bufferImage;
		bufferImage = GamaImage.ofDimensions(width, height);
		Graphics2D g2 = bufferImage.createGraphics();
		if (displayGraphics != null) { displayGraphics.dispose(); }
		displayGraphics = new AWTDisplayGraphics(g2);
		((AWTDisplayGraphics) displayGraphics).setUntranslatedGraphics2D(bufferImage.createGraphics());
		displayGraphics.setDisplaySurface(this);
		if (getScope() != null && getScope().isPaused()) {
			updateDisplay(true);
		} else if (copy != null) { g2.drawImage(copy, 0, 0, newWidth, newHeight, null); }
		if (copy != null) { copy.flush(); }
		return true;
	}

	@Override
	public void updateDisplay(final boolean force) {
		drawAllDisplays();
	}

	/**
	 * Draw all displays.
	 */
	private void drawAllDisplays() {
		if (displayGraphics == null) return;
		displayGraphics.fillBackground(data.getBackgroundColor());
		manager.drawLayersOn(displayGraphics);
	}

	@Override
	public void dispose() {
		if (displayGraphics != null) { displayGraphics.dispose(); }
		if (bufferImage != null) { bufferImage.flush(); }
		if (manager != null) { manager.dispose(); }
		GAMA.releaseScope(scope);
	}

	@Override
	public GamaImage getImage(final int w, final int h) {
		DEBUG.LOG("Asking to snapshot step " + scope.getClock().getCycle() + "  from " + Thread.currentThread()
				+ " in simulation " + scope.getSimulation().getIndex());
		setSize(w, h);
		drawAllDisplays();
		return bufferImage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomIn(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomIn() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomOut(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomOut() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#zoomFit(msi.gama.gui.views. IGamaView)
	 */
	@Override
	public void zoomFit() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.gui.graphics.IDisplaySurface#focusOn(msi.gama.util.GamaGeometry, msi.gama.gui.displays.IDisplay)
	 */
	@Override
	public void focusOn(final IShape geometry) {

	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() { return width; }

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() { return height; }

	@Override
	public void addListener(final IEventLayerListener e) {}

	@Override
	public double getEnvWidth() { return data.getEnvWidth(); }

	@Override
	public double getEnvHeight() { return data.getEnvHeight(); }

	@Override
	public double getDisplayWidth() { return this.getWidth(); }

	@Override
	public double getDisplayHeight() { return this.getHeight(); }

	/**
	 * Method getModelCoordinates()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() { return null; }

	/**
	 * Method getZoomLevel()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() { return 1.0; }

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
	public Collection<IEventLayerListener> getLayerListeners() { return Collections.EMPTY_LIST; }

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
			final Point positionInPixels) {
		return NULL_POINT;
	}

	@Override
	public IList<IAgent> selectAgent(final int xc, final int yc) {
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Method getOutput()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public LayeredDisplayOutput getOutput() { return output; }

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
	public LayeredDisplayData getData() { return data; }

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
	public int getFPS() { return 0; }

	@Override
	public boolean isDisposed() { return false; }

	@Override
	public void getModelCoordinatesInfo(final StringBuilder sb) {}

	@Override
	public void dispatchKeyEvent(final char character) {}

	@Override
	public void dispatchSpecialKeyEvent(final int e) {}

	/**
	 * Dispatch mouse event.
	 *
	 * @param swtEventType
	 *            the swt event type
	 */
	@Override
	public void dispatchMouseEvent(final int swtEventType, final int x, final int y) {}

	@Override
	public void setMousePosition(final int x, final int y) {}

	@Override
	public void draggedTo(final int x, final int y) {}

	@Override
	public void selectAgentsAroundMouse() {}

	@Override
	public void setMenuManager(final Object displaySurfaceMenu) {}

	@Override
	public boolean isVisible() { return true; }

	@Override
	public IGraphics getIGraphics() { return displayGraphics; }

	@Override
	public Rectangle getBoundsForRobotSnapshot() { return new Rectangle(0, 0, width, height); }

	@Override
	public boolean shouldWaitToBecomeRendered() {
		return false;
	}

}