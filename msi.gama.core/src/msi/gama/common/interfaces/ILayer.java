/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.ILayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.primitives.Ints;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.ILayerData;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Represents the concrete layers that are displayed on IDisplaySurface's and managed by its ILayerManager
 *
 * @author A. Drogoul
 * @since nov. 2009
 *
 *
 */
public interface ILayer extends INamed, Comparable<ILayer> {

	/**
	 * Returns the statement (ILayerStatement) that constitutes the definition of this layer, or null if it has none
	 *
	 * @return an instance of ILayerStatement or null
	 */
	ILayerStatement getDefinition();

	/**
	 * Returns the instance of ILayerData that holds all the data used by this layer
	 *
	 * @return an instance of ILayerData (never null)
	 */
	ILayerData getData();

	/**
	 * Returns the name to use in the layers menu in a display
	 *
	 * @return a string representing this layer in the layers menu
	 */
	default String getMenuName() {
		return getType() + ItemList.SEPARATION_CODE + getName();
	}

	/**
	 * Asks this layer to draw itself on the IGraphics instance passed in parameter.
	 *
	 * @param scope
	 *            the current scope (usually that of the surface)
	 * @param simGraphics
	 *            the current instance of IGraphics on which to draw the elements of the layer
	 * @throws GamaRuntimeException
	 */

	void draw(IScope scope, IGraphics simGraphics) throws GamaRuntimeException;

	/**
	 * Asks this layer to dispose of the resources it may use (in addition to the ILayerData instance, which is
	 * automatically disposed of)
	 */
	default void dispose() {}

	/**
	 * Whether the layer requires to stay proportional to the dimensions of the world or not when displaying itself. For
	 * instance, chart layers accept to be stretched to the dimensions of the display instead
	 *
	 * @return true if the layer elements need to be drawn proportionnaly to the dimensions of the world, false
	 *         otherwise
	 */
	default boolean stayProportional() {
		return true;
	}

	/**
	 * Asks the layer to reload itself (i.e. to make itself visible again) on the given surface. Default is to mark
	 * itself as not already drawn before
	 *
	 * @param surface
	 *            the display surface on which this layer is drawn
	 */
	default void reloadOn(final IDisplaySurface surface) {
		forceRedrawingOnce();
	}

	/**
	 * Indicates that this layer will make its first appearance on the surface, before being displayed
	 *
	 * @param surface
	 *            the display surface on which this layer is drawn
	 */
	default void firstLaunchOn(final IDisplaySurface surface) {}

	/**
	 * Indicates that this layer will has been enabled on the surface. Useful when layers "hook" on the surface (for
	 * instance, EventLayers)
	 *
	 * @param surface
	 *            the display surface on which this layer is drawn
	 */
	default void enableOn(final IDisplaySurface surface) {}

	/**
	 * Indicates that this layer has been disabled on the surfaces. Useful when layers "hook" on the surface (for
	 * instance, EventLayers)
	 *
	 * @param surface
	 *            the display surface on which this layer is drawn
	 */
	default void disableOn(final IDisplaySurface surface) {
		forceRedrawingOnce();
	}

	/**
	 * Returns the human-readable type of the layer for use in the UI
	 *
	 * @return a string describing the type of the layer (e.g. "Agents layer", etc.)
	 */
	String getType();

