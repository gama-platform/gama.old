/*******************************************************************************************************
 *
 * FileDrawingAttributes.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;

/**
 * The Class FileDrawingAttributes.
 */
public class FileDrawingAttributes extends DrawingAttributes {

	/** The agent identifier. */
	public final IAgent agentIdentifier;

	/**
	 * Instantiates a new file drawing attributes.
	 *
	 * @param size
	 *            the size
	 * @param rotation
	 *            the rotation
	 * @param location
	 *            the location
	 * @param color
	 *            the color
	 * @param border
	 *            the border
	 * @param agent
	 *            the agent
	 * @param lineWidth
	 *            the line width
	 * @param isImage
	 *            the is image
	 * @param lighting
	 *            the lighting
	 */
	public FileDrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final IAgent agent, final Double lineWidth,
			final boolean isImage, final Boolean lighting) {
		super(size, rotation, location, color, border, lighting);
		this.agentIdentifier = agent;
		setLineWidth(lineWidth);
		setType(isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE);
		setUseCache(true); // by default
	}

	/**
	 * Instantiates a new file drawing attributes.
	 *
	 * @param location
	 *            the location
	 * @param isImage
	 *            the is image
	 */
	public FileDrawingAttributes(final GamaPoint location, final boolean isImage) {
		super(null, null, location, null, null, null);
		agentIdentifier = null;
		setType(isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE);
		setUseCache(true); // by default
	}

	@Override
	public IAgent getAgentIdentifier() { return agentIdentifier; }

}