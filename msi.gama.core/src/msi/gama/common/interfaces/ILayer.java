/*********************************************************************************************
 *
 * 'ILayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 26 nov. 2009
 *
 * @todo Description
 *
 */
public interface ILayer extends INamed, Comparable<ILayer> {

	ILayerStatement getDefinition();

	void setVisibleRegion(Envelope e);

	Envelope getVisibleRegion();

	Point getPositionInPixels();

	Point getSizeInPixels();

	default String getMenuName() {
		return getType() + ItemList.SEPARATION_CODE + getName();
	}

	void drawDisplay(IScope scope, IGraphics simGraphics) throws GamaRuntimeException;

	void dispose();

	default Integer getOrder() {
		return getDefinition().getOrder();
	}

	public abstract boolean stayProportional();

	default void reloadOn(final IDisplaySurface surface) {
		forceRedrawingOnce();
	}

	public abstract String getType();

	default void firstLaunchOn(final IDisplaySurface surface) {}

	default void enableOn(final IDisplaySurface surface) {}

	default void disableOn(final IDisplaySurface surface) {
		forceRedrawingOnce();
	}

	default void setOrder(final Integer o) {
		getDefinition().setOrder(o);
	}

	default void setTransparency(final Double transparency) {
		getDefinition().setTransparency(transparency);
	}

	default void setPosition(final ILocation p) {
		getDefinition().getBox().setPosition(p);
	}

	default ILocation getPosition() {
		return getDefinition().getBox().getPosition();
	}

	default void setExtent(final ILocation p) {
		getDefinition().getBox().setSize(p);
	}

	default ILocation getExtent() {
		return getDefinition().getBox().getSize();
	}

	default void setElevation(final Double elevation) {
		final ILocation original = getDefinition().getBox().getPosition();
		getDefinition().getBox().setPosition(original.getX(), original.getY(), elevation);
	}

	Rectangle2D focusOn(IShape geometry, IDisplaySurface s);

	default Collection<IAgent> getAgentsForMenu(final IScope scope) {
		return Collections.EMPTY_LIST; // by default
	}

	default boolean isProvidingCoordinates() {
		return true; // by default
	}

	default boolean isProvidingWorldCoordinates() {
		return true; // by default
	}

	default boolean containsScreenPoint(final int x, final int y) {
		final Point p = getPositionInPixels();
		final Point s = getSizeInPixels();
		return x >= p.x && y >= p.y && x <= p.x + s.x && y <= p.y + s.y;
	}

	default ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinatesFrom(xOnScreen, yOnScreen, getSizeInPixels(), getPositionInPixels());
	}

	default Point getScreenCoordinatesFrom(final double x, final double y, final IDisplaySurface g) {
		final double xFactor = x / g.getEnvWidth();
		final double yFactor = y / g.getEnvHeight();
		final Point s = getSizeInPixels();
		final int xOnDisplay = (int) (xFactor * s.x);
		final int yOnDisplay = (int) (yFactor * s.y);
		return new Point(xOnDisplay, yOnDisplay);

	}

	default boolean isDynamic() {
		return getDefinition().getRefresh() == null || getDefinition().getRefresh();
	}

	default void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final StringBuilder sb) {
		// By default, returns the coordinates in the world. Redefined for
		// charts
		final ILocation point = getModelCoordinatesFrom(xOnScreen, yOnScreen, g);
		final String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		sb.append(String.format("X%10s | Y%10s", x, y));
	}

	default Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		// Nothing to do by default
		return Collections.EMPTY_SET;
	}

	void addElevation(double d);

	double getAddedElevation();

	public void forceRedrawingOnce();

	default boolean isOverlay() {
		return false;
	}

	@Override
	default String serialize(final boolean includingBuiltIn) {
		return getDefinition().serialize(includingBuiltIn);
	}

	default void recomputeBounds(final IGraphics g, final IScope scope) {}

	@Override
	default int compareTo(final ILayer o) {
		return getDefinition().compareTo(o.getDefinition());
	}

	default Integer getTrace() {
		return getDefinition().getBox().getTrace();
	}

	default Boolean getFading() {
		return getDefinition().getBox().getFading();
	}

	default Boolean isSelectable() {
		return getDefinition().getBox().isSelectable();
	}

}
