/*******************************************************************************************************
 *
 * IField.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.matrix;

import javax.annotation.Nonnull;

import msi.gama.metamodel.shape.GamaPoint;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.grid.IDiffusionTarget;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/**
 * A matrix of doubles with additionnal attributes that can serve as a lightweight replacement for grids (holding only
 * one value, but covering the whole environment and accessible by agents using their location).
 *
 * @author drogoul
 *
 */

@vars ({ @variable (
		name = "no_data",
		type = IType.FLOAT,
		doc = @doc ("Represents the value that indicates the absence of data. "
				+ "Setting it will only change the interpretation made by the field "
				+ "of the values it contains, but not the values themselves")),
		@variable (
				name = "cell_size",
				type = IType.POINT,
				doc = @doc ("Represents the dimension of an individual cell as a point (width, height)"
						+ "Setting it will only change the interpretation made by the field "
						+ "of the values it contains, but not the values themselves")),
		@variable (
				name = "bands",
				type = IType.LIST,
				of = IType.FIELD,
				doc = @doc ("The list of bands that are optionnaly present in the field. The first band is the primary field itself, and each of these bands is a field w/o bands ")) })
public interface IField extends IMatrix<Double>, IDiffusionTarget {

	/** The no no data. */
	double NO_NO_DATA = Double.MAX_VALUE;

	/**
	 * Gets the field.
	 *
	 * @param scope the scope
	 * @return the field
	 */
	@Override
	default IField getField(final IScope scope) {
		return this;
	}

	/**
	 * Returns the values present in this field **This array should better not modified directly **
	 *
	 * @return the direct double array.
	 */
	double[] getMatrix();

	/**
	 * Returns the value that represent the "absence" of data.
	 *
	 * @return the value to consider
	 */
	@Override
	@getter ("no_data")
	double getNoData(IScope scope);

	/**
	 * Sets the value that is bound to represent the "absence" of data
	 *
	 * @param noData
	 *            the value to consider
	 */
	@setter ("no_data")
	void setNoData(IScope scope, double noData);

	/**
	 * Returns the min and max values in the field (computed w/o the no data value)
	 *
	 * @param result
	 *            an existing array of doubles or null
	 * @return the array passed in parameter with the new values or a new array allocated
	 */
	double[] getMinMax(double[] result);

	/**
	 * Returns the bands registered for this field.
	 *
	 * @return a list of fields, never null as the first band is this field itself.
	 */
	@getter ("bands")
	IList<? extends IField> getBands(IScope scope);

	/**
	 * Sets the bands.
	 *
	 * @param scope the scope
	 * @param bands the bands
	 */
	@setter ("bands")
	default void setBands(final IScope scope, final IList<IField> bands) {
		// Nothing to do by default as this value is supposed to be read-only
	}

	/**
	 * Gets the cell size.
	 *
	 * @param scope the scope
	 * @return the cell size
	 */
	@getter ("cell_size")
	GamaPoint getCellSize(IScope scope);

	/**
	 * Sets the cell size.
	 *
	 * @param scope the scope
	 * @param size the size
	 */
	@setter ("cell_size")
	default void setCellSize(final IScope scope, final GamaPoint size) {
		// Nothing to do by default as this value is supposed to be read-only
	}

	/**
	 * Returns the 'cell' (a rectangle shape) that represents the cell at this location
	 *
	 * @param loc
	 *            a world location (location of an agent, for instance)
	 * @return A list of values at this location. Never null nor empty (as there is at least one band).
	 */
	IShape getCellShapeAt(IScope scope, GamaPoint loc);

	/**
	 * Gets the cell shape at.
	 *
	 * @param scope the scope
	 * @param columns the columns
	 * @param rows the rows
	 * @return the cell shape at
	 */
	IShape getCellShapeAt(IScope scope, int columns, int rows);

	/**
	 * Returns a list of all the values present in the bands at this world location
	 *
	 * @param loc
	 *            a world location (location of an agent, for instance)
	 * @return A list of values at this location. Never null nor empty (as there is at least one band).
	 */
	@Nonnull
	IList<Double> getValuesIntersecting(IScope scope, IShape shape);

	/**
	 * Returns a list of the 'cells' (rectangle shapes) that intersect the geometry passed in parameter
	 *
	 * @param scope
	 * @param shape
	 * @return
	 */
	IList<IShape> getCellsIntersecting(IScope scope, IShape shape);

	/**
	 * Returns a list of the 'cells' (rectangle shapes) that overlap the geometry passed in parameter
	 *
	 * @param scope
	 * @param shape
	 * @return
	 */
	IList<IShape> getCellsOverlapping(IScope scope, IShape shape);
	
	
	/**
	 * Gets the locations intersecting.
	 *
	 * @param scope the scope
	 * @param shape the shape
	 * @return the locations intersecting
	 */
	IList<GamaPoint> getLocationsIntersecting(final IScope scope, final IShape shape);

	/**
	 * Gets the neighbors of.
	 *
	 * @param scope the scope
	 * @param point the point
	 * @return the neighbors of
	 */
	IList<GamaPoint> getNeighborsOf(IScope scope, GamaPoint point);

}
