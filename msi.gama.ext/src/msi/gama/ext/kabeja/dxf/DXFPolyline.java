/*******************************************************************************************************
 *
 * DXFPolyline.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.helpers.Point;
import msi.gama.ext.kabeja.dxf.helpers.Vector;
import msi.gama.ext.kabeja.math.MathUtils;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 *
 */
public class DXFPolyline extends DXFEntity {

	/** The Constant QUARTER_CIRCLE_ANGLE. */
	protected static final double QUARTER_CIRCLE_ANGLE = Math.tan(0.39269908169872414D);

	/** The vertices. */
	protected ArrayList<DXFVertex> vertices = new ArrayList<>();

	/** The start width. */
	protected double startWidth = 0.0;

	/** The end width. */
	protected double endWidth = 0.0;

	/** The constant width. */
	protected boolean constantWidth = true;

	/** The sureface type. */
	protected int surefaceType = 0;

	/** The sureface density rows. */
	protected int surefaceDensityRows = 0;

	/** The sureface density columns. */
	protected int surefaceDensityColumns = 0;

	/** The rows. */
	protected int rows = 0;

	/** The columns. */
	protected int columns = 0;

	/**
	 *
	 */
	public DXFPolyline() {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.dxf.DXFEntity#updateViewPort()
	 */
	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();

		Iterator i = vertices.iterator();

		if (i.hasNext()) {
			DXFVertex last;
			DXFVertex first;
			DXFVertex v = null;

			last = first = (DXFVertex) i.next();
			bounds.addToBounds(last.getPoint());

			while (i.hasNext()) {
				v = (DXFVertex) i.next();
				addToBounds(last, v, bounds);
				last = v;
			}

			if (v != null && v.getBulge() != 0.0) { addToBounds(v, first, bounds); }
		} else {
			bounds.setValid(false);
		}

		return bounds;
	}

	/**
	 * Adds the vertex.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void addVertex(final DXFVertex vertex) {
		vertices.add(vertex);

		if (!vertex.isConstantWidth()) { constantWidth = false; }
	}

	/**
	 * Gets the vertex count.
	 *
	 * @return the vertex count
	 */
	public int getVertexCount() { return this.vertices.size(); }

	/**
	 * Gets the vertex iterator.
	 *
	 * @return the vertex iterator
	 */
	public Iterator getVertexIterator() { return this.vertices.iterator(); }

	/**
	 * Removes the vertex.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void removeVertex(final DXFVertex vertex) {
		// remove and check the constantwidth
		constantWidth = true;

		Iterator i = vertices.iterator();

		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();

			if (v == vertex) {
				i.remove();
			} else if (!v.isConstantWidth()) { constantWidth = false; }
		}
	}

	/**
	 * Removes the vertex.
	 *
	 * @param index
	 *            the index
	 */
	public void removeVertex(final int index) {
		constantWidth = true;

		for (int i = 0; i < vertices.size(); i++) {
			DXFVertex v = vertices.get(i);

			if (index == i) {
				vertices.remove(i);
			} else if (!v.isConstantWidth()) { constantWidth = false; }
		}
	}

	/**
	 * Gets the vertex.
	 *
	 * @param i
	 *            the i
	 * @return the vertex
	 */
	public DXFVertex getVertex(final int i) {
		return vertices.get(i);
	}

	/**
	 * Returns the distance between 2 DXFPoints
	 *
	 * @param start
	 * @param end
	 * @return the length between the two points
	 */
	protected double getLength(final DXFPoint start, final DXFPoint end) {
		return Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
	}

