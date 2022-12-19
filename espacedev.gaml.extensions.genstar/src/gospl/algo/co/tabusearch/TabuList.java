/*******************************************************************************************************
 *
 * TabuList.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.tabusearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gospl.GosplPopulation;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;

/**
 * The Class TabuList.
 */
public class TabuList implements ITabuList {

	/** The tabu list. */
	List<ISyntheticPopulationSolution<GosplPopulation>> list = new ArrayList<>();

	/** The max size. */
	private int maxSize;

	/**
	 * Instantiates a new tabu list.
	 *
	 * @param size
	 *            the size
	 */
	public TabuList(final int size) {
		if (size < 1) {
			this.maxSize = 1;
		} else {
			this.maxSize = size;
		}
	}

	@Override
	public Iterator<ISyntheticPopulationSolution<GosplPopulation>> iterator() {
		return list.iterator();
	}

	@Override
	public void add(final ISyntheticPopulationSolution<GosplPopulation> solution) {
		if (list.size() == maxSize) { list.remove(list.get(0)); }
		list.add(solution);
	}

	@Override
	public boolean contains(final ISyntheticPopulationSolution<GosplPopulation> solution) {
		return list.contains(solution);
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public int getSize() { return list.size(); }

}
