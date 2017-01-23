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

import java.util.Arrays;
import java.util.List;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

public abstract class DrawingAttributes {

	protected ColorProperties colorProperties = ColorProperties.NONE;
	protected GeometricProperties geometryProperties = GeometricProperties.create();

	public DrawingAttributes(final GamaPoint size, final GamaPair<Double, GamaPoint> rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border) {
		setBorder(border);
		setColor(color);
		setSize(size);
		setLocation(location == null ? null : new GamaPoint(location));
		if (rotation != null) {
			setRotation(rotation.key, rotation.value);
		}
	}

	public DrawingAttributes(final GamaPoint location) {
		this(location, null, null, null, null);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color) {
		this(null, null, location, color, null);
	}

	public void setColor(final GamaColor fill) {
		colorProperties = colorProperties.withFill(fill);
	}

	protected void setColors(final List<GamaColor> cc) {
		colorProperties = colorProperties.withColors(cc == null ? null : cc.toArray(new GamaColor[cc.size()]));
	}

	public boolean isEmpty() {
		return colorProperties.isEmpty();
	}

	public void setEmpty(final Boolean b) {
		if (b == null || !b)
			colorProperties = colorProperties.toFilled();
		else
			colorProperties = colorProperties.toEmpty();
	}

	public void setBorder(final GamaColor border) {
		colorProperties = colorProperties.withBorder(border);
	}

	public void setSize(final GamaPoint size) {
		geometryProperties = geometryProperties.withSize(size);
	}

	public void setRotation(final Double angle, final GamaPoint axis) {
		geometryProperties = geometryProperties.withRotation(angle, axis);
	}

	public GamaColor getColor() {
		return colorProperties.getFillColor();
	}

	public GamaColor getBorder() {
		return colorProperties.getBorderColor();
	}

	public GamaPoint getLocation() {
		return geometryProperties.getLocation();
	}

	public GamaPoint getSize() {
		return geometryProperties.getSize();
	}

	public Double getDepth() {
		return geometryProperties.getDepth();
	}

	public void setDepth(final Double d) {
		geometryProperties = geometryProperties.withDepth(d);
	}

	public void setLocationIfAbsent(final GamaPoint point) {
		if (getLocation() == null) {
			setLocation(point);
		}
	}

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

	public Double getAngle() {
		return geometryProperties.getAngle();
	}

	public GamaPoint getAxis() {
		return geometryProperties.getAxis();
	}

	public void setAngle(final Double angle) {
		geometryProperties = geometryProperties.withRotation(angle, geometryProperties.getAxis());

	}

	public Double getLineWidth() {
		return GamaPreferences.CORE_LINE_WIDTH.getValue();
	}

	public void setTexture(final Object o) {
		if (o == null)
			colorProperties = colorProperties.withTextures(null);
		else
			colorProperties = colorProperties.withTextures(Arrays.asList(o));

	}

	public void setHighlighted(final GamaColor color) {
		colorProperties = colorProperties.withHighlight(color);
	}

}