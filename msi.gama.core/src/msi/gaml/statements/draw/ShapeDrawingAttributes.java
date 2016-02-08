/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;

public class ShapeDrawingAttributes extends FileDrawingAttributes {

	public double depth = 0.0;
	public boolean empty;
	public final List textures;
	public final IShape.Type type;

	public ShapeDrawingAttributes(final ILocation size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
		final ILocation location, final Boolean empty, final GamaColor color, final GamaColor border,
		final List textures, final IAgent agent, final IShape.Type type) {
		super(size, rotation, location, color, border, agent);
		this.depth = depth == null ? 0.0 : depth.doubleValue();
		this.empty = empty == null ? false : empty.booleanValue();
		this.border = border == null && this.empty ? color : border;
		this.textures = textures == null ? null : new ArrayList(textures);
		this.type = type;
	}

	public ShapeDrawingAttributes(final GamaPoint location) {
		this(location, null, null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
		this(location, color, border, null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border,
		final IShape.Type type) {
		this(null, null, null, location, color == null, color, border, null, null, type);
	}

	/**
	 * @param ag2
	 * @param c
	 * @param borderColor
	 */
	public ShapeDrawingAttributes(final IShape shape, final GamaColor color, final GamaColor border) {
		this(null, null, null, shape.getLocation(), color == null, color, border, null, shape.getAgent(),
			shape.getGeometricalType());
	}

	public void setDepthIfAbsent(final Double d) {
		if ( depth != 0.0 ) { return; }
		depth = d == null ? 0.0 : d;
	}

	@Override
	public List getTextures() {
		return textures;
	}

	/**
	 * Method isEmpty()
	 * @see msi.gaml.statements.draw.DrawingAttributes#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return empty;
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
	 * Method getDepth()
	 * @see msi.gaml.statements.draw.DrawingAttributes#getDepth()
	 */
	@Override
	public double getDepth() {
		return depth;
	}

}