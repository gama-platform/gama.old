/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.List;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;

public class FileDrawingAttributes extends DrawingAttributes {

	public final IAgent agent;
	public GamaColor border;

	public FileDrawingAttributes(final ILocation size, final GamaPair<Double, GamaPoint> rotation,
		final ILocation location, final GamaColor color, final GamaColor border, final IAgent agent) {
		super(size, rotation, location, color);
		this.agent = agent;
		this.border = border;
	}

	public FileDrawingAttributes(final ILocation location) {
		super(null, null, location, null);
		agent = null;
		border = null;
	}

	public FileDrawingAttributes(final ILocation location, final GamaColor color, final GamaColor border) {
		super(null, null, location, color);
		agent = null;
		this.border = border;
	}

	/**
	 * Method getTextures()
	 * @see msi.gaml.statements.draw.DrawingAttributes#getTextures()
	 */
	@Override
	public List getTextures() {
		return null;
	}

	/**
	 * Method isEmpty()
	 * @see msi.gaml.statements.draw.DrawingAttributes#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Method getAgent()
	 * @see msi.gaml.statements.draw.DrawingAttributes#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return agent;
	}

	/**
	 * Method getBorder()
	 * @see msi.gaml.statements.draw.DrawingAttributes#getBorder()
	 */
	@Override
	public GamaColor getBorder() {
		return border;
	}

	/**
	 * Method getDepth()
	 * @see msi.gaml.statements.draw.DrawingAttributes#getDepth()
	 */
	@Override
	public double getDepth() {
		return 0;
	}

}