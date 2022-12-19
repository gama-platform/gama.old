/*******************************************************************************************************
 *
 * INDimensionalMatrix.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.matrix;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import core.metamodel.attribute.IAttribute;
import core.metamodel.io.GSSurveyType;
import core.util.data.GSDataParser;
import gospl.distribution.exception.IllegalNDimensionalMatrixAccess;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * Main interface that forces n dimensional matrix to specify:
 * <p>
 * <ul>
 * <li>{@code <D>} the type of dimension to be used
 * <li>{@code <A>} the type of aspect dimensions contain
 * <li>{@code <T>} the type of value the matrix contains
 * </ul>
 * <p>
 * There is also several methods to access and set the matrix. The collection format is based on spares collection
 * abstraction: there is no memory allocated to null value and there is not any coordinate associated in the matrix
 *
 * @author kevinchapuis
 *
 * @param <D>
 *            Type of random variables
 * @param <A>
 *            Type of variables' values
 * @param <T>
 *            Type of values the matrix is made of
 */
public interface INDimensionalMatrix<D, A, T extends Number> {

	// ---------------------------- GETTERS ---------------------------- //

	/**
	 * Retrieve the matrix value according to the coordinate passed in method parameter.
	 * <p>
	 * <b>WARNING: return the actual control associated to this matrix. This method enables a direct access to the
	 * content of the matrix with no computation at all.</b>
	 *
	 * @param coordinate
	 * @return {@link AControl} associated to the given {@link ACoordinate}
	 */
	AControl<T> getVal(ACoordinate<D, A> coordinate);

	/**
	 * Compute the matrix aggregated value according to one dimension's aspect <br>
	 * The method call is equivalent to {@code getVal(aspect, false)}
	 *
	 * @see #getVal(Object, boolean)
	 *
	 * @param aspect
	 * @return
	 * @throws IllegalNDimensionalMatrixAccess
	 */
	AControl<T> getVal(A aspect) throws IllegalNDimensionalMatrixAccess;

	/**
	 * Compute the matrix aggregated value according to one dimension's aspect. if {@code defaultToNul} is true, then a
	 * missing value will return a null {@link AControl} value
	 *
	 * @see #getVal(aspect)
	 *
	 * @param aspect
	 * @param defaultToNul
	 * @return
	 */
	AControl<T> getVal(A aspect, boolean defaultToNul);

	/**
	 * Compute the matrix aggregated value according to a set of aspect of one or several dimension <br>
	 * The method call is equivalent to {@code getVal(aspect, false)}
	 *
	 * @see #getVal(Collection, boolean)
	 *
	 * @param aspects
	 * @return
	 */
	AControl<T> getVal(Collection<A> aspects);

	/**
	 * Compute the matrix aggregated value according to a set of aspect of one or several dimension. if defaultToNul is
	 * true, then a missing value will return a null {@link AControl} value.
	 * <p>
	 *
	 * @param aspects
	 * @param defaultToNul
	 * @return
	 */
	AControl<T> getVal(Collection<A> aspects, boolean defaultToNul);

	/**
	 * Compute the matrix aggregated value according to a set of aspect of one or several dimension. Pass parameters as:
	 * "gender", "female", "age", "60 and more", ...
	 *
	 * @param coordinates
	 * @return
	 */
	AControl<T> getVal(String... coordinates);

	/**
	 * Compute the matrix aggregated value according to a set of aspect of one or several dimension
	 *
	 * @param aspects
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	AControl<T> getVal(A... aspects);

	/**
	 * Compute the total sum of the entire matrix
	 *
	 * @return
	 */
	AControl<T> getVal();

	// ---------------------------------- SETTERS ---------------------------------- //

	/**
	 * Add a new value associated with a new coordinate. The add can fails if the specified coordinate in parameter has
	 * already be binding with another value
	 *
	 * @param coordinates
	 * @param value
	 * @return <code>true</code> if the value has been added, <code>false</code> otherwise
	 */
	boolean addValue(ACoordinate<D, A> coordinates, AControl<? extends Number> value);

	/**
	 * Add a new value associated with a new coordinate. The add can fails if the specified coordinate in parameter has
	 * already be binding with another value
	 *
	 * @param coordinates
	 * @param value
	 * @return
	 */
	boolean addValue(ACoordinate<D, A> coordinates, T value);

