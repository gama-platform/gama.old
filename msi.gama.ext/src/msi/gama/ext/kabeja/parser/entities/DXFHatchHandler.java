/*******************************************************************************************************
 *
 * DXFHatchHandler.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser.entities;

import java.util.Hashtable;
import java.util.Iterator;

import msi.gama.ext.kabeja.dxf.DXFConstants;
import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.dxf.DXFEntity;
import msi.gama.ext.kabeja.dxf.DXFHatch;
import msi.gama.ext.kabeja.dxf.DXFHatchPattern;
import msi.gama.ext.kabeja.dxf.DXFPolyline;
import msi.gama.ext.kabeja.dxf.DXFVertex;
import msi.gama.ext.kabeja.dxf.helpers.HatchBoundaryLoop;
import msi.gama.ext.kabeja.dxf.helpers.HatchLineFamily;
import msi.gama.ext.kabeja.parser.DXFValue;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFHatchHandler extends AbstractEntityHandler {

	/** The Constant END_SEQUENCE. */
	public static final String END_SEQUENCE = "SEQEND";

	/** The Constant END_SEQUENCE_CODE. */
	public static final int END_SEQUENCE_CODE = -2;

	/** The Constant GROUPCODE_ASSOSIATIVITY_FLAG. */
	public static final int GROUPCODE_ASSOSIATIVITY_FLAG = 71;

	/** The Constant GROUPCODE_BOUNDARY_ANNOTATION. */
	public static final int GROUPCODE_BOUNDARY_ANNOTATION = 73;

	/** The Constant GROUPCODE_BOUNDARY_EDGE_COUNT. */
	public static final int GROUPCODE_BOUNDARY_EDGE_COUNT = 93;

	/** The Constant GROUPCODE_BOUNDARY_EDGE_TYPE. */
	public static final int GROUPCODE_BOUNDARY_EDGE_TYPE = 72;

	/** The Constant GROUPCODE_BOUNDARY_LOOP_COUNT. */
	public static final int GROUPCODE_BOUNDARY_LOOP_COUNT = 91;

	/** The Constant GROUPCODE_BOUNDAYY_LOOP_TYPE. */
	public static final int GROUPCODE_BOUNDAYY_LOOP_TYPE = 92;

	/** The Constant GROUPCODE_DEFINITION_LINE_COUNT. */
	public static final int GROUPCODE_DEFINITION_LINE_COUNT = 78;

	/** The Constant GROUPCODE_DEGENERTE_BOUNDARY_PATH_COUNT. */
	public static final int GROUPCODE_DEGENERTE_BOUNDARY_PATH_COUNT = 99;

	/** The Constant GROUPCODE_HATCH_DOUBLE_FLAG. */
	public static final int GROUPCODE_HATCH_DOUBLE_FLAG = 77;

	/** The Constant GROUPCODE_HATCH_STYLE. */
	public static final int GROUPCODE_HATCH_STYLE = 75;

	/** The Constant GROUPCODE_NAME. */
	public static final int GROUPCODE_NAME = 2;

	/** The Constant GROUPCODE_OFFSET_VECTOR. */
	public static final int GROUPCODE_OFFSET_VECTOR = 11;

	/** The Constant GROUPCODE_PATTERN_ANGLE. */
	public static final int GROUPCODE_PATTERN_ANGLE = 52;

	/** The Constant GROUPCODE_PATTERN_BASE_X. */
	public static final int GROUPCODE_PATTERN_BASE_X = 43;

	/** The Constant GROUPCODE_PATTERN_BASE_Y. */
	public static final int GROUPCODE_PATTERN_BASE_Y = 44;

	/** The Constant GROUPCODE_PATTERN_FILL_COLOR. */
	public static final int GROUPCODE_PATTERN_FILL_COLOR = 63;

	/** The Constant GROUPCODE_PATTERN_LINE_ANGLE. */
	public static final int GROUPCODE_PATTERN_LINE_ANGLE = 53;

	/** The Constant GROUPCODE_PATTERN_LINE_COUNT. */
	public static final int GROUPCODE_PATTERN_LINE_COUNT = 79;

	/** The Constant GROUPCODE_PATTERN_LINE_TYPE_DATA. */
	public static final int GROUPCODE_PATTERN_LINE_TYPE_DATA = 49;

	/** The Constant GROUPCODE_PATTERN_OFFSET_X. */
	public static final int GROUPCODE_PATTERN_OFFSET_X = 45;

	/** The Constant GROUPCODE_PATTERN_OFFSET_Y. */
	public static final int GROUPCODE_PATTERN_OFFSET_Y = 46;

	/** The Constant GROUPCODE_PATTERN_SCALE. */
	public static final int GROUPCODE_PATTERN_SCALE = 41;

	/** The Constant GROUPCODE_PATTERN_TYPE. */
	public static final int GROUPCODE_PATTERN_TYPE = 76;

	/** The Constant GROUPCODE_PIXEL_SIZE. */
	public static final int GROUPCODE_PIXEL_SIZE = 47;

	/** The Constant GROUPCODE_SEED_POINTS_COUNT. */
	public static final int GROUPCODE_SEED_POINTS_COUNT = 98;

	/** The Constant GROUPCODE_SOLID_FILL_FLAG. */
	public static final int GROUPCODE_SOLID_FILL_FLAG = 70;

	/** The boundary handler. */
	protected DXFEntityHandler boundaryHandler;

	/** The boundary handlers. */
	protected Hashtable<String, DXFEntityHandler> boundaryHandlers = new Hashtable<>();

	/** The count. */
	protected int count;

	/** The follow. */
	// private final boolean follow = false;

	/** The hatch. */
	private DXFHatch hatch;

	/** The line pattern. */
	protected HatchLineFamily linePattern = new HatchLineFamily();

	/** The loop. */
	protected HatchBoundaryLoop loop;

	/** The parameters. */
	protected double[] parameters = {};

	/** The parse boundary. */
	private boolean parseBoundary = false;

	/** The pattern. */
	protected DXFHatchPattern pattern;

	/** The polyline. */
	protected DXFPolyline polyline;

	/** The polyline boundary. */
	private boolean polylineBoundary = false;

	/** The vertex. */
	protected DXFVertex vertex;

	/** The last group code. */
	protected int lastGroupCode;

	/**
	 * Instantiates a new DXF hatch handler.
	 */
	public DXFHatchHandler() {
		init();
	}

	/**
	 * End boundary element.
	 */
	protected void endBoundaryElement() {
		if (boundaryHandler != null) {
			// get the last parsed entity
			this.boundaryHandler.endDXFEntity();
			this.loop.addBoundaryEdge(boundaryHandler.getDXFEntity());
			this.boundaryHandler = null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#endDXFEntity()
	 */
	@Override
	public void endDXFEntity() {
		// this.linePattern.setPattern(this.parameters);
		if (this.pattern != null) { this.hatch.setDXFHatchPatternID(this.pattern.getID()); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#getDXFEntity()
	 */
	@Override
	public DXFEntity getDXFEntity() { return hatch; }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.entities.DXFEntityHandler#getDXFEntityName()
	 */
	@Override
	public String getDXFEntityName() { return DXFConstants.ENTITY_TYPE_HATCH; }

	/**
	 * Inits the.
	 */
	protected void init() {
		DXFEntityHandler handler = new DXFSplineHandler();
		boundaryHandlers.put(handler.getDXFEntityName(), handler);

		handler = new DXFLineHandler();
		boundaryHandlers.put(handler.getDXFEntityName(), handler);

		handler = new DXFArcHandler();
		boundaryHandlers.put(handler.getDXFEntityName(), handler);

		handler = new DXFEllipseHandler();
		boundaryHandlers.put(handler.getDXFEntityName(), handler);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#isFollowSequence()
	 */
	@Override
	public boolean isFollowSequence() { return false; }

	/**
	 * Parses the boundary.
	 *
	 * @param groupCode
	 *            the group code
	 * @param value
	 *            the value
	 */
	protected void parseBoundary(final int groupCode, final DXFValue value) {
		if (this.polylineBoundary) {
			parsePolylineBoundary(groupCode, value);
		} else {
			// delegate to the entityhandler
			this.boundaryHandler.parseGroup(groupCode, value);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#parseGroup(int, de.miethxml.kabeja.parser.DXFValue)
	 */
	@Override
	public void parseGroup(final int groupCode, final DXFValue value) {
		switch (groupCode) {
			case GROUPCODE_BOUNDARY_LOOP_COUNT:
				break;

			case GROUPCODE_BOUNDARY_EDGE_COUNT:
				break;

			case GROUPCODE_BOUNDARY_EDGE_TYPE:

				if (this.lastGroupCode == GROUPCODE_BOUNDAYY_LOOP_TYPE) {
					this.polylineBoundary = true;
					this.polyline = new DXFPolyline();
					this.polyline.setDXFDocument(this.doc);
					this.loop.addBoundaryEdge(this.polyline);

					break;
				}

				if (!this.polylineBoundary) {
					this.endBoundaryElement();

					switch (value.getIntegerValue()) {
						case 1:
							boundaryHandler = boundaryHandlers.get(DXFConstants.ENTITY_TYPE_LINE);
							boundaryHandler.startDXFEntity();

							break;

						case 2:
							boundaryHandler = boundaryHandlers.get(DXFConstants.ENTITY_TYPE_ARC);
							boundaryHandler.startDXFEntity();

							break;

						case 3:
							boundaryHandler = boundaryHandlers.get(DXFConstants.ENTITY_TYPE_ELLIPSE);
							boundaryHandler.startDXFEntity();

							break;

						case 4:
							boundaryHandler = boundaryHandlers.get(DXFConstants.ENTITY_TYPE_SPLINE);
							boundaryHandler.startDXFEntity();

							break;
					}
				}

				break;

			case GROUPCODE_BOUNDAYY_LOOP_TYPE:

				// finish up last parsing
				if (!this.polylineBoundary) { this.endBoundaryElement(); }

				// a new loop starts
				this.loop = new HatchBoundaryLoop();
				this.hatch.addBoundaryLoop(loop);

				// set the flags
				this.polylineBoundary = false;
				this.parseBoundary = true;

				if ((value.getIntegerValue() & 2) == 2) {}

				if ((value.getIntegerValue() & 1) == 1) {
					// from external hatch
				}

				if ((value.getIntegerValue() & 4) == 4) {
					// from derivated hatch
				}

				if ((value.getIntegerValue() & 16) == 16) {
					// different from the DXF-Specs
					// if bit is set the loop is not
					this.loop.setOutermost(false);
				}

				break;

			case GROUPCODE_NAME:
				this.hatch.setName(value.getValue());

				break;

			case GROUPCODE_START_X:

				if (parseBoundary) {
					parseBoundary(groupCode, value);
				} else {
					this.hatch.getElevationPoint().setX(value.getDoubleValue());
				}

				break;

			case GROUPCODE_START_Y:

				if (parseBoundary) {
					parseBoundary(groupCode, value);
				} else {
					this.hatch.getElevationPoint().setY(value.getDoubleValue());
				}

				break;

			case GROUPCODE_START_Z:

				if (parseBoundary) {
					parseBoundary(groupCode, value);
				} else {
					this.hatch.getElevationPoint().setZ(value.getDoubleValue());
				}

				break;

			case GROUPCODE_HATCH_STYLE:
				this.parseBoundary = false;
				this.endBoundaryElement();

				// This should be the end of a boundary entity
				break;

			case GROUPCODE_PATTERN_LINE_ANGLE:
				// set the previus parsed line data
				this.parseBoundary = false;
				this.linePattern = new HatchLineFamily();
				this.pattern.addLineFamily(this.linePattern);

				this.linePattern.setRotationAngle(value.getDoubleValue());
				this.count = 0;

				break;

			case GROUPCODE_PATTERN_BASE_X:
				this.linePattern.setBaseX(value.getDoubleValue());

				break;

			case GROUPCODE_PATTERN_BASE_Y:
				this.linePattern.setBaseY(value.getDoubleValue());

				break;

			case GROUPCODE_PATTERN_OFFSET_X:
				this.linePattern.setOffsetX(value.getDoubleValue());

				break;

			case GROUPCODE_PATTERN_OFFSET_Y:
				this.linePattern.setOffsetY(value.getDoubleValue());

				break;

			case GROUPCODE_PATTERN_LINE_COUNT:
				this.parameters = new double[value.getIntegerValue()];
				this.linePattern.setPattern(this.parameters);

				break;

			case GROUPCODE_PATTERN_LINE_TYPE_DATA:
				this.parameters[this.count] = value.getDoubleValue();
				this.count++;

				break;

			case GROUPCODE_PATTERN_SCALE:
				this.hatch.setPatternScale(value.getDoubleValue());

				break;

			default:

				if (parseBoundary) {
					parseBoundary(groupCode, value);
				} else {
					super.parseCommonProperty(groupCode, value, this.hatch);
				}
		}

		this.lastGroupCode = groupCode;
	}

	/**
	 * Parses the polyline boundary.
	 *
	 * @param groupCode
	 *            the group code
	 * @param value
	 *            the value
	 */
	protected void parsePolylineBoundary(final int groupCode, final DXFValue value) {
		switch (groupCode) {
			case GROUPCODE_START_X:
				this.vertex = new DXFVertex();
				this.polyline.addVertex(vertex);
				this.vertex.setX(value.getDoubleValue());

				break;

			case GROUPCODE_START_Y:
				this.vertex.setY(value.getDoubleValue());

				break;

			case GROUPCODE_START_Z:
				this.vertex.setZ(value.getDoubleValue());

				break;

			case DXFPolylineHandler.VERTEX_BULGE:
				this.vertex.setBulge(value.getDoubleValue());

				break;

			case 73:
				this.polyline.setFlags(1);

				break;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Handler#setDXFDocument(org.kabeja.dxf.DXFDocument)
	 */
	@Override
	public void setDXFDocument(final DXFDocument doc) {
		super.setDXFDocument(doc);

		Iterator i = this.boundaryHandlers.values().iterator();

		while (i.hasNext()) {
			DXFEntityHandler handler = (DXFEntityHandler) i.next();
			handler.setDXFDocument(doc);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#startDXFEntity()
	 */
	@Override
	public void startDXFEntity() {
		this.hatch = new DXFHatch();
		this.pattern = new DXFHatchPattern();

		this.pattern.setHatch(this.hatch);
		this.doc.addDXFHatchPattern(pattern);

		// setup the flags
		this.parseBoundary = false;
		this.polylineBoundary = false;
		boundaryHandler = null;
	}
}
