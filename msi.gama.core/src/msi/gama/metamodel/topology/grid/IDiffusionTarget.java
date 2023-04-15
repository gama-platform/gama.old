/*******************************************************************************************************
 *
 * IDiffusionTarget.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import msi.gama.runtime.IScope;

/**
 * The Interface IDiffusionTarget.
 */
public interface IDiffusionTarget {

	/**
	 * Gets the cols.
	 *
	 * @param scope the scope
	 * @return the cols
	 */
	int getCols(IScope scope);

	/**
	 * Gets the rows.
	 *
	 * @param scope the scope
	 * @return the rows
	 */
	int getRows(IScope scope);

	/**
	 * Gets the nb neighbours.
	 *
	 * @return the nb neighbours
	 */
	int getNbNeighbours();

	/**
	 * Gets the value at index.
	 *
	 * @param scope the scope
	 * @param i the i
	 * @param var_diffu the var diffu
	 * @return the value at index
	 */
	double getValueAtIndex(IScope scope, int i, String var_diffu);

	/**
	 * Sets the value at index.
	 *
	 * @param scope the scope
	 * @param i the i
	 * @param var_diffu the var diffu
	 * @param valToPut the val to put
	 */
	void setValueAtIndex(IScope scope, int i, String var_diffu, double valToPut);

	/**
	 * Gets the values into.
	 *
	 * @param scope the scope
	 * @param varName the var name
	 * @param minValue the min value
	 * @param input the input
	 * @return the values into
	 */
	void getValuesInto(IScope scope, String varName, double minValue, double[] input);

}