	/**
	 * Add a new value associated with a new coordinate. The add can fails if the specified coordinate in parameter has
	 * already be binding with another value. This convenience function is called like:
	 * addValue(0.1,"gender","male","age","12-25"...)
	 *
	 * @param coordinates
	 * @param value
	 * @return
	 */
	boolean addValue(T value, String... coordinates);

	/**
	 * Add or replace the value associate with the coordinate in parameter for the new value passed as method's argument
	 *
	 * @param coordinate
	 * @param value
	 * @return <code>true</code> if the value has been added, <code>false</code> otherwise
	 */
	boolean setValue(ACoordinate<D, A> coordinate, AControl<? extends Number> value);

	/**
	 * Add or replace the value associate with the coordinate in parameter for the new value passed as method's argument
	 *
	 * @param coordinate
	 * @param value
	 * @return <code>true</code> if the value has been added, <code>false</code> otherwise
	 */
	boolean setValue(ACoordinate<D, A> coordinate, T value);

	/**
	 * Add or replace the value associate with the coordinate in parameter for the new value passed as method's argument
	 * This convenience function is called like: addValue(0.1,"gender","male","age","12-25"...)
	 *
	 * @param coordinate
	 * @param value
	 * @return <code>true</code> if the value has been added, <code>false</code> otherwise
	 */
	boolean setValue(T value, String... coordinates);

	// ------------------------- Accessors ------------------------- //

	/**
	 * Return a view of the inner matrix: each coordinate is mapped with a numerical value
	 *
	 */
	Map<ACoordinate<D, A>, AControl<T>> getMatrix();

	/**
	 * Return an ordered view of the inner matrix: each coordinate is mapped with a numerical value sorted by increasing
	 * number
	 *
	 * @return
	 */
	LinkedHashMap<ACoordinate<D, A>, AControl<T>> getOrderedMatrix();

	/**
	 * Return the empty coordinate of this matrix
	 *
	 * @return
	 */
	ACoordinate<D, A> getEmptyCoordinate();

	/**
	 * The dimensions of the matrix
	 *
	 * @return
	 */
	Set<D> getDimensions();

	/**
	 * Searches for the dimension having this name.
	 *
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	D getDimension(String name) throws IllegalArgumentException;

	/**
	 * A complete view of the dimensions of the matrix
	 *
	 * @return
	 */
	Map<D, Set<? extends A>> getDimensionsAsAttributesAndValues();

	/**
	 * The dimensions associated with a spectific aspect
	 *
	 * @param aspect
	 * @return
	 * @throws IllegalNDimensionalMatrixAccess
	 */
	D getDimension(A aspect);

	/**
	 * Return all the values dimension contains
	 *
	 * @return
	 */
	Set<A> getAspects();

	/**
	 * Return the values the spectified dimension [{@link IAttribute}] is made of
	 *
	 * @param dimension
	 * @return
	 */
	Set<A> getAspects(D dimension);

	/**
	 * Test and apply referent mapping values to account for diverging encoding between referent attribute.
	 * <p>
	 * For exemple, an attribute age can start at 15 years old when joint distribution with occupation is stated, but
	 * will start at 0 when joint distribution with gender is stated. Hence people under 15 years old can not be linked
	 * to occupation. To avoid such fallacious link between unknown values, this method will return a coordinate with
	 * empty occupation
	 *
	 * @param aspects
	 * @return
	 */
	Set<A> getEmptyReferentCorrelate(ACoordinate<D, A> aspects);

	// ------------------------- descriptors ------------------------- //

	/**
	 * The concrete size of the matrix, i.e. the number of coordinate / value pairs
	 *
	 * @return
	 */
	int size();

	/**
	 * Inform wether the matrix represents a "full distribution matrix" (i.e. each dimension related to all other
	 * dimensions) or a "segmented distribution matrix" (i.e. at least two dimension are unrelated)
	 *
	 * @return
	 */
	boolean isSegmented();

	/**
	 * Gives the {@link GSSurveyType} that characterize "frame of referent" for this matrix. This in turn inform about
	 * the specific target of the {@link AControl} associated to coordinate.
	 *
	 * {@see GosplMetatDataType}
	 *
	 * @return
	 */
	GSSurveyType getMetaDataType();

	/**
	 * To be use to compute the chi square degree of freedom for this n dimensional matrix
	 *
	 * @return
	 */
	int getDegree();

	// ------------------------- coordinate management ------------------------- //

	/**
	 * Check if this coordinate fits the matrix requirement
	 *
	 * @param coordinate
	 * @return
	 */
	boolean isCoordinateCompliant(ACoordinate<D, A> coordinate);

