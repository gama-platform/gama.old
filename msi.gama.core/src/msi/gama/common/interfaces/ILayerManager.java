/*********************************************************************************************
 *
 * 'ILayerManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.geom.Rectangle2D;
import java.util.List;

import msi.gama.metamodel.shape.IShape;

/**
 * The class IDisplayManager.
 *
 * @author drogoul
 * @since 15 d�c. 2011
 *
 */
public interface ILayerManager extends ItemList<ILayer> {

	/**
	 * @param abstractDisplay
	 * @param newValue
	 */
	void enableLayer(ILayer display, Boolean newValue);

	/**
	 * @param abstractDisplay
	 * @return
	 */
	boolean isEnabled(ILayer display);

	/**
	 * @param env_width
	 * @param env_height
	 */
	void outputChanged();

	/**
	 * @param xc
	 * @param yc
	 * @return
	 */
	List<ILayer> getLayersIntersecting(int xc, int yc);

	/**
	 * @param displayGraphics
	 */
	void drawLayersOn(IGraphics displayGraphics);

	/**
	 *
	 */
	void dispose();

	/**
	 * @param createDisplay
	 * @return
	 */
	ILayer addLayer(ILayer createDisplay);

	boolean stayProportional();

	/**
	 * @return
	 * @param geometry
	 */
	Rectangle2D focusOn(IShape geometry, IDisplaySurface s);

	/**
	 * @return
	 */
	boolean isProvidingCoordinates();

	/**
	 * @return
	 */
	boolean isProvidingWorldCoordinates();

}
