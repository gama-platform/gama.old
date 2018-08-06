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

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.ILayerData;
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

	public ILayerData getData();

	default String getMenuName() {
		return getType() + ItemList.SEPARATION_CODE + getName();
	}

	void draw(IScope scope, IGraphics simGraphics) throws GamaRuntimeException;

	default void dispose() {

	}

	default Integer getOrder() {
		return getDefinition().getOrder();
	}

	default boolean stayProportional() {
		return true;
	}

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
		getData().setTransparency(transparency);
	}

	default void setPosition(final ILocation p) {
		getData().setPosition(p);
	}

	default ILocation getPosition() {
		return getData().getPosition();
	}

	default void setExtent(final ILocation p) {
		getData().setSize(p);
	}

	default ILocation getExtent() {
		return getData().getSize();
	}

	default void setElevation(final Double elevation) {
		final ILocation original = getData().getPosition();
		getData().setPosition(original.getX(), original.getY(), elevation);
	}

	default Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final Envelope3D envelope = geometry.getEnvelope();
		final Point min = this.getScreenCoordinatesFrom(envelope.getMinX(), envelope.getMinY(), s);
		final Point max = this.getScreenCoordinatesFrom(envelope.getMaxX(), envelope.getMaxY(), s);
		final Rectangle2D r = new Rectangle2D.Double(min.x, min.y, max.x - min.x, max.y - min.y);
		return r;
	}

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
		final Point p = getData().getPositionInPixels();
		final Point s = getData().getSizeInPixels();
		return x >= p.x && y >= p.y && x <= p.x + s.x && y <= p.y + s.y;
	}

	default ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinatesFrom(xOnScreen, yOnScreen, getData().getSizeInPixels(),
				getData().getPositionInPixels());
	}

	default Point getScreenCoordinatesFrom(final double x, final double y, final IDisplaySurface g) {
		final double xFactor = x / g.getEnvWidth();
		final double yFactor = y / g.getEnvHeight();
		final Point s = getData().getSizeInPixels();
		final int xOnDisplay = (int) (xFactor * s.x);
		final int yOnDisplay = (int) (yFactor * s.y);
		return new Point(xOnDisplay, yOnDisplay);

	}

	default boolean isDynamic() {
		return getData().getRefresh() == null || getData().getRefresh();
	}

	default void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final StringBuilder sb) {
		final ILocation point = getModelCoordinatesFrom(xOnScreen, yOnScreen, g);
		final String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		sb.append(String.format("X%10s | Y%10s", x, y));
	}

	default Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		return Collections.EMPTY_SET;
	}

	public void forceRedrawingOnce();

	default boolean isOverlay() {
		return false;
	}

	@Override
	default String serialize(final boolean includingBuiltIn) {
		return getDefinition().serialize(includingBuiltIn);
	}

	@Override
	default int compareTo(final ILayer o) {
		return getDefinition().compareTo(o.getDefinition());
	}

	default Integer getTrace() {
		return getData().getTrace();
	}

	default Boolean getFading() {
		return getData().getFading();
	}

	default Boolean isSelectable() {
		return getData().isSelectable();
	}

}
