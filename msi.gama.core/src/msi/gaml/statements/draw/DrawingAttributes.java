/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.DrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.util.Arrays;
import java.util.List;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gaml.operators.IUnits;

public class DrawingAttributes implements IDisposable {

	private static int INDEX = 0;

	private final int uniqueIndex = INDEX++;
	protected final ColorProperties colorProperties = ColorProperties.create();
	protected final GeometricProperties geometryProperties = GeometricProperties.create();
	protected IAgent agent;
	protected boolean selected;
	protected boolean synthetic; // if the attributes have been built on the fly

	public DrawingAttributes() {}

	public DrawingAttributes(final Scaling3D size, final GamaPair<Double, GamaPoint> rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final Boolean lighting) {
		setBorder(border);
		setColor(color);
		setSize(size);
		setLocation(location == null ? null : GamaPoint.create(location));
		if (rotation != null) {
			setRotation(rotation.key, rotation.value);
		}
		setLighting(lighting);
	}

	public DrawingAttributes(final Scaling3D size, final GamaPair<Double, GamaPoint> rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final IAgent agent, final Double lineWidth,
			final boolean isImage, final Boolean lighting) {
		this(size, rotation, location, color, border, lighting);
		this.agent = agent;
		setLineWidth(lineWidth);
		setType(isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE);
	}

	public DrawingAttributes(final Scaling3D size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
			final GamaPoint location, final Boolean empty, final GamaColor color, final List<GamaColor> colors,
			final GamaColor border, final List textures, final GamaMaterial material, final IAgent agent,
			final IShape.Type type, final Double lineWidth, final Boolean lighting) {
		this(size, rotation, location, color, border, agent, lineWidth, false, lighting);
		setHeightIfAbsent(depth);
		setEmpty(empty);
		setTextures(textures);
		setType(type);
		setColors(colors);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
		this(location, color, border, (IShape.Type) null);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border,
			final IShape.Type type) {
		this(null, null, null, location, color == null, color, null, border, null, null, null, type, null, null);
	}

	public DrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color, final GamaColor border) {
		this(shape, agent, color, border, shape.getGeometricalType(), null);
	}

	public DrawingAttributes(final IShape shape, final IAgent agent, final GamaColor color, final GamaColor border,
			final IShape.Type type, final Double lineWidth) {
		this(null, null, null, shape.getLocation(), color == null, color, null, border, null, null, agent, type,
				lineWidth, null);
	}

	@Override
	public void dispose() {
		colorProperties.dispose();
		geometryProperties.dispose();
	}

	public int getIndex() {
		return uniqueIndex;
	}

	public void setSynthetic(final boolean s) {
		synthetic = s;
	}

	public boolean isSynthetic() {
		return synthetic;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(final boolean b) {
		selected = b;
	}

	public IShape.Type getType() {
		return geometryProperties.getType();
	}

	public void setType(final Type type) {
		geometryProperties.setType(type);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color) {
		this(null, null, location, color, null, true);
	}

	public void setColor(final GamaColor fill) {
		colorProperties.withFill(fill);
	}

	public void setLighting(final Boolean lighting) {
		colorProperties.withLighting(lighting);
	}

	protected void setColors(final List<GamaColor> cc) {
		colorProperties.withColors(cc == null ? null : cc.toArray(new GamaColor[cc.size()]));
	}

	public boolean isEmpty() {
		return colorProperties.isEmpty();
	}

	public void setEmpty(final Boolean b) {
		if (b == null || !b) {
			colorProperties.toFilled();
		} else {
			colorProperties.toEmpty();
		}
	}

	public void setBorder(final GamaColor border) {
		colorProperties.withBorder(border);
	}

	public void setSize(final Scaling3D size) {
		geometryProperties.withSize(size);
	}

	public void setRotation(final Double angle, final GamaPoint axis) {
		if (angle == null || axis != null && axis.isNull()) {
			geometryProperties.withRotation(null);
		} else if (axis == null) {
			geometryProperties.withRotation(new AxisAngle(Rotation3D.PLUS_K, angle));
		} else {
			geometryProperties.withRotation(new AxisAngle(axis, angle));
		}
	}

	public final GamaColor getColor() {
		if (selected) { return ColorProperties.SELECTED_COLOR; }

		return colorProperties.getFillColor();
	}

	public GamaColor getBorder() {
		return colorProperties.getBorderColor();
	}

	public GamaPoint getLocation() {
		return geometryProperties.getLocation();
	}

	public Scaling3D getSize() {
		return geometryProperties.getSize();
	}

	public Double getHeight() {
		return geometryProperties.getHeight();
	}

	public void setHeight(final Double d) {
		geometryProperties.withHeight(d);
	}

	public void setHeightIfAbsent(final Double d) {
		if (getHeight() == null) {
			setHeight(d);
		}
	}

	public void setLocationIfAbsent(final GamaPoint point) {
		if (getLocation() == null) {
			setLocation(point);
		}
	}

	public List getTextures() {
		return colorProperties.getTextures();
	}

	public void setTextures(final List textures) {
		colorProperties.withTextures(textures);
	}

	public IAgent getAgent() {
		return agent;
	}

	public GamaColor[] getColors() {
		return colorProperties.getColors();
	}

	public String getSpeciesName() {
		return null;
	}

	public void setLocation(final GamaPoint location) {
		geometryProperties.withLocation(location);
	}

	public AxisAngle getRotation() {
		return geometryProperties.getRotation();
	}

	/**
	 * Returns the angle of the rotation in degrees (or null if no rotation is defined)
	 *
	 * @return
	 */
	public Double getAngle() {
		return geometryProperties.getAngle();
	}

	public GamaPoint getAxis() {
		return geometryProperties.getAxis();
	}

	public Double getLineWidth() {
		return geometryProperties.getLineWidth();
	}

	public void setLineWidth(final Double d) {
		geometryProperties.setLineWidth(d);
	}

	public void setTexture(final Object o) {
		if (o == null) {
			colorProperties.withTextures(null);
		} else {
			colorProperties.withTextures(Arrays.asList(o));
		}
	}

	public void setHighlighted(final GamaColor color) {
		colorProperties.highlight = color;
	}

	public void markSelected(final int pickedIndex) {
		setSelected(pickedIndex == uniqueIndex);
	}

	public boolean isAnimated() {
		return colorProperties.isAnimated();
	}

	public GamaPoint getAnchor() {
		return IUnits.bottom_left;
	}

	public boolean isLighting() {
		return colorProperties.isLighting();
	}

	public boolean useCache() {
		return colorProperties.useCache;
	}

	public void setUseCache(final boolean b) {
		colorProperties.useCache = b;
	}

}