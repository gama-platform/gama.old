/*******************************************************************************************************
 *
 * MinimalGridAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

/**
 * The Class MinimalGridAgent.
 */
public class MinimalGridAgent extends AbstractAgent implements IGridAgent {

	/** The geometry. */
	private final IShape geometry;

	/** The population. */
	private final GridPopulation population;

	/**
	 * Instantiates a new minimal grid agent.
	 *
	 * @param index
	 *            the index
	 * @param gridPopulation
	 *            TODO
	 */
	public MinimalGridAgent(final GridPopulation gridPopulation, final int index) {
		super(index);
		population = gridPopulation;
		geometry = population.grid.matrix[index].getGeometry();
	}

	@Override
	public GamaColor getColor() {
		if (population.grid.isHexagon) return (GamaColor) getAttribute(IKeyword.COLOR);
		return GamaColor.get(population.grid.supportImagePixels[getIndex()]);
	}

	@Override
	public void setColor(final GamaColor color) {
		if (population.grid.isHexagon) {
			setAttribute(IKeyword.COLOR, color);
		} else {
			population.grid.supportImagePixels[getIndex()] = color.getRGB();
		}
	}

	@Override
	public void setGeometricalType(final Type t) {}

	@Override
	public final int getX() {
		if (population.grid.isHexagon()) return population.grid.getX(getGeometry());
		return (int) (getLocation().getX() / population.grid.cellWidth);
	}

	@Override
	public final int getY() {
		if (population.grid.isHexagon()) return population.grid.getY(getGeometry());
		return (int) (getLocation().getY() / population.grid.cellHeight);
	}

	@Override
	public double getValue() {
		if (population.grid.gridValue != null) return population.grid.gridValue[getIndex()];
		return 0d;
	}

	@Override
	public void setValue(final double d) {
		if (population.grid.gridValue != null) { population.grid.gridValue[getIndex()] = d; }
	}

	@Override
	public IPopulation<?> getPopulation() { return population; }

	@Override
	public IShape getGeometry(final IScope scope) {
		return geometry;
	}

	@Override
	public IList<IAgent> getNeighbors(final IScope scope) {
		return Cast.asList(scope, population.grid.getNeighborhood().getNeighborsIn(scope, getIndex(), 1));
	}

	/**
	 * Method getPoints()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<GamaPoint> getPoints() { return geometry.getPoints(); }

	@Override
	public void setDepth(final double depth) {

	}

	/**
	 * Method getArea()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getArea()
	 */
	@Override
	public Double getArea() { return geometry.getArea(); }

	/**
	 * Method getVolume()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getVolume()
	 */
	@Override
	public Double getVolume() { return geometry.getVolume(); }

	/**
	 * Method getPerimeter()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() { return geometry.getPerimeter(); }

	/**
	 * Method getHoles()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHoles()
	 */
	@Override
	public IList<GamaShape> getHoles() { return geometry.getHoles(); }

	/**
	 * Method getCentroid()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getCentroid()
	 */
	@Override
	public GamaPoint getCentroid() { return geometry.getCentroid(); }

	/**
	 * Method getExteriorRing()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getExteriorRing(msi.gama.runtime.IScope)
	 */
	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return geometry.getExteriorRing(scope);
	}

	/**
	 * Method getWidth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getWidth()
	 */
	@Override
	public Double getWidth() { return geometry.getWidth(); }

	/**
	 * Method getHeight()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHeight()
	 */
	@Override
	public Double getHeight() { return geometry.getHeight(); }

	/**
	 * Method getDepth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getDepth()
	 */
	@Override
	public Double getDepth() { return geometry.getDepth(); }

	/**
	 * Method getGeometricEnvelope()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricEnvelope()
	 */
	@Override
	public GamaShape getGeometricEnvelope() { return geometry.getGeometricEnvelope(); }

	@Override
	public IList<? extends IShape> getGeometries() { return geometry.getGeometries(); }

	/**
	 * Method isMultiple()
	 *
	 * @see msi.gama.metamodel.shape.IShape#isMultiple()
	 */
	@Override
	public boolean isMultiple() { return geometry.isMultiple(); }

	@Override
	public IList<Double> getBands() {
		if (population.grid.nbBands == 1) {
			final IList<Double> bd = GamaListFactory.create(null, Types.FLOAT);
			bd.add(getValue());
			return bd;
		}
		return population.grid.bands.get(getIndex());
	}

}