	/**
	 * Caculate the radius of a cut circle segment between 2 DXFVertex
	 *
	 * @param bulge
	 *            the vertex bulge
	 * @param length
	 *            the length of the circle cut
	 */
	public double getRadius(final double bulge, final double length) {
		double h = bulge * length / 2;
		double value = h / 2 + Math.pow(length, 2) / (8 * h);

		return Math.abs(value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_POLYLINE; }

	/**
	 * @return Returns the endWidth.
	 */
	public double getEndWidth() { return endWidth; }

	/**
	 * @param endWidth
	 *            The endWidth to set.
	 */
	public void setEndWidth(final double endWidth) { this.endWidth = endWidth; }

	/**
	 * @return Returns the startWidth.
	 */
	public double getStartWidth() { return startWidth; }

	/**
	 * @param startWidth
	 *            The startWidth to set.
	 */
	public void setStartWidth(final double startWidth) { this.startWidth = startWidth; }

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed() {
		// the closed Flag
		return (this.flags & 1) == 1;
	}

	/**
	 * Checks if is curve fit vertices added.
	 *
	 * @return true, if is curve fit vertices added
	 */
	public boolean isCurveFitVerticesAdded() { return (this.flags & 2) == 2; }

	/**
	 * Checks if is spline fit vertices added.
	 *
	 * @return true, if is spline fit vertices added
	 */
	public boolean isSplineFitVerticesAdded() { return (this.flags & 4) == 4; }

	/**
	 * Checks if is 3 D polygon.
	 *
	 * @return true, if is 3 D polygon
	 */
	public boolean is3DPolygon() {
		return (this.flags & 8) == 8;
	}

	/**
	 * Checks if is 3 D polygon mesh.
	 *
	 * @return true, if is 3 D polygon mesh
	 */
	public boolean is3DPolygonMesh() {
		return (this.flags & 16) == 16;
	}

	/**
	 * Checks if is polyface mesh.
	 *
	 * @return true, if is polyface mesh
	 */
	public boolean isPolyfaceMesh() { return (this.flags & 64) == 64; }

	/**
	 * Checks if is closed mesh N direction.
	 *
	 * @return true, if is closed mesh N direction
	 */
	public boolean isClosedMeshNDirection() { return (this.flags & 32) == 32; }

	/**
	 * Checks if is closed mesh M direction.
	 *
	 * @return true, if is closed mesh M direction
	 */
	public boolean isClosedMeshMDirection() { return (this.flags & 1) == 1; }

	/**
	 * Checks if is quad spline.
	 *
	 * @return true, if is quad spline
	 */
	public boolean isQuadSpline() {
		if (isSplineFitVerticesAdded()) return this.surefaceType == 5;

		return false;
	}

	/**
	 * Checks if is cubic spline.
	 *
	 * @return true, if is cubic spline
	 */
	public boolean isCubicSpline() {
		if (isSplineFitVerticesAdded()) return this.surefaceType == 6;

		return false;
	}

	/**
	 * Checks if is constant width.
	 *
	 * @return true, if is constant width
	 */
	public boolean isConstantWidth() {
		// TODO review to see if the
		// property is always set correct
		if (!this.constantWidth) return false;
		this.constantWidth = true;

		Iterator i = vertices.iterator();

		while (i.hasNext()) {
			DXFVertex vertex = (DXFVertex) i.next();

			if (!vertex.isConstantWidth()) {
				this.constantWidth = false;

				return this.constantWidth;
			}
		}

		return this.constantWidth;
	}

	/**
	 * @return Returns the surefaceType.
	 */
	public int getSurefaceType() { return surefaceType; }

	/**
	 * @param surefaceType
	 *            The surefaceType to set.
	 */
	public void setSurefaceType(final int surefaceType) { this.surefaceType = surefaceType; }

	/**
	 * @return Returns the columns.
	 */
	public int getSurefaceDensityColumns() { return surefaceDensityColumns; }

	/**
	 * @param columns
	 *            The columns to set.
	 */
	public void setSurefaceDensityColumns(final int columns) { this.surefaceDensityColumns = columns; }

	/**
	 * @return Returns the rows.
	 */
	public int getSurefaceDensityRows() { return surefaceDensityRows; }

	/**
	 * @param rows
	 *            The rows to set.
	 */
	public void setSurefaceDensityRows(final int rows) { this.surefaceDensityRows = rows; }

	/**
	 * Adds the to bounds.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param bounds
	 *            the bounds
	 */
	protected void addToBounds(final DXFVertex start, final DXFVertex end, final Bounds bounds) {
		if (start.getBulge() != 0) {
			// calculte the height
			double l = MathUtils.distance(start.getPoint(), end.getPoint());

			// double h = Math.abs(last.getBulge()) * l / 2;
			double r = this.getRadius(start.getBulge(), l);

			double s = l / 2;
			Vector edgeDirection = MathUtils.getVector(start.getPoint(), end.getPoint());
			edgeDirection = MathUtils.normalize(edgeDirection);

			Point centerPoint = MathUtils.getPointOfStraightLine(start.getPoint(), edgeDirection, s);

			Vector centerPointDirection = MathUtils.crossProduct(edgeDirection, this.getExtrusion().getNormal());
			centerPointDirection = MathUtils.normalize(centerPointDirection);

			// double t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
			// double t = 0;
			double h = Math.abs(start.getBulge() * l) / 2;

			// if(Math.abs(start.getBulge())>=1.0){
			// t = h-r;
			// }else{
			// //t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
			// t=r-h;
			// }
			// the center point of the arc
			int startQ = 0;
			int endQ = 0;

			double bulge = start.getBulge();

			if (bulge > 0) {
				// the arc goes over the right side, but where is the center
				// point?
				if (bulge > 1.0) {
					double t = h - r;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, t);
				} else {
					double t = r - h;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, -1 * t);
				}

				endQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
				startQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);
			} else {
				// the arc goes over the left side, but where is the center
				// point?
				if (bulge < -1.0) {
					double t = h - r;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, -1 * t);
				} else {
					double t = r - h;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, t);
				}

				startQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
				endQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);
			}

			if (endQ < startQ || endQ == startQ && Math.abs(start.getBulge()) > QUARTER_CIRCLE_ANGLE) { endQ += 4; }

			while (endQ > startQ) {
				switch (startQ) {
					case 0:
						bounds.addToBounds(centerPoint.getX(), centerPoint.getY() + r, centerPoint.getZ());

						break;

					case 1:
						bounds.addToBounds(centerPoint.getX() - r, centerPoint.getY(), centerPoint.getZ());

						break;

					case 2:
						bounds.addToBounds(centerPoint.getX(), centerPoint.getY() - r, centerPoint.getZ());

						break;

					case 3:
						bounds.addToBounds(centerPoint.getX() + r, centerPoint.getY(), centerPoint.getZ());
						endQ -= 4;
						startQ -= 4;

						break;
				}

				startQ++;
			}
		}

		bounds.addToBounds(start.getPoint());
		bounds.addToBounds(end.getPoint());
	}

	/**
	 * Gets the poly face mesh vertex.
	 *
	 * @param index
	 *            the index
	 * @return the poly face mesh vertex
	 */
	public DXFVertex getPolyFaceMeshVertex(final int index) {
		Iterator i = this.vertices.iterator();
		int count = 1;

		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();

			if (v.isPolyFaceMeshVertex()) {
				if (count == index) return v;
				count++;
			}
		}

		return null;
	}

	/**
	 * @return Returns the column.
	 */
	public int getColumns() { return columns; }

	/**
	 * @param column
	 *            The column to set.
	 */
	public void setColumns(final int column) { this.columns = column; }

	/**
	 * @return Returns the rows.
	 */
	public int getRows() { return rows; }

	/**
	 * @param rows
	 *            The rows to set.
	 */
	public void setRows(final int rows) { this.rows = rows; }

	/**
	 * Checks if is simple mesh.
	 *
	 * @return true, if is simple mesh
	 */
	public boolean isSimpleMesh() { return this.surefaceType == 0 && (this.flags & 4) == 0; }

	/**
	 * Checks if is quad sureface mesh.
	 *
	 * @return true, if is quad sureface mesh
	 */
	public boolean isQuadSurefaceMesh() { return this.surefaceType == 5 && (this.flags & 4) == 4; }

	/**
	 * Checks if is cubic sureface mesh.
	 *
	 * @return true, if is cubic sureface mesh
	 */
	public boolean isCubicSurefaceMesh() { return this.surefaceType == 6 && (this.flags & 4) == 4; }

	/**
	 * Checks if is bezier sureface mesh.
	 *
	 * @return true, if is bezier sureface mesh
	 */
	public boolean isBezierSurefaceMesh() { return this.surefaceType == 8 && (this.flags & 4) == 4; }

	@Override
	public double getLength() {
		double length = 0.0;

		if (isCubicSpline() || isQuadSpline()) return getSplineApproximationLength();
		if (isPolyfaceMesh()) return getPolyfaceLength();
		if (is3DPolygonMesh() || isBezierSurefaceMesh() || isCubicSurefaceMesh())
			return getMeshLength();
		else {
			// a normal polyline with or without bulges
			Iterator i = this.vertices.iterator();
			DXFVertex first;
			DXFVertex last = first = (DXFVertex) i.next();

			while (i.hasNext()) {
				DXFVertex v = (DXFVertex) i.next();
				length += this.getSegmentLength(last, v);
				last = v;
			}

			if (this.isClosed()) { length += this.getSegmentLength(last, first); }
		}

		return length;
	}

	/**
	 * Gets the segment length.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the segment length
	 */
	protected double getSegmentLength(final DXFVertex start, final DXFVertex end) {
		double l = MathUtils.distance(start.getPoint(), end.getPoint());

		if (start.getBulge() == 0.0) return l;
		double alpha = 4 * Math.atan(Math.abs(start.getBulge()));

		double r = l / (2 * Math.sin(alpha / 2));
		return Math.PI * Math.toDegrees(alpha) * r / 180;
	}

	/**
	 * Gets the spline approximation length.
	 *
	 * @return the spline approximation length
	 */
	protected double getSplineApproximationLength() {
		double length = 0.0;

		// use the approximation
		Iterator i = this.vertices.iterator();
		DXFVertex first;
		DXFVertex last = first = null;

		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();

			if (v.is2DSplineApproximationVertex()) {
				if (first == null) {
					first = last = v;
				} else {
					length += getSegmentLength(last, v);
					last = v;
				}
			}
		}

		if (this.isClosed()) { length += getSegmentLength(last, first); }

		return length;
	}

	/**
	 * Gets the polyface length.
	 *
	 * @return the polyface length
	 */
	protected double getPolyfaceLength() {
		double length = 0.0;
		Iterator i = this.vertices.iterator();

		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();

			if (v.isFaceRecord()) {
				DXFVertex v1 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex0());
				DXFVertex v2 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex1());
				DXFVertex v3 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex2());
				DXFVertex v4 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex3());

				if (v.isPolyFaceEdge0Visible() && v.getPolyFaceMeshVertex0() != 0) {
					length += getSegmentLength(v1, v2);
				}

				if (v.isPolyFaceEdge1Visible() && v.getPolyFaceMeshVertex1() != 0) {
					length += getSegmentLength(v2, v3);
				}

				if (v.isPolyFaceEdge2Visible() && v.getPolyFaceMeshVertex2() != 0) {
					length += getSegmentLength(v3, v4);
				}

				if (v.isPolyFaceEdge3Visible() && v.getPolyFaceMeshVertex3() != 0) {
					length += getSegmentLength(v4, v1);
				} else if (v4 == null && v3 != null) {
					// triangle
					length += getSegmentLength(v3, v1);
				}
			}
		}

		return length;
	}

	/**
	 * Gets the mesh length.
	 *
	 * @return the mesh length
	 */
	protected double getMeshLength() {
		double length = 0.0;

		if (isSimpleMesh()) {
			DXFVertex[][] points = new DXFVertex[this.rows][this.columns];
			Iterator it = this.vertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.rows; i++) {
				for (int x = 0; x < this.columns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v;

					if (x > 0) { length += getSegmentLength(points[i][x - 1], points[i][x]); }
				}

				if (isClosedMeshNDirection()) {
					length += getSegmentLength(points[i][points[i].length - 1], points[i][0]);
				}
			}

			// create a line for each column
			for (int i = 0; i < this.columns; i++) {
				for (int x = 0; x < this.rows; x++) {
					if (x > 0) { length += getSegmentLength(points[x - 1][i], points[x][i]); }
				}

				if (isClosedMeshMDirection()) {
					length += getSegmentLength(points[points[i].length - 1][i], points[0][i]);
				}
			}
		} else {
			DXFVertex[][] points = new DXFVertex[this.surefaceDensityRows][this.surefaceDensityColumns];
			Iterator vi = this.vertices.iterator();
			List<DXFVertex> appVertices = new ArrayList<>();

			while (vi.hasNext()) {
				DXFVertex v = (DXFVertex) vi.next();

				if (v.isMeshApproximationVertex()) { appVertices.add(v); }
			}

			Iterator it = appVertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.surefaceDensityRows; i++) {
				for (int x = 0; x < this.surefaceDensityColumns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v;

					if (x > 0) { length += getSegmentLength(points[i][x - 1], points[i][x]); }
				}

				if (isClosedMeshNDirection()) {
					length += getSegmentLength(points[i][points[i].length - 1], points[i][0]);
				}
			}

			// create a line for each column
			for (int i = 0; i < this.surefaceDensityColumns; i++) {
				for (int x = 0; x < this.surefaceDensityRows; x++) {
					if (x > 0) { length += getSegmentLength(points[x - 1][i], points[x][i]); }
				}

				if (isClosedMeshMDirection()) {
					length += getSegmentLength(points[points[i].length - 1][i], points[0][i]);
				}
			}
		}

		return length;
	}
}
