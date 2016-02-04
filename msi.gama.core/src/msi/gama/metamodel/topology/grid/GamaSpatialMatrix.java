/*********************************************************************************************
 *
 *
 * 'GamaSpatialMatrix.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.*;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.matrix.*;
import msi.gama.util.path.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;

/**
 * This matrix contains geometries and can serve to organize the agents of a population as a grid in
 * the environment, or as a support for grid topologies
 */
public class GamaSpatialMatrix extends GamaMatrix<IShape> implements IGrid {

	/** The geometry of host. */

	public final boolean useIndividualShapes;
	public boolean useNeighboursCache = false;

	public final IShape environmentFrame;
	final Envelope bounds;
	final double precision;
	protected IShape[] matrix;

	double cellWidth, cellHeight;
	public int[] supportImagePixels;
	public double[] gridValue;
	protected Boolean usesVN = null;
	protected Boolean isTorus = null;
	protected Boolean isHexagon = null;
	protected GridDiffuser_deprecated diffuser_deprecated; // this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.
	protected GridDiffuser diffuser;
	public INeighbourhood neighbourhood;

	int actualNumberOfCells;
	int firstCell, lastCell;
	// UnmodifiableIterator<? extends IShape> iterator = null;
	private ISpecies cellSpecies;
	// private IAgentFilter cellFilter;

	Map hexAgentToLoc = null;

	private final IShape referenceShape;

	@Override
	public void dispose() {
		neighbourhood = null;
		gridValue = null;
		matrix = null;
		diffuser = null;
		diffuser_deprecated = null;
	}

