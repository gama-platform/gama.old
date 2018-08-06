/*********************************************************************************************
 *
 * 'LayerBox.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static msi.gama.common.interfaces.IKeyword.FADING;
import static msi.gama.common.interfaces.IKeyword.POSITION;
import static msi.gama.common.interfaces.IKeyword.SELECTABLE;
import static msi.gama.common.interfaces.IKeyword.SIZE;
import static msi.gama.common.interfaces.IKeyword.TRACE;
import static msi.gama.common.interfaces.IKeyword.TRANSPARENCY;
import static msi.gaml.operators.Cast.asBool;
import static msi.gaml.operators.Cast.asFloat;
import static msi.gaml.operators.Cast.asInt;
import static msi.gaml.types.Types.BOOL;
import static msi.gaml.types.Types.FLOAT;
import static msi.gaml.types.Types.INT;
import static msi.gaml.types.Types.POINT;

import java.awt.Point;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.PixelUnitExpression;
import msi.gaml.statements.draw.AttributeHolder;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 16 nov. 2010
 *
 * @todo Description
 *
 */
public class LayerData extends AttributeHolder implements ILayerData {

	protected final Point positionInPixels = new Point();
	protected final Point sizeInPixels = new Point();
	protected double addedElevation;
	boolean positionIsInPixels, sizeIsInPixels;
	Envelope visibleRegion;

	Attribute<GamaPointType, ILocation> size;
	Attribute<GamaPointType, ILocation> position;
	Attribute<GamaBoolType, Boolean> refresh;
	final Attribute<GamaBoolType, Boolean> fading;
	final Attribute<GamaIntegerType, Integer> trace;
	Attribute<GamaBoolType, Boolean> selectable;
	Attribute<GamaFloatType, Double> transparency;

	public LayerData(final ILayerStatement def) throws GamaRuntimeException {
		final IExpression sizeExp = def.getFacet(SIZE);
		sizeIsInPixels = sizeExp != null && sizeExp.findAny((p) -> p instanceof PixelUnitExpression);
		size = create(sizeExp, POINT, new GamaPoint(1, 1, 1));
		final IExpression posExp = def.getFacet(POSITION);
		positionIsInPixels = posExp != null && posExp.findAny((p) -> p instanceof PixelUnitExpression);
		position = create(posExp, POINT, new GamaPoint());
		refresh = create(def.getRefreshFacet(), BOOL, true);
		fading = create(def.getFacet(FADING), BOOL, false);
		final IExpression traceExp = def.getFacet(TRACE);
		trace = create(traceExp, (scope) -> traceExp.getGamlType() == BOOL && asBool(scope, traceExp.value(scope))
				? MAX_VALUE : asInt(scope, traceExp.value(scope)), INT, 0);
		selectable = create(def.getFacet(SELECTABLE), BOOL, true);
		transparency = create(def.getFacet(TRANSPARENCY),
				(scope) -> 1d - min(max(asFloat(scope, def.getFacet(TRANSPARENCY).value(scope)), 0d), 1d), FLOAT, 1d);

	}

	@Override
	public void compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		size.refresh(scope);
		position.refresh(scope);
		refresh.refresh(scope);
		fading.refresh(scope);
		trace.refresh(scope);
		selectable.refresh(scope);
		transparency.refresh(scope);
		computePixelsDimensions(g);
	}

	@Override
	public void setTransparency(final double f) {
		transparency = create(null, Types.FLOAT, 1d - Math.min(Math.max(f, 0d), 1d));
	}

	@Override
	public void setSize(final ILocation p) {
		setSize(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setSize(final double width, final double height, final double depth) {
		size = create(null, Types.POINT, new GamaPoint(width, height, depth));
		sizeIsInPixels = false;
	}

	@Override
	public void setPosition(final ILocation p) {
		setPosition(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setPosition(final double x, final double y, final double z) {
		position = create(null, Types.POINT, new GamaPoint(x, y, z));
		positionIsInPixels = false;
	}

	@Override
	public void setRefresh(final Boolean r) {
		refresh = create(null, Types.BOOL, r);

	}

	@Override
	public void addElevation(final double elevation) {
		addedElevation = elevation;
	}

	@Override
	public final Double getTransparency() {
		return transparency.value;
	}

	@Override
	public ILocation getPosition() {
		if (addedElevation > 0) { return position.value.toGamaPoint().plus(0, 0, addedElevation); }
		return position.value;
	}

	@Override
	public ILocation getSize() {
		return size.value;
	}

	@Override
	public Boolean getRefresh() {
		return refresh.value;
	}

	@Override
	public void setSelectable(final Boolean b) {
		selectable = create(null, Types.BOOL, b);
	}

	/**
	 * Method getTrace()
	 * 
	 * @see msi.gama.outputs.layers.ILayerData#getTrace()
	 */
	@Override
	public Integer getTrace() {
		return trace.value;
	}

	/**
	 * Method getFading()
	 * 
	 * @see msi.gama.outputs.layers.ILayerData#getFading()
	 */
	@Override
	public Boolean getFading() {
		return fading.value;
	}

	@Override
	public Boolean isSelectable() {
		return selectable.value;
	}

	@Override
	public boolean isRelativePosition() {
		return !positionIsInPixels;
	}

	@Override
	public boolean isRelativeSize() {
		return !sizeIsInPixels;
	}

	@Override
	public Point getSizeInPixels() {
		return sizeInPixels;
	}

	@Override
	public Point getPositionInPixels() {
		return positionInPixels;
	}

	/**
	 * @param boundingBox
	 * @param g
	 */
	@Override
	public void computePixelsDimensions(final IGraphics g) {
		// Voir comment conserver cette information
		final int pixelWidth = g.getDisplayWidth();
		final int pixelHeight = g.getDisplayHeight();
		final double xRatio = g.getxRatioBetweenPixelsAndModelUnits();
		final double yRatio = g.getyRatioBetweenPixelsAndModelUnits();

		ILocation point = getPosition();
		// Computation of x
		final double x = point.getX();

		double relative_x;
		if (!isRelativePosition()) {
			relative_x = xRatio * x;
		} else {
			relative_x = Math.abs(x) <= 1 ? pixelWidth * x : xRatio * x;
		}
		final double absolute_x = Math.signum(x) < 0 ? pixelWidth + relative_x : relative_x;
		// Computation of y

		final double y = point.getY();
		double relative_y;
		if (!isRelativePosition()) {
			relative_y = yRatio * y;
		} else {
			relative_y = Math.abs(y) <= 1 ? pixelHeight * y : yRatio * y;
		}

		relative_y = Math.abs(y) <= 1 ? pixelHeight * y : yRatio * y;
		final double absolute_y = Math.signum(y) < 0 ? pixelHeight + relative_y : relative_y;

		point = getSize();
		// Computation of width
		final double w = point.getX();
		double absolute_width;
		if (!isRelativeSize()) {
			absolute_width = xRatio * w;
		} else {
			absolute_width = Math.abs(w) <= 1 ? pixelWidth * w : xRatio * w;
		}
		// Computation of height
		final double h = point.getY();
		double absolute_height;
		if (!isRelativeSize()) {
			absolute_height = yRatio * h;
		} else {
			absolute_height = Math.abs(h) <= 1 ? pixelHeight * h : yRatio * h;
		}

		getSizeInPixels().setLocation(absolute_width, absolute_height);
		getPositionInPixels().setLocation(absolute_x, absolute_y);
	}

	@Override
	public void setVisibleRegion(final Envelope e) {
		visibleRegion = e;
	}

	@Override
	public Envelope getVisibleRegion() {
		return visibleRegion;
	}

}