	/**
	 * Returns a rectangle that represents, in screen coordinates, the position of the geometry on which to focus in
	 * this layer
	 *
	 * @param geometry
	 *            a geometry or an agent
	 * @param s
	 *            the surface on which this focus is requested
	 * @return a rectangle in screen coordinates
	 */
	default Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final Envelope3D envelope = geometry.getEnvelope();
		final Point min = this.getScreenCoordinatesFrom(envelope.getMinX(), envelope.getMinY(), s);
		final Point max = this.getScreenCoordinatesFrom(envelope.getMaxX(), envelope.getMaxY(), s);
		return new Rectangle2D.Double(min.x, min.y, (double) max.x - min.x, (double) max.y - min.y);
	}

	/**
	 * Returns the collection of agents populating this layer in order to display them in the agents menu of the display
	 *
	 * @param scope
	 *            the current scope (usually the surface's one)
	 * @return a collection of agents or an empty collection if no agents are drawn on this layer
	 */
	default Collection<IAgent> getAgentsForMenu(final IScope scope) {
		return Collections.EMPTY_LIST; // by default
	}

	/**
	 * Whether this layer can be used as a support for providing coordinates (used to indicate the position of the
	 * mouse)
	 *
	 * @return true if this layer can provide coordinates, false otherwise
	 */
	default boolean isProvidingCoordinates() {
		return true; // by default
	}

	/**
	 * Whether this layer can be used as a support for providing world coordinates (used to indicate the position of the
	 * mouse)
	 *
	 * @return true if this layer can provide coordinates, false otherwise
	 */
	default boolean isProvidingWorldCoordinates() {
		return true; // by default
	}

	/**
	 * Returns whether this layer contains the mouse cursor (or the point on screen passed in parameter)
	 *
	 * @param x
	 *            the x-ordinate on screen
	 * @param y
	 *            the y-ordinate on screen
	 * @return true if {x,y} is inside the layer, false otherwise
	 */
	default boolean containsScreenPoint(final int x, final int y) {
		final Point p = getData().getPositionInPixels();
		final Point s = getData().getSizeInPixels();
		return x >= p.x && y >= p.y && x <= p.x + s.x && y <= p.y + s.y;
	}

	/**
	 * Returns the world (model) coordinates of the mouse cursor (or the point on screen passed in parameter)
	 *
	 * @param xOnScreen
	 *            the x-ordinate on screen
	 * @param yOnScreen
	 *            the y-ordinate on screen
	 * @param g
	 *            the surface on which the layer is displayed
	 * @return a point describing a position in the world
	 */
	default ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinatesFrom(xOnScreen, yOnScreen, getData().getSizeInPixels(),
				getData().getPositionInPixels());
	}

	/**
	 * Returns a point on screen whose coordinates correspond to the location in the world passed in parameter
	 *
	 * @param x
	 *            the x-ordinate in the world
	 * @param y
	 *            the y-ordinate in the world
	 * @param g
	 *            the surface on which the layer is displayed
	 * @return a point describing a position on screen
	 */
	default Point getScreenCoordinatesFrom(final double x, final double y, final IDisplaySurface g) {
		final double xFactor = x / g.getEnvWidth();
		final double yFactor = y / g.getEnvHeight();
		final Point s = getData().getSizeInPixels();
		final int xOnDisplay = (int) (xFactor * s.x);
		final int yOnDisplay = (int) (yFactor * s.y);
		return new Point(xOnDisplay, yOnDisplay);

	}

	/**
	 * Feeds a StringBuilder with coordinates information about the location on screen correctly formatted
	 *
	 * @param xOnScreen
	 *            the x-ordinate on screen
	 * @param yOnScreen
	 *            the y-ordinate on screen
	 * @param g
	 *            the surface on which this layer is displayed
	 * @param sb
	 *            the StringBuilder to feed
	 */

	default void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final StringBuilder sb) {
		final ILocation point = getModelCoordinatesFrom(xOnScreen, yOnScreen, g);
		final String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		sb.append(String.format("X%10s | Y%10s", x, y));
	}

	/**
	 * Collect all the agents intersecting (or close) to the screen point passed in parameter
	 *
	 * @param x
	 *            the x-ordinate on screen
	 * @param y
	 *            the y-ordinate on screen
	 * @param g
	 *            the surface on which this layer is displayed
	 * @return a set of agents (or an empty set if no agents are concerned or displayed)
	 */
	default Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		return Collections.EMPTY_SET;
	}

	/**
	 * Indicates to the layer that it has not been drawn once.
	 */
	void forceRedrawingOnce();

	/**
	 * Whether this layer is an overlay or not
	 *
	 * @return true if it is an overlay, false otherwise
	 */
	default boolean isOverlay() {
		return false;
	}

	/**
	 * Returns a textual description of the layer that can be reinterpreted by GAML
	 */
	@Override
	default String serialize(final boolean includingBuiltIn) {
		return getDefinition().serialize(includingBuiltIn);
	}

	/**
	 * Compares two layers using their definition order
	 */
	@Override
	default int compareTo(final ILayer o) {
		return Ints.compare(getDefinition().getOrder(), o.getDefinition().getOrder());
	}

	/**
	 * Whether the layer is to be displayed in the side controls so that the user can control its properties. Default is
	 * true.
	 *
	 * @return true by default, false if the layer shouldnt be displayed in the layer side controls
	 */

	default Boolean isControllable() {
		return true;
	}

}
