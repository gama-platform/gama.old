/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.LayerData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.common.interfaces.IKeyword.FADING;
import static msi.gama.common.interfaces.IKeyword.POSITION;
import static msi.gama.common.interfaces.IKeyword.REFRESH;
import static msi.gama.common.interfaces.IKeyword.SELECTABLE;
import static msi.gama.common.interfaces.IKeyword.SIZE;
import static msi.gama.common.interfaces.IKeyword.TRACE;
import static msi.gama.common.interfaces.IKeyword.TRANSPARENCY;
import static msi.gaml.types.Types.BOOL;
import static msi.gaml.types.Types.FLOAT;
import static msi.gaml.types.Types.INT;
import static msi.gaml.types.Types.POINT;

import java.awt.Point;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.PixelUnitExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.AttributeHolder;

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

	Attribute<ILocation> size;
	Attribute<ILocation> position;
	final Attribute<Boolean> refresh;
	final Attribute<Boolean> fading;
	final Attribute<Integer> trace;
	Attribute<Boolean> selectable;
	Attribute<Double> transparency;

	public LayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		final IExpression sizeExp = def.getFacet(SIZE);
		sizeIsInPixels = sizeExp != null && sizeExp.findAny((p) -> p instanceof PixelUnitExpression);
		size = create(SIZE, sizeExp, POINT, new GamaPoint(1, 1, 1), (e) -> {
			return Cast.asPoint(null, e.getConstValue());
		});
		final IExpression posExp = def.getFacet(POSITION);
		positionIsInPixels = posExp != null && posExp.findAny((p) -> p instanceof PixelUnitExpression);
		position = create(POSITION, posExp, POINT, new GamaPoint(), (e) -> {
			return Cast.asPoint(null, e.getConstValue());
		});
		refresh = create(REFRESH, def.getRefreshFacet(), BOOL, true, (e) -> {
			return Cast.asBool(null, e.getConstValue());
		});
		fading = create(FADING, BOOL, false);
		trace = create(TRACE, (scope, exp) -> exp.getGamlType() == BOOL && Cast.asBool(scope, exp.value(scope))
				? Integer.MAX_VALUE : Cast.asInt(scope, exp.value(scope)), INT, 0, (e) -> {
					return e.getGamlType() == BOOL && Cast.asBool(null, e.getConstValue()) ? Integer.MAX_VALUE
							: Cast.asInt(null, e.getConstValue());
				});
		selectable = create(SELECTABLE, BOOL, true);
		transparency = create(TRANSPARENCY,
				(scope, exp) -> Math.min(Math.max(Cast.asFloat(scope, exp.value(scope)), 0d), 1d), FLOAT, 0d, (e) -> {
					return Math.min(Math.max(Cast.asFloat(null, e.getConstValue()), 0d), 1d);
				});

	}

	@Override
	public void compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		this.refresh(scope);
		computePixelsDimensions(g);
	}

	@Override
	public void setTransparency(final double f) {
		transparency = create(TRANSPARENCY, Math.min(Math.max(f, 0d), 1d));
	}

	@Override
	public void setSize(final ILocation p) {
		setSize(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setSize(final double width, final double height, final double depth) {
		size = create(SIZE, new GamaPoint(width, height, depth));
		sizeIsInPixels = false;
	}

	@Override
	public void setPosition(final ILocation p) {
		setPosition(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setPosition(final double x, final double y, final double z) {
		position = create(POSITION, new GamaPoint(x, y, z));
		positionIsInPixels = false;
	}

	@Override
	public void addElevation(final double elevation) {
		addedElevation = elevation;
	}

	@Override
	public final Double getTransparency(final IScope scope) {
		return Cast.asFloat(scope, transparency.value(scope));
	}

	@Override
	public ILocation getPosition() {
		return position.get();
	}

	@Override
	public ILocation getSize() {
		return size.get();
	}

	@Override
	public Boolean getRefresh() {
		return refresh.get();
	}

	@Override
	public void setSelectable(final Boolean b) {
		selectable = create(SELECTABLE, b);
	}

	/**
	 * Method getTrace()
	 *
	 * @see msi.gama.outputs.layers.ILayerData#getTrace()
	 */
	@Override
	public Integer getTrace() {
		return trace.get();
	}

	/**
	 * Method getFading()
	 *
	 * @see msi.gama.outputs.layers.ILayerData#getFading()
	 */
	@Override
	public Boolean getFading() {
		return fading.get();
	}

	@Override
	public Boolean isSelectable() {
		return selectable.get();
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

		// relative_y = Math.abs(y) <= 1 ? pixelHeight * y : yRatio * y;
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

	@Override
	public double getAddedElevation() {
		return addedElevation;
	}

}
