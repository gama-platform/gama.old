/*********************************************************************************************
 *
 * 'TextDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class TextDrawingAttributes extends DrawingAttributes {

	public final GamaFont font;
	public final boolean perspective;
	public final Integer anchor;

	public TextDrawingAttributes(final Scaling3D size, final GamaPair<Double, GamaPoint> rotation,
			final GamaPoint location, final Integer anchor, final GamaColor color, final GamaFont font,
			final Boolean perspective) {
		super(size, rotation, location, color, null);
		this.font = font;
		this.anchor = anchor;
		this.perspective = perspective == null ? true : perspective.booleanValue();
	}

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
	public AgentIdentifier getAgentIdentifier() {
		return null;
	}

	@Override
	public Type getType() {
		return Type.POLYGON;
	}

}