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
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;
import msi.gaml.compilation.ScheduledAction;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * This matrix contains geometries and can serve to organize the agents of a population as a grid in
 * the environment, or as a support for grid topologies
 */
public class GamaSpatialMatrix extends GamaMatrix<IShape> /* implements ISpatialIndex */implements
	ISpatialIndex {

	public class SpatialMatrixIterator implements Iterator<IShape> {

		int i, n;

		void reset() {
			i = firstCell;
			n = lastCell;
		}

		@Override
		public boolean hasNext() {
			while (i <= n && matrix[i] == null) {
				i++;
			}
			return i <= n;
		}

		@Override
		public IShape next() {
			return matrix[i++];
		}

		@Override
		public void remove() {}

	}

	/** The geometry of host. */
	public final IShape environmentFrame;
	final Envelope bounds;
	final double precision;

	protected IShape[] matrix;

	double cellWidth, cellHeight;
	public int[] supportImagePixels;
	protected Boolean usesVN = null;
	protected Boolean isTorus = null;

	protected Boolean isHexagon = null;
	protected GridDiffuser diffuser;
	public GridNeighbourhood neighbourhood;
	public static final short DIFFUSION = 0;
	public static final short GRADIENT = 1;
	public static int GRID_NUMBER = 0;
	int actualNumberOfCells;
	int firstCell, lastCell;
	SpatialMatrixIterator iterator = new SpatialMatrixIterator();
	private ISpecies cellSpecies;
	private IAgentFilter cellFilter;

	GamaMap hexAgentToLoc = null;

	public GamaSpatialMatrix(final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN) throws GamaRuntimeException {
		super(cols, rows);
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		precision = bounds.getWidth() / 1000;
		int size = numRows * numCols;
		createMatrix(size);
		supportImagePixels = new int[size];
		this.isTorus = isTorus;
		this.usesVN = usesVN;
		GRID_NUMBER++;
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		this.isHexagon = false;
		createCells(false);
	}

	public GamaSpatialMatrix(final IShape environment, final Integer cols, final Integer rows,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon)
		throws GamaRuntimeException {
		super(cols, rows);
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		precision = bounds.getWidth() / 1000;
		int size = 2 * numRows * numCols;
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

	protected void createMatrix(final int size) {
		matrix = new IShape[size];
	}

	public void createHexagons(final boolean partialCells) {
		double widthEnv = environmentFrame.getEnvelope().getWidth();
		double heightEnv = environmentFrame.getEnvelope().getHeight();
		double xmin = environmentFrame.getEnvelope().getMinX();
		double ymin = environmentFrame.getEnvelope().getMinY();
		GamaShape gbg = new GamaShape(environmentFrame.getInnerGeometry().buffer(0.1, 2));
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
				GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight, new GamaPoint(
						xmin + c * cellWidth * 0.75, ymin + l * cellHeight));
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
				GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(cellWidth, cellHeight, new GamaPoint(
						xmin + c * cellWidth * 0.75, ymin + (l + 0.5) * cellHeight));
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

	public void createCells(final boolean partialCells) throws GamaRuntimeException {
		// Geometry g = environmentFrame.getInnerGeometry();
		boolean isRectangle = environmentFrame.getInnerGeometry().isRectangle();
		GamaPoint p = new GamaPoint(0, 0);
		GamaPoint origin =
			new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope()
				.getMinY());

		IShape translatedReferenceFrame =
			Spatial.Transformations.translated_by(environmentFrame, origin);
		// GeometryUtils.translation(g, -origin.x, -origin.y);

		double cmx = cellWidth / 2;
		double cmy = cellHeight / 2;
		for ( int i = 0, n = numRows * numCols; i < n; i++ ) {
			int yy = i / numCols;
			int xx = i - yy * numCols;
			p.x = xx * cellWidth + cmx;
			p.y = yy * cellHeight + cmy;
			IShape rect = GamaGeometryType.buildRectangle(cellWidth, cellHeight, p);
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

	public GridNeighbourhood getNeighbourhood() {
		if ( neighbourhood == null ) {
			neighbourhood =
				isHexagon ? new GridHexagonalNeighbourhood(matrix, numCols, numRows, isTorus)
					: usesVN ? new GridVonNeumannNeighbourhood(matrix, numCols, numRows, isTorus)
						: new GridMooreNeighbourhood(matrix, numCols, numRows, isTorus);
		}
		return neighbourhood;
	}

	public int[] getDisplayData() {
		return supportImagePixels;
	}

	public GamaColor getColor(final ILocation p) {
		int index = this.getPlaceIndexAt(p);
		if ( index == -1 ) { return null; }
		return GamaColor.getInt(supportImagePixels[index]);
	}

	public void setColor(final ILocation p, final GamaColor color) {
		int index = this.getPlaceIndexAt(p);
		if ( index != -1 ) {
			supportImagePixels[index] = color.getRGB();
		}
	}

	protected ILocation getOrigin() {
		Envelope e = environmentFrame.getEnvelope();
		return new GamaPoint(e.getMinX(), e.getMinY());
	}

	public final int getPlaceIndexAt(final int xx, final int yy) {
		if ( isHexagon ) { return yy * numCols + xx; }
		if ( isTorus ) { return (yy < 0 ? yy + numCols : yy) % numRows * numCols +
			(xx < 0 ? xx + numCols : xx) % numCols; }
		if ( xx < 0 || xx >= numCols || yy < 0 || yy >= numRows ) {
			;
			return -1;
		}

		return yy * numCols + xx;
	}

	protected final int getPlaceIndexAt(final ILocation p) {
		if ( isHexagon ) {
			final int xx = (int) (p.getX() / (cellWidth * 0.75));
			final int yy =
				xx % 2 == 0 ? (int) (p.getY() / cellHeight)
					: (int) ((p.getY() - cellHeight) / cellHeight);

			int i = getPlaceIndexAt(xx, yy);
			if ( matrix[i] == null ) { return -1; }
			if ( matrix[i].getLocation() == p ) { return i; }
			List<Integer> toObserve =
				GridHexagonalNeighbourhood.getNeighboursAtRadius1(i, numCols, numRows, isTorus);
			toObserve.add(i);
			double dMin = Double.MAX_VALUE;
			int x = 0, y = 0;
			for ( Integer id : toObserve ) {
				IShape sh = matrix[id];
				if ( sh == null ) {
					continue;
				}
				double dist = sh.getLocation().euclidianDistanceTo(p);
				if ( dist < dMin ) {
					dMin = dist;
					GamaPoint pt = (GamaPoint) hexAgentToLoc.get(sh);
					x = (int) pt.x;
					y = (int) pt.y;
				}
			}
			i = getPlaceIndexAt(x, y);
			return i;

		}
		double px = p.getX();
		double py = p.getY();
		final double xx = px == bounds.getMaxX() ? (px - precision) / cellWidth : px / cellWidth;
		final double yy = py == bounds.getMaxY() ? (py - precision) / cellHeight : py / cellHeight;
		final int x = (int) xx;
		final int y = (int) yy;
		int i = getPlaceIndexAt(x, y);
		return i;
	}

	public final int getX(final double xx) {
		return (int) (xx / cellWidth);
	}

	public final int getX(final IShape shape) {
		return (int) ((GamaPoint) hexAgentToLoc.get(shape)).x;
	}

	public final int getY(final IShape shape) {
		return (int) ((GamaPoint) hexAgentToLoc.get(shape)).y;
	}

	public final int getY(final double yy) {
		return (int) (yy / cellHeight);
	}

	public IShape getPlaceAt(final ILocation c) {
		if ( c == null ) { return null; }
		int p = getPlaceIndexAt(c);
		if ( p == -1 ) { return null; }
		return matrix[p];
	}

	public void diffuse(final IScope scope) throws GamaRuntimeException {
		diffuser.diffuse(scope);
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		// TODO not allowed for the moment (fixed grid)
		//
	}

	@Override
	public IShape get(final int col, final int row) {
		int index = getPlaceIndexAt(col, row);
		if ( index != -1 ) { return matrix[index]; }
		return null;
	}

	@Override
	public void set(final int col, final int row, final Object obj) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
	}

	@Override
	public IShape remove(final int col, final int row) {
		// TODO not allowed for the moment (fixed grid)
		return null;
	}

	@Override
	public boolean _removeFirst(final IShape o) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
		return false;

	}

	@Override
	public boolean _removeAll(final IContainer<?, IShape> value) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
		return false;
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, null);
	}

	@Override
	protected GamaList _listValue(final IScope scope) {
		GamaList result = new GamaList(actualNumberOfCells);
		if ( actualNumberOfCells == 0 ) { return new GamaList(); }
		for ( IShape a : this ) {
			IAgent ag = a.getAgent();
			result.add(ag == null ? a : ag);
		}
		return result;
	}

	@Override
	public Iterator<IShape> iterator() {
		iterator.reset();
		return iterator;
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize) {
		return this;
	}

	@Override
	public Integer _length() {
		return actualNumberOfCells;
	}

	@Override
	public IShape _first() {
		if ( firstCell == -1 ) { return null; }
		return matrix[firstCell];
	}

	@Override
	public IShape _last() {
		if ( lastCell == -1 ) { return null; }
		return matrix[lastCell];
	}

	@Override
	public IMatrix _reverse() throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgent _max(final IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgent _min(final IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatrix copy() throws GamaRuntimeException {
		GamaSpatialMatrix result =
			new GamaSpatialMatrix(environmentFrame, numCols, numRows, isTorus, usesVN);
		return result;
	}

	@Override
	public boolean _contains(final Object o) {
		return listValue(null).contains(o);
	}

	@Override
	public void _putAll(final IShape value, final Object param) throws GamaRuntimeException {
		// TODO Not allowed for the moment

	}

	@Override
	public Object _product(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).product(scope);
	}

	@Override
	public Object _sum(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).sum(scope);
	}

	@Override
	public boolean _isEmpty() {
		return actualNumberOfCells == 0;
	}

	// public void setTorus(final Boolean isTorus2) {
	// isTorus = isTorus2;
	// }

	@Override
	public String toGaml() {
		return new GamaList(this.matrix).toGaml() + " as_spatial_matrix";
	}

	// @Override
	// public String toJava() {
	// return "GamaMatrixType.from(" + Cast.toJava(new GamaList(this.matrix)) + ", false)";
	// }

	@Override
	public boolean checkValue(final Object value) {
		return value == null || value instanceof IShape;
	}

	protected int distanceBetween(final Coordinate p1, final Coordinate p2) {
		// TODO ATTENTION ne tient pas compte de l'inclusion des points dans la matrice
		int dx = (int) (Maths.abs(p2.x - p1.x) / cellWidth);
		int dy = (int) (Maths.abs(p2.y - p1.y) / cellHeight);
		return dx + dy;
	}

	public int manhattanDistanceBetween(final IShape g1, final IShape g2) {
		// TODO ATTENTION ne tient pas compte du voisinage de Moore
		Coordinate[] coord =
			new DistanceOp(g1.getInnerGeometry(), g2.getInnerGeometry()).nearestPoints();
		return distanceBetween(coord[0], coord[1]);
	}

	/**
	 * @throws GamaRuntimeException
	 *             Returns the cells making up the neighbourhood of a geometrical shape. First, the
	 *             cells
	 *             covered by this shape are computed, then their neighbours are collated (excluding
	 *             the
	 *             previous ones). A special case is made for point geometries and for agents
	 *             contained in this
	 *             matrix.
	 * @param source
	 * @param distance
	 * @return
	 */
	public GamaList<IAgent> getNeighboursOf(final IScope scope, final ITopology t,
		final IShape shape, final Double distance) {
		if ( shape.isPoint() || shape.getAgent() != null &&
			shape.getAgent().getSpecies() == cellSpecies ) { return getNeighboursOf(scope, t,
			shape.getLocation(), distance); }
		final Collection<? extends IAgent> coveredPlaces =
			getAgentsCoveredBy(shape, cellFilter, true);
		Set<IAgent> result = new HashSet();
		for ( IAgent a : coveredPlaces ) {
			result.addAll(getNeighbourhood().getNeighboursIn(getPlaceIndexAt(a.getLocation()),
				distance.intValue()));
		}
		result.removeAll(coveredPlaces);
		return new GamaList(result);
	}

	public GamaList<IAgent> getNeighboursOf(final IScope scope, final ITopology t,
		final ILocation shape, final Double distance) {
		return getNeighbourhood().getNeighboursIn(getPlaceIndexAt(shape), distance.intValue());
	}

	public IPath computeShortestPathBetween(final IScope scope, final IShape source,
		final IShape target, final ITopology topo) throws GamaRuntimeException {
		GamaMap dists = new GamaMap();
		int currentplace = getPlaceIndexAt(source.getLocation());
		int targetplace = getPlaceIndexAt(target.getLocation());
		IAgent startAg = matrix[currentplace].getAgent();
		IAgent endAg = matrix[targetplace].getAgent();

		int cpt = 0;
		dists.put(startAg, Integer.valueOf(cpt));
		int max = this.numCols * this.numRows;
		List<IAgent> neighb = getNeighs(scope, startAg, dists, topo, new GamaList());
		while (true) {
			cpt++;
			HashSet<IAgent> neighb2 = new HashSet<IAgent>();
			for ( IAgent ag : neighb ) {
				dists.put(ag, Integer.valueOf(cpt));
				if ( ag == endAg ) {
					List<ILocation> pts = new GamaList();
					pts.add(ag.getLocation());
					IAgent agDes = ag;
					while (cpt > 0) {
						cpt--;
						agDes = getNeighDesc(scope, agDes, dists, topo, cpt);
						pts.add(agDes.getLocation());
					}
					List<ILocation> nodes = new GamaList();
					for ( int i = pts.size() - 1; i >= 0; i-- ) {
						nodes.add(pts.get(i));
					}
					return new GamaPath(topo, nodes);
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
	private List<IAgent> getNeighs(final IScope scope, final IAgent agent, final GamaMap dists,
		final ITopology t, final List<IAgent> currentList) throws GamaRuntimeException {
		List<IAgent> agents = getNeighboursOf(scope, t, agent.getLocation(), 1.0);
		List<IAgent> neighs = new GamaList<IAgent>();
		for ( IAgent ag : agents ) {
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
	private IAgent getNeighDesc(final IScope scope, final IAgent agent, final GamaMap dists,
		final ITopology t, final int cpt) throws GamaRuntimeException {
		List<IAgent> agents = getNeighboursOf(scope, t, agent.getLocation(), 1.0);
		Collections.shuffle(agents);
		for ( IAgent ag : agents ) {
			if ( dists.contains(scope, ag) && dists.get(ag).equals(Integer.valueOf(cpt)) ) { return ag; }
		}
		return null;
	}

	public final IAgent getAgentAt(final ILocation c) {
		IShape g = getPlaceAt(c);
		if ( g == null ) { return null; }
		return g.getAgent();
	}

	public void refreshDisplayData(final IScope scope) throws GamaRuntimeException {
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				int index = j * this.numCols + i;
				IShape g = matrix[index];
				if ( g != null ) {
					IAgent a = g.getAgent();
					if ( a != null ) {
						supportImagePixels[index] =
							((GamaColor) a.getDirectVarValue(scope, IKeyword.COLOR)).getRGB();
					}
				}
			}
		}
	}

	public GridDiffuser getDiffuser(final IScope scope) {
		if ( diffuser != null ) { return diffuser; }
		diffuser = new GridDiffuser(matrix, getNeighbourhood(), cellWidth);
		scope.getSimulationScope().getScheduler().insertEndAction(new ScheduledAction() {

			@Override
			public void execute(final IScope scope) throws GamaRuntimeException {
				diffuse(scope);
			}

		});
		return diffuser;
	}

	public void diffuseVariable(final IScope scope, final String name, final double value,
		final short type, final double prop, final double variation, final ILocation location,
		final double range) {
		getDiffuser(scope).diffuseVariable(name, value, type, prop, variation, location, range);
	}

	/**
	 * @param source
	 * @param f
	 * @param covered
	 * @return
	 */
	public Collection<? extends IAgent> getAgentsCoveredBy(final IShape source,
		final IAgentFilter f, final boolean covered) {
		if ( !f.filterSpecies(cellSpecies) ) { return Collections.EMPTY_LIST; }
		Envelope env = source.getEnvelope();
		return (Collection<? extends IAgent>) allInEnvelope(source, env, f, covered);
	}

	/**
	 * @param species
	 */
	public void setCellSpecies(final IPopulation pop) {
		cellSpecies = pop.getSpecies();
		cellFilter = In.population(pop);
	}

	public Boolean getIsHexagon() {
		return isHexagon;
	}

	public List<IAgent> getAgents() {
		List<IAgent> agents = new GamaList<IAgent>();
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] != null ) {
				agents.add(matrix[i].getAgent());
			}
		}
		return agents;
	}

	@Override
	public void insert(final IShape a) {

	}

	@Override
	public void remove(final IShape previous, final IShape a) {

	}

	// @Override
	// public void insert(final Envelope bounds, final IShape o) {}
	//
	// @Override
	// public void insert(final Coordinate location, final IShape agent) {}
	//
	// @Override
	// public void remove(final Envelope bounds, final IShape o) {}
	//
	// @Override
	// public void remove(final Coordinate previousLoc, final IShape agent) {}
	//
	@Override
	public IList<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		Collection<IShape> set = allInEnvelope(source, ENVELOPE, f, false);
		GamaList external_results = new GamaList();
		for ( IShape a : set ) {
			if ( source.euclidianDistanceTo(a) < dist ) {
				external_results.add(a);
			}
		}
		return external_results;
	}

	// @Override
	// public IList<IShape> allAtDistance(final ILocation source, final double dist,
	// final IAgentFilter f) {
	// double exp = dist * Maths.SQRT2;
	// ENVELOPE.init(source.getEnvelope());
	// ENVELOPE.expandBy(exp);
	// Collection<IShape> set = allInEnvelope(source, ENVELOPE, f, false);
	// GamaList external_results = new GamaList();
	// for ( IShape a : set ) {
	// if ( source.euclidianDistanceTo(a) < dist ) {
	// external_results.add(a);
	// }
	// }
	// return external_results;
	// }

	@Override
	public IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		Collection<IShape> in_square = allInEnvelope(source, ENVELOPE, f, false);
		double min_distance = dist;
		IShape min_agent = null;
		for ( IShape a : in_square ) {
			Double dd = source.euclidianDistanceTo(a);
			if ( dd < min_distance ) {
				min_distance = dd;
				min_agent = a;
			}
		}
		return min_agent;
	}

	//
	// @Override
	// public IShape firstAtDistance(final ILocation source, final double dist, final IAgentFilter
	// f) {
	// double exp = dist * Maths.SQRT2;
	// ENVELOPE.init(source.getEnvelope());
	// ENVELOPE.expandBy(exp);
	// Collection<IShape> in_square = allInEnvelope(source, ENVELOPE, f, false);
	// double min_distance = dist;
	// IShape min_agent = null;
	// for ( IShape a : in_square ) {
	// Double dd = source.euclidianDistanceTo(a);
	// if ( dd < min_distance ) {
	// min_distance = dd;
	// min_agent = a;
	// }
	// }
	// return min_agent;
	// }

	@Override
	public IList<IShape> allInEnvelope(final IShape source, final Envelope env,
		final IAgentFilter f, final boolean covered) {
		// if ( !f.filterSpecies(cellSpecies) ) { return GamaList.EMPTY_LIST; }
		int minX = getX(env.getMinX());
		int minY = getY(env.getMinY());
		int maxX = getX(env.getMaxX());
		int maxY = getY(env.getMaxY());
		IList<IShape> ags = new GamaList();
		// BUGFIX AD 28/01/13 Changed "<" into "<="
		for ( int i = minX; i <= maxX; i++ ) {
			for ( int j = minY; j <= maxY; j++ ) {
				int index = getPlaceIndexAt(i, j);
				// BUGFIX AD 28/01/13 Changed "1" into "-1"
				if ( index != -1 ) {
					IAgent a = matrix[index].getAgent();
					if ( a != null ) {
						if ( covered && env.covers(a.getEnvelope()) || !covered &&
							env.intersects(a.getEnvelope()) ) {
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

	// @Override
	// public void update() {}

	// @Override
	// public void cleanCache() {}

}
