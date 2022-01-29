/*******************************************************************************************************
 *
 * DXFViewport.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import msi.gama.ext.kabeja.dxf.helpers.Point;
import msi.gama.ext.kabeja.dxf.helpers.Vector;
import msi.gama.ext.kabeja.math.MathUtils;
import msi.gama.ext.kabeja.math.ParametricPlane;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFViewport extends DXFEntity {

	/** The viewport ID. */
	private String viewportID = "";

	/** The plot style name. */
	private String plotStyleName = "";

	/** The lower left corner. */
	private Point lowerLeftCorner = new Point();

	/** The upper right corner. */
	private Point upperRightCorner = new Point();

	/** The center point. */
	private Point centerPoint = new Point();

	/** The snap base point. */
	private Point snapBasePoint = new Point();

	/** The snap spacing point. */
	private Point snapSpacingPoint = new Point();

	/** The grid spacing point. */
	private Point gridSpacingPoint = new Point();

	/** The view direction vector. */
	private Vector viewDirectionVector = new Vector();

	/** The view center point. */
	private Point viewCenterPoint = new Point();

	/** The view target point. */
	private Point viewTargetPoint = new Point();

	/** The ucs origin. */
	private Vector ucsOrigin = new Vector();

	/** The ucs X axis. */
	private Vector ucsXAxis = new Vector();

	/** The ucs Y axis. */
	private Vector ucsYAxis = new Vector();

	/** The ucs type. */
	private int ucsType = 0;

	/** The viewport status. */
	private int viewportStatus = 0;

	/** The ucs elevation. */
	private double ucsElevation = 0.0;

	/** The use UCS. */
	private boolean useUCS = false;

	/** The height. */
	private double height;

	/** The width. */
	private double width;

	/** The ratio. */
	private double ratio;

	/** The lens length. */
	private double lensLength;

	/** The view height. */
	private double viewHeight;

	/** The front clipping plane. */
	private double frontClippingPlane;

	/** The back clipping plane. */
	private double backClippingPlane;

	/** The twist angle. */
	private double twistAngle;

	/** The snap angle. */
	private double snapAngle;

	/** The circle zoom. */
	private double circleZoom;

	/** The fast zoom. */
	private double fastZoom;

	/** The snap. */
	private boolean snap;

	/** The grid. */
	private boolean grid;

	/** The active. */
	private boolean active = false;

	/** The render mode. */
	private int renderMode;

	/** The frozen layer set. */
	private final Set<String> frozenLayerSet = new HashSet<>();

	/**
	 * @return Returns the backClippingPlane.
	 */
	public double getBackClippingPlane() { return backClippingPlane; }

	/**
	 * @param backClippingPlane
	 *            The backClippingPlane to set.
	 */
	public void setBackClippingPlane(final double backClippingPlane) { this.backClippingPlane = backClippingPlane; }

	/**
	 * @return Returns the centerPoint.
	 */
	public Point getCenterPoint() { return centerPoint; }

	/**
	 * @param centerPoint
	 *            The centerPoint to set.
	 */
	public void setCenterPoint(final Point centerPoint) { this.centerPoint = centerPoint; }

	/**
	 * @return Returns the circleZoom.
	 */
	public double getCircleZoom() { return circleZoom; }

	/**
	 * @param circleZoom
	 *            The circleZoom to set.
	 */
	public void setCircleZoom(final double circleZoom) { this.circleZoom = circleZoom; }

	/**
	 * @return Returns the fastZoom.
	 */
	public double getFastZoom() { return fastZoom; }

	/**
	 * @param fastZoom
	 *            The fastZoom to set.
	 */
	public void setFastZoom(final double fastZoom) { this.fastZoom = fastZoom; }

	/**
	 * @return Returns the frontClippingPlane.
	 */
	public double getFrontClippingPlane() { return frontClippingPlane; }

	/**
	 * @param frontClippingPlane
	 *            The frontClippingPlane to set.
	 */
	public void setFrontClippingPlane(final double frontClippingPlane) {
		this.frontClippingPlane = frontClippingPlane;
	}

	/**
	 * @return Returns the grid.
	 */
	public boolean isGrid() { return grid; }

	/**
	 * @param grid
	 *            The grid to set.
	 */
	public void setGrid(final boolean grid) { this.grid = grid; }

	/**
	 * @return Returns the gridSpacingPoint.
	 */
	public Point getGridSpacingPoint() { return gridSpacingPoint; }

	/**
	 * @param gridSpacingPoint
	 *            The gridSpacingPoint to set.
	 */
	public void setGridSpacingPoint(final Point gridSpacingPoint) { this.gridSpacingPoint = gridSpacingPoint; }

	/**
	 * @return Returns the height.
	 */
	public double getHeight() { return height; }

	/**
	 * @param height
	 *            The height to set.
	 */
	public void setHeight(final double height) { this.height = height; }

	/**
	 * @return Returns the lensLength.
	 */
	public double getLensLength() { return lensLength; }

	/**
	 * @param lensLength
	 *            The lensLength to set.
	 */
	public void setLensLength(final double lensLength) { this.lensLength = lensLength; }

	/**
	 * @return Returns the lowerLeftCorner.
	 */
	public Point getLowerLeftCorner() { return lowerLeftCorner; }

	/**
	 * @param lowerLeftCorner
	 *            The lowerLeftCorner to set.
	 */
	public void setLowerLeftCorner(final Point lowerLeftCorner) { this.lowerLeftCorner = lowerLeftCorner; }

	/**
	 * @return Returns the name.
	 */
	public String getViewportID() { return viewportID; }

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setViewportID(final String name) { this.viewportID = name; }

	/**
	 * @return Returns the ratio.
	 */
	public double getAspectRatio() { return ratio; }

	/**
	 * @param ratio
	 *            The ratio to set.
	 */
	public void setAspectRatio(final double ratio) { this.ratio = ratio; }

	/**
	 * @param rotationAngle
	 *            The rotationAngle to set.
	 */
	public void setTwistAngle(final double rotationAngle) { this.twistAngle = rotationAngle; }

	/**
	 * @return Returns the snap.
	 */
	public boolean isSnap() { return snap; }

	/**
	 * @param snap
	 *            The snap to set.
	 */
	public void setSnap(final boolean snap) { this.snap = snap; }

	/**
	 * @return Returns the snapBasePoint.
	 */
	public Point getSnapBasePoint() { return snapBasePoint; }

	/**
	 * @param snapBasePoint
	 *            The snapBasePoint to set.
	 */
	public void setSnapBasePoint(final Point snapBasePoint) { this.snapBasePoint = snapBasePoint; }

	/**
	 * @return Returns the upperRightCorner.
	 */
	public Point getUpperRightCorner() { return upperRightCorner; }

	/**
	 * @param upperRightCorner
	 *            The upperRightCorner to set.
	 */
	public void setUpperRightCorner(final Point upperRightCorner) { this.upperRightCorner = upperRightCorner; }

	/**
	 * @return Returns the viewDirectionPoint.
	 */
	public Vector getViewDirectionVector() { return viewDirectionVector; }

	/**
	 * @param viewDirectionPoint
	 *            The viewDirectionPoint to set.
	 */
	public void setViewDirectionVector(final Vector viewDirectionPoint) {
		this.viewDirectionVector = viewDirectionPoint;
	}

	/**
	 * @return Returns the viewTargetPoint.
	 */
	public Point getViewCenterPoint() { return viewCenterPoint; }

	/**
	 * @param viewTargetPoint
	 *            The viewTargetPoint to set.
	 */
	public void setViewCenterPoint(final Point viewTargetPoint) { this.viewCenterPoint = viewTargetPoint; }

	/**
	 * @return Returns the active.
	 */
	public boolean isActive() { return active; }

	/**
	 * @param active
	 *            The active to set.
	 */
	public void setActive(final boolean active) { this.active = active; }

	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();

		if (this.viewportStatus > 0) {
			bounds.addToBounds(this.centerPoint.getX() - this.width / 2, this.centerPoint.getY() - this.height / 2,
					0.0);
			bounds.addToBounds(this.centerPoint.getX() + this.width / 2, this.centerPoint.getY() + this.height / 2,
					0.0);
		}

		return bounds;
	}

	@Override
	public double getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_VIEWPORT; }

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public double getWidth() { return width; }

	/**
	 * Sets the width.
	 *
	 * @param width
	 *            the new width
	 */
	public void setWidth(final double width) { this.width = width; }

	/**
	 * Sets the render mode.
	 *
	 * @param renderMode
	 *            the new render mode
	 */
	public void setRenderMode(final int renderMode) { this.renderMode = renderMode; }

	/**
	 * Gets the view height.
	 *
	 * @return the view height
	 */
	public double getViewHeight() { return viewHeight; }

	/**
	 * Sets the view height.
	 *
	 * @param viewHeight
	 *            the new view height
	 */
	public void setViewHeight(final double viewHeight) { this.viewHeight = viewHeight; }

	/**
	 * Gets the ucs origin.
	 *
	 * @return the ucs origin
	 */
	public Vector getUcsOrigin() { return ucsOrigin; }

	/**
	 * Sets the ucs origin.
	 *
	 * @param ucsOrigin
	 *            the new ucs origin
	 */
	public void setUcsOrigin(final Vector ucsOrigin) { this.ucsOrigin = ucsOrigin; }

	/**
	 * Gets the ucs X axis.
	 *
	 * @return the ucs X axis
	 */
	public Vector getUcsXAxis() { return ucsXAxis; }

	/**
	 * Sets the ucs X axis.
	 *
	 * @param ucsXAxis
	 *            the new ucs X axis
	 */
	public void setUcsXAxis(final Vector ucsXAxis) { this.ucsXAxis = ucsXAxis; }

	/**
	 * Gets the ucs Y axis.
	 *
	 * @return the ucs Y axis
	 */
	public Vector getUcsYAxis() { return ucsYAxis; }

	/**
	 * Sets the ucs Y axis.
	 *
	 * @param ucsYAxis
	 *            the new ucs Y axis
	 */
	public void setUcsYAxis(final Vector ucsYAxis) { this.ucsYAxis = ucsYAxis; }

	/**
	 * Gets the ucs type.
	 *
	 * @return the ucs type
	 */
	public int getUcsType() { return ucsType; }

	/**
	 * Sets the ucs type.
	 *
	 * @param ucsType
	 *            the new ucs type
	 */
	public void setUcsType(final int ucsType) { this.ucsType = ucsType; }

	/**
	 * Gets the ucs elevation.
	 *
	 * @return the ucs elevation
	 */
	public double getUcsElevation() { return ucsElevation; }

	/**
	 * Sets the ucs elevation.
	 *
	 * @param ucsElevation
	 *            the new ucs elevation
	 */
	public void setUcsElevation(final double ucsElevation) { this.ucsElevation = ucsElevation; }

	/**
	 * Checks if is use UCS.
	 *
	 * @return true, if is use UCS
	 */
	public boolean isUseUCS() { return useUCS; }

	/**
	 * Sets the use UCS.
	 *
	 * @param useUCS
	 *            the new use UCS
	 */
	public void setUseUCS(final boolean useUCS) { this.useUCS = useUCS; }

	/**
	 * Gets the ratio.
	 *
	 * @return the ratio
	 */
	public double getRatio() { return ratio; }

	/**
	 * Sets the ratio.
	 *
	 * @param ratio
	 *            the new ratio
	 */
	public void setRatio(final double ratio) { this.ratio = ratio; }

	/**
	 * Gets the render mode.
	 *
	 * @return the render mode
	 */
	public int getRenderMode() { return renderMode; }

	/**
	 * Gets the snap angle.
	 *
	 * @return the snap angle
	 */
	public double getSnapAngle() { return snapAngle; }

	/**
	 * Sets the snap angle.
	 *
	 * @param snapAngle
	 *            the new snap angle
	 */
	public void setSnapAngle(final double snapAngle) { this.snapAngle = snapAngle; }

	/**
	 * Gets the view target point.
	 *
	 * @return the view target point
	 */
	public Point getViewTargetPoint() { return viewTargetPoint; }

	/**
	 * Sets the view target point.
	 *
	 * @param viewTargetPoint
	 *            the new view target point
	 */
	public void setViewTargetPoint(final Point viewTargetPoint) { this.viewTargetPoint = viewTargetPoint; }

	/**
	 * Gets the snap spacing point.
	 *
	 * @return the snap spacing point
	 */
	public Point getSnapSpacingPoint() { return snapSpacingPoint; }

	/**
	 * Sets the snap spacing point.
	 *
	 * @param snapSpacingPoint
	 *            the new snap spacing point
	 */
	public void setSnapSpacingPoint(final Point snapSpacingPoint) { this.snapSpacingPoint = snapSpacingPoint; }

	/**
	 * Gets the plot style name.
	 *
	 * @return the plot style name
	 */
	public String getPlotStyleName() { return plotStyleName; }

	/**
	 * Sets the plot style name.
	 *
	 * @param plotStyleName
	 *            the new plot style name
	 */
	public void setPlotStyleName(final String plotStyleName) { this.plotStyleName = plotStyleName; }

	/**
	 * Gets the viewport status.
	 *
	 * @return the viewport status
	 */
	public int getViewportStatus() { return viewportStatus; }

	/**
	 * Sets the viewport status.
	 *
	 * @param viewportStatus
	 *            the new viewport status
	 */
	public void setViewportStatus(final int viewportStatus) { this.viewportStatus = viewportStatus; }

	/**
	 * Gets the twist angle.
	 *
	 * @return the twist angle
	 */
	public double getTwistAngle() { return twistAngle; }

	/**
	 * Adds the frozen layer.
	 *
	 * @param layerName
	 *            the layer name
	 */
	public void addFrozenLayer(final String layerName) {
		this.frozenLayerSet.add(layerName);
	}

	/**
	 * Checks if is frozen layer.
	 *
	 * @param layerName
	 *            the layer name
	 * @return true, if is frozen layer
	 */
	public boolean isFrozenLayer(final String layerName) {
		return this.frozenLayerSet.contains(layerName);
	}

	/**
	 * Gets the frozen layer iterator.
	 *
	 * @return the frozen layer iterator
	 */
	public Iterator getFrozenLayerIterator() { return this.frozenLayerSet.iterator(); }

	/**
	 * Gets the zoom XP factor.
	 *
	 * @return the zoom XP factor
	 */
	public double getZoomXPFactor() {
		if (this.viewHeight != 0.0) return this.height / this.viewHeight;
		return this.calculateZoomXPFactor();
	}

	/**
	 * Calculate zoom XP factor.
	 *
	 * @return the double
	 */
	public double calculateZoomXPFactor() {
		double c = this.getViewDirectionVector().getLength() * 42 / this.lensLength;
		double f = this.width / this.height;
		double b = Math.sqrt(Math.pow(c, 2) / (Math.pow(f, 2) + 1));

		return this.height / b;
	}

	/**
	 * Gets the modelspace view bounds.
	 *
	 * @return the modelspace view bounds
	 */
	public Bounds getModelspaceViewBounds() {
		double f = this.width / this.height;

		// the half of width and height
		double modelH = this.height / this.getZoomXPFactor() / 2;
		double modelW = f * modelH;
		// double wf = modelW / modelH;
		Vector directionX = null;

		if (this.viewDirectionVector.getX() == 0.0 && this.viewDirectionVector.getY() == 0.0
				&& this.viewDirectionVector.getZ() == 1.0) {
			directionX = new Vector(1, 0, 0);
		} else {
			directionX = MathUtils.crossProduct(DXFConstants.DEFAULT_Z_AXIS_VECTOR, this.viewDirectionVector);
		}

		ParametricPlane plane = new ParametricPlane(this.viewTargetPoint, directionX,
				MathUtils.crossProduct(this.viewDirectionVector, directionX), this.viewDirectionVector);
		Bounds bounds = new Bounds();
		Point p = plane.getPoint(this.viewCenterPoint.getX() - modelW, this.viewCenterPoint.getY() - modelH);
		bounds.addToBounds(p);
		p = plane.getPoint(this.viewCenterPoint.getX() + modelW, this.viewCenterPoint.getY() + modelH);
		bounds.addToBounds(p);

		return bounds;
	}
}
