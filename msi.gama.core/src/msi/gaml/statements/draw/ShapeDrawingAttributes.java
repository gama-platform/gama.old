/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ShapeDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ShapeDrawingAttributes extends FileDrawingAttributes {

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

	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color,
			final GamaColor border) {
		this(shape, agent, color, border, shape.getGeometricalType(), null, shape.getDepth());
	}

	public ShapeDrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color, final GamaColor border,
			final IShape.Type type, final Double lineWidth, final Double depth) {
		this(null, depth, null, (GamaPoint) shape.getLocation(), color == null, color, /* null, */ border, null, null,
				agent, type, lineWidth, null);
	}

}