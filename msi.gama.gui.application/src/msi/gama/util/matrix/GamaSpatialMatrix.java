/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.matrix;

import java.util.*;
import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;
import msi.gaml.operators.Maths;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * This matrix contains geometries and can serve to organize the agents of a population as a grid in
 * the environment, or as a support for grid topologies
 */
public class GamaSpatialMatrix extends GamaMatrix<IGeometry> {

	public class SpatialMatrixIterator implements Iterator<IGeometry> {

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
		public IGeometry next() {
			return matrix[i++];
		}

		@Override
		public void remove() {}

	}

	/** The geometry of host. */
	public final GamaGeometry environmentFrame;
	final Envelope bounds;
	final double precision;

	protected IGeometry[] matrix;

	double cellWidth, cellHeight;
	public int[] supportImagePixels;
	protected Boolean usesVN = null;
	// protected Boolean isTorus = null; // TODO Deactivated for the moment
	protected GridDiffuser diffuser;
	public GridNeighbourhood neighbourhood;
	public static final short DIFFUSION = 0;
	public static final short GRADIENT = 1;
	public static int GRID_NUMBER = 0;
	int actualNumberOfCells;
	int firstCell, lastCell;
	SpatialMatrixIterator iterator = new SpatialMatrixIterator();

	public GamaSpatialMatrix(final IGeometry environment, final Integer cols, final Integer rows,
		final boolean usesVN) {
		super(cols, rows);
		environmentFrame = environment.getGeometry();
		bounds = environmentFrame.getEnvelope();
		cellWidth = bounds.getWidth() / cols;
		cellHeight = bounds.getHeight() / rows;
		precision = bounds.getWidth() / 1000;
		int size = numRows * numCols;
		createMatrix(size);
		supportImagePixels = new int[size];
		// this.isTorus = isTorus;
		this.usesVN = usesVN;
		GRID_NUMBER++;
		actualNumberOfCells = 0;
		firstCell = -1;
		lastCell = -1;
		createCells(false);
	}

	protected void createMatrix(final int size) {
		matrix = new IGeometry[size];
	}

	public void createCells(final boolean partialCells) {
		Geometry g = environmentFrame.getInnerGeometry();
		boolean isRectangle = g.isRectangle();
		GamaPoint p = new GamaPoint(0, 0);
		GamaPoint origin =
			new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope()
				.getMinY());

		Geometry translatedReferenceFrame = GeometricFunctions.translation(g, -origin.x, -origin.y);

		double cmx = cellWidth / 2;
		double cmy = cellHeight / 2;
		for ( int i = 0, n = numRows * numCols; i < n; i++ ) {
			int yy = i / numCols;
			int xx = i - yy * numCols;
			p.x = xx * cellWidth + cmx;
			p.y = yy * cellHeight + cmy;
			GamaGeometry rect = GamaGeometry.buildRectangle(cellWidth, cellHeight, p);
			boolean ok = isRectangle || rect.getInnerGeometry().coveredBy(translatedReferenceFrame);
			if ( partialCells && !ok &&
				rect.getInnerGeometry().intersects(translatedReferenceFrame) ) {
				rect.setInnerGeometry(rect.getInnerGeometry()
					.intersection(translatedReferenceFrame));
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
				usesVN ? new GridVonNeumannNeighbourhood(matrix, numCols, numRows/* , isTorus */)
					: new GridMooreNeighbourhood(matrix, numCols, numRows/* , isTorus */);
		}
		return neighbourhood;
	}

	public int[] getDisplayData() {
		return supportImagePixels;
	}

	public GamaColor getColor(final GamaPoint p) {
		int index = this.getPlaceIndexAt(p);
		if ( index == -1 ) { return null; }
		return GamaColor.getInt(supportImagePixels[index]);
	}

	public void setColor(final GamaPoint p, final GamaColor color) {
		int index = this.getPlaceIndexAt(p);
		if ( index != -1 ) {
			supportImagePixels[index] = color.getRGB();
		}
	}

	protected GamaPoint getOrigin() {
		Envelope e = environmentFrame.getEnvelope();
		return new GamaPoint(e.getMinX(), e.getMinY());
	}

	public final int getPlaceIndexAt(final int xx, final int yy) {
		// if ( isTorus ) { return (yy < 0 ? yy + numCols : yy) % numRows * numCols +
		// (xx < 0 ? xx + numCols : xx) % numCols; }
		if ( xx < 0 || xx >= numCols || yy < 0 || yy >= numRows ) {
			;
			return -1;
		}

		return yy * numCols + xx;
	}

	protected final int getPlaceIndexAt(final GamaPoint p) {
		final double xx = p.x == bounds.getMaxX() ? (p.x - precision) / cellWidth : p.x / cellWidth;
		final double yy =
			p.y == bounds.getMaxY() ? (p.y - precision) / cellHeight : p.y / cellHeight;
		final int x = (int) xx;
		final int y = (int) yy;
		int i = getPlaceIndexAt(x, y);
		return i;
	}

	public final int getX(final double xx) {
		return (int) (xx / cellWidth);
	}

	public final int getY(final double yy) {
		return (int) (yy / cellHeight);
	}

	public IGeometry getPlaceAt(final GamaPoint c) {
		if ( c == null ) { return null; }
		int p = getPlaceIndexAt(c);
		if ( p == -1 ) { return null; }
		return matrix[p];
	}

	public void diffuse(final IScope scope) throws GamaRuntimeException {
		diffuser.diffuse(scope);
	}

	@Override
	public void shuffleWith(final RandomAgent randomAgent) {
		// TODO not allowed for the moment (fixed grid)
		//
	}

