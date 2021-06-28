package msi.gama.util.matrix;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.IFieldMatrixProvider;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.Types;
import one.util.streamex.DoubleStreamEx;
import one.util.streamex.StreamEx;

public class GamaField extends GamaFloatMatrix implements IField {

	final GamaPoint temp = new GamaPoint();
	GamaPoint worldDimensions = null;
	GamaPoint cellDimensions = null;
	double epsilon, noDataValue;
	IList<IField> bands = GamaListFactory.create(Types.FIELD);

	public GamaField(final IScope scope, final IFieldMatrixProvider provider) {
		this(scope, provider.getCols(scope), provider.getRows(scope), provider.getFieldData(scope),
				provider.getNoData(scope));
		int nbBands = provider.getBandsNumber(scope);
		for (int i = 0; i < nbBands; i++) {
			bands.add(new GamaField(scope, this, provider.getBand(scope, i)));
		}
	}

	private GamaField(final IScope scope, final GamaField primary, final double[] band) {
		this(scope, primary.numCols, primary.numRows, band, primary.noDataValue);
		worldDimensions = primary.worldDimensions;
		cellDimensions = primary.cellDimensions;
		epsilon = primary.epsilon;
	}

	public GamaField(final IScope scope, final int cols, final int rows, final double[] objects,
			final double noDataValue) {
		super(objects); // no copy
		this.noDataValue = noDataValue;
		numCols = cols;
		numRows = rows;
		this.noDataValue = noDataValue;
		bands.add(this);
	}

	/**
	 * Call this method before any computation that involves the world/cell dimensions. Computed lazily to avoid
	 * deadlock problems (when the shape of the workd, for instance, is computed after a field)
	 *
	 * @param scope
	 */
	private void computeDimensions(final IScope scope) {
		if (worldDimensions != null) return;
		IShape world = scope.getSimulation().getGeometry();
		worldDimensions = new GamaPoint(world.getWidth(), world.getHeight());
		cellDimensions = new GamaPoint(world.getWidth() / this.numCols, world.getHeight() / this.numRows);
		epsilon = cellDimensions.x / 1000;

	}

	@Override
	public Double getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) return null;
		final int size = indices.size();
		if (size == 1) {
			final Object index = indices.get(0);
			if (index instanceof GamaPoint)
				return get(scope, (GamaPoint) index);
			else
				return this.getNthElement(Cast.asInt(scope, index));
		}
		final int px = Cast.asInt(scope, indices.get(0));
		final int py = Cast.asInt(scope, indices.get(1));
		if (px > numCols - 1 || px < 0)
			throw GamaRuntimeException.error("Access to a field element out of its bounds: " + px, scope);
		if (py > numRows - 1 || py < 0)
			throw GamaRuntimeException.error("Access to a field element out of its bounds: " + py, scope);
		return get(scope, px, py);
	}

	/**
	 * Access through location: this corresponds to an access through world coordinates by agents. The access through
	 * grid coordinates is already taken in charge by matrices
	 */
	@Override
	public Double get(final IScope scope, final ILocation p) {
		worldCoordinatesToIndices(scope, p, temp);
		return get(scope, (int) temp.x, (int) temp.y);
	}

	/**
	 * If the index is a list of int indices, we translate it into a point. If it is already a location, it is an access
	 * through world coordinates by agents.
	 */
	@Override
	protected ILocation buildIndex(final IScope scope, final Object object) {
		if (object instanceof IList) {
			IList list = (IList) object;
			return new GamaPoint(Cast.asInt(scope, list.get(0)), Cast.asInt(scope, list.get(1)));
		} else if (object instanceof ILocation) {
			computeDimensions(scope);
			ILocation p = (ILocation) object;
			return worldCoordinatesToIndices(scope, p, temp);
		} else
			return GamaPointType.staticCast(scope, object, false);
	}

	private GamaPoint worldCoordinatesToIndices(final IScope scope, final ILocation p, final GamaPoint into) {
		final double px = p.getX();
		final double py = p.getY();
		final double xx = (px == worldDimensions.x ? px - epsilon : px) / cellDimensions.x;
		final double yy = (py == worldDimensions.y ? py - epsilon : py) / cellDimensions.y;
		final int cols = (int) xx;
		final int rows = (int) yy;
		if (cols > numCols - 1 || cols < 0)
			throw GamaRuntimeException.error("Access to a field element out of its bounds: " + px, scope);
		if (rows > numRows - 1 || rows < 0)
			throw GamaRuntimeException.error("Access to a field element out of its bounds: " + py, scope);
		if (into == null) return new GamaPoint(cols, rows);
		into.setLocation(cols, rows, 0);
		return into;

	}

	@Override
	public double getNoData(final IScope scope) {
		return noDataValue;
	}

	@Override
	public void setNoData(final IScope scope, final double noData) {
		if (noData != noDataValue) { noDataValue = noData; }
		if (bands.size() > 1) {
			for (int i = 1; i < bands.size(); i++) {
				bands.get(i).setNoData(scope, noData);
			}
		}
	}

	@Override
	public IList<IField> getBands(final IScope scope) {
		return bands;
	}

	@Override
	public double[] getMinMax(final double[] result) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (double f : getMatrix()) {
			if (f == noDataValue) { continue; }
			if (f > max) {
				max = f;
			} else if (f < min) { min = f; }
		}
		if (result == null) return new double[] { min, max };
		result[0] = min;
		result[1] = max;
		return result;
	}

	/**
	 * Inherited from IDiffusionTarger. The variable name (to diffuse) is not considered and the number of neighbours is
	 * 8 by default (should be set as a property of the diffuser...)
	 */

	@Override
	public int getNbNeighbours() {
		return 8; // ??? default ??
	}

	@Override
	public double getValueAtIndex(final IScope scope, final int i, final String var_diffu) {
		return getMatrix()[i];
	}

	@Override
	public void setValueAtIndex(final IScope scope, final int i, final String var_diffu, final double val) {
		getMatrix()[i] = val;
	}

	@Override
	public void getValuesInto(final IScope scope, final String varName, final double minValue, final double[] input) {
		System.arraycopy(getMatrix(), 0, input, 0, input.length);
		for (int i = 0; i < input.length; i++) {
			if (input[i] < minValue) { input[i] = 0; }
		}
	}

	/**
	 * Redefined from IMatrix to take into account no_data when computing max and min.
	 */

	/**
	 * We only stream away the values different from noDataValue (should normally allow most of the algorithms in
	 * Containers to work)
	 *
	 */
	@Override
	public StreamEx<Double> stream(final IScope scope) {
		return DoubleStreamEx.of(getMatrix()).filter(d -> d != noDataValue).boxed();
	}

	/**
	 * We only iterate over the values different from noDataValue (should normally allow most of the algorithms in
	 * Statistics to work)
	 *
	 */
	@Override
	public java.lang.Iterable<Double> iterable(final IScope scope) {
		return Iterables.filter(Doubles.asList(getMatrix()), e -> e != noDataValue);
	}

}
