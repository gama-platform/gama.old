/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.grid;

import java.awt.Graphics2D;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.matrix.*;
import msi.gama.util.path.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.variables.IVariable;
import com.google.common.collect.UnmodifiableIterator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * This matrix contains geometries and can serve to organize the agents of a population as a grid in
 * the environment, or as a support for grid topologies
 */
public class GamaSpatialMatrix extends GamaMatrix<IShape> implements IGrid {

	/** The geometry of host. */

	public final IShape environmentFrame;
	final Envelope bounds;
	final double precision;
	public static int GRID_NUMBER = 0;
	protected IShape[] matrix;

	double cellWidth, cellHeight;
	public int[] supportImagePixels;
	public double[] gridValue;
	protected Boolean usesVN = null;
	protected Boolean isTorus = null;

	protected Boolean isHexagon = null;
	protected GridDiffuser diffuser;
	public GridNeighbourhood neighbourhood;

	int actualNumberOfCells;
	int firstCell, lastCell;
	UnmodifiableIterator<? extends IShape> iterator = null;
	private ISpecies cellSpecies;
	private IAgentFilter cellFilter;

	GamaMap hexAgentToLoc = null;

	// private final IShape cellShape;

	// GamaMap<GamaShape, Double> gridValue;

	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN) throws GamaRuntimeException {
		super(scope, cols, rows);
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
		GRID_NUMBER++;
		actualNumberOfCells = 0;
		// cellShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		firstCell = -1;
		lastCell = -1;
		this.isHexagon = false;
		createCells(scope, false);
	}

	public GamaSpatialMatrix(final IScope scope, final GamaGridFile gfile, final boolean isTorus, final boolean usesVN)
		throws GamaRuntimeException {
		super(scope, 100, 100);
		numRows = gfile.getNbRows();
		numCols = gfile.getNbCols();
		environmentFrame = gfile.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / numCols;
		cellHeight = bounds.getHeight() / numRows;
		precision = bounds.getWidth() / 1000;
		final int size = gfile.length(scope);
		gridValue = new double[size];
		createMatrix(size);
		supportImagePixels = new int[size];
		// cellShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		this.isTorus = isTorus;
		this.usesVN = usesVN;
		GRID_NUMBER++;
		actualNumberOfCells = 0;
		this.isHexagon = false;
		firstCell = 0;

		for ( int i = 0; i < size; i++ ) {
			final GamaShape g = gfile.get(scope, i);
			gridValue[i] = (Double) g.getAttribute("grid_value");
			matrix[i] = g;
		}
		actualNumberOfCells = size;
		lastCell = size - 1;
		// createCells(scope, false);
	}

	public GamaSpatialMatrix(final IScope scope, final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon) throws GamaRuntimeException {
		super(scope, cols, rows);
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		// cellShape = GamaGeometryType.buildRectangle(cellWidth, cellHeight, new GamaPoint(0, 0));
		precision = bounds.getWidth() / 1000;
		final int size = 2 * numRows * numCols;
		createMatrix(size);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = false;
		this.isHexagon = isHexagon;
		GRID_NUMBER++;
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		createHexagons(false);
	}

	private void createMatrix(final int size) {
		matrix = new IShape[size];
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
		hexAgentToLoc = new GamaMap();
		int i = 0;
		for ( int l = 0; l < numRows; l++ ) {
			for ( int c = 0; c < numCols; c = c + 2 ) {
				i = c + numRows * l;
				final GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight, new GamaPoint(xmin + c *
						cellWidth * 0.75, ymin + l * cellHeight));
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
				final GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight, new GamaPoint(xmin + c *
						cellWidth * 0.75, ymin + (l + 0.5) * cellHeight));
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
		// Geometry g = environmentFrame.getInnerGeometry();
		final boolean isRectangle = environmentFrame.getInnerGeometry().isRectangle();
		final GamaPoint p = new GamaPoint(0, 0);
		final GamaPoint origin =
			new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope().getMinY());

		final IShape translatedReferenceFrame = Spatial.Transformations.translated_by(scope, environmentFrame, origin);
		// GeometryUtils.translation(g, -origin.x, -origin.y);

		final double cmx = cellWidth / 2;
		final double cmy = cellHeight / 2;
		for ( int i = 0, n = numRows * numCols; i < n; i++ ) {
			final int yy = i / numCols;
			final int xx = i - yy * numCols;
			p.x = xx * cellWidth + cmx;
			p.y = yy * cellHeight + cmy;
			// WARNING HACK
			final IShape rect = GamaGeometryType.buildRectangle(cellWidth, cellHeight, p);
			// final IShape rect = p.getGeometry();
			// WARNING
			boolean ok = isRectangle || translatedReferenceFrame.covers(rect);
			if ( partialCells && !ok && rect.intersects(translatedReferenceFrame) ) {
				rect.setGeometry(Spatial.Operators.inter(rect, translatedReferenceFrame));
				ok = true;
			}
			if ( ok ) {
				if ( firstCell == -1 ) {
					firstCell = i;
				}
				matrix[i] = rect;
				actualNumberOfCells++;
				lastCell = i;
			}
		}
	}

	@Override
	public GridNeighbourhood getNeighbourhood() {
		if ( neighbourhood == null ) {
			neighbourhood =
				isHexagon ? new GridHexagonalNeighbourhood(matrix, numCols, numRows, isTorus) : usesVN
					? new GridVonNeumannNeighbourhood(matrix, numCols, numRows, isTorus) : new GridMooreNeighbourhood(
						matrix, numCols, numRows, isTorus);
		}
		return neighbourhood;
	}

	@Override
	public int[] getDisplayData() {
		return supportImagePixels;
	}

	private final int getPlaceIndexAt(final int xx, final int yy) {
		if ( isHexagon ) { return yy * numCols + xx; }
		if ( isTorus ) { return (yy < 0 ? yy + numCols : yy) % numRows * numCols + (xx < 0 ? xx + numCols : xx) %
			numCols; }
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
			final List<Integer> toObserve =
				GridHexagonalNeighbourhood.getNeighboursAtRadius1(i, numCols, numRows, isTorus);
			toObserve.add(i);
			double dMin = Double.MAX_VALUE;
			int x = 0, y = 0;
			for ( final Integer id : toObserve ) {
				final IShape sh = matrix[id];
				if ( sh == null ) {
					continue;
				}
				final double dist = sh.getLocation().euclidianDistanceTo(p);
				if ( dist < dMin ) {
					dMin = dist;
					final GamaPoint pt = (GamaPoint) hexAgentToLoc.get(sh);
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

	private void diffuse(final IScope scope) throws GamaRuntimeException {
		diffuser.diffuse(scope);
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
	protected GamaList _listValue(final IScope scope) {
		final GamaList result = new GamaList(actualNumberOfCells);
		if ( actualNumberOfCells == 0 ) { return new GamaList(); }
		for ( final IShape a : this ) {
			final IAgent ag = a.getAgent();
			result.add(ag == null ? a : ag);
		}
		return result;
	}

	@Override
	public Iterator<IShape> iterator() {
		return JavaUtils.iterator(matrix);
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize) {
		return this;
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
	public IMatrix copy(final IScope scope) throws GamaRuntimeException {
		final IGrid result = new GamaSpatialMatrix(scope, environmentFrame, numCols, numRows, isTorus, usesVN);
		return result;
	}

	@Override
	public boolean _contains(final IScope scope, final Object o) {
		return listValue(scope).contains(o);
	}

	@Override
	public void _putAll(final IScope scope, final Object value, final Object param) throws GamaRuntimeException {
		// TODO Not allowed for the moment

	}

	@Override
	public boolean _isEmpty(final IScope scope) {
		return actualNumberOfCells == 0;
	}

	@Override
	public String toGaml() {
		return new GamaList(this.matrix).toGaml() + " as_spatial_matrix";
	}

	@Override
	public int manhattanDistanceBetween(final IShape g1, final IShape g2) {
		// TODO ATTENTION ne tient pas compte du voisinage de Moore
		final Coordinate[] coord = new DistanceOp(g1.getInnerGeometry(), g2.getInnerGeometry()).nearestPoints();
		final Coordinate p1 = coord[0];
		final Coordinate p2 = coord[1];
		// TODO ATTENTION ne tient pas compte de l'inclusion des points dans la matrice
		final int dx = (int) (Maths.abs(p2.x - p1.x) / cellWidth);
		final int dy = (int) (Maths.abs(p2.y - p1.y) / cellHeight);
		return dx + dy;
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
	public GamaList<IAgent> getNeighboursOf(final IScope scope, final ITopology t, final IShape shape,
		final Double distance) {
		if ( shape.isPoint() || shape.getAgent() != null && shape.getAgent().getSpecies() == cellSpecies ) { return getNeighboursOf(
			scope, t, shape.getLocation(), distance); }
		final Collection<? extends IAgent> coveredPlaces =
			!cellFilter.filterSpecies(cellSpecies) ? Collections.EMPTY_LIST
				: (Collection<? extends IAgent>) allInEnvelope(shape, shape.getEnvelope(), cellFilter, true);
		final Set<IAgent> result = new HashSet<IAgent>();
		for ( final IAgent a : coveredPlaces ) {
			result.addAll(getNeighbourhood().getNeighboursIn(getPlaceIndexAt(a.getLocation()), distance.intValue()));
		}
		result.removeAll(coveredPlaces);
		return new GamaList<IAgent>(result);
	}

	@Override
	public GamaList<IAgent> getNeighboursOf(final IScope scope, final ITopology t, final ILocation shape,
		final Double distance) {
		return getNeighbourhood().getNeighboursIn(getPlaceIndexAt(shape), distance.intValue());
	}

	@Override
	public GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
		final ITopology topo) throws GamaRuntimeException {
		final GamaMap<IAgent, Integer> dists = new GamaMap<IAgent, Integer>();
		final int currentplace = getPlaceIndexAt(source.getLocation());
		final int targetplace = getPlaceIndexAt(target.getLocation());
		final IAgent startAg = matrix[currentplace].getAgent();
		final IAgent endAg = matrix[targetplace].getAgent();

		int cpt = 0;
		dists.put(startAg, Integer.valueOf(cpt));
		final int max = this.numCols * this.numRows;
		List<IAgent> neighb = getNeighs(scope, startAg, dists, topo, new GamaList<IAgent>());
		while (true) {
			cpt++;
			final HashSet<IAgent> neighb2 = new HashSet<IAgent>();
			for ( final IAgent ag : neighb ) {
				dists.put(ag, Integer.valueOf(cpt));
				if ( ag == endAg ) {
					final List<ILocation> pts = new GamaList<ILocation>();
					pts.add(ag.getLocation());
					IAgent agDes = ag;
					while (cpt > 0) {
						cpt--;
						agDes = getNeighDesc(scope, agDes, dists, topo, cpt);
						pts.add(agDes.getLocation());
					}
					final IList<IShape> nodes = new GamaList<IShape>();
					for ( int i = pts.size() - 1; i >= 0; i-- ) {
						nodes.add(pts.get(i));
					}
					// return new GamaPath(topo, nodes);
					return PathFactory.newInstance(topo, nodes);
				}
				neighb2.addAll(getNeighs(scope, ag, dists, topo, neighb));
			}
			neighb = new GamaList<IAgent>(neighb2);
			if ( cpt > max ) { return null; }
		}
	}

	/**
	 * @throws GamaRuntimeException
	 *             Method used by square discretisation pathfinder to find the valid neighborhood of
	 *             a location
	 * 
	 * @param i index of the x position of the current position
	 * @param j index of the y position of the current position
	 * @param matrix representing the background geometry
	 * @return the "valid" neighborhood of the current position (Van Neuman)
	 */
	private List<IAgent> getNeighs(final IScope scope, final IAgent agent, final GamaMap dists, final ITopology t,
		final List<IAgent> currentList) throws GamaRuntimeException {
		final List<IAgent> agents = getNeighboursOf(scope, t, agent.getLocation(), 1.0);
		final List<IAgent> neighs = new GamaList<IAgent>();
		for ( final IAgent ag : agents ) {
			if ( !dists.contains(scope, ag) && !currentList.contains(ag) ) {
				neighs.add(ag);
			}
		}

		return neighs;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Method used by square discretisation pathfinder to obtain the best path
	 * 
	 * @param cpt current distance to the target (in number of cells)
	 * @param i index of the x position of the current position
	 * @param j index of the y position of the current position
	 * @param matrix representing the background geometry
	 * @return the next position of the shortest path
	 * @return
	 */
	private IAgent getNeighDesc(final IScope scope, final IAgent agent, final GamaMap dists, final ITopology t,
		final int cpt) throws GamaRuntimeException {
		final List<IAgent> agents = getNeighboursOf(scope, t, agent.getLocation(), 1.0);
		Collections.shuffle(agents);
		for ( final IAgent ag : agents ) {
			if ( dists.contains(scope, ag) && dists.get(ag).equals(Integer.valueOf(cpt)) ) { return ag; }
		}
		return null;
	}

	@Override
	public final IAgent getAgentAt(final ILocation c) {
		final IShape g = getPlaceAt(c);
		if ( g == null ) { return null; }
		return g.getAgent();
	}

	private GridDiffuser getDiffuser(final IScope scope) {
		if ( diffuser != null ) { return diffuser; }
		diffuser = new GridDiffuser(matrix, getNeighbourhood(), cellWidth);
		scope.getSimulationScope().getScheduler().insertEndAction(new GamaHelper() {

			@Override
			public Object run(final IScope scope) throws GamaRuntimeException {
				diffuse(scope);
				return null;
			}

		});
		return diffuser;
	}

	@Override
	public void diffuseVariable(final IScope scope, final String name, final double value, final short type,
		final double prop, final double variation, final ILocation location, final double range) {
		getDiffuser(scope).diffuseVariable(name, value, type, prop, variation, location, range);
	}

	@Override
	public void setCellSpecies(final IPopulation pop) {
		cellSpecies = pop.getSpecies();
		cellFilter = In.population(pop);
	}

	@Override
	public Boolean isHexagon() {
		return isHexagon;
	}

	@Override
	public List<IAgent> getAgents() {
		// Later, do return Arrays.asList(matrix);
		final List<IAgent> agents = new GamaList<IAgent>();
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] != null ) {
				agents.add(matrix[i].getAgent());
			}
		}
		return agents;
	}

	@Override
	public void insert(final IShape a) {}

	@Override
	public void remove(final IShape previous, final IShape a) {}

	//
	@Override
	public IList<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		final Collection<IShape> set = allInEnvelope(source, ENVELOPE, f, false);
		final GamaList external_results = new GamaList();
		for ( final IShape a : set ) {
			if ( source.euclidianDistanceTo(a) < dist ) {
				external_results.add(a);
			}
		}
		return external_results;
	}

	@Override
	public IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		final Collection<IShape> in_square = allInEnvelope(source, ENVELOPE, f, false);
		double min_distance = dist;
		IShape min_agent = null;
		for ( final IShape a : in_square ) {
			final Double dd = source.euclidianDistanceTo(a);
			if ( dd < min_distance ) {
				min_distance = dd;
				min_agent = a;
			}
		}
		return min_agent;
	}

	@Override
	public IList<IShape> allInEnvelope(final IShape source, final Envelope env, final IAgentFilter f,
		final boolean covered) {
		if ( !f.filterSpecies(cellSpecies) ) { return GamaList.EMPTY_LIST; }
		final int minX = (int) (env.getMinX() / cellWidth);
		final int minY = (int) (env.getMinY() / cellHeight);
		final int maxX = (int) (env.getMaxX() / cellWidth);
		final int maxY = (int) (env.getMaxY() / cellHeight);
		final IList<IShape> ags = new GamaList();
		// BUGFIX AD 28/01/13 Changed "<" into "<="
		for ( int i = minX; i <= maxX; i++ ) {
			for ( int j = minY; j <= maxY; j++ ) {
				final int index = getPlaceIndexAt(i, j);
				// BUGFIX AD 28/01/13 Changed "1" into "-1"
				if ( index != -1 ) {
					final IAgent a = matrix[index].getAgent();
					if ( a != null ) {
						final Envelope e = a.getEnvelope();
						// BUGFIX AD 29/03/13 The filter was absent
						if ( f.accept(source, a) && (covered && env.covers(e) || !covered && env.intersects(e)) ) {
							ags.add(a);
						}
					}
				}
			}
		}
		return ags;
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {}

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

	/**
	 * Class GridPopulation.
	 * 
	 * @author drogoul
	 * @since 14 mai 2013
	 * 
	 */
	public class GridPopulation extends GamaPopulation {

		/**
		 * @param host
		 * @param species
		 */
		public GridPopulation(final ITopology t, final IAgent host, final ISpecies species) {
			super(host, species);
			topology = t;
		}

		@Override
		public IList<? extends IAgent> createAgents(final IScope scope, final IContainer<?, IShape> geometries) {
			for ( int i = 0; i < matrix.length; i++ ) {
				final IAgent g = new GridAgent(i);
				matrix[i] = g;
				g.schedule();
			}

			for ( final String s : orderedVarNames ) {
				final IVariable var = species.getVar(s);
				for ( final IAgent g : this ) {
					var.initializeWith(scope, g, null);
				}
			}
			// WARNING FOR THE MOMENT NO EVENT IS FIRED
			// fireAgentsAdded(list);
			// WARNING DOES NOT RESPECT THE CONTRACT (RETURNS NULL)
			return null;

		}

		@Override
		public void step(final IScope scope) throws GamaRuntimeException {
			final IExpression ags = getSpecies().getSchedule();
			if ( ags != null ) {
				// In case there is a schedule specified, we do the "normal" step
				super.step(scope);
				return;
			}
			final int frequency = scheduleFrequency == null ? 1 : Cast.asInt(scope, scheduleFrequency.value(scope));
			final int step = scope.getClock().getCycle();
			if ( step % frequency != 0 ) { return; }

			for ( final IShape s : matrix ) {
				if ( scope.interrupted() ) { return; }
				final IAgent a = (IAgent) s;
				if ( a != null && !a.dead() ) {
					scope.push(a);
					try {
						a.step(scope);
					} catch (final GamaRuntimeException g) {
						g.addAgent(a.getName());
						GAMA.reportError(g);
					} finally {
						scope.pop(a);
					}
				}
			}
		}

		@Override
		public void updateVariables(final IScope scope, final IAgent a) {
			super.updateVariables(scope, a);
		}

		@Override
		public IAgent getAgent(final Integer index) {
			// WARNING No verifications made.
			return (IAgent) GamaSpatialMatrix.this.matrix[index];
		}

		@Override
		public boolean isGrid() {
			return true;
		}

		@Override
		public void initializeFor(final IScope scope) throws GamaRuntimeException {
			topology.initialize(this);
		}

		@Override
		public IAgent getAgent(final ILocation coord) {
			return GamaSpatialMatrix.this.getAgentAt(coord);
		}

		@Override
		protected void computeTopology(final IScope scope) throws GamaRuntimeException {
			// Topology is already known. Nothing to do
		}

		@Override
		public void killMembers() throws GamaRuntimeException {
			for ( final IShape a : GamaSpatialMatrix.this.matrix ) {
				a.dispose();
			}
		}

		@Override
		public int size() {
			return GamaSpatialMatrix.this.matrix.length;
		}

		@Override
		public GamaList<IAgent> getAgentsList() {
			// WARNING matrix is supposed to contain IShapes. Normally, they are IAgents if we call it from here, but
			// it'd be better to check before
			return new GamaList.Array(matrix);
		}

		@Override
		public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
			if ( indices == null || indices.isEmpty() ) { return null; }
			return get(scope, Cast.asInt(scope, indices.get(0)));
		}

		@Override
		public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
			return (IAgent) matrix[index];
		}

		@Override
		public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
			for ( final IShape s : matrix ) {
				if ( s != null && s.equals(o) ) { return true; }
			}
			return false;
		}

		@Override
		public IAgent first(final IScope scope) throws GamaRuntimeException {
			return (IAgent) matrix[0];
		}

		@Override
		public IAgent last(final IScope scope) throws GamaRuntimeException {
			return (IAgent) matrix[size() - 1];
		}

		@Override
		public int length(final IScope scope) {
			return size();
		}

		@Override
		public IAgent any(final IScope scope) {
			return (IAgent) GamaSpatialMatrix.this.any(scope);
		}

		@Override
		public Iterator<IAgent> iterator() {
			return JavaUtils.iterator(matrix);
		}

		@Override
		public Iterable<IAgent> iterable(final IScope scope) {
			return this;
		}

		@Override
		public IList listValue(final IScope scope) throws GamaRuntimeException {
			return getAgentsList();
		}

		@Override
		public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
			return GamaSpatialMatrix.this;
		}

		@Override
		public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
			return GamaSpatialMatrix.this;
		}

		public class GridAgent extends GamlAgent {

			// WARNING HACK TO ACCELERATE SOME OF THE OPERATIONS OF GRIDS
			// WARNING THE PROBLEM IS THAT THESE AGENTS ARE BREAKING THE HIERARCHY

			public GridAgent(final int index) {
				super(GridPopulation.this);
				this.index = index;
				setGeometry(matrix[index]);
			}

			public GamaColor getColor() {
				if ( isHexagon ) { return (GamaColor) getAttribute(IKeyword.COLOR); }
				// final int index = GamaSpatialMatrix.this.getPlaceIndexAt(getLocation());
				return GamaColor.getInt(supportImagePixels[index]);
			}

			public void setColor(final GamaColor color) {
				if ( isHexagon ) {
					setAttribute(IKeyword.COLOR, color);
				} else {
					// final int index = GamaSpatialMatrix.this.getPlaceIndexAt(getLocation());
					supportImagePixels[index] = color.getRGB();
				}
			}

			public final int getX() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getX(getGeometry()); }
				return (int) (getLocation().getX() / cellWidth);
			}

			public final int getY() {
				if ( isHexagon() ) { return GamaSpatialMatrix.this.getY(getGeometry()); }
				return (int) (getLocation().getY() / cellHeight);
			}

			@Override
			protected Object stepSubPopulations(final IScope scope) {
				return null;
			}

			public double getValue() {
				return gridValue[index];
			}

		}

	}

}
