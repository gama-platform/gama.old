/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.grid.GamaSpatialMatrix.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.distance.DistanceOp;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.JavaUtils;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaProxyGeometry;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Projections;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import one.util.streamex.StreamEx;

/**
 * This matrix contains geometries and can serve to organize the agents of a population as a grid in the environment, or
 * as a support for grid topologies
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaSpatialMatrix extends GamaMatrix<IShape> implements IGrid {

	/** The geometry of host. */

	public final boolean useIndividualShapes;
	public final IShape environmentFrame;
	public boolean useNeighborsCache = false;
	public String optimizer = "A*"; // possible value: ["BF","Dijkstra", "A*"]
	final Envelope bounds;
	final double precision;
	protected IShape[] matrix;

	double cellWidth, cellHeight;
	public int[] supportImagePixels;
	public double[] gridValue;
	public int nbBands = 1;
	public List<IList<Double>> bands = null;
	protected Boolean usesVN = null;
	protected Boolean isTorus = null;
	protected Boolean isHexagon = null;

	protected Boolean isHorizontalOrientation = null;
	public INeighborhood neighborhood;

	int actualNumberOfCells;
	int firstCell, lastCell;
	// UnmodifiableIterator<? extends IShape> iterator = null;
	private ISpecies cellSpecies;
	// private IAgentFilter cellFilter;

	Map hexAgentToLoc = null;

	final IShape referenceShape;

	@Override
	public void dispose() {
		if (neighborhood != null) { neighborhood.clear(); }
		neighborhood = null;
		gridValue = null;
		_clear();
		matrix = null;
		cellSpecies = null;
	}

	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
			final boolean isTorus, final boolean usesVN, final boolean indiv, final boolean useNeighborsCache,
			final String optimizer) throws GamaRuntimeException {
		super(cols, rows, Types.GEOMETRY);
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		precision = bounds.getWidth() / 1000;
		final int size = numRows * numCols;
		createMatrix(size);
		// image = ImageUtils.createCompatibleImage(cols, rows);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = usesVN;
		actualNumberOfCells = 0;
		referenceShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		firstCell = -1;
		lastCell = -1;
		this.isHexagon = false;
		useIndividualShapes = indiv;
		this.useNeighborsCache = useNeighborsCache;
		createCells(scope, false);
		this.optimizer = optimizer;
	}

	public GamaSpatialMatrix(final IScope scope, final GamaGridFile gfile, final boolean isTorus, final boolean usesVN,
			final boolean indiv, final boolean useNeighborsCache, final String optimizer) throws GamaRuntimeException {
		super(100, 100, Types.GEOMETRY);
		// DEBUG.OUT("GamaSpatialMatrix.GamaSpatialMatrix create
		// new");
		numRows = gfile.getRows(scope);
		numCols = gfile.getCols(scope);

		environmentFrame = scope.getSimulation().getGeometry();
		// environmentFrame = gfile.getGeometry(scope);
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / numCols;
		cellHeight = bounds.getHeight() / numRows;
		precision = bounds.getWidth() / 1000;
		final int size = gfile.length(scope);
		createMatrix(size);
		supportImagePixels = new int[size];
		referenceShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		this.isTorus = isTorus;
		this.usesVN = usesVN;
		useIndividualShapes = indiv;
		this.isHexagon = false;
		this.useNeighborsCache = useNeighborsCache;
		this.optimizer = optimizer;
		gridValue = gfile.getFieldData(scope).clone();
		this.nbBands = gfile.getBandsNumber(scope);
		if (nbBands > 1) {
			bands = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				IList<Double> bb = GamaListFactory.create(Types.FLOAT);
				for (int b = 0; b < nbBands; b++) {
					bb.add(gfile.getBand(scope, b)[i]);
				}
				bands.add(bb);
			}
		}

		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		createCells(scope, false);
	}

	public GamaSpatialMatrix(final IScope scope, final IList<GamaGridFile> gfiles, final boolean isTorus,
			final boolean usesVN, final boolean indiv, final boolean useNeighborsCache, final String optimizer)
			throws GamaRuntimeException {
		this(scope, gfiles.firstValue(scope), isTorus, usesVN, indiv, useNeighborsCache, optimizer);
		GamaGridFile gfile = gfiles.firstValue(scope);

		this.nbBands = gfiles.size();
		bands = new ArrayList<>();

		String initCRS = new ArrayList<>(gfile.getGis(scope).getInitialCRS(scope).getIdentifiers()).get(0).toString();
		List<String> crsF = new ArrayList<>();

		for (int j = 1; j < gfiles.size(); j++) {
			crsF.add(new ArrayList<>(gfiles.get(j).getGis(scope).getInitialCRS(scope).getIdentifiers()).get(0)
					.toString());
		}
		for (int i = 0; i < matrix.length; i++) {
			final IList vals = GamaListFactory.create(Types.FLOAT);
			vals.add(gridValue[i]);
			for (int j = 1; j < gfiles.size(); j++) {
				final GamaGridFile gfile2 = gfiles.get(j);
				String taCRS = crsF.get(j - 1);
				ILocation loc = matrix[i].getLocation();
				if (initCRS != null && taCRS != null) {
					IShape s = Projections.transform_CRS(scope, loc, initCRS, taCRS);
					if (s != null) { loc = s.getLocation(); }
				}
				final Double v = gfile2.valueOf(scope, loc);
				vals.add(v);
			}
			bands.add(vals);
			// System.out.println("vals: " + vals);
			// WARNING A bit overkill as we only use the GamaGisGeometry for its
			// attribute...
			// matrix[i] = g;
		}

		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		createCells(scope, false);
	}

	// constructor used to build hexagonal grid (-> useVN = false)
	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
			final boolean isTorus, final boolean usesVN, final boolean isHexagon, final boolean horizontalOrientation,
			final boolean indiv, final boolean useNeighborsCache, final String optimizer) {
		super(cols, rows, Types.GEOMETRY);
		// scope.getGui().debug("GamaSpatialMatrix.GamaSpatialMatrix create
		// new");
		isHorizontalOrientation = horizontalOrientation;
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		// TODO False
		referenceShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		precision = bounds.getWidth() / 1000;
		final int size = numRows * numCols;
		createMatrix(size);
		// image = ImageUtils.createCompatibleImage(cols, rows);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = false;
		this.isHexagon = isHexagon;
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		useIndividualShapes = indiv;

		this.optimizer = optimizer;
		this.useNeighborsCache = useNeighborsCache;
		if (isHorizontalOrientation != null && !isHorizontalOrientation) {
			createHexagonsVertical(scope, false);
		} else {
			createHexagonsHorizontal(scope, false);
		}
	}

	private void createMatrix(final int size) {
		matrix = new IShape[size];
		gridValue = new double[size];
	}

	private void createHexagonsHorizontal(final IScope scope, final boolean partialCells) {
		final double widthEnv = environmentFrame.getEnvelope().getWidth();
		final double heightEnv = environmentFrame.getEnvelope().getHeight();
		double xmin = environmentFrame.getEnvelope().getMinX();
		double ymin = environmentFrame.getEnvelope().getMinY();
		// final GamaShape gbg = new
		// GamaShape(environmentFrame.getInnerGeometry().buffer(0.1, 2));
		cellWidth = widthEnv / (numCols * 0.75 + 0.25);
		cellHeight = heightEnv / (numRows + 0.5);
		xmin += cellWidth / 2.0;
		ymin += cellHeight / 2.0;
		// numCols = (int) (width / cellWidth);
		hexAgentToLoc = GamaMapFactory.create();
		int i = 0;
		for (int l = 0; l < numRows; l++) {
			for (int c = 0; c < numCols; c = c + 2) {
				i = c + numCols * l;
				final GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight,
						new GamaPoint(xmin + c * cellWidth * 0.75, ymin + l * cellHeight));
				// if (gbg.covers(poly)) {
				if (firstCell == -1) { firstCell = i; }
				matrix[i] = poly;
				hexAgentToLoc.put(poly, new GamaPoint(c, l));
				actualNumberOfCells++;
				lastCell = Math.max(lastCell, i);
				// }
			}
		}

		for (int l = 0; l < numRows; l++) {
			for (int c = 1; c < numCols; c = c + 2) {
				i = c + numCols * l;

				final GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight,
						new GamaPoint(xmin + c * cellWidth * 0.75, ymin + (l + 0.5) * cellHeight));

				// if (gbg.covers(poly)) {
				if (firstCell == -1) { firstCell = i; }
				matrix[i] = poly;
				hexAgentToLoc.put(poly, new GamaPoint(c, l));
				actualNumberOfCells++;
				lastCell = Math.max(lastCell, i);
				// }
			}
		}
	}

	private void createHexagonsVertical(final IScope scope, final boolean partialCells) {
		final double widthEnv = environmentFrame.getEnvelope().getWidth();
		final double heightEnv = environmentFrame.getEnvelope().getHeight();
		double xmin = environmentFrame.getEnvelope().getMinX();
		double ymin = environmentFrame.getEnvelope().getMinY();
		// final GamaShape gbg = new
		// GamaShape(environmentFrame.getInnerGeometry().buffer(0.1, 2));
		cellWidth = widthEnv / (numCols + 0.5);
		cellHeight = heightEnv / (numRows * 0.75 + 0.25);
		xmin += cellWidth / 2.0;
		ymin += cellHeight / 2.0;
		// numCols = (int) (width / cellWidth);
		hexAgentToLoc = GamaMapFactory.create();
		int i = 0;
		for (int l = 0; l < numRows; l = l + 2) {
			for (int c = 0; c < numCols; c++) {
				i = c + numCols * l;
				final IShape poly = Spatial.Transformations.rotated_by(scope, GamaGeometryType.buildHexagon(cellHeight,
						cellWidth, new GamaPoint(xmin + c * cellWidth, ymin + l * cellHeight * 0.75)), 90.0);

				// if (gbg.covers(poly)) {
				if (firstCell == -1) { firstCell = i; }
				matrix[i] = poly;
				hexAgentToLoc.put(poly, new GamaPoint(c, l));
				actualNumberOfCells++;
				lastCell = Math.max(lastCell, i);
				// }
			}
		}

		for (int l = 1; l < numRows; l = l + 2) {
			for (int c = 0; c < numCols; c++) {
				i = c + numCols * l;

				final IShape poly = Spatial.Transformations.rotated_by(scope, GamaGeometryType.buildHexagon(cellHeight,
						cellWidth, new GamaPoint(xmin * 2 + c * cellWidth, ymin + l * cellHeight * 0.75)), 90.0);

				// if (gbg.covers(poly)) {
				if (firstCell == -1) { firstCell = i; }
				matrix[i] = poly;
				hexAgentToLoc.put(poly, new GamaPoint(c, l));
				actualNumberOfCells++;
				lastCell = Math.max(lastCell, i);
				// }
			}
		}
	}

	private void createCells(final IScope scope, final boolean partialCells) throws GamaRuntimeException {
		final boolean isRectangle = environmentFrame.getInnerGeometry().isRectangle();
		final GamaPoint origin =
				new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope().getMinY());

		final IShape translatedReferenceFrame = Spatial.Transformations.translated_by(scope, environmentFrame, origin);
		final GamaPoint[][] xs = new GamaPoint[numCols + 1][numRows + 1];
		for (int i = 0; i < numCols + 1; i++) {
			for (int j = 0; j < numRows + 1; j++) {
				final GamaPoint p = new GamaPoint(i * cellWidth, j * cellHeight);
				xs[i][j] = p;
			}
		}

		for (int i = 0, n = numRows * numCols; i < n; i++) {
			final int yy = i / numCols;
			final int xx = i - yy * numCols;

			// WARNING HACK
			IShape rect = null;
			if (useIndividualShapes) {
				// Change in the function used in building cells in order to minimize computations and mutualize points.
				// See #2896
				rect = new GamaShape(GeometryUtils.GEOMETRY_FACTORY.buildRectangle(new GamaPoint[] { xs[xx][yy],
						xs[xx + 1][yy], xs[xx + 1][yy + 1], xs[xx][yy + 1], xs[xx][yy] }));
			} else {
				final double cmx = cellWidth / 2;
				final double cmy = cellHeight / 2;
				rect = new CellProxyGeometry(new GamaPoint(xx * cellWidth + cmx, yy * cellHeight + cmy));
			}
			boolean ok = isRectangle || translatedReferenceFrame.covers(rect);
			if (partialCells && !ok && rect.intersects(translatedReferenceFrame)) {
				rect.setGeometry(Spatial.Operators.inter(scope, rect, translatedReferenceFrame));
				ok = true;
			}
			if (ok) {
				if (firstCell == -1) { firstCell = i; }
				matrix[i] = rect;
				actualNumberOfCells++;
				lastCell = i;
			}
		}
	}

	@Override
	public INeighborhood getNeighborhood() {
		if (neighborhood == null) {
			if (useNeighborsCache) {
				neighborhood = isHexagon ? isHorizontalOrientation != null && !isHorizontalOrientation
						? new GridHexagonalNeighborhoodVertical(this) : new GridHexagonalNeighborhoodHorizontal(this)
						: usesVN ? new GridVonNeumannNeighborhood(this) : new GridMooreNeighborhood(this);
			} else {
				neighborhood = new NoCacheNeighborhood(this);
			}
		}
		return neighborhood;
	}

	@Override
	public int[] getDisplayData() {
		return supportImagePixels;
	}

	@Override
	public double[] getGridValue() {
		return gridValue;
	}

	@Override
	public double[] getGridValueOf(final IScope scope, final IExpression exp) {
		final double[] result = new double[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			final IShape s = matrix[i];
			if (s != null) {
				final IAgent a = s.getAgent();
				if (a != null) { result[i] = Cast.asFloat(scope, scope.evaluate(exp, a).getValue()); }
			}

		}
		return result;
	}

	public double getGridValue(final int col, final int row) {
		final int index = getPlaceIndexAt(col, row);
		if (index != -1) return gridValue[index];
		return 0.0;
	}

	final int getPlaceIndexAt(final int xx, final int yy) {
		if (isHexagon) return yy * numCols + xx;
		if (isTorus) return (yy < 0 ? yy + numCols : yy) % numRows * numCols + (xx < 0 ? xx + numCols : xx) % numCols;
		if (xx < 0 || xx >= numCols || yy < 0 || yy >= numRows) return -1;

		return yy * numCols + xx;
	}

	private final int getPlaceIndexAt(final ILocation p) {
		if (isHexagon) {
			int xx = 0;
			int yy = 0;
			if (isHorizontalOrientation) {
				xx = (int) (p.getX() / (cellWidth * 0.75));
				yy = xx % 2 == 0 ? (int) (p.getY() / cellHeight) : (int) ((p.getY() - cellHeight) / cellHeight);
			} else {
				yy = (int) (p.getY() / (cellHeight * 0.75));
				xx = yy % 2 == 0 ? (int) (p.getX() / cellWidth) : (int) ((p.getX() - cellWidth) / cellWidth);

			}
			xx = Math.min(xx, this.numCols - 1);
			yy = Math.min(yy, this.numRows - 1);
			final int i = getPlaceIndexAt(xx, yy);
			if (matrix[i] == null) return -1;
			if (matrix[i].getLocation() == p || matrix[i].intersects(p)) return i;
			final Set<Integer> toObserve =
					((GridHexagonalNeighborhood) getNeighborhood()).getNeighborsAtRadius1(i, numCols, numRows, isTorus);
			toObserve.add(i);
			int x = 0, y = 0;
			final Iterator<Integer> it = toObserve.iterator();
			while (it.hasNext()) {
				final int id = it.next();

				final IShape sh = matrix[id];
				if (sh.intersects(p)) {
					final GamaPoint pt = (GamaPoint) hexAgentToLoc.get(sh.getGeometry());
					x = (int) pt.x;
					y = (int) pt.y;
					return getPlaceIndexAt(x, y);
				}

			}
			return -1;

		}
		final double px = p.getX();
		final double py = p.getY();
		final double xx = px == bounds.getMaxX() ? (px - precision) / cellWidth : px / cellWidth;
		final double yy = py == bounds.getMaxY() ? (py - precision) / cellHeight : py / cellHeight;
		final int x = (int) xx;
		final int y = (int) yy;
		return getPlaceIndexAt(x, y);
	}

	@Override
	public final int getX(final IShape shape) {
		return (int) ((GamaPoint) hexAgentToLoc.get(shape)).x;
	}

	@Override
	public final int getY(final IShape shape) {
		return (int) ((GamaPoint) hexAgentToLoc.get(shape)).y;
	}

	@Override
	public IShape getPlaceAt(final ILocation c) {
		if (c == null) return null;
		final int p = getPlaceIndexAt(c);
		if (p == -1) return null;
		return matrix[p];
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		// TODO not allowed for the moment (fixed grid)
		//
	}

	@Override
	public IShape get(final IScope scope, final int col, final int row) {
		final int index = getPlaceIndexAt(col, row);
		if (index != -1) return matrix[index];
		return null;
	}

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
	}

	@Override
	public IShape remove(final IScope scope, final int col, final int row) {
		// TODO not allowed for the moment (fixed grid)
		return null;
	}

	@Override
	public boolean _removeFirst(final IScope scope, final IShape o) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
		return false;

	}

	@Override
	public boolean _removeAll(final IScope scope, final IContainer<?, IShape> value) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
		return false;
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, null);
		matrix = null;
	}

	@Override
	protected IList _listValue(final IScope scope, final IType contentType, final boolean cast) {
		if (actualNumberOfCells == 0) return GamaListFactory.EMPTY_LIST;
		if (cellSpecies == null)
			return cast ? GamaListFactory.create(scope, contentType, matrix)
					: GamaListFactory.wrap(contentType, matrix);
		else {
			final IList result = GamaListFactory.create(contentType, actualNumberOfCells);
			for (final IShape a : matrix) {
				if (a != null) {
					final IAgent ag = a.getAgent();
					final Object toAdd = ag == null ? a : ag;
					result.add(cast ? contentType.cast(scope, toAdd, null, false) : toAdd);
				}
			}
			return result;
		}
	}

	@Override
	public java.lang.Iterable<IShape> iterable(final IScope scope) {
		return Arrays.asList(matrix);
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize, final IType type,
			final boolean copy) {
		// WARNING: copy is not taken into account here
		return GamaMatrixType.from(scope, this, type, preferredSize, copy);
	}

	@Override
	public Integer _length(final IScope scope) {
		return actualNumberOfCells;
	}

	@Override
	public IShape _first(final IScope scope) {
		if (firstCell == -1) return null;
		return matrix[firstCell];
	}

	@Override
	public IShape _last(final IScope scope) {
		if (lastCell == -1) return null;
		return matrix[lastCell];
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatrix copy(final IScope scope, final ILocation size, final boolean copy) throws GamaRuntimeException {
		if (size == null && !copy) return this;
		return new GamaSpatialMatrix(scope, environmentFrame, numCols, numRows, isTorus, usesVN, useIndividualShapes,
				useNeighborsCache, optimizer);
	}

	@Override
	public boolean _contains(final IScope scope, final Object o) {
		if (cellSpecies != null) return cellSpecies.contains(scope, o);
		return false;
	}

	@Override
	public void _putAll(final IScope scope, final Object value) throws GamaRuntimeException {
		// TODO Not allowed for the moment

	}

	@Override
	public boolean _isEmpty(final IScope scope) {
		return actualNumberOfCells == 0;
	}

	/**
	 * Method usesIndiviualShapes()
	 *
	 * @see msi.gama.metamodel.topology.grid.IGrid#usesIndiviualShapes()
	 */
	@Override
	public boolean usesIndiviualShapes() {
		return useIndividualShapes;
	}

	@Override
	public int manhattanDistanceBetween(final IShape g1, final IShape g2) {

		IGridAgent s1 = g1.getAgent() != null && g1.getAgent().getSpecies() == this.getCellSpecies()
				? (IGridAgent) g1.getAgent() : null;
		IGridAgent s2 = g2.getAgent() != null && g2.getAgent().getSpecies() == this.getCellSpecies()
				? (IGridAgent) g2.getAgent() : null;

		if (s1 == null || s2 == null) {
			ILocation p1 = g1.isPoint() ? g1.getLocation() : null;
			ILocation p2 = g2.isPoint() ? g2.getLocation() : null;
			if (s1 == null) {
				s1 = (IGridAgent) this.getPlaceAt(g1.getLocation());
				if (!s1.covers(g1)) { s1 = null; }
			}
			if (s2 == null) {
				s2 = (IGridAgent) this.getPlaceAt(g2.getLocation());
				if (!s2.covers(g2)) { s2 = null; }
			}
			final Coordinate[] coord = new DistanceOp(g1.getInnerGeometry(), g2.getInnerGeometry()).nearestPoints();
			if (s1 == null) {
				p1 = new GamaPoint(coord[0]);
				s1 = (IGridAgent) this.getPlaceAt(p1);
			}
			if (s2 == null) {
				p2 = new GamaPoint(coord[1]);
				s2 = (IGridAgent) this.getPlaceAt(p2);
			}
		}

		final int dx = Math.abs(s1.getX() - s2.getX());
		final int dy = Math.abs(s1.getY() - s2.getY());
		if (usesVN) return dx + dy;
		return Math.max(dx, dy);
	}

	/**
	 * Returns the cells making up the neighborhood of a geometrical shape. First, the cells covered by this shape are
	 * computed, then their neighbors are collated (excluding the previous ones). A special case is made for point
	 * geometries and for agents contained in this matrix.
	 *
	 * @param source
	 * @param distance
	 * @return
	 */
	@Override
	public Set<IAgent> getNeighborsOf(final IScope scope, final IShape shape, final Double distance,
			final IAgentFilter filter) {

		// If the shape is a point or if it is a cell of this matrix, we run the
		// method with an ILocation instead
		if (shape.isPoint() || shape.getAgent() != null && shape.getAgent().getSpecies() == cellSpecies)
			return getNeighborsOf(scope, shape.getLocation(), distance, filter);

		// We compute all the cells covered by the shape (we know that it is not
		// a cell of the matrix) -- no filter used
		// here, as we must take all the cells into account
		final Set<IAgent> coveredPlaces = allInEnvelope(scope, shape, shape.getEnvelope(), null, true);
		// final Set<IAgent> placesToRemove = Sets.newHashSet(coveredPlaces);

		// We now compute all the cells that are at "distance" away from these
		// covered cells
		final Set<IAgent> allPlaces = new LinkedHashSet();
		for (final IAgent ag : coveredPlaces) {
			allPlaces.addAll(
					getNeighborhood().getNeighborsIn(scope, getPlaceIndexAt(ag.getLocation()), distance.intValue()));
		}

		// And we filter these cells by removing those that are in the "interior
		// cells" (which are not part of the
		// neighborood) and that are not accepted by the IAgentFilter. A
		// special case is made if the filter is only
		// accepting cells of this matrix : in that case, we simply remove the
		// "interior cells" from the iterator and
		// we return it;

		allPlaces.removeAll(coveredPlaces);
		if (filter != null) { if (filter.getSpecies() != cellSpecies) { filter.filter(scope, shape, allPlaces); } }
		return allPlaces;
	}

	protected Set<IAgent> getNeighborsOf(final IScope scope, final ILocation shape, final Double distance,
			final IAgentFilter filter) {
		final Set<IAgent> allPlaces =
				getNeighborhood().getNeighborsIn(scope, getPlaceIndexAt(shape), distance.intValue());
		if (filter != null) {
			if (filter.getSpecies() == cellSpecies) return allPlaces;
			filter.filter(scope, shape, allPlaces);
		}
		return allPlaces;
	}

	static IAgent testPlace(final IScope scope, final IShape source, final IAgentFilter filter, final IShape toTest) {
		if (filter.accept(scope, source, toTest)) return toTest.getAgent();
		final List<IAgent> agents =
				new ArrayList<>(scope.getTopology().getAgentsIn(scope, toTest, filter, filter.getSpecies() != null
						&& toTest.getAgent() != null && filter.getSpecies().equals(toTest.getAgent().getSpecies())));
		agents.remove(source);
		if (agents.isEmpty()) return null;
		scope.getRandom().shuffleInPlace(agents);
		return agents.get(0);
	}

	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter)
			throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		if (filter.accept(scope, source, startAg)) return startAg;
		IAgent agT = testPlace(scope, source, filter, startAg);
		if (agT != null) return agT;
		final List<Integer> cells = new ArrayList<>();

		int cpt = 0;
		cells.add(startAg.getIndex());
		// final int max = this.numCols * this.numRows;
		List<IAgent> neighb = getNeighborhoods(scope, startAg, cells, new ArrayList<IAgent>());
		scope.getRandom().shuffleInPlace(neighb);
		while (cpt < this.numCols * this.numRows) {
			cpt++;
			try (ICollector<IAgent> neighb2 = Collector.getOrderedSet()) {
				for (final IAgent ag : neighb) {
					agT = testPlace(scope, source, filter, ag);
					if (agT != null) return agT;
					cells.add(ag.getIndex());
					neighb2.addAll(getNeighborhoods(scope, ag, cells, neighb));
				}
				neighb2.shuffleInPlaceWith(scope.getRandom());
				neighb = new ArrayList<>(neighb2.items());
			}
		}
		return null;
	}

	private List<IAgent> getNeighborhoods(final IScope scope, final IAgent agent, final List<Integer> cells,
			final List<IAgent> currentList) throws GamaRuntimeException {
		final List<IAgent> agents = new ArrayList(getNeighborsOf(scope, agent.getLocation(), 1.0, null));
		final List<IAgent> neighs = new ArrayList<>();
		for (final IAgent ag : agents) {
			if (!cells.contains(ag.getIndex()) && !currentList.contains(ag) && !neighs.contains(ag)) { neighs.add(ag); }
		}
		return neighs;
	}

	double heuristic(final IAgent next, final IAgent goal) {
		return next.getLocation().euclidianDistanceTo(goal.getLocation());
	}

	public GamaSpatialPath computeShortestPathBetweenBF(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();
		if (startAg == endAg) return simplePath(scope, source, target, topo, startAg, endAg);
		final boolean[] open = new boolean[this.getAgents().size()];
		initOpen(open, on);

		final List<IAgent> frontier = new ArrayList<>();
		final Map<IAgent, IAgent> cameFrom = new Hashtable<>();

		frontier.add(startAg);
		while (!frontier.isEmpty()) {
			final IAgent current = frontier.remove(0);
			if (current == endAg) return finalPath(scope, source, target, topo, startAg, current, cameFrom);
			final Collection<IAgent> neigh = getNeighborhood().getNeighborsIn(scope, current.getIndex(), 1);

			for (final IAgent next : neigh) {
				if (!open[next.getIndex()]) { continue; }
				frontier.add(next);
				cameFrom.put(next, current);
				open[next.getIndex()] = false;
			}
		}
		return null;

	}

	public GamaSpatialPath computeShortestPathBetweenDijkstra(final IScope scope, final IShape source,
			final IShape target, final ITopology topo, final IList<IAgent> on, final Map<IAgent, Object> onWithWeight)
			throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();
		if (startAg == endAg) return simplePath(scope, source, target, topo, startAg, endAg);
		final Double maxDim = onWithWeight != null ? Math.max(this.cellHeight, this.cellWidth) : 0.0;

		final boolean[] open = new boolean[this.getAgents().size()];
		initOpen(open, onWithWeight != null ? onWithWeight.keySet() : on);

		final Map<IAgent, IAgent> cameFrom = new HashMap<>();

		final PriorityQueue frontier = newPriorityQueue();

		final Map<IAgent, Double> costSoFar = new HashMap<>();
		costSoFar.put(startAg, 0.0);

		frontier.add(new ArrayList() {
			{
				add(startAg);
				add(0.0);
			}
		});
		while (!frontier.isEmpty()) {
			final IAgent current = (IAgent) ((List) frontier.remove()).get(0);
			if (current == endAg) {
				if (onWithWeight != null)
					return finalPath(scope, source, target, topo, startAg, current, cameFrom, onWithWeight);
				else
					return finalPath(scope, source, target, topo, startAg, current, cameFrom);
			}
			Collection<IAgent> neigh;
			neigh = getNeighborhood().getNeighborsIn(scope, current.getIndex(), 1);
			final Double cost = costSoFar.get(current);

			for (final IAgent next : neigh) {
				if (!open[next.getIndex()]) { continue; }
				final double dist = current.getLocation().euclidianDistanceTo(next.getLocation());
				final double nextCost = cost + (onWithWeight == null ? dist
						: Cast.asFloat(scope, onWithWeight.get(next)) + (dist > maxDim ? Double.MIN_VALUE : 0.0));

				frontier.add(new ArrayList() {
					{
						add(next);
						add(nextCost);
					}
				});
				open[next.getIndex()] = false;

				if (!costSoFar.containsKey(next) || nextCost < costSoFar.get(next)) {
					costSoFar.put(next, nextCost);
					cameFrom.put(next, current);
				}
			}
		}
		return null;

	}

	@SuppressWarnings ("null")
	public GamaSpatialPath computeShortestPathBetweenAStar(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on, final Map<IAgent, Object> onWithWeight)
			throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();
		final boolean weighted = onWithWeight != null;
		if (startAg == endAg) return simplePath(scope, source, target, topo, startAg, endAg);
		final Double maxDim = weighted ? Math.max(this.cellHeight, this.cellWidth) : 0.0;

		final boolean[] open = new boolean[this.getAgents().size()];
		initOpen(open, weighted ? onWithWeight.keySet() : on);
		final PriorityQueue frontier = newPriorityQueue();
		final Map<IAgent, IAgent> cameFrom = new HashMap<>();
		final Map<IAgent, Double> costSoFar = new HashMap<>();

		frontier.add(new ArrayList() {
			{
				add(startAg);
				add(weighted ? Cast.asFloat(scope, onWithWeight.get(startAg)) : 0.0);
			}
		});
		costSoFar.put(startAg, 0.0);
		while (!frontier.isEmpty()) {
			final IAgent current = (IAgent) ((List) frontier.remove()).get(0);
			if (current == endAg) {
				if (weighted)
					return finalPath(scope, source, target, topo, startAg, current, cameFrom, onWithWeight);
				else
					return finalPath(scope, source, target, topo, startAg, current, cameFrom);
			}
			final Double cost = costSoFar.get(current);
			final Set<IAgent> neigh = getNeighborhood().getNeighborsIn(scope, current.getIndex(), 1);
			for (final IAgent next : neigh) {
				if (!open[next.getIndex()]) { continue; }
				final double dist = current.getLocation().euclidianDistanceTo(next.getLocation());
				final double nextCost = cost + (!weighted ? dist
						: Cast.asFloat(scope, onWithWeight.get(next)) + (dist > maxDim ? Double.MIN_VALUE : 0.0));
				if (!costSoFar.containsKey(next) || nextCost < costSoFar.get(next)) {
					costSoFar.put(next, nextCost);
					frontier.add(new ArrayList() {
						{
							add(next);
							add(nextCost + heuristic(next, endAg));
						}
					});
					cameFrom.put(next, current);

				}
			}
		}
		return null;

	}

	public GamaSpatialPath computeShortestPathBetweenJPS(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException {

		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();

		if (startAg == endAg) return simplePath(scope, source, target, topo, startAg, endAg);
		final boolean[] open = new boolean[this.getAgents().size()];
		initOpen(open, on);
		final PriorityQueue frontier = newPriorityQueue();
		final Map<IAgent, IAgent> cameFrom = new HashMap<>();
		final Map<IAgent, Double> costSoFar = new HashMap<>();

		frontier.add(new ArrayList() {
			{
				add(startAg);
				add(0.0);
			}
		});
		costSoFar.put(startAg, 0.0);
		while (!frontier.isEmpty()) {
			final IAgent current = (IAgent) ((List) frontier.remove()).get(0);
			if (current == endAg) return finalPath(scope, source, target, topo, startAg, current, cameFrom);
			final Double cost = costSoFar.get(current);
			final Set<IAgent> neigh = getNeighborsPrune(scope, current, cameFrom.get(current), open);

			for (final IAgent next : neigh) {
				if (!open[next.getIndex()]) { continue; }
				final IAgent jumpt = jump(scope, next, current, open, endAg);
				final IAgent ne = jumpt == null ? next : jumpt;
				final double nextCost = cost + current.getLocation().euclidianDistanceTo(ne.getLocation());
				if (!costSoFar.containsKey(ne) || nextCost < costSoFar.get(ne)) {
					costSoFar.put(ne, nextCost);
					frontier.add(new ArrayList() {
						{
							add(ne);
							add(nextCost + heuristic(ne, endAg));
						}
					});
					cameFrom.put(ne, current);
				}
			}
		}
		return null;
	}

	public boolean walkable(final IScope scope, final int x, final int y, final boolean[] open) {
		final IAgent n = (IAgent) this.get(scope, x, y);
		return n != null && open[n.getIndex()];
	}

	public boolean notwalkable(final IScope scope, final int x, final int y, final boolean[] open) {
		final IAgent n = (IAgent) this.get(scope, x, y);
		return n == null || !open[n.getIndex()];
	}

	public IAgent jump(final IScope scope, final IAgent node, final IAgent parent, final boolean[] open,
			final IAgent endAg) {
		if (node == null || !open[node.getIndex()]) return null;
		if (node == endAg) return node;
		final int x = ((IGridAgent) node).getX();
		final int y = ((IGridAgent) node).getY();
		open[node.getIndex()] = true;
		int px, py, dx, dy;

		px = ((IGridAgent) parent).getX();
		py = ((IGridAgent) parent).getY();
		dx = (x - px) / Math.max(Math.abs(x - px), 1);
		dy = (y - py) / Math.max(Math.abs(y - py), 1);
		final IAgent next = (IAgent) this.get(scope, x + dx, y + dy);

		if (dx != 0 && dy != 0) {
			if (walkable(scope, x - dx, y + dy, open) && notwalkable(scope, x - dx, y, open)
					|| walkable(scope, x + dx, y - dy, open) && notwalkable(scope, x, y - dy, open))
				return node;
		} else {
			if (dx != 0) {
				if (walkable(scope, x + dx, y + 1, open) && notwalkable(scope, x, y + 1, open)
						|| walkable(scope, x + dx, y - 1, open) && notwalkable(scope, x, y - 1, open))
					return node;
			} else {
				if (walkable(scope, x + 1, y + dy, open) && notwalkable(scope, x + 1, y, open)
						|| walkable(scope, x - 1, y + dy, open) && notwalkable(scope, x - 1, y, open))
					return node;
			}
		}

		if (dx != 0 && dy != 0) {
			final IAgent jx = jump(scope, (IAgent) get(scope, x + dx, y), node, open, endAg);
			final IAgent jy = jump(scope, (IAgent) get(scope, x, y + dy), node, open, endAg);
			if (jx != null || jy != null) return node;
		}
		return jump(scope, next, node, open, endAg);
	}

	public Set<IAgent> getNeighborsPrune(final IScope scope, final IAgent node, final IAgent parent,
			final boolean[] open) {
		if (parent == null) return getNeighborhood().getNeighborsIn(scope, node.getIndex(), 1);
		try (Collector.AsSet<IAgent> neighbors = Collector.getSet()) {
			final int x = ((IGridAgent) node).getX();
			final int y = ((IGridAgent) node).getY();

			int px, py, dx, dy;

			px = ((IGridAgent) parent).getX();
			py = ((IGridAgent) parent).getY();

			dx = (x - px) / Math.max(Math.abs(x - px), 1);
			dy = (y - py) / Math.max(Math.abs(y - py), 1);

			if (dx != 0 && dy != 0) {
				final IAgent nei1 = (IAgent) this.get(scope, x, y + dy);
				if (nei1 != null && open[nei1.getIndex()]) { neighbors.add(nei1); }
				final IAgent nei2 = (IAgent) this.get(scope, x + dx, y);
				if (nei2 != null && open[nei2.getIndex()]) { neighbors.add(nei2); }
				final IAgent nei3 = (IAgent) this.get(scope, x + dx, y + dy);
				if (nei3 != null && open[nei3.getIndex()]) { neighbors.add(nei3); }
				final IAgent neidx = (IAgent) this.get(scope, x - dx, y);
				if (neidx != null && !open[neidx.getIndex()]) {
					final IAgent neidiag = (IAgent) this.get(scope, x - dx, y + dy);
					if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
				}
				final IAgent neidy = (IAgent) this.get(scope, x, y - dy);
				if (neidy != null && !open[neidy.getIndex()]) {
					final IAgent neidiag = (IAgent) this.get(scope, x + dx, y - dy);
					if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
				}

			} else {
				if (dy == 0) {
					final IAgent nei = (IAgent) this.get(scope, x + dx, y);
					if (nei != null && open[nei.getIndex()]) { neighbors.add(nei); }
					final IAgent neiup = (IAgent) this.get(scope, x, y + 1);
					if (neiup != null && !open[neiup.getIndex()]) {
						final IAgent neidiag = (IAgent) this.get(scope, x + dx, y + 1);
						if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
					}
					final IAgent neidown = (IAgent) this.get(scope, x, y - 1);
					if (neidown != null && !open[neidown.getIndex()]) {
						final IAgent neidiag = (IAgent) this.get(scope, x + dx, y - 1);
						if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
					}
				} else {
					final IAgent nei = (IAgent) this.get(scope, x, y + dy);
					if (nei != null && open[nei.getIndex()]) { neighbors.add(nei); }
					final IAgent neiright = (IAgent) this.get(scope, x + 1, y);
					if (neiright != null && !open[neiright.getIndex()]) {
						final IAgent neidiag = (IAgent) this.get(scope, x + 1, y + dy);
						if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
					}
					final IAgent neileft = (IAgent) this.get(scope, x - 1, y);
					if (neileft != null && !open[neileft.getIndex()]) {
						final IAgent neidiag = (IAgent) this.get(scope, x - 1, y + dy);
						if (neidiag != null && open[neidiag.getIndex()]) { neighbors.add(neidiag); }
					}
				}
			}
			return neighbors.items();
		}
	}

	@Override
	public GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException {
		if ("Dijkstra".equals(optimizer))
			return computeShortestPathBetweenDijkstra(scope, source, target, topo, on, null);
		else if (!neighborhood.isVN() && "JPS".equals(optimizer))
			return computeShortestPathBetweenJPS(scope, source, target, topo, on);
		else if ("BF".equals(optimizer)) return computeShortestPathBetweenBF(scope, source, target, topo, on);
		return computeShortestPathBetweenAStar(scope, source, target, topo, on, null);
	}

	@Override
	public GamaSpatialPath computeShortestPathBetweenWeighted(final IScope scope, final IShape source,
			final IShape target, final ITopology topo, final Map<IAgent, Object> on) {
		if ("A*".equals(optimizer)) return computeShortestPathBetweenAStar(scope, source, target, topo, null, on);
		return computeShortestPathBetweenDijkstra(scope, source, target, topo, null, on);
	}

	private GamaSpatialPath simplePath(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IAgent startAg, final IAgent endAg) {
		final IList<IShape> nodesPt = GamaListFactory.create(Types.GEOMETRY);
		nodesPt.add(source.getLocation());
		nodesPt.add(target.getLocation());
		return PathFactory.newInstance(scope, topo, nodesPt, 0.0);
	}

	private GamaSpatialPath finalPath(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IAgent startAg, final IAgent agent, final Map<IAgent, IAgent> cameFrom,
			final Map<IAgent, Object> on) {
		IAgent current = agent;
		final IList<IShape> nodesPt = GamaListFactory.create(Types.GEOMETRY);
		double weight = Cast.asFloat(scope, on.get(current));
		nodesPt.add(target.getLocation());
		while (current != startAg) {
			current = cameFrom.get(current);
			weight += Cast.asFloat(scope, on.get(current));
			if (current != startAg) { nodesPt.add(current.getLocation()); }
		}
		nodesPt.add(source.getLocation());
		Collections.reverse(nodesPt);
		return PathFactory.newInstance(scope, topo, nodesPt, weight);
	}

	private GamaSpatialPath finalPath(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IAgent startAg, final IAgent agent, final Map<IAgent, IAgent> cameFrom) {
		IAgent current = agent;
		final IList<IShape> nodesPt = GamaListFactory.create(Types.GEOMETRY);
		double weight = 1;
		nodesPt.add(target.getLocation());
		while (current != startAg) {
			current = cameFrom.get(current);
			weight += 1;
			if (current != startAg) { nodesPt.add(current.getLocation()); }
		}
		nodesPt.add(source.getLocation());
		Collections.reverse(nodesPt);
		return PathFactory.newInstance(scope, topo, nodesPt, weight);
	}

	private void initOpen(final boolean[] open, final Collection<IAgent> on) {
		if (on == null) {
			Arrays.fill(open, true);
		} else {
			Arrays.fill(open, false);
			for (final IAgent ag : on) {
				open[ag.getIndex()] = true;
			}
		}
	}

	private PriorityQueue newPriorityQueue() {
		final Comparator<List> comparator = (o1, o2) -> ((Double) o1.get(1)).compareTo((Double) o2.get(1));

		return new PriorityQueue(comparator);
	}

	@Override
	public final IAgent getAgentAt(final ILocation c) {
		final IShape g = getPlaceAt(c);
		if (g == null) return null;
		return g.getAgent();
	}

	@Override
	public void setCellSpecies(final IPopulation pop) {
		cellSpecies = pop.getSpecies();
	}

	@Override
	public ISpecies getCellSpecies() {
		return cellSpecies;
	}

	@Override
	public Boolean isHexagon() {
		return isHexagon;
	}

	@Override
	public Boolean isHorizontalOrientation() {
		return isHorizontalOrientation;
	}

	@Override
	public List<IAgent> getAgents() {
		if (matrix == null) return Collections.EMPTY_LIST;
		// Later, do return Arrays.asList(matrix);
		final List<IAgent> agents = GamaListFactory.create(Types.AGENT);
		for (final IShape element : matrix) {
			if (element != null) { agents.add(element.getAgent()); }
		}
		return agents;
	}

	public Object[] getMatrix() {
		return matrix;
	}

	@Override
	public void insert(final IAgent a) {}

	@Override
	public void remove(final Envelope3D previous, final IAgent a) {}

	//
	@Override
	public Set<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		try {
			env.expandBy(exp);
			final Set<IAgent> result = allInEnvelope(scope, source, env, f, false);
			result.removeIf(each -> source.euclidianDistanceTo(each) >= dist);
			return result;
		} finally {
			env.dispose();
		}
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		try {
			env.expandBy(exp);
			final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
			final Set<IAgent> shapes = allInEnvelope(scope, source, env, f, false);
			if (shapes.isEmpty()) return null;
			return ordering.min(shapes);
		} finally {
			env.dispose();
		}
	}

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		try {
			env.expandBy(exp);
			final Set<IAgent> shapes = allInEnvelope(scope, source, env, f, false);
			shapes.removeAll(alreadyChosen);
			if (shapes.size() <= number) return shapes;
			final boolean gridSpe = f.getSpecies() != null && f.getSpecies().isGrid();
			final Ordering<IShape> ordering =
					gridSpe ? Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input.getLocation()))
							: Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
			return ordering.leastOf(shapes, number);
		} finally {
			env.dispose();
		}
	}

	private Set<IAgent> inEnvelope(final Envelope env) {
		// TODO Is it really efficient?
		final Set<IAgent> shapes = new LinkedHashSet();
		int minX = 0;
		int minY = 0;
		int maxX = numCols - 1;
		int maxY = numRows - 1;

		if (this.isHexagon) {
			if (this.isHorizontalOrientation) {
				minX = max(0, (int) (env.getMinX() / (cellWidth / 0.75)));
				minY = max(0, (int) (env.getMinY() / cellHeight));
				maxX = min(numCols - 1, (int) (env.getMaxX() / (cellWidth * 0.75)));
				maxY = min(numRows - 1, (int) (env.getMaxY() / cellHeight));
			} else {
				minX = max(0, (int) (env.getMinX() / cellWidth));
				minY = max(0, (int) (env.getMinY() / (cellHeight / 0.75)));
				maxX = min(numCols - 1, (int) (env.getMaxX() / cellWidth));
				maxY = min(numRows - 1, (int) (env.getMaxY() / (cellHeight * 0.75)));
			}
		} else {
			minX = max(0, (int) (env.getMinX() / cellWidth));
			minY = max(0, (int) (env.getMinY() / cellHeight));
			maxX = min(numCols - 1, (int) (env.getMaxX() / cellWidth));
			maxY = min(numRows - 1, (int) (env.getMaxY() / cellHeight));
		}
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				final int index = getPlaceIndexAt(i, j);
				// BUGFIX AD 28/01/13 Changed "1" into "-1"
				if (index != -1) {
					final IAgent ag = matrix[index].getAgent();
					if (ag != null) { shapes.add(ag); }
				}
			}
		}

		return shapes;

	}

	@Override
	public Set<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope env, final IAgentFilter f,
			final boolean covered) {
		// scope.getGui().debug("GamaSpatialMatrix.allInEnvelope");
		// if ( !f.filterSpecies(cellSpecies) ) { return
		// Iterators.emptyIterator(); }
		final Set<IAgent> shapes = inEnvelope(env);
		shapes.remove(source);

		shapes.removeIf(each -> {
			final Envelope3D e = each.getEnvelope();
			return each.getAgent() == null || !(covered ? env.covers(e) : env.intersects(e));
		});

		if (f != null) { f.filter(scope, source, shapes); }
		return shapes;
	}
	//
	// @Override
	// public void drawOn(final Graphics2D g2, final int width, final int
	// height) {}

	/**
	 * Method isTorus()
	 *
	 * @see msi.gama.metamodel.topology.grid.IGrid#isTorus()
	 */
	@Override
	public boolean isTorus() {
		return isTorus;
	}

	/**
	 * Method getEnvironmentFrame()
	 *
	 * @see msi.gama.metamodel.topology.grid.IGrid#getEnvironmentFrame()
	 */
	@Override
	public IShape getEnvironmentFrame() {
		return environmentFrame;
	}

	private class CellProxyGeometry extends GamaProxyGeometry {

		public CellProxyGeometry(final ILocation loc) {
			super(loc);
		}

		@Override
		public void setGeometricalType(final Type t) {}

		/**
		 * Method getReferenceGeometry(). Directly refers to the reference shape declared by the matrix.
		 *
		 * @see msi.gama.metamodel.shape.GamaProxyGeometry#getReferenceGeometry()
		 */
		@Override
		protected IShape getReferenceGeometry() {
			return referenceShape;
		}

		@Override
		public IAgent getAgent() {
			// Gather the object stored in the matrix at this object location
			final IShape s = getPlaceAt(getLocation());
			// If it is this object, we are dealing with a non-agent grid.
			if (s == this) return null;
			// Otherwise, we return the agent associated to this object.
			return s.getAgent();
		}

		@Override
		public void setDepth(final double depth) {
			// TODO Auto-generated method stub

		}

		/**
		 * Method getGeometries()
		 *
		 * @see msi.gama.metamodel.shape.IShape#getGeometries()
		 */
		@Override
		public IList<? extends IShape> getGeometries() {

			final IList<IShape> result = GamaListFactory.create(Types.GEOMETRY);
			if (isMultiple()) {
				final Geometry g = getInnerGeometry();
				for (int i = 0, n = g.getNumGeometries(); i < n; i++) {
					result.add(new GamaShape(g.getGeometryN(i)));
				}
			} else {
				result.add(this);
			}
			return result;

		}

		/**
		 * Method isMultiple()
		 *
		 * @see msi.gama.metamodel.shape.IShape#isMultiple()
		 */
		@Override
		public boolean isMultiple() {
			return getReferenceGeometry().isMultiple();
		}

	}

	/**
	 * Class GridPopulation.
	 *
	 * @author drogoul
	 * @since 14 mai 2013
	 *
	 */
	public class GridPopulation<G extends IAgent> extends GamaPopulation<G> {

		public GridPopulation(final ITopology t, final IMacroAgent host, final ISpecies species) {
			super(host, species);
			topology = t;
		}

		@Override
		public Stream<G> stream() {
			return (Stream<G>) StreamEx.of(matrix);
		}

		@Override
		public StreamEx<G> stream(final IScope scope) {
			return (StreamEx<G>) StreamEx.of(matrix);
		}

		@Override
		public IList<G> createAgents(final IScope scope, final int number,
				final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
				final boolean toBeScheduled, final RemoteSequence sequence) throws GamaRuntimeException {

			createAgents(scope, null, sequence);
			for (final Map attr : initialValues) {
				final IAgent agt = getAgent((Integer) attr.get("grid_x"), (Integer) attr.get("grid_y"));
				agt.setExtraAttributes(attr);
			}
			return (IList) getAgents(scope);
		}

		@Override
		public IList<G> createAgents(final IScope scope, final IContainer<?, ? extends IShape> geometries,
				final RemoteSequence sequence) {
			for (int i = 0; i < actualNumberOfCells; i++) {
				final IShape s = matrix[i];
				final Class javaBase = species.getDescription().getJavaBase();

				final boolean usesRegularAgents = GamlAgent.class.isAssignableFrom(javaBase);
				if (s != null) {
					final IAgent g = usesRegularAgents ? new GamlGridAgent(i) : new MinimalGridAgent(i);
					matrix[i] = g;
				}
			}

			for (final String s : orderedVarNames) {
				final IVariable var = species.getVar(s);
				for (int i = 0; i < actualNumberOfCells; i++) {
					final IAgent a = (IAgent) matrix[i];
					if (a != null) { var.initializeWith(scope, a, null); }
				}
			}

			for (int i = 0; i < actualNumberOfCells; i++) {
				final IAgent a = (IAgent) matrix[i];
				if (a != null) { a.schedule(scope); }
			}
			this.fireAgentsAdded(scope, (IList) getAgents(scope));
			return null;

		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			return getName();
		}

		@Override
		protected boolean stepAgents(final IScope scope) {
			return GamaExecutorService.step(scope, matrix, getSpecies());
		}

		public int getNbCols() {
			return GamaSpatialMatrix.this.numCols;
		}

		public int getNbRows() {
			return GamaSpatialMatrix.this.numRows;
		}

		public IAgent getAgent(final Integer col, final Integer row) {
			if (col >= getNbCols() || col < 0 || row >= getNbRows() || row < 0) return null;
			final IShape s = GamaSpatialMatrix.this.get(null, col, row);
			return s == null ? null : s.getAgent();
		}

		public Double getGridValue(final Integer col, final Integer row) {
			if (col >= getNbCols() || col < 0 || row >= getNbRows() || row < 0) return 0.0;
			return GamaSpatialMatrix.this.getGridValue(col, row);
		}

		@Override
		public G getAgent(final Integer index) {
			if (index >= size() || index < 0) return null;
			final IShape s = GamaSpatialMatrix.this.matrix[index];
			return (G) (s == null ? null : s.getAgent());
		}

		@Override
		public boolean isGrid() {
			return true;
		}

		@Override
		public void initializeFor(final IScope scope) throws GamaRuntimeException {
			topology.initialize(scope, this);
			// scope.getGui().debug("GamaSpatialMatrix.GridPopulation.initializeFor
			// : size " + size());
		}

		@Override
		public G getAgent(final IScope scope, final ILocation coord) {
			return (G) GamaSpatialMatrix.this.getAgentAt(coord);
		}

		@Override
		protected void computeTopology(final IScope scope) throws GamaRuntimeException {
			// Topology is already known. Nothing to do
		}

		@Override
		public void killMembers() throws GamaRuntimeException {
			for (final IShape a : GamaSpatialMatrix.this.matrix) {
				if (a != null) { a.dispose(); }
			}
		}

		@Override
		public synchronized G[] toArray() {
			return (G[]) Arrays.copyOf(matrix, matrix.length, IAgent[].class);
		}

		@Override
		public int size() {
			return actualNumberOfCells;
		}

		@Override
		public G getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
			if (indices == null) return null;
			final int n = indices.length(scope);
			if (n == 0) return null;
			final int x = Cast.asInt(scope, indices.get(scope, 0));
			if (n == 1) return getAgent(Cast.asInt(scope, x));
			final int y = Cast.asInt(scope, indices.get(scope, 1));
			final IShape s = GamaSpatialMatrix.this.get(scope, x, y);
			if (s == null) return null;
			return (G) s.getAgent();
		}

		@Override
		public G get(final IScope scope, final Integer index) throws GamaRuntimeException {
			// WARNING False if the matrix is not dense
			return (G) matrix[index];
		}

		// @Override
		// public boolean contains(final IScope scope, final Object o) throws
		// GamaRuntimeException {
		// return _contains(scope, o);
		// }

		@Override
		public G firstValue(final IScope scope) throws GamaRuntimeException {
			return (G) _first(scope);
		}

		@Override
		public G lastValue(final IScope scope) throws GamaRuntimeException {
			return (G) _last(scope);
		}

		@Override
		public int length(final IScope scope) {
			return actualNumberOfCells;
		}

		@Override
		public G anyValue(final IScope scope) {
			return (G) GamaSpatialMatrix.this.anyValue(scope);
		}

		@Override
		public Iterator<G> iterator() {
			return JavaUtils.iterator(matrix);
		}

		@Override
		public boolean containsKey(final IScope scope, final Object o) {
			if (o instanceof Integer) return super.containsKey(scope, o);
			if (o instanceof GamaPoint) return GamaSpatialMatrix.this.containsKey(scope, o);
			return false;
		}

		@Override
		public java.lang.Iterable<G> iterable(final IScope scope) {
			return listValue(scope, Types.NO_TYPE, false); // TODO Types.AGENT
			// ??
		}

		@Override
		public boolean isEmpty() {
			return _isEmpty(null);
		}

		@Override
		public boolean isEmpty(final IScope scope) {
			return _isEmpty(scope);
		}

		@Override
		public IList<G> listValue(final IScope scope, final IType contentsType, final boolean copy)
				throws GamaRuntimeException {
			return _listValue(scope, contentsType, false);
		}

		@Override
		public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
				throws GamaRuntimeException {
			if (contentsType == null || contentsType.id() == IType.NONE
					|| contentsType.getSpeciesName() != null
							&& contentsType.getSpeciesName().equals(getSpecies().getName()))
				return GamaSpatialMatrix.this;
			return GamaSpatialMatrix.this.matrixValue(scope, contentsType, copy);
		}

		@Override
		public IMatrix matrixValue(final IScope scope, final IType type, final ILocation size, final boolean copy)
				throws GamaRuntimeException {

			if (type == null || type.id() == IType.NONE
					|| type.getSpeciesName() != null && type.getSpeciesName().equals(getSpecies().getName()))
				return GamaSpatialMatrix.this;
			return GamaSpatialMatrix.this.matrixValue(scope, type, copy);
		}

		public class GamlGridAgent extends GamlAgent implements IGridAgent {

			// WARNING HACK TO ACCELERATE SOME OF THE OPERATIONS OF GRIDS
			// WARNING THE PROBLEM IS THAT THESE AGENTS ARE BREAKING THE
			// HIERARCHY

			public GamlGridAgent(final int index) {
				super(GridPopulation.this, index, matrix[index].getGeometry());
				// setIndex(index);
				// geometry = matrix[getIndex()].getGeometry(); // TODO Verify
				// this
			}

			@Override
			public GamaColor getColor() {
				if (isHexagon) return (GamaColor) getAttribute(IKeyword.COLOR);
				return GamaColor.getInt(supportImagePixels[getIndex()]);
			}

			@Override
			public void setColor(final GamaColor color) {
				if (isHexagon) {
					setAttribute(IKeyword.COLOR, color);
				} else {
					supportImagePixels[getIndex()] = color.getRGB();
				}
			}

			@Override
			public final int getX() {
				if (isHexagon()) return GamaSpatialMatrix.this.getX(getGeometry());
				return (int) (getLocation().getX() / cellWidth);
			}

			@Override
			public final int getY() {
				if (isHexagon()) return GamaSpatialMatrix.this.getY(getGeometry());
				return (int) (getLocation().getY() / cellHeight);
			}

			@Override
			public double getValue() {
				if (gridValue != null) return gridValue[getIndex()];
				return 0d;
			}

			@Override
			public IList<Double> getBands() {
				if (nbBands == 1) {
					final IList bd = GamaListFactory.create(null, Types.FLOAT);
					bd.add(getValue());
					return bd;
				}
				return bands.get(getIndex());
			}

			@Override
			public void setValue(final double d) {
				if (gridValue != null) { gridValue[getIndex()] = d; }
			}

			@Override
			public IPopulation getPopulation() {
				return GridPopulation.this;
			}

			@Override
			public IList<IAgent> getNeighbors(final IScope scope) {
				return Cast.asList(scope, getNeighborhood().getNeighborsIn(scope, getIndex(), 1));
			}

		}

		public class MinimalGridAgent extends AbstractAgent implements IGridAgent {

			private final IShape geometry;

			public MinimalGridAgent(final int index) {
				super(index);
				geometry = matrix[index].getGeometry();
			}

			@Override
			public GamaColor getColor() {
				if (isHexagon) return (GamaColor) getAttribute(IKeyword.COLOR);
				return GamaColor.getInt(supportImagePixels[getIndex()]);
			}

			@Override
			public void setColor(final GamaColor color) {
				if (isHexagon) {
					setAttribute(IKeyword.COLOR, color);
				} else {
					// image.setRGB(getX(), getY(), color.getRGB());

					supportImagePixels[getIndex()] = color.getRGB();
				}
			}

			@Override
			public void setGeometricalType(final Type t) {}

			@Override
			public final int getX() {
				if (isHexagon()) return GamaSpatialMatrix.this.getX(getGeometry());
				return (int) (getLocation().getX() / cellWidth);
			}

			@Override
			public final int getY() {
				if (isHexagon()) return GamaSpatialMatrix.this.getY(getGeometry());
				return (int) (getLocation().getY() / cellHeight);
			}

			@Override
			public double getValue() {
				if (gridValue != null) return gridValue[getIndex()];
				return 0d;
			}

			@Override
			public void setValue(final double d) {
				if (gridValue != null) { gridValue[getIndex()] = d; }
			}

			@Override
			public IPopulation getPopulation() {
				return GridPopulation.this;
			}

			@Override
			public IShape getGeometry() {
				return geometry;
			}

			@Override
			public IList<IAgent> getNeighbors(final IScope scope) {
				return Cast.asList(scope, getNeighborhood().getNeighborsIn(scope, getIndex(), 1));
			}

			/**
			 * Method getPoints()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getPoints()
			 */
			@Override
			public IList<? extends ILocation> getPoints() {
				return geometry.getPoints();
			}

			@Override
			public void setDepth(final double depth) {
				// TODO Auto-generated method stub

			}

			/**
			 * Method getArea()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getArea()
			 */
			@Override
			public Double getArea() {
				return geometry.getArea();
			}

			/**
			 * Method getVolume()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getVolume()
			 */
			@Override
			public Double getVolume() {
				return geometry.getVolume();
			}

			/**
			 * Method getPerimeter()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
			 */
			@Override
			public double getPerimeter() {
				return geometry.getPerimeter();
			}

			/**
			 * Method getHoles()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getHoles()
			 */
			@Override
			public IList<GamaShape> getHoles() {
				return geometry.getHoles();
			}

			/**
			 * Method getCentroid()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getCentroid()
			 */
			@Override
			public GamaPoint getCentroid() {
				return geometry.getCentroid();
			}

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
			public Double getWidth() {
				return geometry.getWidth();
			}

			/**
			 * Method getHeight()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getHeight()
			 */
			@Override
			public Double getHeight() {
				return geometry.getHeight();
			}

			/**
			 * Method getDepth()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getDepth()
			 */
			@Override
			public Double getDepth() {
				return geometry.getDepth();
			}

			/**
			 * Method getGeometricEnvelope()
			 *
			 * @see msi.gama.metamodel.shape.IShape#getGeometricEnvelope()
			 */
			@Override
			public GamaShape getGeometricEnvelope() {
				return geometry.getGeometricEnvelope();
			}

			@Override
			public IList<? extends IShape> getGeometries() {
				return geometry.getGeometries();
			}

			/**
			 * Method isMultiple()
			 *
			 * @see msi.gama.metamodel.shape.IShape#isMultiple()
			 */
			@Override
			public boolean isMultiple() {
				return geometry.isMultiple();
			}

			@Override
			public IList<Double> getBands() {
				if (nbBands == 1) {
					final IList bd = GamaListFactory.create(null, Types.FLOAT);
					bd.add(getValue());
					return bd;
				}
				return bands.get(getIndex());
			}

		}

	}

	/**
	 * Method usesNeighborsCache()
	 *
	 * @see msi.gama.metamodel.topology.grid.IGrid#usesNeighborsCache()
	 */
	@Override
	public boolean usesNeighborsCache() {
		return useNeighborsCache;
	}

	@Override
	public IShape getNthElement(final Integer index) {
		if (index == null) return null;
		if (index > lastCell) return null;
		return matrix[index];
	}

	@Override
	protected void setNthElement(final IScope scope, final int index, final Object value) {}

	@Override
	public Collection<IAgent> allAgents() {
		return this.getAgents();
	}

	@Override
	public StreamEx<IShape> stream(final IScope scope) {
		return StreamEx.of(matrix);
	}

	@Override
	public String optimizer() {
		return optimizer;
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	/**
	 * Inherited from IFieldMatrixProvider
	 */

	@Override
	public double[] getBand(final IScope scope, final int index) {
		if (index == 0) return getFieldData(scope);
		double[] result = new double[bands.size()];
		int i = 0;
		for (List<Double> ll : bands) {
			result[i++] = ll.get(index);
		}
		return result;
	}

	@Override
	public double[] getFieldData(final IScope scope) {
		return gridValue;
	}

	/**
	 * Inherited from IDiffusionTarget
	 */

	@Override
	public int getNbNeighbours() {
		// AD: And hex ?
		return getNeighborhood().isVN() ? 4 : 8;
	}

	@Override
	public double getValueAtIndex(final IScope scope, final int i, final String varName) {
		IAgent a = matrix[i].getAgent();
		return Cast.asFloat(scope, a.getDirectVarValue(scope, varName));
	}

	@Override
	public void setValueAtIndex(final IScope scope, final int i, final String varName, final double valToPut) {
		IAgent a = matrix[i].getAgent();
		a.setDirectVarValue(scope, varName, valToPut);
	}

	@Override
	public void getValuesInto(final IScope scope, final String varName, final double minValue, final double[] input) {
		for (int i = 0; i < input.length; i++) {
			double val = Cast.asFloat(scope, getValueAtIndex(scope, i, varName));
			input[i] = val < minValue ? 0 : val;
		}
	}

}
