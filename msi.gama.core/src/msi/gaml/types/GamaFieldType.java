package msi.gaml.types;

import java.util.Arrays;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
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

	public static IField buildField(final IScope scope, final Object object) {
		return staticCast(scope, object, null, null, false);
	}

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
			if (obj instanceof ISpecies) {
				ISpecies species = (ISpecies) obj;
				if (species.isGrid()) return staticCast(scope, species.getPopulation(scope).getTopology().getPlaces(),
						param, contentType, copy);
			}

		} else if (size.x <= 0 || size.y < 0)
			throw GamaRuntimeException.error("Dimensions of a field should be positive.", scope);
		if (obj instanceof IContainer) return staticCast(scope,
				((IContainer) obj).matrixValue(scope, contentType, size, copy), null, contentType, copy);
		return with(scope, obj, size, contentType);

	}

	public static IField with(final IScope scope, final Object val, final GamaPoint p, final IType contentsType)
			throws GamaRuntimeException {
		int x = p == null ? 1 : (int) p.x;
		int y = p == null ? 1 : (int) p.y;
		return withObject(scope, val, x, y, contentsType);
	}

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
	public IType<?> getContentType() {
		return Types.FLOAT;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	/**
	 * Constructors to be used in GAML besides the default "casting" one (i.e. field(xxx))
	 */

	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns, number of rows, the initial value of its cells and the value representing the absence of value") })
	public static IField buildField(final IScope scope, final int cols, final int rows, final double init,
			final double no) {
		double[] data = new double[cols * rows];
		Arrays.fill(data, init);
		return new GamaField(scope, cols, rows, data, no);
	}

	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns, "
					+ "number of rows and the initial value of its cells. The value representing the absence of value is set to #max_float") })
	public static IField buildField(final IScope scope, final int cols, final int rows, final double init) {
		return buildField(scope, cols, rows, init, Double.MAX_VALUE);
	}

	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying, in order, its number of columns and number of rows. "
					+ "The initial value of its cells is set to 0.0 and the value representing the absence of value is set to #max_float") })
	public static IField buildField(final IScope scope, final int cols, final int rows) {
		return buildField(scope, cols, rows, 0d);
	}

	@operator (
			value = IKeyword.FIELD,
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Allows to build a field by specifying an arbitrary object (assuming this object can return a matrix of float) "
					+ "and a value representing the absence of data. ") })
	public static IField buildFieldWithNoData(final IScope scope, final Object object, final double noData) {
		IField field = buildField(scope, object);
		field.setNoData(scope, noData);
		return field;
	}

	@operator (
			value = "cell_at",
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the rectangular shape that corresponds to the 'cell' in the field at this location. This cell has no attributes. A future version may load it with the value of the field at this attribute") })
	public static IShape buildShapeFromFieldLocation(final IScope scope, final IField field, final ILocation location) {
		return field.getCellShapeAt(scope, location);
	}

	@operator (
			value = "cells_in",
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of 'cells' that 'intersect' with the geometry passed in argument. The cells are ordered by their x-, then y-coordinates") })
	public static IList<IShape> getShapesFromGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getCellsIntersecting(scope, shape);
	}

	@operator (
			value = "values_in",
			can_be_const = false,
			category = { IOperatorCategory.GRID },
			concept = { IConcept.GRID },
			doc = { @doc ("Returns the list of values in the field whose 'cell' 'intersects' with the geometry passed in argument. The values are ordered by the x-, then y-coordinate, of their 'cell'") })
	public static IList<Double> getValuesFromGeometry(final IScope scope, final IField field, final IShape shape) {
		return field.getValuesIntersecting(scope, shape);
	}

}
