/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaPair;

public class TextDrawingAttributes extends DrawingAttributes {

	public final GamaFont font;
	public final boolean perspective;

	public TextDrawingAttributes(final ILocation size, final GamaPair<Double, GamaPoint> rotation,
			final ILocation location, final GamaColor color, final GamaFont font, final Boolean perspective) {
		super(size, rotation, location, color);
		this.font = font;
		this.perspective = perspective == null ? true : perspective.booleanValue();
	}

	/**
	 * Method getTextures()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getTextures()
	 */
	@Override
	public List getTextures() {
		return null;
	}

	/**
	 * Method isEmpty()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Method getAgent()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getAgent()
	 */
	// @Override
	// public IAgent getAgent() {
	// return null;
	// }

	/**
	 * Method getBorder()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getBorder()
	 */
	@Override
	public GamaColor getBorder() {
		return null;
	}

	/**
	 * Method getDepth()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getDepth()
	 */
	@Override
	public double getDepth() {
		return 0;
	}

	@Override
	public AgentIdentifier getAgentIdentifier() {
		return null;
	}

}