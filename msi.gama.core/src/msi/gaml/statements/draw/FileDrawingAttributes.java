/*********************************************************************************************
 *
 * 'FileDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

public class FileDrawingAttributes extends DrawingAttributes {

	public final AgentIdentifier agentIdentifier;
	public double lineWidth;
	public final boolean isImage;

	public FileDrawingAttributes(final Scaling3D size, final GamaPair<Double, GamaPoint> rotation,
			final GamaPoint location, final GamaColor color, final GamaColor border, final IAgent agent,
			final Double lineWidth, final boolean isImage) {
		super(size, rotation, location, color, border);
		this.agentIdentifier = AgentIdentifier.of(agent);
		setLineWidth(lineWidth == null ? GamaPreferences.OpenGL.CORE_LINE_WIDTH.getValue() : lineWidth);
		this.isImage = isImage;
	}

	public void setLineWidth(final Double d) {
		lineWidth = d;
	}

	@Override
	public boolean isImage() {
		return isImage;
	}

	@Override
	public Double getLineWidth() {
		return lineWidth;
	}

	public FileDrawingAttributes(final GamaPoint location, final boolean isImage) {
		super(null, null, location, null, null);
		agentIdentifier = null;
		setLineWidth(GamaPreferences.OpenGL.CORE_LINE_WIDTH.getValue());
		this.isImage = isImage;
	}
	//
	// public FileDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
	// super(null, null, location, color, border);
	// agentIdentifier = null;
	// setLineWidth(GamaPreferences.OpenGL.CORE_LINE_WIDTH.getValue());
	// }

	/**
	 * Method getMaterial()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getMaterial()
	 */
	@Override
	public GamaMaterial getMaterial() {
		// TODO
		return null;
	}

	@Override
	public IShape.Type getType() {
		return isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE;
	}

	@Override
	public AgentIdentifier getAgentIdentifier() {
		return agentIdentifier;
	}

}