	public IContainerType getPreciseType(final IScope scope) {
		if ( cellSpecies == null ) {
			return super.getType();
		} else {
			return Types.MATRIX.of(scope.getModelContext().getTypeNamed(cellSpecies.getName()));
		}
	}

	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN, final boolean indiv, final boolean useNeighboursCache)
			throws GamaRuntimeException {
		super(cols, rows, Types.GEOMETRY);
		// scope.getGui().debug("GamaSpatialMatrix.GamaSpatialMatrix create new");
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		precision = bounds.getWidth() / 1000;
		final int size = numRows * numCols;
		createMatrix(size);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = usesVN;
		actualNumberOfCells = 0;
		referenceShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		firstCell = -1;
		lastCell = -1;
		this.isHexagon = false;
		useIndividualShapes = indiv;
		this.useNeighboursCache = useNeighboursCache;
		createCells(scope, false);
	}

	public GamaSpatialMatrix(final IScope scope, final GamaGridFile gfile, final boolean isTorus, final boolean usesVN,
		final boolean indiv, final boolean useNeighboursCache) throws GamaRuntimeException {
		super(100, 100, Types.GEOMETRY);
		// scope.getGui().debug("GamaSpatialMatrix.GamaSpatialMatrix create new");
		numRows = gfile.getNbRows(scope);
		numCols = gfile.getNbCols(scope);
		environmentFrame = gfile.getGeometry(scope);
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
		this.useNeighboursCache = useNeighboursCache;

		for ( int i = 0; i < size; i++ ) {
			final IShape g = gfile.get(scope, i);
			gridValue[i] = (Double) g.getAttribute("grid_value");
			// WARNING A bit overkill as we only use the GamaGisGeometry for its attribute...
			// matrix[i] = g;
		}
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		createCells(scope, false);
	}

	// constructor used to build hexagonal grid (-> useVN = false)
	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon, final boolean indiv,
		final boolean useNeighboursCache) {
		super(cols, rows, Types.GEOMETRY);
		// scope.getGui().debug("GamaSpatialMatrix.GamaSpatialMatrix create new");
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		// TODO False
		referenceShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		precision = bounds.getWidth() / 1000;
		final int size = 2 * numRows * numCols;
		createMatrix(size);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = false;
		this.isHexagon = isHexagon;
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		useIndividualShapes = indiv;
		this.useNeighboursCache = useNeighboursCache;
		createHexagons(false);
	}

	private void createMatrix(final int size) {
		matrix = new IShape[size];
		gridValue = new double[size];
	}

	private void createHexagons(final boolean partialCells) {
		final double widthEnv = environmentFrame.getEnvelope().getWidth();
		final double heightEnv = environmentFrame.getEnvelope().getHeight();
		double xmin = environmentFrame.getEnvelope().getMinX();
		double ymin = environmentFrame.getEnvelope().getMinY();
		final GamaShape gbg = new GamaShape(environmentFrame.getInnerGeometry().buffer(0.1, 2));
		cellWidth = widthEnv / (numCols * 0.75 + 0.25);
		cellHeight = heightEnv / (numRows + 0.5);
		xmin += cellWidth / 2.0;
		ymin += cellHeight / 2.0;
		// numCols = (int) (width / cellWidth);
		hexAgentToLoc = new TOrderedHashMap();
		int i = 0;
		for ( int l = 0; l < numRows; l++ ) {
			for ( int c = 0; c < numCols; c = c + 2 ) {
				i = c + numRows * l;
				final GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight,
					new GamaPoint(xmin + c * cellWidth * 0.75, ymin + l * cellHeight));
				if ( gbg.covers(poly) ) {
					if ( firstCell == -1 ) {
						firstCell = i;
					}
					matrix[i] = poly;
					hexAgentToLoc.put(poly, new GamaPoint(c, l));
					actualNumberOfCells++;
					lastCell = Math.max(lastCell, i);
				}
			}
		}
		for ( int l = 0; l < numRows; l++ ) {
			for ( int c = 1; c < numCols; c = c + 2 ) {
				i = c + numRows * l;
				final GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight,
					new GamaPoint(xmin + c * cellWidth * 0.75, ymin + (l + 0.5) * cellHeight));
				if ( gbg.covers(poly) ) {
					if ( firstCell == -1 ) {
						firstCell = i;
					}
					matrix[i] = poly;
					hexAgentToLoc.put(poly, new GamaPoint(c, l));
					actualNumberOfCells++;
					lastCell = Math.max(lastCell, i);
				}
			}
		}
	}

	private void createCells(final IScope scope, final boolean partialCells) throws GamaRuntimeException {
		final boolean isRectangle = environmentFrame.getInnerGeometry().isRectangle();
		final GamaPoint p = new GamaPoint(0, 0);
		final GamaPoint origin =
			new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope().getMinY());

		final IShape translatedReferenceFrame = Spatial.Transformations.translated_by(scope, environmentFrame, origin);

		final double cmx = cellWidth / 2;
		final double cmy = cellHeight / 2;
		for ( int i = 0, n = numRows * numCols; i < n; i++ ) {
			final int yy = i / numCols;
			final int xx = i - yy * numCols;
			p.x = xx * cellWidth + cmx;
			p.y = yy * cellHeight + cmy;
			// WARNING HACK
			IShape rect = null;
			if ( useIndividualShapes ) {
				rect = GamaGeometryType.buildRectangle(cellWidth, cellHeight, p);
			} else {
				rect = new CellProxyGeometry(p.copy(scope));
			}
			boolean ok = isRectangle || translatedReferenceFrame.covers(rect);
			if ( partialCells && !ok && rect.intersects(translatedReferenceFrame) ) {
				rect.setGeometry(Spatial.Operators.inter(scope, rect, translatedReferenceFrame));
				ok = true;
			}
			if ( ok ) {
				if ( firstCell == -1 ) {
					firstCell = i;
				}
				// scope.getGui().debug("GamaSpatialMatrix.createCells: " + rect.getLocation() + " at " + xx + ";" + yy);
				matrix[i] = rect;
				actualNumberOfCells++;
				lastCell = i;
			}
		}
	}

	@Override
	public INeighbourhood getNeighbourhood() {
		if ( neighbourhood == null ) {
			if ( useNeighboursCache ) {
				neighbourhood = isHexagon ? new GridHexagonalNeighbourhood()
					: usesVN ? new GridVonNeumannNeighbourhood() : new GridMooreNeighbourhood();
			} else {
				neighbourhood = new NoCacheNeighbourhood();
			}
		}
		return neighbourhood;
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
		double[] result = new double[matrix.length];
		for ( int i = 0; i < matrix.length; i++ ) {
			IShape s = matrix[i];
			if ( s != null ) {
				IAgent a = s.getAgent();
				if ( a != null ) {
					result[i] = Cast.asFloat(scope, scope.evaluate(exp, a));
				}
			}

		}
		return result;
	}

	public double getGridValue(final int col, final int row) {
		final int index = getPlaceIndexAt(col, row);
		if ( index != -1 ) { return gridValue[index]; }
		return 0.0;
	}

	private final int getPlaceIndexAt(final int xx, final int yy) {
		if ( isHexagon ) { return yy * numCols + xx; }
		if ( isTorus ) { return (yy < 0 ? yy + numCols : yy) % numRows * numCols +
			(xx < 0 ? xx + numCols : xx) % numCols; }
		if ( xx < 0 || xx >= numCols || yy < 0 || yy >= numRows ) {
			;
			return -1;
		}

		return yy * numCols + xx;
	}

	private final int getPlaceIndexAt(final ILocation p) {
		if ( isHexagon ) {
			final int xx = (int) (p.getX() / (cellWidth * 0.75));
			final int yy = xx % 2 == 0 ? (int) (p.getY() / cellHeight) : (int) ((p.getY() - cellHeight) / cellHeight);

			int i = getPlaceIndexAt(xx, yy);
			if ( matrix[i] == null ) { return -1; }
			if ( matrix[i].getLocation() == p ) { return i; }
			final TIntHashSet toObserve =
				((GridHexagonalNeighbourhood) getNeighbourhood()).getNeighboursAtRadius1(i, numCols, numRows, isTorus);
			toObserve.add(i);
			double dMin = Double.MAX_VALUE;
			int x = 0, y = 0;
			TIntIterator it = toObserve.iterator();
			while (it.hasNext()) {
				int id = it.next();

				final IShape sh = matrix[id];
				if ( sh == null ) {
					continue;
				}
				final double dist = sh.getLocation().euclidianDistanceTo(p);
				if ( dist < dMin ) {
					dMin = dist;
					final GamaPoint pt = (GamaPoint) hexAgentToLoc.get(sh.getGeometry());
					x = (int) pt.x;
					y = (int) pt.y;
				}

			}
			i = getPlaceIndexAt(x, y);
			return i;

		}
		final double px = p.getX();
		final double py = p.getY();
		final double xx = px == bounds.getMaxX() ? (px - precision) / cellWidth : px / cellWidth;
		final double yy = py == bounds.getMaxY() ? (py - precision) / cellHeight : py / cellHeight;
		final int x = (int) xx;
		final int y = (int) yy;
		final int i = getPlaceIndexAt(x, y);
		return i;
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
		if ( c == null ) { return null; }
		final int p = getPlaceIndexAt(c);
		if ( p == -1 ) { return null; }
		return matrix[p];
	}

	private void diffuse_deprecated(final IScope scope) throws GamaRuntimeException {
		// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.
		diffuser_deprecated.diffuse_deprecated(scope);
	}

	private void diffuse(final IScope scope) throws GamaRuntimeException {
		diffuser.diffuse();
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		// TODO not allowed for the moment (fixed grid)
		//
	}

	@Override
	public IShape get(final IScope scope, final int col, final int row) {
		final int index = getPlaceIndexAt(col, row);
		if ( index != -1 ) { return matrix[index]; }
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
	}

	@Override
	protected IList _listValue(final IScope scope, final IType contentType, final boolean cast) {
		if ( actualNumberOfCells == 0 ) { return GamaListFactory.EMPTY_LIST; }
		if ( cellSpecies == null ) {

			return cast ? GamaListFactory.create(scope, contentType, matrix)
				: GamaListFactory.createWithoutCasting(contentType, matrix);
		} else {
			final IList result = GamaListFactory.create(contentType, actualNumberOfCells);
			for ( final IShape a : matrix ) {
				if ( a != null ) {
					final IAgent ag = a.getAgent();
					Object toAdd = ag == null ? a : ag;
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
		if ( firstCell == -1 ) { return null; }
		return matrix[firstCell];
	}

	@Override
	public IShape _last(final IScope scope) {
		if ( lastCell == -1 ) { return null; }
		return matrix[lastCell];
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatrix copy(final IScope scope, final ILocation size, final boolean copy) throws GamaRuntimeException {
		if ( size == null && !copy ) { return this; }
		return new GamaSpatialMatrix(scope, environmentFrame, numCols, numRows, isTorus, usesVN, useIndividualShapes,
			useNeighboursCache);
	}

	@Override
	public boolean _contains(final IScope scope, final Object o) {
		if ( cellSpecies != null ) { return cellSpecies.contains(scope, o); }
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
	 * @see msi.gama.metamodel.topology.grid.IGrid#usesIndiviualShapes()
	 */
	@Override
	public boolean usesIndiviualShapes() {
		return useIndividualShapes;
	}

	@Override
	public int manhattanDistanceBetween(final IShape g1, final IShape g2) {
		// New algorithm : we get the cells at the nearest points and compute the distance between their centroids ?
		IShape s1 = g1.getAgent() != null && g1.getAgent().getSpecies() == this.getCellSpecies() ? g1 : null;
		IShape s2 = g2.getAgent() != null && g2.getAgent().getSpecies() == this.getCellSpecies() ? g2 : null;

		if ( s1 == null || s2 == null ) {
			ILocation p1 = g1.isPoint() ? g1.getLocation() : null;
			ILocation p2 = g2.isPoint() ? g2.getLocation() : null;
			final Coordinate[] coord = new DistanceOp(g1.getInnerGeometry(), g2.getInnerGeometry()).nearestPoints();
			if ( s1 == null ) {
				p1 = new GamaPoint(coord[0]);
				s1 = this.getPlaceAt(p1);
			}
			if ( s2 == null ) {
				p2 = new GamaPoint(coord[1]);
				s2 = this.getPlaceAt(p2);
			}
		}

		final Coordinate[] coord = new DistanceOp(s1.getInnerGeometry(), s2.getInnerGeometry()).nearestPoints();
		final int dx = (int) (Maths.abs(coord[0].x - coord[1].x) / cellWidth) + 1;
		final int dy = (int) (Maths.abs(coord[0].y - coord[1].y) / cellHeight) + 1;
		if ( usesVN ) {
			int result = dx + dy;
			if ( result == 2 ) {
				double centroid_dx = Maths.abs(g2.getLocation().getX() - g1.getLocation().getX());
				double centroid_dy = Maths.abs(g2.getLocation().getY() - g1.getLocation().getY());
				if ( centroid_dx < cellWidth ) {
					result -= 1;
				}
				if ( centroid_dy < cellHeight ) {
					result -= 1;
				}
			}
			return result;
		}
		int result = Math.max(dx, dy);
		double centroid_dx = Maths.abs(g2.getLocation().getX() - g1.getLocation().getX());
		double centroid_dy = Maths.abs(g2.getLocation().getY() - g1.getLocation().getY());
		if ( centroid_dx < cellWidth && centroid_dy < cellHeight ) {
			result -= 1;
		}
		return result;
	}

	/**
	 * Returns the cells making up the neighbourhood of a geometrical shape. First, the
	 * cells covered by this shape are computed, then their neighbours are collated (excluding
	 * the previous ones). A special case is made for point geometries and for agents
	 * contained in this matrix.
	 * @param source
	 * @param distance
	 * @return
	 */
	@Override
	public Set<IAgent> getNeighboursOf(final IScope scope, final IShape shape, final Double distance,
		final IAgentFilter filter) {

		// If the shape is a point or if it is a cell of this matrix, we run the method with an ILocation instead
		if ( shape.isPoint() ||
			shape.getAgent() != null && shape.getAgent().getSpecies() == cellSpecies ) { return getNeighboursOf(scope,
				shape.getLocation(), distance, filter); }

		// We compute all the cells covered by the shape (we know that it is not a cell of the matrix) -- no filter used
		// here, as we must take all the cells into account
		final Set<IAgent> coveredPlaces = allInEnvelope(scope, shape, shape.getEnvelope(), null, true);
		// final Set<IAgent> placesToRemove = Sets.newHashSet(coveredPlaces);

		// We now compute all the cells that are at "distance" away from these covered cells
		final Set<IAgent> allPlaces = new LinkedHashSet();
		for ( IAgent ag : coveredPlaces ) {
			allPlaces.addAll(
				getNeighbourhood().getNeighboursIn(scope, getPlaceIndexAt(ag.getLocation()), distance.intValue()));
		}

		// And we filter these cells by removing those that are in the "interior cells" (which are not part of the
		// neighbourood) and that are not accepted by the IAgentFilter. A special case is made if the filter is only
		// accepting cells of this matrix : in that case, we simply remove the "interior cells" from the iterator and
		// we return it;

		allPlaces.removeAll(coveredPlaces);
		if ( filter != null ) {
			if ( filter.getSpecies() != cellSpecies ) {
				filter.filter(scope, shape, allPlaces);
			}
		}
		return allPlaces;
	}

	protected Set<IAgent> getNeighboursOf(final IScope scope, final ILocation shape, final Double distance,
		final IAgentFilter filter) {
		Set<IAgent> allPlaces = getNeighbourhood().getNeighboursIn(scope, getPlaceIndexAt(shape), distance.intValue());
		if ( filter != null ) {
			if ( filter.getSpecies() == cellSpecies ) { return allPlaces; }
			filter.filter(scope, shape, allPlaces);
		}
		return allPlaces;
	}

	IAgent testPlace(final IScope scope, final IShape source, final IAgentFilter filter, final IShape toTest) {
		List<IAgent> agents = new ArrayList<IAgent>(scope.getTopology().getAgentsIn(scope, toTest, filter, false));
		agents.remove(source);
		if ( agents == null || agents.isEmpty() ) { return null; }
		return (IAgent) scope.getRandom().shuffle(agents).get(0);
	}

	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter)
		throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		if ( filter.accept(scope, source, startAg) ) { return startAg; }
		IAgent agT = testPlace(scope, source, filter, startAg);
		if ( agT != null ) { return agT; }
		final List<IAgent> cells = new ArrayList<IAgent>();

		int cpt = 0;
		cells.add(startAg);
		final int max = this.numCols * this.numRows;
		List<IAgent> neighb =
			scope.getRandom().shuffle(getNeighborhoods(scope, startAg, cells, new ArrayList<IAgent>()));
		while (cpt < this.numCols * this.numRows) {
			cpt++;
			final Set<IAgent> neighb2 = new THashSet<IAgent>();
			for ( final IAgent ag : neighb ) {
				agT = testPlace(scope, source, filter, ag);
				if ( agT != null ) { return agT; }
				cells.add(ag);
				neighb2.addAll(getNeighborhoods(scope, ag, cells, neighb));

			}
			neighb = new ArrayList<IAgent>(neighb2);

		}
		return null;
	}

	private List<IAgent> getNeighborhoods(final IScope scope, final IAgent agent, final List cells,
		final List<IAgent> currentList) throws GamaRuntimeException {
		final List<IAgent> agents = new ArrayList(getNeighboursOf(scope, agent.getLocation(), 1.0, null));
		final List<IAgent> neighs = new ArrayList<IAgent>();
		for ( IAgent ag : agents ) {
			if ( !cells.contains(ag) && !currentList.contains(ag) && !neighs.contains(ag) ) {
				neighs.add(ag);
			}
		}
		return neighs;
	}

	@Override
	public GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
		final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException {
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();
		final IList<IAgent> nodes = GamaListFactory.create(Types.GEOMETRY);
		final int[] dists = new int[this.getAgents().size()];
		if ( startAg == endAg ) { return PathFactory.newInstance(scope, topo, nodes); }

		for ( IAgent ag : this.getAgents() ) {
			if ( on != null ) {
				dists[ag.getIndex()] = -2;
			} else {
				dists[ag.getIndex()] = -1;
			}
		}
		if ( on != null ) {
			for ( IAgent ag : on ) {
				dists[ag.getIndex()] = -1;
			}
		}

		int cpt = 0;
		dists[startAg.getIndex()] = 0;
		final int max = this.numCols * this.numRows;
		final Set<IAgent> agentsTmp = getNeighboursOf(scope, startAg.getLocation(), 1.0, null);
		Set<IAgent> neighb = new THashSet<IAgent>();
		for ( IAgent ag : agentsTmp ) {
			if ( dists[ag.getIndex()] == -1 ) {
				neighb.add(ag);
			}
		}
		while (true) {
			cpt++;
			final Set<IAgent> neighb2 = new THashSet<IAgent>();
			for ( final IAgent cel : neighb ) {
				if ( dists[cel.getIndex()] == -1 ) {
					dists[cel.getIndex()] = cpt;
					if ( cel == endAg ) {
						nodes.add(cel);
						IAgent cel2 = cel;
						while (cpt > 0) {
							cpt--;
							final List<IAgent> agsTmp = scope.getRandom()
								.shuffle(new ArrayList(getNeighboursOf(scope, cel2.getLocation(), 1.0, null)));
							double minDist = Double.MAX_VALUE;
							IAgent ca = null;
							for ( IAgent ag : agsTmp ) {
								if ( dists[ag.getIndex()] == cpt ) {
									double dist = ag.getLocation().euclidianDistanceTo(cel2.getLocation());
									if ( dist < minDist ) {
										ca = ag;
										minDist = dist;
									}
								}
							}
							cel2 = ca;
							nodes.add(cel2);
						}
						nodes.remove(startAg);
						IList<IShape> nodesPt = GamaListFactory.create(Types.GEOMETRY);
						for ( IAgent nd : nodes ) {
							nodesPt.add(nd.getLocation());
						}
						Collections.reverse(nodesPt);
						return PathFactory.newInstance(scope, topo, nodesPt);
					}
					final Set<IAgent> agentsTmp2 = getNeighboursOf(scope, cel.getLocation(), 1.0, null);
					for ( IAgent ag : agentsTmp2 ) {
						if ( dists[ag.getIndex()] == -1 ) {
							neighb2.add(ag);
						}
					}
				}
			}

			neighb = neighb2;
			if ( cpt > max ) { return null; }

		}
	}

	@Override
	public final IAgent getAgentAt(final ILocation c) {
		final IShape g = getPlaceAt(c);
		if ( g == null ) { return null; }
		return g.getAgent();
	}

	private GridDiffuser_deprecated getDiffuser_deprecated(final IScope scope) {
		// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.
		if ( diffuser_deprecated != null ) { return diffuser_deprecated; }
		diffuser_deprecated = new GridDiffuser_deprecated();
		scope.getExperiment().getActionExecuter().insertEndAction(new GamaHelper() {

			@Override
			public Object run(final IScope scope) throws GamaRuntimeException {
				diffuse_deprecated(scope);
				return null;
			}

		});
		return diffuser_deprecated;
	}

	private GridDiffuser getDiffuser(final IScope scope) {
		if ( diffuser != null ) { return diffuser; }
		diffuser = new GridDiffuser();
		scope.getExperiment().getActionExecuter().insertEndAction(new GamaHelper() {

			@Override
			public Object run(final IScope scope) throws GamaRuntimeException {
				if ( diffuser != null ) {
					diffuse(scope);
				}
				return null;
			}

		});
		return diffuser;
	}

	@Override
	public void diffuseVariable_deprecated(final IScope scope, final String name, final double value, final short type,
		final double prop, final double variation, final ILocation location, final double range,
		final Object candidates) {
		// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.
		getDiffuser_deprecated(scope).diffuseVariable(scope, name, value, type, prop, variation, location, range,
			candidates);
	}

	@Override
	public void diffuseVariable(final IScope scope, final boolean method_diffu, final boolean is_gradient,
		final double[][] mat_diffu, final double[][] mask, final String var_diffu, final IPopulation pop) {
		getDiffuser(scope).addDiffusion(scope, var_diffu, (GridPopulation) pop, method_diffu, is_gradient, mat_diffu,
			mask);
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
	public List<IAgent> getAgents() {
		if ( matrix == null ) { return Collections.EMPTY_LIST; }
		// Later, do return Arrays.asList(matrix);
		final List<IAgent> agents = GamaListFactory.create(Types.AGENT);
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] != null ) {
				agents.add(matrix[i].getAgent());
			}
		}
		return agents;
	}

	public Object[] getMatrix() {
		return matrix;
	}

	@Override
	public void insert(final IAgent a) {}

	@Override
	public void remove(final IShape previous, final IAgent a) {}

	//
	@Override
	public Set<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// scope.getGui().debug("GamaSpatialMatrix.allAtDistance");
		final double exp = dist * Maths.SQRT2;
		Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		Set<IAgent> result = allInEnvelope(scope, source, env, f, false);
		Iterator<IAgent> it = result.iterator();
		while (it.hasNext()) {
			if ( source.euclidianDistanceTo(it.next()) >= dist ) {
				it.remove();
			}
		}
		// return Iterators.filter(allInEnvelope(scope, source, ENVELOPE, f, false), new Predicate<IShape>() {
		//
		// // TODO Make it a static class
		// @Override
		// public boolean apply(final IShape input) {
		// return source.euclidianDistanceTo(input) < dist;
		// }
		// });
		return result;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// scope.getGui().debug("GamaSpatialMatrix.firstAtDistance");
		final double exp = dist * Maths.SQRT2;
		Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		// final Iterator<IShape> in_square = allInEnvelope(source, ENVELOPE, f, false);
		Ordering<IShape> ordering = Ordering.natural().onResultOf(new Function<IShape, Double>() {

			// TODO Make it a static class
			@Override
			public Double apply(final IShape input) {
				return source.euclidianDistanceTo(input);
			}
		});
		Set<IAgent> shapes = allInEnvelope(scope, source, env, f, false);
		if ( shapes.isEmpty() ) { return null; }
		return ordering.min(shapes);
		// let this throw NoSuchElementException as necessary
		// IShape minSoFar = shapes.next();
		// while (shapes.hasNext()) {
		// IShape next = shapes.next();
		// minSoFar = ordering.compare(minSoFar, next) <= 0 ? minSoFar : next;
		// }
		// return minSoFar;
	}

	private Set<IAgent> inEnvelope(final Envelope env) {
		// TODO Is it really efficient?
		final Set<IAgent> shapes = new LinkedHashSet();
		final int minX = (int) (env.getMinX() / cellWidth);
		final int minY = (int) (env.getMinY() / cellHeight);
		final int maxX = (int) (env.getMaxX() / cellWidth);
		final int maxY = (int) (env.getMaxY() / cellHeight);
		for ( int i = minX; i <= maxX; i++ ) {
			for ( int j = minY; j <= maxY; j++ ) {
				final int index = getPlaceIndexAt(i, j);
				// BUGFIX AD 28/01/13 Changed "1" into "-1"
				if ( index != -1 ) {
					IAgent ag = matrix[index].getAgent();
					if ( ag != null ) {
						shapes.add(ag);
					}
				}
			}
		}
		return shapes;

	}

	@Override
	public Set<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope env, final IAgentFilter f,
		final boolean covered) {
		// scope.getGui().debug("GamaSpatialMatrix.allInEnvelope");
		// if ( !f.filterSpecies(cellSpecies) ) { return Iterators.emptyIterator(); }
		Set<IAgent> shapes = inEnvelope(env);
		shapes.remove(source);
		Iterator<IAgent> it = shapes.iterator();
		while (it.hasNext()) {
			IShape s = it.next();
			Envelope3D e = s.getEnvelope();
			if ( s.getAgent() == null || !(covered ? env.covers(e) : env.intersects(e)) ) {
				it.remove();
			}
		}
		if ( f != null ) {
			f.filter(scope, source, shapes);
		}
		return shapes;
	}
	//
	// @Override
	// public void drawOn(final Graphics2D g2, final int width, final int height) {}

	/**
	 * Method isTorus()
	 * @see msi.gama.metamodel.topology.grid.IGrid#isTorus()
	 */
	@Override
	public boolean isTorus() {
		return isTorus;
	}

	/**
	 * Method getEnvironmentFrame()
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

		/**
		 * Method getReferenceGeometry(). Directly refers to the reference shape declared by the matrix.
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
			if ( s == this ) { return null; }
			// Otherwise, we return the agent associated to this object.
			return s.getAgent();
		}

		@Override
		public void setDepth(final double depth) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Class GridPopulation.
	 *
	 * @author drogoul
	 * @since 14 mai 2013
	 *
	 */
	public class GridPopulation extends GamaPopulation {

		boolean usesRegularAgents;

		/**
		 * @param host
		 * @param species
		 */
		public GridPopulation(final ITopology t, final IMacroAgent host, final ISpecies species,
			final boolean useRegularAgents) {
			super(host, species);
			usesRegularAgents = useRegularAgents;
			topology = t;
		}

		@Override
		public IList<? extends IAgent> createAgents(final IScope scope, final IContainer<?, IShape> geometries) {
			for ( int i = 0; i < actualNumberOfCells; i++ ) {
				final IShape s = matrix[i];
				if ( s != null ) {
					final IAgent g = usesRegularAgents ? new GamlGridAgent(i) : new MinimalGridAgent(i);
					matrix[i] = g;

					// g.scheduleAndExecute(null);
				}
			}

			for ( final String s : orderedVarNames ) {
				final IVariable var = species.getVar(s);
				for ( int i = 0; i < actualNumberOfCells; i++ ) {
					final IAgent a = (IAgent) matrix[i];
					if ( a != null ) {
						var.initializeWith(scope, a, null);
					}
				}
			}

			for ( int i = 0; i < actualNumberOfCells; i++ ) {
				final IAgent a = (IAgent) matrix[i];
				if ( a != null ) {
					a.schedule();
				}
			}
			// WARNING FOR THE MOMENT NO EVENT IS FIRED
			// fireAgentsAdded(list);
			// WARNING DOES NOT RESPECT THE CONTRACT (RETURNS NULL)
			return null;

		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			return getName();
		}

		@Override
		public boolean step(final IScope scope) throws GamaRuntimeException {
			final IExpression ags = getSpecies().getSchedule();
			if ( ags != null ) {
				// In case there is a schedule specified, we do the "normal" step
				return super.step(scope);
			}
			final int frequency = scheduleFrequency == null ? 1 : Cast.asInt(scope, scheduleFrequency.value(scope));
			final int step = scope.getClock().getCycle();
			if ( frequency == 0 || step % frequency != 0 ) { return true; }

			for ( final IShape s : matrix ) {
				if ( !scope.step((IAgent) s) ) { return false; }
			}
			return true;
		}

		public int getNbCols() {
			return GamaSpatialMatrix.this.numCols;
		}

		public int getNbRows() {
			return GamaSpatialMatrix.this.numRows;
		}

		public IAgent getAgent(final Integer col, final Integer row) {
			if ( col >= getNbCols() || col < 0 || row >= getNbRows() || row < 0 ) { return null; }
			final IShape s = GamaSpatialMatrix.this.get(null, col, row);
			return s == null ? null : s.getAgent();
		}

		public Double getGridValue(final Integer col, final Integer row) {
			if ( col >= getNbCols() || col < 0 || row >= getNbRows() || row < 0 ) { return 0.0; }
			return GamaSpatialMatrix.this.getGridValue(col, row);
		}

		@Override
		public IAgent getAgent(final Integer index) {
			if ( index >= size() || index < 0 ) { return null; }
			final IShape s = GamaSpatialMatrix.this.matrix[index];
			return s == null ? null : s.getAgent();
		}

		@Override
		public boolean isGrid() {
			return true;
		}

		@Override
		public void initializeFor(final IScope scope) throws GamaRuntimeException {
			topology.initialize(scope, this);
			// scope.getGui().debug("GamaSpatialMatrix.GridPopulation.initializeFor : size " + size());
		}

		@Override
		public IAgent getAgent(final IScope scope, final ILocation coord) {
			return GamaSpatialMatrix.this.getAgentAt(coord);
		}

		@Override
		protected void computeTopology(final IScope scope) throws GamaRuntimeException {
			// Topology is already known. Nothing to do
		}

		// @Override
		// public void killMembers() throws GamaRuntimeException {
		// for ( final IShape a : GamaSpatialMatrix.this.matrix ) {
		// if ( a != null ) {
		// a.dispose();
		// }
		// }
		// }

		@Override
		public synchronized IAgent[] toArray() {
			return Arrays.copyOf(matrix, matrix.length, IAgent[].class);
		}

		@Override
		public int size() {
			return actualNumberOfCells;
		}

		@Override
		public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
			if ( indices == null ) { return null; }
			int n = indices.length(scope);
			if ( n == 0 ) { return null; }
			int x = Cast.asInt(scope, indices.get(scope, 0));
			if ( n == 1 ) { return getAgent(Cast.asInt(scope, x)); }
			int y = Cast.asInt(scope, indices.get(scope, 1));
			IShape s = GamaSpatialMatrix.this.get(scope, x, y);
			if ( s == null ) { return null; }
			return s.getAgent();
		}

		@Override
		public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
			// WARNING False if the matrix is not dense
			return (IAgent) matrix[index];
		}

		// @Override
		// public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		// return _contains(scope, o);
		// }

		@Override
		public IAgent firstValue(final IScope scope) throws GamaRuntimeException {
			return (IAgent) _first(scope);
		}

		@Override
		public IAgent lastValue(final IScope scope) throws GamaRuntimeException {
			return (IAgent) _last(scope);
		}

		@Override
		public int length(final IScope scope) {
			return actualNumberOfCells;
		}

		@Override
		public IAgent anyValue(final IScope scope) {
			return (IAgent) GamaSpatialMatrix.this.anyValue(scope);
		}

		@Override
		public Iterator<IAgent> iterator() {
			return JavaUtils.iterator(matrix);
		}

		@Override
		public java.lang.Iterable<IAgent> iterable(final IScope scope) {
			return listValue(scope, Types.NO_TYPE, false); // TODO Types.AGENT ??
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
		public IList<IAgent> listValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
			return _listValue(scope, contentsType, false)/* .listValue(scope, contentsType, false); */;
		}

		@Override
		public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
			if ( contentsType == null || contentsType.id() == IType.NONE || contentsType.getSpeciesName() != null &&
				contentsType.getSpeciesName().equals(getSpecies().getName()) ) { return GamaSpatialMatrix.this; }
			return GamaSpatialMatrix.this.matrixValue(scope, contentsType, copy);
		}

		@Override
		public IMatrix matrixValue(final IScope scope, final IType type, final ILocation size, final boolean copy)
			throws GamaRuntimeException {

			if ( type == null || type.id() == IType.NONE || type.getSpeciesName() != null &&
				type.getSpeciesName().equals(getSpecies().getName()) ) { return GamaSpatialMatrix.this; }
			return GamaSpatialMatrix.this.matrixValue(scope, type, copy);
		}

		public class GamlGridAgent extends GamlAgent implements IGridAgent {

			// WARNING HACK TO ACCELERATE SOME OF THE OPERATIONS OF GRIDS
			// WARNING THE PROBLEM IS THAT THESE AGENTS ARE BREAKING THE HIERARCHY

			public GamlGridAgent(final int index) {
				super(GridPopulation.this);
				setIndex(index);
				geometry = matrix[getIndex()].getGeometry(); // TODO Verify this
			}

			@Override
			public GamaColor getColor() {
				if ( isHexagon ) { return (GamaColor) getAttribute(IKeyword.COLOR); }
				return GamaColor.getInt(supportImagePixels[getIndex()]);
			}

			@Override
			public void setColor(final GamaColor color) {
				if ( isHexagon ) {
					setAttribute(IKeyword.COLOR, color);
				} else {
					supportImagePixels[getIndex()] = color.getRGB();
				}
			}

			@Override
			public final int getX() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getX(getGeometry()); }
				return (int) (getLocation().getX() / cellWidth);
			}

			@Override
			public final int getY() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getY(getGeometry()); }
				return (int) (getLocation().getY() / cellHeight);
			}

			@Override
			public double getValue() {
				if ( gridValue != null ) { return gridValue[getIndex()]; }
				return 0d;
			}

			@Override
			public void setValue(final double d) {
				if ( gridValue != null ) {
					gridValue[getIndex()] = d;
				}
			}

			@Override
			public IPopulation getPopulation() {
				return GridPopulation.this;
			}

			@Override
			public IList<IAgent> getNeighbors(final IScope scope) {
				return Cast.asList(scope, getNeighbourhood().getNeighboursIn(scope, getIndex(), 1));
			}

		}

		public class MinimalGridAgent extends MinimalAgent implements IGridAgent {

			private final IShape geometry;

			public MinimalGridAgent(final int index) {
				setIndex(index);
				geometry = matrix[index].getGeometry();
			}

			@Override
			public GamaColor getColor() {
				if ( isHexagon ) { return (GamaColor) getAttribute(IKeyword.COLOR); }
				return GamaColor.getInt(supportImagePixels[getIndex()]);
			}

			@Override
			public void setColor(final GamaColor color) {
				if ( isHexagon ) {
					setAttribute(IKeyword.COLOR, color);
				} else {
					supportImagePixels[getIndex()] = color.getRGB();
				}
			}

			@Override
			public final int getX() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getX(getGeometry()); }
				return (int) (getLocation().getX() / cellWidth);
			}

			@Override
			public final int getY() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getY(getGeometry()); }
				return (int) (getLocation().getY() / cellHeight);
			}

			@Override
			public double getValue() {
				if ( gridValue != null ) { return gridValue[getIndex()]; }
				return 0d;
			}

			@Override
			public void setValue(final double d) {
				if ( gridValue != null ) {
					gridValue[getIndex()] = d;
				}
			}

			@Override
			public IPopulation getPopulation() {
				return GridPopulation.this;
			}

			@Override
			protected IPopulation checkedPopulation() {
				// The population is never null
				return GridPopulation.this;
			}

			@Override
			protected IShape checkedGeometry() {
				// The geometry is never null (?)
				return geometry;
			}

			@Override
			public IShape getGeometry() {
				return geometry;
			}

			@Override
			public IList<IAgent> getNeighbors(final IScope scope) {
				return Cast.asList(scope, getNeighbourhood().getNeighboursIn(scope, getIndex(), 1));
			}

			/**
			 * Method getPoints()
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

			// hqnghi must-implement methods from GamlAgent
			@Override
			public void addExternMicroPopulation(final String expName, final IPopulation pop) {
				// TODO Auto-generated method stub

			}

			@Override
			public IPopulation getExternMicroPopulationFor(final String expName) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, IPopulation> getExternMicroPopulations() {
				// TODO Auto-generated method stub
				return null;
			}
			// end-hqnghi

		}

	}

	// private class IntToAgents implements Function<Integer, IAgent> {
	//
	// @Override
	// public IAgent apply(final Integer input) {
	// return matrix[input].getAgent();
	// }
	//
	// }
	//
	// private final Function<Integer, IAgent> intToAgents = new IntToAgents();

	public class NoCacheNeighbourhood implements INeighbourhood {

		public NoCacheNeighbourhood() {}

		/**
		 * Method getNeighboursIn()
		 * @see msi.gama.metamodel.topology.grid.INeighbourhood#getNeighboursIn(int, int)
		 */
		@Override
		public Set<IAgent> getNeighboursIn(final IScope scope, final int placeIndex, final int radius) {
			return computeNeighboursFrom(scope, placeIndex, 1, radius);
		}

		private Set<IAgent> computeNeighboursFrom(final IScope scope, final int placeIndex, final int begin,
			final int end) {
			Set<IAgent> result = new TLinkedHashSet();
			for ( int i = begin; i <= end; i++ ) {
				for ( Integer index : usesVN ? get4NeighboursAtRadius(placeIndex, i)
					: get8NeighboursAtRadius(placeIndex, i) ) {
					result.add(matrix[index].getAgent());
				}
			}
			// Addresses Issue 1071 by explicitly shuffling the result
			scope.getRandom().shuffle2(result);
			return result;
		}

		protected List<Integer> get8NeighboursAtRadius(final int placeIndex, final int radius) {
			final int y = placeIndex / numCols;
			final int x = placeIndex - y * numCols;
			final List<Integer> v = new ArrayList<Integer>(radius + 1 * radius + 1);
			int p;
			for ( int i = 1 - radius; i < radius; i++ ) {
				p = getPlaceIndexAt(x + i, y - radius);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x - i, y + radius);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			for ( int i = -radius; i < radius + 1; i++ ) {
				p = getPlaceIndexAt(x - radius, y - i);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x + radius, y + i);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			return v;
		}

		protected List<Integer> get4NeighboursAtRadius(final int placeIndex, final int radius) {
			final int y = placeIndex / numCols;
			final int x = placeIndex - y * numCols;

			final List<Integer> v = new ArrayList<Integer>(radius << 2);
			int p;
			for ( int i = -radius; i < radius; i++ ) {
				p = getPlaceIndexAt(x - i, y - Math.abs(i) + radius);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x + i, y + Math.abs(i) - radius);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			return v;
		}

		/**
		 * Method isVN()
		 * @see msi.gama.metamodel.topology.grid.INeighbourhood#isVN()
		 */
		@Override
		public boolean isVN() {
			return false;
		}

		/**
		 * Method getRawNeighboursIncluding()
		 * @see msi.gama.metamodel.topology.grid.INeighbourhood#getRawNeighboursIncluding(int, int)
		 */
		@Override
		public int[] getRawNeighboursIncluding(final IScope scope, final int placeIndex, final int range) {
			throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbours cache in the grid",
				scope);
		}

		/**
		 * Method neighboursIndexOf()
		 * @see msi.gama.metamodel.topology.grid.INeighbourhood#neighboursIndexOf(int, int)
		 */
		@Override
		public int neighboursIndexOf(final IScope scope, final int placeIndex, final int n) {
			throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbours cache in the grid",
				scope);
		}

	}

	/**
	 * Written by drogoul Modified on 8 mars 2011
	 *
	 * @todo Description
	 *
	 */
	public abstract class GridNeighbourhood implements INeighbourhood {

		// i : index of agents; j : index of neighbours
		protected int[][] neighbours;
		// i : index of agents; j : index of the neighbours by distance
		protected int[][] neighboursIndexes;

		public GridNeighbourhood() {
			neighbours = new int[matrix.length][0];
			// neighboursIndexes = new ArrayList[agents.length];
			neighboursIndexes = new int[matrix.length][];
		}

		@Override
		public int[] getRawNeighboursIncluding(final IScope scope, final int placeIndex, final int radius) {
			// List<Integer> n = neighboursIndexes[placeIndex];
			int[] n = neighboursIndexes[placeIndex];
			if ( n == null ) {
				// n = new ArrayList<Integer>();
				n = new int[0];
				neighboursIndexes[placeIndex] = n;
			}
			// final int size = n.size();
			final int size = n.length;
			if ( radius > size ) {
				computeNeighboursFrom(placeIndex, size + 1, radius);
			}
			return neighbours[placeIndex];
		}

		protected abstract TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius);

		private void computeNeighboursFrom(final int placeIndex, final int begin, final int end) {
			for ( int i = begin; i <= end; i++ ) {
				// final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex].get(i - 2);
				final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex][i - 2];
				final TIntHashSet list = getNeighboursAtRadius(placeIndex, i);
				int[] listArray = list.toArray();
				int size = listArray.length;
				// final int size = list.size();
				// final int[] listArray = new int[size];
				// for ( int j = 0; j < size; j++ ) {
				// listArray[j] = list.get(j);
				// }
				final int[] newArray = new int[neighbours[placeIndex].length + size];
				if ( neighbours[placeIndex].length != 0 ) {
					java.lang.System.arraycopy(neighbours[placeIndex], 0, newArray, 0, neighbours[placeIndex].length);
				}
				java.lang.System.arraycopy(listArray, 0, newArray, neighbours[placeIndex].length, size);
				neighbours[placeIndex] = newArray;
				// neighboursIndexes[placeIndex].add(previousIndex + size);
				addToNeighboursIndex(placeIndex, previousIndex + size);
			}
		}

		private final void addToNeighboursIndex(final int placeIndex, final int newIndex) {
			final int[] previous = neighboursIndexes[placeIndex];
			final int[] newOne = new int[previous.length + 1];
			java.lang.System.arraycopy(previous, 0, newOne, 0, previous.length);
			newOne[previous.length] = newIndex;
			neighboursIndexes[placeIndex] = newOne;
		}

		@Override
		public int neighboursIndexOf(final IScope scope, final int placeIndex, final int n) {
			if ( n == 1 ) { return 0; }
			final int size = neighboursIndexes[placeIndex].length;
			if ( n > size ) { return neighbours[placeIndex].length - 1; }
			return neighboursIndexes[placeIndex][n - 2];
		}

		@Override
		public Set<IAgent> getNeighboursIn(final IScope scope, final int placeIndex, final int radius) {
			int[] n = neighboursIndexes[placeIndex];
			if ( n == null ) {
				n = new int[0];
				neighboursIndexes[placeIndex] = n;
			}
			final int size = n.length;
			if ( radius > size ) {
				computeNeighboursFrom(placeIndex, size + 1, radius);
			}
			final int[] nn = neighbours[placeIndex];
			final int nnSize = neighboursIndexes[placeIndex][radius - 1];
			final Set<IAgent> result = new TLinkedHashSet();
			for ( int i = 0; i < nnSize; i++ ) {
				result.add(matrix[nn[i]].getAgent());
			}
			scope.getRandom().shuffle2(result);
			return result;
		}

		@Override
		public abstract boolean isVN();

	}

	protected class GridDiffuser_deprecated {
		// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.

		private class GridDiffusion_deprecated {

			IShape[] places;
			double[] values;
			final double proportion;
			final double variation;
			final double range;
			final short type;
			final IContainer<?, IAgent> candidates;

			private GridDiffusion_deprecated(final short type, final double proportion, final double variation,
				final double range, final IContainer<?, IAgent> cand) {
				this.type = type;
				this.proportion = proportion;
				this.variation = variation;
				this.range = range;
				places = new IAgent[0];
				values = new double[0];
				candidates = cand;
			}

			// TODO Take candidates into account here as well (by making an intersection ? or better a union)
			void add(final IScope scope, final IAgent p, final double value) {
				if ( candidates != null && !candidates.contains(scope, p) ) { return; }
				for ( int i = 0; i < places.length; i++ ) {
					if ( places[i] == p ) {
						final double v = values[i];
						values[i] = type == IGrid.GRADIENT ? Math.max(v, value) : v + value;
						return;
					}
				}
				places = Arrays.copyOf(places, places.length + 1);
				values = Arrays.copyOf(values, values.length + 1);
				places[places.length - 1] = p;
				values[values.length - 1] = value;
			}
		}

		private final int neighboursSize;

		public GridDiffuser_deprecated() {
			neighboursSize = neighbourhood.isVN() ? 4 : 8;
		}

		protected final Map<String, GridDiffusion_deprecated> diffusions_deprecated = new THashMap();

		// public static final short GRADIENT = 1;
		// public static final short DIFFUSION = 0;

		protected void addDiffusion(final IScope scope, final short type, final String var, final IAgent agent,
			final double value, final double proportion, final double variation, final double range,
			final IContainer<?, IAgent> candidates) {
			if ( !diffusions_deprecated.containsKey(var) ) {
				diffusions_deprecated.put(var,
					new GridDiffusion_deprecated(type, proportion, variation, range, candidates));
			}
			diffusions_deprecated.get(var).add(scope, agent, value);
		}

		public void diffuse_deprecated(final IScope scope) throws GamaRuntimeException {
			for ( final String v : diffusions_deprecated.keySet() ) {
				final GridDiffusion_deprecated d = diffusions_deprecated.get(v);
				if ( d.type == IGrid.DIFFUSION ) {
					spreadDiffusion(scope, v, d);
				} else {
					spreadGradient(scope, v, d);
				}
			}
			diffusions_deprecated.clear();
		}

		protected final int getPlaceIndexAt(final ILocation p) {
			final double xx = p.getX() / cellWidth;
			final double yy = p.getY() / cellWidth;
			final int x = (int) xx;
			final int y = (int) yy;
			return GamaSpatialMatrix.this.getPlaceIndexAt(x, y);
		}

		public void diffuseVariable(final IScope scope, final String name, final double value, final short type,
			final double proportion, final double variation, final ILocation location, final double range,
			final Object candidates) {
			final int p = getPlaceIndexAt(location);
			if ( p == -1 ) { return; }
			IContainer<?, IAgent> cand = candidates instanceof IPopulation ? null
				: candidates instanceof IContainer ? (IContainer) candidates : null;
			addDiffusion(scope, type, name, matrix[p].getAgent(), value, proportion, variation, range, cand);
		}

		private void spreadDiffusion(final IScope scope, final String v, final GridDiffusion_deprecated gridDiffusion)
			throws GamaRuntimeException {
			int[] neighbours;
			IAgent p;
			final double proportion = gridDiffusion.proportion;
			final double variation = gridDiffusion.variation;
			int range = (int) (gridDiffusion.range / cellWidth);
			if ( range < 0 ) {
				range = 1000;
			}
			if ( range == 0 ) { return; }

			int n;
			double r0, rn;
			final double prop = proportion / neighboursSize;
			final double propInit = 1 - proportion;
			for ( int i = 0, halt = gridDiffusion.places.length; i < halt; i++ ) {
				p = gridDiffusion.places[i].getAgent();
				// if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, p) ) {
				// continue;
				// }
				final int placeIndex = p.getIndex();
				r0 = gridDiffusion.values[i];
				final Double previous = (Double) scope.getAgentVarValue(p, v);
				// If we cant get access to the value of the variable, it means probably that the agent is dead. Better
				// to stop spreading !
				if ( previous == null ) { return; }
				scope.setAgentVarValue(p, v, previous + r0 * propInit);
				rn = r0 * prop - variation;
				int max_range = 1;
				double vn = rn;
				while (vn > 0.1) {
					vn = vn * prop - variation;
					max_range++;
				}
				range = Math.min(range, max_range);
				try {
					neighbours = neighbourhood.getRawNeighboursIncluding(scope, placeIndex, range);
				} catch (final GamaRuntimeException e) {
					// We change the neighbourhood to a cached version dynamically
					GAMA.reportError(scope, e, false);
					useNeighboursCache = true;
					neighbourhood = null;
					neighbours = getNeighbourhood().getRawNeighboursIncluding(scope, placeIndex, range);
				}
				for ( n = 1; n <= range; n++ ) {
					final int begin = neighbourhood.neighboursIndexOf(scope, placeIndex, n);
					final int end = neighbourhood.neighboursIndexOf(scope, placeIndex, n + 1);
					for ( int k = begin; k < end; k++ ) {
						final IAgent z = matrix[neighbours[k]].getAgent();
						if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, z) ) {
							continue;
						}
						// v.addDirectFloat(z, rn);
						final Double value = (Double) scope.getAgentVarValue(z, v);
						// If we cant get access to the value of the variable, it means probably that the agent is dead.
						// Better to stop spreading !
						if ( value == null ) { return; }
						scope.setAgentVarValue(z, v, value + rn);
					}
					rn = rn * prop - variation;
				}
			}
		}

		private void spreadGradient(final IScope scope, final String v, final GridDiffusion_deprecated gridDiffusion)
			throws GamaRuntimeException {
			int[] neighbours;
			IAgent p;
			final double proportion = gridDiffusion.proportion;
			final double variation = gridDiffusion.variation;
			int range = (int) (gridDiffusion.range / cellWidth);
			if ( range < 0 ) {
				range = 1000;
			}
			if ( range == 0 ) { return; }

			int n;
			double r0, rn;
			for ( int i = 0, halt = gridDiffusion.places.length; i < halt; i++ ) {
				p = gridDiffusion.places[i].getAgent();
				final int placeIndex = p.getIndex();
				r0 = gridDiffusion.values[i];
				final Double previous = (Double) scope.getAgentVarValue(p, v);
				// If we cant get access to the value of the variable, it means probably that the agent is dead. Better
				// to stop spreading !
				if ( previous == null || previous > r0 ) { return; }
				scope.setAgentVarValue(p, v, r0);
				rn = r0 * proportion - variation;
				int max_range = 1;
				double vn = rn;
				while (vn > 0.1) {
					vn = vn * proportion - variation;
					max_range++;
				}
				range = Math.min(range, max_range);
				try {
					neighbours = neighbourhood.getRawNeighboursIncluding(scope, placeIndex, range);
				} catch (final GamaRuntimeException e) {
					// We change the neighbourhood to a cached version dynamically
					GAMA.reportError(scope, e, false);
					useNeighboursCache = true;
					neighbourhood = null;
					neighbours = getNeighbourhood().getRawNeighboursIncluding(scope, placeIndex, range);
				}
				boolean cont = true;
				for ( n = 1; n <= range; n++ ) {
					final int begin = neighbourhood.neighboursIndexOf(scope, placeIndex, n);
					final int end = neighbourhood.neighboursIndexOf(scope, placeIndex, n + 1);
					cont = false;
					for ( int k = begin; k < end; k++ ) {
						final IAgent z = matrix[neighbours[k]].getAgent();
						if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, z) ) {
							continue;
						}
						final Double value = (Double) scope.getAgentVarValue(z, v);
						// If we cant get access to the value of the variable, it means probably that the agent is dead.
						// Better to stop spreading !
						if ( value == null ) { return; }
						if ( value < rn ) {
							scope.setAgentVarValue(z, v, rn);
							cont = true;
						}
					}
					if ( !cont ) {
						break;
					}
					rn = rn * proportion - variation;
				}
			}
		}

	}

	public class GridHexagonalNeighbourhood extends GridNeighbourhood {

		final int getIndexAt(int x, int y, final int xSize, final int ySize, final boolean isTorus) {
			if ( x < 0 || y < 0 || x > xSize - 1 || y > ySize - 1 ) {
				if ( !isTorus ) { return -1; }
				if ( x < 0 ) {
					x = xSize - 1;
				}
				if ( y < 0 ) {
					y = ySize - 1;
				}
				if ( x > xSize - 1 ) {
					x = 0;
				}
				if ( y > ySize - 1 ) {
					y = 0;
				}
			}
			return y * xSize + x;
		}

		public TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius, final int xSize,
			final int ySize, final boolean isTorus) {
			final TIntHashSet currentNeigh = new TIntHashSet();
			currentNeigh.add(placeIndex);
			for ( int i = 0; i < radius; i++ ) {
				final TIntHashSet newNeigh = new TIntHashSet();
				TIntIterator it = currentNeigh.iterator();
				while (it.hasNext()) {
					newNeigh.addAll(getNeighboursAtRadius1(it.next(), xSize, ySize, isTorus));
				}
				currentNeigh.addAll(newNeigh);
			}
			currentNeigh.remove(placeIndex);
			return currentNeigh;

		}

		public TIntHashSet getNeighboursAtRadius1(final int placeIndex, final int xSize, final int ySize,
			final boolean isTorus) {
			final int y = placeIndex / xSize;
			final int x = placeIndex - y * xSize;
			final TIntHashSet neigh = new TIntHashSet();
			int id = getIndexAt(x, y - 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x, y + 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x + 1, y, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			if ( x % 2 == 0 ) {
				id = getIndexAt(x + 1, y - 1, xSize, ySize, isTorus);
				if ( id != -1 ) {
					neigh.add(id);
				}
				id = getIndexAt(x - 1, y - 1, xSize, ySize, isTorus);
				if ( id != -1 ) {
					neigh.add(id);
				}
			} else {
				id = getIndexAt(x + 1, y + 1, xSize, ySize, isTorus);
				if ( id != -1 ) {
					neigh.add(id);
				}
				id = getIndexAt(x - 1, y + 1, xSize, ySize, isTorus);
				if ( id != -1 ) {
					neigh.add(id);
				}
			}
			return neigh;
		}

		@Override
		protected TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius) {
			final TIntHashSet neigh2 = new TIntHashSet();
			final TIntHashSet neigh = getNeighboursAtRadius(placeIndex, radius, numCols, numRows, isTorus);
			TIntIterator it = neigh.iterator();
			while (it.hasNext()) {
				int id = it.next();
				if ( matrix[id] != null ) {
					neigh2.add(id);
				}
			}
			return neigh2;
		}

		@Override
		public boolean isVN() {
			return false;
		}

	}

	/**
	 * Written by drogoul Modified on 8 mars 2011
	 *
	 * @todo Description
	 *
	 */
	public class GridMooreNeighbourhood extends GridNeighbourhood {

		public GridMooreNeighbourhood() {
			super();
		}

		@Override
		protected TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius) {
			final int y = placeIndex / numCols;
			final int x = placeIndex - y * numCols;
			final TIntHashSet v = new TIntHashSet(radius + 1 * radius + 1);
			int p;
			for ( int i = 1 - radius; i < radius; i++ ) {
				p = getPlaceIndexAt(x + i, y - radius);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x - i, y + radius);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			for ( int i = -radius; i < radius + 1; i++ ) {
				p = getPlaceIndexAt(x - radius, y - i);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x + radius, y + i);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			return v;
		}

		@Override
		public boolean isVN() {
			return false;
		}
	}

	/**
	 * Written by drogoul Modified on 8 mars 2011
	 *
	 * @todo Description
	 *
	 */
	public class GridVonNeumannNeighbourhood extends GridNeighbourhood {

		@Override
		protected TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius) {
			final int y = placeIndex / numCols;
			final int x = placeIndex - y * numCols;

			final TIntHashSet v = new TIntHashSet(radius << 2);
			int p;
			for ( int i = -radius; i < radius; i++ ) {
				p = getPlaceIndexAt(x - i, y - Math.abs(i) + radius);
				if ( p != -1 ) {
					v.add(p);
				}
				p = getPlaceIndexAt(x + i, y + Math.abs(i) - radius);
				if ( p != -1 ) {
					v.add(p);
				}
			}
			return v;
		}

		@Override
		public boolean isVN() {
			return true;
		}
	}

	/**
	 * Method usesNeighboursCache()
	 * @see msi.gama.metamodel.topology.grid.IGrid#usesNeighboursCache()
	 */
	@Override
	public boolean usesNeighboursCache() {
		return useNeighboursCache;
	}

	@Override
	protected IShape getNthElement(final Integer index) {
		if ( index == null ) { return null; }
		if ( index > lastCell ) { return null; }
		return matrix[index];
	}

	/**
	 * Method getGridValueOfColorAttribute()
	 * @see msi.gama.metamodel.topology.grid.IGrid#getGridValueOfColorAttribute()
	 */
	@Override
	public double[] getGridValueOfColorAttribute() {
		return null;
	}

}
