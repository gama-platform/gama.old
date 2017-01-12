/*********************************************************************************************
 *
 * 'ShapeDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ShapeDrawingAttributes extends FileDrawingAttributes {

	public boolean empty;
	public final List textures;
	public GamaMaterial material;
	public IShape.Type type;

	public ShapeDrawingAttributes(final GamaPoint size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
			final GamaPoint location, final Boolean empty, final GamaColor color, final List<GamaColor> colors,
			final GamaColor border, final List textures, final GamaMaterial material, final IAgent agent,
			final IShape.Type type, final Double lineWidth) {
		super(size, rotation, location, color, border, agent, lineWidth);
		setDepthIfAbsent(depth);
		this.empty = empty == null ? false : empty.booleanValue();
		setBorder(border == null && this.empty ? getColor() : border);
		this.textures = textures == null ? null : new ArrayList(textures);
		this.material = material == null ? null : material;
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
		this(null, null, null, location, color == null, color, null, border, null, null, null, type, null);
	}

	/**
	 * @param ag2
	 * @param c
	 * @param borderColor
	 */
	public ShapeDrawingAttributes(final IShape shape, final GamaColor color, final GamaColor border) {
		this(null, null, null, (GamaPoint) shape.getLocation(), color == null, color, null, border, null, null,
				shape.getAgent(), shape.getGeometricalType(), null);
	}

	public ShapeDrawingAttributes(final IShape shape, final GamaColor color, final GamaColor border,
			final double lineWidth) {
		this(null, null, null, (GamaPoint) shape.getLocation(), color == null, color, null, border, null, null,
				shape.getAgent(), shape.getGeometricalType(), lineWidth);
	}

	public void setDepthIfAbsent(final Double d) {
		if (getDepth() == null)
			setDepth(d);
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
	 * Method getMaterial()
	 * 
	 * @see msi.gaml.statements.draw.DrawingAttributes#getMaterial()
	 */
	@Override
	public GamaMaterial getMaterial() {
		return material;
	}

	@Override
	public void setEmpty(final boolean b) {
		empty = b;
	}

}