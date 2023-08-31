/*******************************************************************************************************
 *
 * GamlGridAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

/**
 * The Class GamlGridAgent.
 */
public class GamlGridAgent extends GamlAgent implements IGridAgent {

	/**
	 * Instantiates a new gaml grid agent.
	 *
	 * @param index
	 *            the index
	 * @param gridPopulation
	 *            TODO
	 */
	public GamlGridAgent(final GridPopulation population, final int index) {
		super(population, index, population.grid.matrix[index].getGeometry());
	}

	@Override
	public GridPopulation getPopulation() { return (GridPopulation) super.getPopulation(); }

	@Override
	public GamaColor getColor() {
		if (getPopulation().grid.isHexagon) return (GamaColor) getAttribute(IKeyword.COLOR);
		return GamaColor.get(getPopulation().grid.supportImagePixels[getIndex()]);
	}

	@Override
	public void setColor(final GamaColor color) {
		if (getPopulation().grid.isHexagon) {
			setAttribute(IKeyword.COLOR, color);
		} else {
			getPopulation().grid.supportImagePixels[getIndex()] = color.getRGB();
		}
	}

	@Override
	public final int getX() {
		if (getPopulation().grid.isHexagon()) return getPopulation().grid.getX(getGeometry());
		return (int) (getLocation().getX() / getPopulation().grid.cellWidth);
	}

	@Override
	public final int getY() {
		if (getPopulation().grid.isHexagon()) return getPopulation().grid.getY(getGeometry());
		return (int) (getLocation().getY() / getPopulation().grid.cellHeight);
	}

	@Override
	public double getValue() {
		if (getPopulation().grid.gridValue != null) return getPopulation().grid.gridValue[getIndex()];
		return 0d;
	}

	@Override
	public IList<Double> getBands() {
		if (getPopulation().grid.nbBands == 1) {
			final IList<Double> bd = GamaListFactory.create(null, Types.FLOAT);
			bd.add(getValue());
			return bd;
		}
		return getPopulation().grid.bands.get(getIndex());
	}

	@Override
	public void setValue(final double d) {
		if (getPopulation().grid.gridValue != null) { getPopulation().grid.gridValue[getIndex()] = d; }
	}

	@Override
	public IList<IAgent> getNeighbors(final IScope scope) {
		return Cast.asList(scope, getPopulation().grid.getNeighborhood().getNeighborsIn(scope, getIndex(), 1));
	}

}