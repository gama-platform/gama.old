/*********************************************************************************************
 *
 * 'DrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gaml.operators.IUnits;

public abstract class DrawingAttributes {

	private static int INDEX = 0;
	static final GamaColor SELECTED_COLOR = new GamaColor(Color.red);
	private final int uniqueIndex;
	protected final ColorProperties colorProperties = new ColorProperties();
	protected GeometricProperties geometryProperties = GeometricProperties.create();
	protected boolean selected;
	protected boolean synthetic; // if the attributes have been built on the fly
	protected GamaColor highlight;

	public DrawingAttributes(final Scaling3D size, final GamaPair<Double, GamaPoint> rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final Boolean lighting) {
		setBorder(border);
		setColor(color);
		setSize(size);
		setLocation(location == null ? null : new GamaPoint(location));
		if (rotation != null) {
			setRotation(rotation.key, rotation.value);
		}
		withLighting(lighting);
		uniqueIndex = INDEX++;
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

	public abstract IShape.Type getType();

	public DrawingAttributes(final GamaPoint location, final GamaColor color) {
		this(null, null, location, color, null, true);
	}

	public void setColor(final GamaColor fill) {
		colorProperties.withFill(fill);
	}

	public DrawingAttributes withLighting(final Boolean lighting) {
		if (lighting == null) { return this; }
		colorProperties.withLighting(lighting);
		return this;
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
		geometryProperties = geometryProperties.withSize(size);
	}

	public void setRotation(final Double angle, final GamaPoint axis) {
		if (angle == null) {
			geometryProperties = geometryProperties.withRotation(null);
		} else if (axis == null) {
			geometryProperties = geometryProperties.withRotation(new AxisAngle(Rotation3D.PLUS_K, angle));
		} else {
			geometryProperties = geometryProperties.withRotation(new AxisAngle(axis, angle));
		}
	}

	public final GamaColor getColor() {
		if (selected) { return SELECTED_COLOR; }
		if (highlight != null) { return highlight; }
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
		geometryProperties = geometryProperties.withHeight(d);
	}

	public void setLocationIfAbsent(final GamaPoint point) {
		if (getLocation() == null) {
			setLocation(point);
		}
	}

	@SuppressWarnings ("rawtypes")
	public List getTextures() {
		return colorProperties.getTextures();
	}

	public abstract AgentIdentifier getAgentIdentifier();

	public GamaColor[] getColors() {
		return colorProperties.getColors();
	}

	public abstract GamaMaterial getMaterial();

	public String getSpeciesName() {
		return null;
	}

	public void setLocation(final GamaPoint location) {
		geometryProperties = geometryProperties.withLocation(location);
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
		if (geometryProperties.getRotation() == null) { return null; }
		return geometryProperties.getRotation().angle;
	}

	public GamaPoint getAxis() {
		if (geometryProperties.getRotation() == null) { return null; }
		return geometryProperties.getRotation().getAxis();
	}

	public Double getLineWidth() {
		return GamaPreferences.Displays.CORE_LINE_WIDTH.getValue();
	}

	public void setTexture(final Object o) {
		if (o == null) {
			colorProperties.withTextures(null);
		} else {
			colorProperties.withTextures(Arrays.asList(o));
		}

	}

	public void setHighlighted(final GamaColor color) {
		highlight = color;
	}

	public void markSelected(final int pickedIndex) {
		setSelected(pickedIndex == uniqueIndex);
	}

	public boolean isAnimated() {
		return colorProperties.isAnimated();
	}

	public int getFrameCount() {
		return colorProperties.getFrameCount();
	}

	public int getAverageDelay() {
		return colorProperties.getAverageDelay();
	}

	public GamaPoint getAnchor() {
		return IUnits.bottom_left;
	}

	public boolean isLighting() {
		return colorProperties.isLighting();
	}

}