	@Override
	public IGeometry get(final int col, final int row) {
		int index = getPlaceIndexAt(col, row);
		if ( index != -1 ) { return matrix[index]; }
		return null;
	}

	@Override
	public void put(final int col, final int row, final IGeometry obj) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)

	}

	@Override
	public IGeometry remove(final int col, final int row) {
		// TODO not allowed for the moment (fixed grid)
		return null;
	}

	@Override
	public boolean _removeFirst(final IGeometry o) throws GamaRuntimeException {
		// TODO not allowed for the moment (fixed grid)
		return false;

	}

	@Override
	public boolean _removeAll(final IGamaContainer<?, IGeometry> o) {
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
		for ( IGeometry a : this ) {
			if ( a.getAgent() != null ) {
				result.add(a.getAgent());
			} else {
				result.add(a);
			}
		}
		return result;
	}

	@Override
	public Iterator<IGeometry> iterator() {
		iterator.reset();
		return iterator;
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final GamaPoint preferredSize) {
		return this;
	}

	@Override
	public Integer _length() {
		return actualNumberOfCells;
	}

	@Override
	public IGeometry _first() {
		if ( firstCell == -1 ) { return null; }
		return matrix[firstCell];
	}

	@Override
	public IGeometry _last() {
		if ( lastCell == -1 ) { return null; }
		return matrix[lastCell];
	}

	@Override
	public IMatrix _reverse() throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgent _max() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgent _min() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatrix copy() {
		GamaSpatialMatrix result =
			new GamaSpatialMatrix(environmentFrame, numCols, numRows/* , isTorus */, usesVN);
		System.arraycopy(matrix, 0, result.matrix, 0, matrix.length);
		return result;
	}

	@Override
	public boolean _contains(final Object o) {
		return listValue(null).contains(o);
	}

	@Override
	public void _putAll(final IGeometry value, final Object param) throws GamaRuntimeException {
		// TODO Not allowed for the moment

	}

	@Override
	public Object _product() throws GamaRuntimeException {
		return listValue(null).product();
	}

	@Override
	public Object _sum() throws GamaRuntimeException {
		return listValue(null).sum();
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

	@Override
	public String toJava() {
		return "GamaMatrixType.from(" + Cast.toJava(new GamaList(this.matrix)) + ", false)";
	}

	@Override
	public boolean checkValue(final Object value) {
		return value == null || value instanceof IGeometry;
	}

	protected int distanceBetween(final Coordinate p1, final Coordinate p2) {
		// TODO ATTENTION ne tient pas compte de l'inclusion des points dans la matrice
		int dx = (int) (Maths.abs(p2.x - p1.x) / cellWidth);
		int dy = (int) (Maths.abs(p2.y - p1.y) / cellHeight);
		return dx + dy;
	}

	public int manhattanDistanceBetween(final IGeometry g1, final IGeometry g2) {
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
		final IGeometry shape, final Double distance) throws GamaRuntimeException {
		if ( shape.isPoint() || shape instanceof IAgent && ((IAgent) shape).getTopology() == t ) { return getNeighbourhood()
			.getNeighboursIn(getPlaceIndexAt(shape.getLocation()), distance.intValue()); }
		final GamaList<IAgent> coveredPlaces =
			t.getAgentsIn(shape, In.list(scope, listValue(scope)), true);
		Set<IAgent> result = new HashSet();
		for ( IAgent a : coveredPlaces ) {
			result.addAll(getNeighbourhood().getNeighboursIn(getPlaceIndexAt(a.getLocation()),
				distance.intValue()));
		}
		result.removeAll(coveredPlaces);
		return new GamaList(result);
	}

	public GamaPath computeShortestPathBetween(final IScope scope, final IGeometry source,
		final IGeometry target, final ITopology topo) throws GamaRuntimeException {
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
					List<GamaPoint> pts = new GamaList<GamaPoint>();
					pts.add(ag.getLocation());
					IAgent agDes = ag;
					while (cpt > 0) {
						cpt--;
						agDes = getNeighDesc(scope, agDes, dists, topo, cpt);
						pts.add(agDes.getLocation());
					}
					List<IGeometry> nodes = new GamaList<IGeometry>();
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
			if ( !dists.contains(ag) && !currentList.contains(ag) ) {
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
			if ( dists.contains(ag) && dists.get(ag).equals(Integer.valueOf(cpt)) ) { return ag; }
		}
		return null;
	}

	public final IAgent getAgentAt(final GamaPoint c) {
		IGeometry g = getPlaceAt(c);
		if ( g == null ) { return null; }
		return g.getAgent();
	}

	public void refreshDisplayData() throws GamaRuntimeException {
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				int index = j * this.numCols + i;
				IGeometry g = matrix[index];
				if ( g != null ) {
					IAgent a = g.getAgent();
					if ( a != null ) {
						supportImagePixels[index] =
							((GamaColor) a.getDirectVarValue("color")).getRGB();
					}
				}
			}
		}
	}

	public GridDiffuser getDiffuser(final IScope scope) {
		if ( diffuser != null ) { return diffuser; }
		diffuser = new GridDiffuser(matrix, getNeighbourhood(), cellWidth);
		scope.getSimulationScope().getScheduler().insertEndAction(this, "diffuse");
		return diffuser;
	}

	public void diffuseVariable(final IScope scope, final String name, final double value,
		final short type, final double prop, final double variation, final GamaPoint location,
		final double range) {
		getDiffuser(scope).diffuseVariable(name, value, type, prop, variation, location, range);
	}
}
