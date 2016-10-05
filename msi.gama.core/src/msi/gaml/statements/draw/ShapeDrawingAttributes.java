/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ShapeDrawingAttributes extends FileDrawingAttributes {

	public double depth = 0.0;
	public boolean empty;
	public final List textures;
	public GamaMaterial material;
	public IShape.Type type;
	public List<GamaColor> colors;

	public ShapeDrawingAttributes(final ILocation size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
			final ILocation location, final Boolean empty, final GamaColor color, final List<GamaColor> colors,
			final GamaColor border, final List textures, final GamaMaterial material, final IAgent agent,
			final IShape.Type type) {
		super(size, rotation, location, color, border, agent);
		this.depth = depth == null ? 0.0 : depth.doubleValue();
		this.empty = empty == null ? false : empty.booleanValue();
		this.border = border == null && this.empty ? color : border;
		this.textures = textures == null ? null : new ArrayList(textures);
		this.material = material == null ? null : material;
		this.type = type;
		this.colors = colors;
	}

	public ShapeDrawingAttributes(final GamaPoint location) {
		this(location, null, null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
		this(location, color, border, null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border,
			final IShape.Type type) {
		this(null, null, null, location, color == null, color, null, border, null, null, null, type);
	}

	/**
	 * @param ag2
	 * @param c
	 * @param borderColor
	 */
	public ShapeDrawingAttributes(final IShape shape, final GamaColor color, final GamaColor border) {
		this(null, null, null, shape.getLocation(), color == null, color, null, border, null, null, shape.getAgent(),
				shape.getGeometricalType());
	}

	public void setDepthIfAbsent(final Double d) {
		if (depth != 0.0) {
			return;
		}
		depth = d == null ? 0.0 : d;
	}

	@Override
	public List getTextures() {
		return textures;
	}

	/**
	 * Method isEmpty()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return empty;
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
	 * Method getDepth()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getDepth()
	 */
	@Override
	public double getDepth() {
		return depth;
	}

	@Override
	public List<GamaColor> getColors() {
		return colors;
	}

	/**
	 * Method getMaterial()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getMaterial()
	 */
	@Override
	public GamaMaterial getMaterial() {
		return material;
	}

}