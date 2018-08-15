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

import java.util.List;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ShapeDrawingAttributes extends FileDrawingAttributes {

	public GamaMaterial material;
	public IShape.Type type;

	public ShapeDrawingAttributes(final Scaling3D size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
			final GamaPoint location, final Boolean empty, final GamaColor color, final List<GamaColor> colors,
			final GamaColor border, final List textures, final GamaMaterial material, final IAgent agent,
			final IShape.Type type, final Double lineWidth, final Boolean lighting) {
		super(size, rotation, location, color, border, agent, lineWidth, false, lighting);
		setHeightIfAbsent(depth);
		setEmpty(empty);
		setTextures(textures);
		this.material = material == null ? null : material;
		this.type = type;
		setColors(colors);
	}

	private void setTextures(final List textures) {
		colorProperties.withTextures(textures);
	}

	@Override
	public ShapeDrawingAttributes withLighting(final Boolean lighting) {
		return (ShapeDrawingAttributes) super.withLighting(lighting);
	}

	public ShapeDrawingAttributes(final GamaPoint location) {
		this(location, null, null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
		this(location, color, border, (IShape.Type) null);
	}

	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border,
			final IShape.Type type) {
		this(null, null, null, location, color == null, color, null, border, null, null, null, type, null, null);
	}

	/**
	 * @param ag2
	 * @param c
	 * @param borderColor
	 */
	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color,
			final GamaColor border) {
		this(shape, agent, color, border, null);
	}

	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color, final GamaColor border,
			final Double lineWidth) {
		this(null, null, null, (GamaPoint) shape.getLocation(), color == null, color, null, border, null, null, agent,
				shape.getGeometricalType(), lineWidth, null);
	}

	public void setHeightIfAbsent(final Double d) {
		if (getHeight() == null) {
			setHeight(d);
		}
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
	public IShape.Type getType() {
		return type;
	}

}