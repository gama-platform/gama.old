/*******************************************************************************************************
 *
 * msi.gama.outputs.display.NullDisplaySurface.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.display;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;

/**
 * Class NullDisplaySurface.
 *
 * @author drogoul
 * @since 26 mars 2014
 *
 */
public class NullDisplaySurface implements IDisplaySurface {

	/**
	 * Method getImage()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage(final int w, final int h) {
		return null;
	}

	@Override
	public IScope getScope() {
		return null;
	}

	/**
	 * Method dispose()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * Method updateDisplay()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#updateDisplay()
	 */
	@Override
	public void updateDisplay(final boolean force) {}

	/**
	 * Method zoomIn()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {}

	/**
	 * Method zoomOut()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {}

	/**
	 * Method zoomFit()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {}

	/**
	 * Method getManager()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	 */
	@Override
	public ILayerManager getManager() {
		return null;
	}

	/**
	 * Method focusOn()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void focusOn(final IShape geometry) {}

	/**
	 * Method getWidth()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() {
		return 0;
	}

	/**
	 * Method getHeight()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return 0;
	}

	/**
	 * Method initialize()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.LayeredDisplayOutput)
	 */
	@Override
	public void outputReloaded() {}

	/**
	 * Method addMouseListener()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addListener(final IEventLayerListener e) {}

	/**
	 * Method removeMouseListener()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeListener(final IEventLayerListener e) {}

	/**
	 * Method getEnvWidth()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvWidth()
	 */
	@Override
	public double getEnvWidth() {
		return 0;
	}

	/**
	 * Method getEnvHeight()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvHeight()
	 */
	@Override
	public double getEnvHeight() {
		return 0;
	}

	/**
	 * Method getDisplayWidth()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayWidth()
	 */
	@Override
	public double getDisplayWidth() {
		return 0;
	}

	/**
	 * Method getDisplayHeight()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayHeight()
	 */
	@Override
	public double getDisplayHeight() {
		return 0;
	}

	/**
	 * Method setZoomListener()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#setZoomListener(msi.gama.common.interfaces.IDisplaySurface.IZoomListener)
	 */
	// @Override
	// public void setZoomListener(final IZoomListener listener) {}

	/**
	 * Method getModelCoordinates()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		return new GamaPoint();
	}

	/**
	 * Method getModelCoordinatesFrom()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesFrom(int, int, java.awt.Point, java.awt.Point)
	 */
	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
			final Point positionInPixels) {
		return new GamaPoint();
	}

	/**
	 * Method selectAgent()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#selectAgent(int, int)
	 */
	@Override
	public IList<IAgent> selectAgent(final int x, final int y) {
		return null;
	}

	// /**
	// * Method isSynchronized()
	// * @see msi.gama.common.interfaces.IDisplaySurface#isSynchronized()
	// */
	// @Override
	// public boolean isSynchronized() {
	// return false;
	// }

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
		return 0;
	}

	/**
	 * Method setSize()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {}

	/**
	 * Method getOutput()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public LayeredDisplayOutput getOutput() {
		return null;
	}

	/**
	 * Method waitForUpdateAndRun()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {}

	/**
	 * Method getData()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getData()
	 */
	@Override
	public LayeredDisplayData getData() {
		return null;
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

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return Collections.EMPTY_LIST;
	}

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

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#isDisposed()
	 */
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
	public void selectAgentsAroundMouse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draggedTo(final int x, final int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMenuManager(final Object displaySurfaceMenu) {
		// TODO Auto-generated method stub

	}

}
