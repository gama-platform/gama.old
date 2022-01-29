/*******************************************************************************************************
 *
 * DXFHatch.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
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

import msi.gama.ext.kabeja.dxf.helpers.HatchBoundaryLoop;
import msi.gama.ext.kabeja.dxf.helpers.Point;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFHatch extends DXFEntity {

	/** The name. */
	private String name = "";

	/** The associativity flag. */
	private int associativityFlag = 0;

	/** The boundary path count. */
	private int boundaryPathCount = 0;

	/** The hatch style. */
	private int hatchStyle = 0;

	/** The pattern type. */
	private int patternType = 0;

	/** The pattern angle. */
	private double patternAngle = 0.0;

	/** The pattern scale spacing. */
	private double patternScaleSpacing = 1.0;

	/** The boundary annotation. */
	private boolean boundaryAnnotation = false;

	/** The pattern double. */
	private boolean patternDouble = false;

	/** The defination lines count. */
	private int definationLinesCount = 0;

	/** The pixel size. */
	private double pixelSize = 0.0;

	/** The seed point count. */
	private int seedPointCount = 0;

	/** The offset vector. */
	private double offsetVector = 0.0;

	/** The degenerate boundary path count. */
	private int degenerateBoundaryPathCount = 0;

	/** The gradient hatch. */
	private boolean gradientHatch = false;

	/** The elevation point. */
	private Point elevationPoint = new Point();

	/** The boundaries. */
	private final List<HatchBoundaryLoop> boundaries = new ArrayList<>();
	//
	// /** The patterns. */
	// private final List patterns = new ArrayList();

	/** The pattern ID. */
	private String patternID = "";

	/** The pattern scale. */
	private double patternScale;

	/**
	 * Instantiates a new DXF hatch.
	 */
	public DXFHatch() {}

	/**
	 * @return Returns the associativityFlag.
	 */
	public int getAssociativityFlag() { return associativityFlag; }

	/**
	 * @param associativityFlag
	 *            The associativityFlag to set.
	 */
	public void setAssociativityFlag(final int associativityFlag) { this.associativityFlag = associativityFlag; }

	/**
	 * @return Returns the boundaryAnnotation.
	 */
	public boolean isBoundaryAnnotation() { return boundaryAnnotation; }

	/**
	 * @param boundaryAnnotation
	 *            The boundaryAnnotation to set.
	 */
	public void setBoundaryAnnotation(final boolean boundaryAnnotation) {
		this.boundaryAnnotation = boundaryAnnotation;
	}

	/**
	 * @return Returns the boundaryPathCount.
	 */
	public int getBoundaryPathCount() { return boundaryPathCount; }

	/**
	 * @param boundaryPathCount
	 *            The boundaryPathCount to set.
	 */
	public void setBoundaryPathCount(final int boundaryPathCount) { this.boundaryPathCount = boundaryPathCount; }

	/**
	 * @return Returns the definationLinesCount.
	 */
	public int getDefinationLinesCount() { return definationLinesCount; }

	/**
	 * @param definationLinesCount
	 *            The definationLinesCount to set.
	 */
	public void setDefinationLinesCount(final int definationLinesCount) {
		this.definationLinesCount = definationLinesCount;
	}

	/**
	 * @return Returns the degenerateBoundaryPathCount.
	 */
	public int getDegenerateBoundaryPathCount() { return degenerateBoundaryPathCount; }

	/**
	 * @param degenerateBoundaryPathCount
	 *            The degenerateBoundaryPathCount to set.
	 */
	public void setDegenerateBoundaryPathCount(final int degenerateBoundaryPathCount) {
		this.degenerateBoundaryPathCount = degenerateBoundaryPathCount;
	}

	/**
	 * @return Returns the gradientHatch.
	 */
	public boolean isGradientHatch() { return gradientHatch; }

	/**
	 * @param gradientHatch
	 *            The gradientHatch to set.
	 */
	public void setGradientHatch(final boolean gradientHatch) { this.gradientHatch = gradientHatch; }

	/**
	 * @return Returns the hatchStyle.
	 */
	public int getHatchStyle() { return hatchStyle; }

	/**
	 * @param hatchStyle
	 *            The hatchStyle to set.
	 */
	public void setHatchStyle(final int hatchStyle) { this.hatchStyle = hatchStyle; }

	/**
	 * @return Returns the name.
	 */
	public String getName() { return name; }

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(final String name) { this.name = name; }

	/**
	 * @return Returns the offsetVector.
	 */
	public double getOffsetVector() { return offsetVector; }

	/**
	 * @param offsetVector
	 *            The offsetVector to set.
	 */
	public void setOffsetVector(final double offsetVector) { this.offsetVector = offsetVector; }

	/**
	 * @return Returns the patternAngle.
	 */
	public double getPatternAngle() { return patternAngle; }

	/**
	 * @param patternAngle
	 *            The patternAngle to set.
	 */
	public void setPatternAngle(final double patternAngle) { this.patternAngle = patternAngle; }

	/**
	 * @return Returns the patternDouble.
	 */
	public boolean isPatternDouble() { return patternDouble; }

	/**
	 * @param patternDouble
	 *            The patternDouble to set.
	 */
	public void setPatternDouble(final boolean patternDouble) { this.patternDouble = patternDouble; }

	/**
	 * @return Returns the patternScaleSpacing.
	 */
	public double getPatternScaleSpacing() { return patternScaleSpacing; }

	/**
	 * @param patternScaleSpacing
	 *            The patternScaleSpacing to set.
	 */
	public void setPatternScaleSpacing(final double patternScaleSpacing) {
		this.patternScaleSpacing = patternScaleSpacing;
	}

	/**
	 * @return Returns the patternType.
	 */
	public int getPatternType() { return patternType; }

	/**
	 * @param patternType
	 *            The patternType to set.
	 */
	public void setPatternType(final int patternType) { this.patternType = patternType; }

	/**
	 * @return Returns the pixelSize.
	 */
	public double getPixelSize() { return pixelSize; }

	/**
	 * @param pixelSize
	 *            The pixelSize to set.
	 */
	public void setPixelSize(final double pixelSize) { this.pixelSize = pixelSize; }

	/**
	 * @return Returns the seedPointCount.
	 */
	public int getSeedPointCount() { return seedPointCount; }

	/**
	 * @param seedPointCount
	 *            The seedPointCount to set.
	 */
	public void setSeedPointCount(final int seedPointCount) { this.seedPointCount = seedPointCount; }

	/**
	 * @return Returns the solid.
	 */
	public boolean isSolid() { return this.flags == 1; }

	/**
	 * @param solid
	 *            The solid to set.
	 */
	public void setSolid(final boolean solid) {
		this.flags = 1;
		// this. solid = solid;
	}

	/**
	 * Adds the boundary loop.
	 *
	 * @param loop
	 *            the loop
	 */
	public void addBoundaryLoop(final HatchBoundaryLoop loop) {
		this.boundaries.add(loop);
	}

	/**
	 * Gets the boundary loops.
	 *
	 * @return the boundary loops
	 */
	public Iterator getBoundaryLoops() { return this.boundaries.iterator(); }

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();
		Iterator i = this.boundaries.iterator();

		while (i.hasNext()) {
			HatchBoundaryLoop loop = (HatchBoundaryLoop) i.next();
			Bounds b = loop.getBounds();

			if (b.isValid()) { bounds.addToBounds(b); }
		}

		return bounds;
	}

	/**
	 * @return Returns the elevationPoint.
	 */
	public Point getElevationPoint() { return elevationPoint; }

	/**
	 * @param elevationPoint
	 *            The elevationPoint to set.
	 */
	public void setElevationPoint(final Point elevationPoint) { this.elevationPoint = elevationPoint; }

	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_HATCH; }

	/**
	 * @return Returns the ID of the pattern (also called pattern name).
	 */
	public String getDXFHatchPatternID() { return this.patternID; }

	/**
	 * @param patternID
	 *            The patternID to set.
	 */
	public void setDXFHatchPatternID(final String patternID) { this.patternID = patternID; }

	@Override
	public double getLength() { return 0; }

	/**
	 * Gets the pattern scale.
	 *
	 * @return the pattern scale
	 */
	public double getPatternScale() { return patternScale; }

	/**
	 * Sets the pattern scale.
	 *
	 * @param patternScale
	 *            the new pattern scale
	 */
	public void setPatternScale(final double patternScale) { this.patternScale = patternScale; }
}
