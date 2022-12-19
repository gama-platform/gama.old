/*******************************************************************************************************
 *
 * ShapeDrawingAttributes.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.file.GamaImageFile;

/**
 * The Class ShapeDrawingAttributes.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ShapeDrawingAttributes extends FileDrawingAttributes {

	/**
	 * Instantiates a new shape drawing attributes.
	 *
	 * @param size the size
	 * @param depth the depth
	 * @param rotation the rotation
	 * @param location the location
	 * @param empty the empty
	 * @param color the color
	 * @param border the border
	 * @param textures the textures
	 * @param material the material
	 * @param agent the agent
	 * @param type the type
	 * @param lineWidth the line width
	 * @param lighting the lighting
	 */
	public ShapeDrawingAttributes(final Scaling3D size, final Double depth, final AxisAngle rotation,
			final GamaPoint location, final Boolean empty, final GamaColor color, /* final List<GamaColor> colors, */
			final GamaColor border, final List<GamaImageFile> textures, final GamaMaterial material, final IAgent agent,
			final IShape.Type type, final Double lineWidth, final Boolean lighting) {
		super(size, rotation, location, color, border, agent, lineWidth, false, lighting);
		setHeight(depth);
		setEmpty(empty);
		setTextures(textures);
		setMaterial(material);
		setType(type);
		// setColors(colors);
	}

	/**
	 * Instantiates a new shape drawing attributes.
	 *
	 * @param location the location
	 * @param color the color
	 * @param border the border
	 * @param type the type
	 */
	public ShapeDrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border,
			final IShape.Type type) {
		super(null, null, location, color, border, null, null, false, null);
		setHeight(null);
		setEmpty(color == null);
		setTextures(null);
		setMaterial(null);
		setType(type);
		// this(null, null, null, location, color == null, color, /* null, */ border, null, null, null, type, null,
		// null);
	}

	/**
	 * Instantiates a new shape drawing attributes.
	 *
	 * @param shape the shape
	 * @param agent the agent
	 * @param color the color
	 * @param border the border
	 */
	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color,
			final GamaColor border) {
		this(shape, agent, color, border, shape.getGeometricalType(), null, shape.getDepth());
	}

	/**
	 * Instantiates a new shape drawing attributes.
	 *
	 * @param shape the shape
	 * @param agent the agent
	 * @param color the color
	 * @param border the border
	 * @param type the type
	 * @param lineWidth the line width
	 * @param depth the depth
	 */
	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color, final GamaColor border,
			final IShape.Type type, final Double lineWidth, final Double depth) {
		this(null, depth, null, shape.getLocation(), color == null, color, /* null, */ border, null, null, agent, type,
				lineWidth, null);
	}

}