/**
 * Created by drogoul, 26 mars 2014
 * 
 */
package msi.gama.headless.runtime;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
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
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		return null;
	}

	/**
	 * Method dispose()
	 * @see msi.gama.common.interfaces.IDisplaySurface#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * Method updateDisplay()
	 * @see msi.gama.common.interfaces.IDisplaySurface#updateDisplay()
	 */
	@Override
	public void updateDisplay() {}

	/**
	 * Method forceUpdateDisplay()
	 * @see msi.gama.common.interfaces.IDisplaySurface#forceUpdateDisplay()
	 */
	@Override
	public void forceUpdateDisplay() {}

	/**
	 * Method computeBoundsFrom()
	 * @see msi.gama.common.interfaces.IDisplaySurface#computeBoundsFrom(int, int)
	 */
	@Override
	public int[] computeBoundsFrom(final int width, final int height) {
		return new int[] { 0, 0 };
	}

	/**
	 * Method resizeImage()
	 * @see msi.gama.common.interfaces.IDisplaySurface#resizeImage(int, int)
	 */
	@Override
	public boolean resizeImage(final int width, final int height) {
		return false;
	}

	/**
	 * Method zoomIn()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {}

	/**
	 * Method zoomOut()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {}

	/**
	 * Method zoomFit()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {}

	/**
	 * Method getManager()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	 */
	@Override
	public ILayerManager getManager() {
		return null;
	}

	/**
	 * Method focusOn()
	 * @see msi.gama.common.interfaces.IDisplaySurface#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void focusOn(final IShape geometry) {}

	/**
	 * Method canBeUpdated()
	 * @see msi.gama.common.interfaces.IDisplaySurface#canBeUpdated()
	 */
	@Override
	public boolean canBeUpdated() {
		return false;
	}

	/**
	 * Method canBeUpdated()
	 * @see msi.gama.common.interfaces.IDisplaySurface#canBeUpdated(boolean)
	 */
	@Override
	public void canBeUpdated(final boolean ok) {}

	/**
	 * Method setBackgroundColor()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setBackgroundColor(java.awt.Color)
	 */
	@Override
	public void setBackgroundColor(final Color background) {}

	/**
	 * Method setPaused()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setPaused(boolean)
	 */
	@Override
	public void setPaused(final boolean b) {}

	/**
	 * Method isPaused()
	 * @see msi.gama.common.interfaces.IDisplaySurface#isPaused()
	 */
	@Override
	public boolean isPaused() {
		return false;
	}

	/**
	 * Method setQualityRendering()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setQualityRendering(boolean)
	 */
	@Override
	public void setQualityRendering(final boolean quality) {}

	/**
	 * Method setSynchronized()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSynchronized(boolean)
	 */
	@Override
	public void setSynchronized(final boolean checked) {}

	/**
	 * Method setAutoSave()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setAutoSave(boolean, int, int)
	 */
	@Override
	public void setAutoSave(final boolean autosave, final int x, final int y) {}

	/**
	 * Method initOutput3D()
	 * @see msi.gama.common.interfaces.IDisplaySurface#initOutput3D(boolean, msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public void initOutput3D(final boolean output3d, final ILocation output3dNbCycles) {}

	/**
	 * Method setSnapshotFileName()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSnapshotFileName(java.lang.String)
	 */
	@Override
	public void setSnapshotFileName(final String string) {}

	/**
	 * Method snapshot()
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {}

	/**
	 * Method getWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() {
		return 0;
	}

	/**
	 * Method getHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return 0;
	}

	/**
	 * Method getImageWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImageWidth()
	 */
	@Override
	public int getImageWidth() {
		return 0;
	}

	/**
	 * Method getImageHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImageHeight()
	 */
	@Override
	public int getImageHeight() {
		return 0;
	}

	/**
	 * Method setOrigin()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setOrigin(int, int)
	 */
	@Override
	public void setOrigin(final int i, final int j) {}

	/**
	 * Method getOriginX()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginX()
	 */
	@Override
	public int getOriginX() {
		return 0;
	}

	/**
	 * Method getOriginY()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginY()
	 */
	@Override
	public int getOriginY() {
		return 0;
	}

	/**
	 * Method initialize()
	 * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.LayeredDisplayOutput)
	 */
	@Override
	public void initialize(final double w, final double h, final LayeredDisplayOutput layerDisplayOutput) {}

	/**
	 * Method outputChanged()
	 * @see msi.gama.common.interfaces.IDisplaySurface#outputChanged(double, double,
	 *      msi.gama.outputs.LayeredDisplayOutput)
	 */
	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {}

	/**
	 * Method getHighlightColor()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHighlightColor()
	 */
	@Override
	public int[] getHighlightColor() {
		return null;
	}

	/**
	 * Method setHighlightColor()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setHighlightColor(int[])
	 */
	@Override
	public void setHighlightColor(final int[] rgb) {}

	/**
	 * Method addMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addMouseListener(final MouseListener e) {}

	/**
	 * Method removeMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeMouseListener(final MouseListener e) {}

	/**
	 * Method getEnvWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvWidth()
	 */
	@Override
	public double getEnvWidth() {
		return 0;
	}

	/**
	 * Method getEnvHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvHeight()
	 */
	@Override
	public double getEnvHeight() {
		return 0;
	}

	/**
	 * Method getDisplayWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayWidth()
	 */
	@Override
	public int getDisplayWidth() {
		return 0;
	}

	/**
	 * Method getDisplayHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayHeight()
	 */
	@Override
	public int getDisplayHeight() {
		return 0;
	}

	/**
	 * Method setZoomListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setZoomListener(msi.gama.common.interfaces.IDisplaySurface.IZoomListener)
	 */
	@Override
	public void setZoomListener(final IZoomListener listener) {}

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		return new GamaPoint();
	}

	/**
	 * Method getModelCoordinatesFrom()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesFrom(int, int, java.awt.Point, java.awt.Point)
	 */
	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		return new GamaPoint();
	}

	/**
	 * Method selectAgent()
	 * @see msi.gama.common.interfaces.IDisplaySurface#selectAgent(int, int)
	 */
	@Override
	public IList<IAgent> selectAgent(final int x, final int y) {
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
		return 0;
	}

	/**
	 * Method setSize()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {}

	/**
	 * Method getQualityRendering()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getQualityRendering()
	 */
	@Override
	public boolean getQualityRendering() {
		return false;
	}

}