	/**
	 * Retrieve all coordinate that describe this set of value. Simply translated, this will return all coordinates
	 * which contains the {@code values} (one per dimension) passed as argument
	 *
	 * @param values
	 * @return
	 */
	Collection<ACoordinate<D, A>> getCoordinates(Set<A> values);

	/**
	 * Does the same as {@link #getCoordinates(Set)} but rather than return a null coordinate if there is none in the
	 * matrix, will create it
	 *
	 * @param values
	 * @return
	 * @throws NullPointerException
	 */
	Collection<ACoordinate<D, A>> getOrCreateCoordinates(Set<A> values);

	/**
	 * Retrieve all coordinate that describe this set of value. Simply translated, this will return all coordinates
	 * which contains the {@code values} (one per dimension) passed as argument
	 *
	 * pass the parameters as: "gender", "male", "age", "12-25"...
	 *
	 * @param keyAndVal
	 * @return
	 */
	Collection<ACoordinate<D, A>> getCoordinates(String... keyAndVal) throws IllegalArgumentException;

	/**
	 * Retrieve all coordinate that describe this set of value. Simply translated, this will return all coordinates
	 * which contains the {@code values} (one per dimension) passed as argument
	 *
	 * pass the parameters as: "gender", "male", "age", "12-25"...
	 *
	 * @param keyAndVal
	 * @return
	 */
	Set<A> getValues(String... keyAndVal) throws IllegalArgumentException;

	/**
	 * Retrieve the only coordinate that describe this set of value.
	 * <p>
	 *
	 * @throws NullPointerException
	 *             if the set of values in argument does not correspond to any coordinate in the matrix
	 * @param values
	 * @return
	 */
	ACoordinate<D, A> getCoordinate(Set<A> values) throws NullPointerException;

	/**
	 * Retrieve the only coordinate that describe this set of value.
	 *
	 * pass the parameters as: "gender", "male", "age", "12-25"...
	 *
	 * @param keyAndVal
	 * @return
	 */
	ACoordinate<D, A> getCoordinate(String... keyAndVal) throws IllegalArgumentException;

	// ------------------------- Inner value utilities ------------------------- //

	/**
	 * Get relative {@code T} null value
	 *
	 * @return
	 */
	AControl<T> getNulVal();

	/**
	 * Get the value that guarantee that any {@code T} value multiply by {@link #getIdentityProductVal()} stay the same
	 *
	 * @return
	 */
	AControl<T> getIdentityProductVal();

	/**
	 * Get the smallest unit value relative to {@code T} parametric type
	 *
	 * @return
	 */
	AControl<T> getAtomicVal();

	/**
	 * Parses a value from a string and encapsulates it in a {@link AControl}
	 *
	 * @param parser
	 * @param val
	 * @return
	 */
	AControl<T> parseVal(GSDataParser parser, String val);

	// ------------------------- utility methods ------------------------- //

	/**
	 * Checks if all of the coordinates of the matrix have defined values. Note that not all matrices should have this
	 * contract. Sparse matricies are often relevant.
	 *
	 * @param checkGlobalSum
	 * @param checkAllCoordinatesHaveValue
	 * @return true if the
	 */
	boolean checkAllCoordinatesHaveValues();

	/**
	 * Ensures the global contract of the matrix is ok depending to its type: a global frequency has to sump up to 1,
	 * for instance. If the type enables no check, true is always returned.
	 *
	 * @return
	 */
	boolean checkGlobalSum();

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	String toString();

	/**
	 * To csv.
	 *
	 * @param csvSeparator the csv separator
	 * @return the string
	 */
	String toCsv(char csvSeparator);

	/**
	 * if it is relevant, normalizes the values
	 */
	void normalize() throws IllegalArgumentException;

	/**
	 * Returns a human readable label, or null if undefined.
	 *
	 * @return
	 */
	String getLabel();

	/**
	 * Returns the genesis of the matrix, that is the successive steps that brought it to its current state. Useful to
	 * expose meaningful error messages to the user.
	 *
	 * @return
	 */
	String getGenesisAsString();

	/**
	 * imports into this matrix the genesis of another one. Should be called after creating a matrix to keep a memory of
	 * where it comes from.
	 *
	 * @param o
	 */
	void inheritGenesis(AFullNDimensionalMatrix<?> o);

	/**
	 * add one line to the genesis (history) of this matrix. This line should better be kept quiet short for
	 * readibility.
	 *
	 * @param step
	 */
	void addGenesis(String step);

}
