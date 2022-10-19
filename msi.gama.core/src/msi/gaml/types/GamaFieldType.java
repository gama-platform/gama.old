/*******************************************************************************************************
 *
 * GamaFieldType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Arrays;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.IFieldMatrixProvider;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.IField;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

/**
 * The Class GamaFieldType.
 */
@type (
		name = IKeyword.FIELD,
		id = IType.FIELD,
		wraps = { IField.class, GamaField.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.GRID, IConcept.MATRIX },
		doc = @doc ("Fields are two-dimensional matrices holding float values. They can be easily created from arbitrary sources (grid, raster or DEM files, matrices, "
				+ "grids) and of course by hand. The values they hold are accessible by agents like grids are, using their current location. They can be the target of the "
				+ "'diffuse' statement and can be displayed using the 'mesh' layer definition. "
				+ "As such, they represent a lightweight alternative to grids, as they hold spatialized discrete values without having to build agents, which can be particularly "
				+ "interesting for models with large raster data. Several fields can of course be defined, and it makes sense to define them in the global section as, for the moment, "
				+ "they cover by default the whole environment, exactly like grids, and are created alongside them"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaFieldType extends GamaMatrixType {

	/**
	 * Builds the field.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the i field
	 */
	public static IField buildField(final IScope scope, final Object object) {
		return staticCast(scope, object, null, null, false);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param contentType
	 *            the content type
	 * @param copy
	 *            the copy
	 * @return the i field
	 */
	public static IField staticCast(final IScope scope, final Object obj, final Object param, final IType contentType,
			final boolean copy) {
		if (obj == null && param == null) return null;
		final GamaPoint size = param instanceof GamaPoint ? (GamaPoint) param : null;

		if (size == null) {
			if (obj instanceof IField && !copy) return (IField) obj;
			if (obj instanceof IFieldMatrixProvider) return ((IFieldMatrixProvider) obj).getField(scope);
			if (obj instanceof IContainer) return staticCast(scope,
					((IContainer) obj).matrixValue(scope, contentType, copy), null, contentType, copy);
			// Special case for grid species
			if (obj instanceof ISpecies species && species.isGrid()) return staticCast(scope,
					species.getPopulation(scope).getTopology().getPlaces(), param, contentType, copy);

		} else if (size.x <= 0 || size.y < 0)
			throw GamaRuntimeException.error("Dimensions of a field should be positive.", scope);
		if (obj instanceof IContainer) return staticCast(scope,
				((IContainer) obj).matrixValue(scope, contentType, size, copy), null, contentType, copy);
		return with(scope, obj, size, contentType);

	}

	/**
	 * With.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param p
	 *            the p
	 * @param contentsType
	 *            the contents type
	 * @return the i field
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IField with(final IScope scope, final Object val, final GamaPoint p, final IType contentsType)
			throws GamaRuntimeException {
		int x = p == null ? 1 : (int) p.x;
		int y = p == null ? 1 : (int) p.y;
		return withObject(scope, val, x, y, contentsType);
	}

	/**
	 * With object.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param contentsType
	 *            the contents type
	 * @return the i field
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IField withObject(final IScope scope, final Object val, final int cols, final int rows,
			final IType contentsType) throws GamaRuntimeException {
		Double toStore = Cast.asFloat(scope, val);
		final GamaFloatMatrix matrix = new GamaFloatMatrix(cols, rows);
		matrix.setAllValues(scope, toStore);
		return buildField(scope, matrix);
	}

	@Override
	public IField cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, param, contentsType, copy);
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		return Types.FLOAT;
	}

	@Override
	public IType<?> getContentType() { return Types.FLOAT; }

	@Override
	public boolean isDrawable() { return true; }

	/**
	 * Constructors to be used in GAML besides the default "casting" one (i.e. field(xxx))
	 */

	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns, number of rows, the initial value of its cells and the value representing the absence of value") })
	@no_test
	public static IField buildField(final IScope scope, final int cols, final int rows, final double init,
			final double no) {
		double[] data = new double[cols * rows];
		Arrays.fill(data, init);
		return new GamaField(scope, cols, rows, data, no);
	}

	/**
	 * Builds the field.
	 *
	 * @param scope
	 *            the scope
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param init
	 *            the init
	 * @return the i field
	 */
	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns, "
					+ "number of rows and the initial value of its cells. The value representing the absence of value is set to #max_float") })
	@no_test
	public static IField buildField(final IScope scope, final int cols, final int rows, final double init) {
		return buildField(scope, cols, rows, init, IField.NO_NO_DATA);
	}

	/**
	 * Builds the field.
	 *
	 * @param scope
	 *            the scope
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @return the i field
	 */
	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns and number of rows. "
					+ "The initial value of its cells is set to 0.0 and the value representing the absence of value is set to #max_float") })
	@no_test
	public static IField buildField(final IScope scope, final int cols, final int rows) {
		return buildField(scope, cols, rows, 0d);
	}

	/**
	 * Builds the field with no data.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @param noData
	 *            the no data
	 * @return the i field
	 */
	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying an arbitrary object (assuming this object can return a matrix of float) "
					+ "and a value representing the absence of data. ") })
	@no_test
	public static IField buildFieldWithNoData(final IScope scope, final Object object, final double noData) {
		IField field = buildField(scope, object);
		field.setNoData(scope, noData);
		return field;
	}

	/**
	 * Builds the shape from field location.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param location
	 *            the location
	 * @return the i shape
	 */
	@operator (
			value = "cell_at",
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the rectangular shape that corresponds to the 'cell' in the field at this location. This cell has no attributes. A future version may load it with the value of the field at this attribute") })
	@no_test
	public static IShape buildShapeFromFieldLocation(final IScope scope, final IField field, final GamaPoint location) {
		return field.getCellShapeAt(scope, location);
	}

	/**
	 * Builds the shape from field location.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param columns
	 *            the columns
	 * @param rows
	 *            the rows
	 * @return the i shape
	 */
	@operator (
			value = "cell_at",
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the rectangular shape that corresponds to the 'cell' in the field at this location in the matrix (column, row). This cell has no attributes. A future version may load it with the value of the field at this attribute") })
	@no_test
	public static IShape buildShapeFromFieldLocation(final IScope scope, final IField field, final int columns,
			final int rows) {
		return field.getCellShapeAt(scope, columns, rows);
	}

	/**
	 * Gets the shapes from geometry (cells with a point inside the geometry).
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param shape
	 *            the shape
	 * @return the shapes from geometry
	 */
	@operator (
			value = "cells_in",
			can_be_const = false,
			content_type = IType.GEOMETRY,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of 'cells' that 'intersect' with the geometry passed in argument. "
					+ "(Intersection is understood as the cell center is insside the geometry; if the  geometry is a polyline or a point, results will not be accurate."
					+ "The cells are ordered by their x-, then y-coordinates") })
	@no_test
	public static IList<IShape> getShapesFromGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getCellsIntersecting(scope, shape);
	}

	/**
	 * Gets the shapes from geometry (cells overlapping the geometry).
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param shape
	 *            the shape
	 * @return the shapes from geometry
	 */
	@operator (
			value = "cells_overlapping",
			can_be_const = false,
			content_type = IType.GEOMETRY,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of 'cells' that 'overlap' the geometry passed in argument. "
					+ "It is much less efficient than the cells_in operator, but is relevant is a polynie or a point. "
					+ "The cells are ordered by their x-, then y-coordinates") })
	@no_test
	public static IList<IShape> getShapesOverGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getCellsOverlapping(scope, shape);
	}

	/**
	 * Gets the values from geometry.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param shape
	 *            the shape
	 * @return the values from geometry
	 */
	@operator (
			value = "values_in",
			can_be_const = false,
			content_type = IType.FLOAT,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of values in the field whose 'cell' 'intersects' with the geometry passed in argument. The values are ordered by the x-, then y-coordinate, of their 'cell'") })
	@no_test
	public static IList<Double> getValuesFromGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getValuesIntersecting(scope, shape);
	}

	/**
	 * Gets the points from geometry.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param shape
	 *            the shape
	 * @return the points from geometry
	 */
	@operator (
			value = "points_in",
			can_be_const = false,
			content_type = IType.POINT,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of values in the field whose 'cell' 'intersects' with the geometry passed in argument. The values are ordered by the x-, then y-coordinate, of their 'cell'") })
	@no_test
	public static IList<GamaPoint> getPointsFromGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getLocationsIntersecting(scope, shape);
	}

	/**
	 * Gets the neighbors of.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param point
	 *            the point
	 * @return the neighbors of
	 */
	@operator (
			value = "neighbors_of",
			can_be_const = false,
			content_type = IType.POINT,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of the 'neighbors' of a given world coordinate point, which correspond to the world coordinates of the cells that surround the cell located at this point") })
	@no_test
	public static IList<GamaPoint> getNeighborsOf(final IScope scope, final IField field, final GamaPoint point) {
		return field.getNeighborsOf(scope, point);
	}

}
