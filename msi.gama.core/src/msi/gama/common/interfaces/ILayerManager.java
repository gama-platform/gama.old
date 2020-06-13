/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.ILayerManager.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.geom.Rectangle2D;
import java.util.List;

import msi.gama.metamodel.shape.IShape;

/**
 * The class ILayerManager. Manages a fixed set of layers on behalf of a IDisplaySurface
 *
 * @author drogoul
 * @since 15 dec. 2011
 *
 */
public interface ILayerManager extends ItemList<ILayer>, IDisposable {

	/**
	 * Forces all layers to reload on the surface
	 */
	void outputChanged();

	/**
	 * @param xc
	 *            x-ordinate on screen
	 * @param yc
	 *            y-ordinate on screen
	 * @return a list of ILayers that contain the screen point {x,y} or an empty list if none contain it
	 */
	List<ILayer> getLayersIntersecting(int xc, int yc);

	/**
	 * Asks this manager to draw all of its enabled layers on the graphics passed in parameter
	 *
	 * @param displayGraphics
	 *            an instance of IGraphics on which to draw the layers
	 */
	void drawLayersOn(IGraphics displayGraphics);

	/**
	 * Whether the layers in this manager are to be drawn by respecting the world's proportions or not
	 *
	 * @return true if at least one layer needs to be drawn proportionnaly, false otherwise
	 */
	boolean stayProportional();

	/**
	 * Returns a rectangle that represent the area to focus on in order to focus on the geometry passed in parameter
	 *
	 * @param geometry
	 *            the geometry or agent on which to focus on
	 * @param s
	 *            the surface of this manager
	 * @return a rectangle in screen coordinates
	 */
	Rectangle2D focusOn(IShape geometry, IDisplaySurface s);

	/**
	 * Whether any of the layer managed by this manager can return coordinates for the position of the mouse
	 *
	 * @return true if at least one layer can provide coordinates
	 */
	boolean isProvidingCoordinates();

	/**
	 * Whether any of the layers managed by this manager can return world coordinates for the position of the mouse
	 *
	 * @return true if at least one layer can provide world coordinates
	 */
	boolean isProvidingWorldCoordinates();

	boolean hasMouseMenuEventLayer();

}
