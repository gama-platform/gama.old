/*********************************************************************************************
 *
 * 'FileDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

public class FileDrawingAttributes extends DrawingAttributes {

	public final AgentIdentifier agentIdentifier;
	public GamaColor border;

	public FileDrawingAttributes(final ILocation size, final GamaPair<Double, GamaPoint> rotation,
			final ILocation location, final GamaColor color, final GamaColor border, final IAgent agent) {
		super(size, rotation, location, color);
		this.agentIdentifier = AgentIdentifier.of(agent);
		this.border = border;
	}

	public FileDrawingAttributes(final ILocation location) {
		super(null, null, location, null);
		agentIdentifier = null;
		border = null;
	}

	public FileDrawingAttributes(final ILocation location, final GamaColor color, final GamaColor border) {
		super(null, null, location, color);
		agentIdentifier = null;
		this.border = border;
	}

	/**
	 * Method getTextures()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getTextures()
	 */
	@Override
	public List<?> getTextures() {
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
	// return agent;
	// }

	/**
	 * Method getBorder()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getBorder()
	 */
	@Override
	public GamaColor getBorder() {
		return border;
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
		return agentIdentifier;
	}

}