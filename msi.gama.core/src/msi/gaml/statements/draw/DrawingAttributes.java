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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;

public abstract class DrawingAttributes {

	/**
	 * COLORS
	 */
	int ID_COLOR = 0;
	int ID_BORDER = 1;
	/**
	 * POINTS
	 */
	int ID_SIZE = 1;
	int ID_AXIS = 2;
	int ID_LOCATION = 0;
	int ID_CELL_SIZE = 3;
	/**
	 * DOUBLES
	 */
	int ID_ANGLE = 0;
	int ID_DEPTH = 1;
	int ID_LINE_WIDTH = 2;
	/**
	 * BOOLEANS
	 */
	int ID_EMPTY = 0;
	int ID_TRIANGULATED = 1;

	/**
	 * Array of colors: 0 fill; 1 border; 2+ others
	 */
	protected GamaColor[] colors;
	protected GamaPoint[] positions;
	protected Double[] lengths;
	protected Boolean[] flags;

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public DrawingAttributes(final GamaPoint size, final GamaPair<Double, GamaPoint> rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border) {
		setBorder(border);
		setColor(color);
		setSize(size == null ? null : new GamaPoint(size));
		setLocation(location == null ? null : new GamaPoint(location));
		if (rotation != null) {
			setAxis(rotation.value == null ? null : new GamaPoint(rotation.value));
			setAngle(rotation.key == null ? 0 : rotation.key);
		}
	}

	public DrawingAttributes(final GamaPoint location) {
		this(location, null, null, null, null);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color) {
		this(null, null, location, color, null);
	}

	public void setColor(final GamaColor color) {
		setColor(ID_COLOR, color);
	}

	public void setBorder(final GamaColor border) {
		setColor(ID_BORDER, border);
	}

	public void setSize(final GamaPoint size) {
		setPosition(ID_SIZE, size);
	}

	public void setAxis(final GamaPoint axis) {
		setPosition(ID_AXIS, axis);
	}

	public void setAngle(final Double angle) {
		setLength(ID_ANGLE, angle);
	}

	public GamaColor getColor() {
		return get(colors, ID_COLOR);
	}

	public GamaColor getBorder() {
		return get(colors, ID_BORDER);
	}

	public GamaPoint getLocation() {
		return get(positions, ID_LOCATION);
	}

	public GamaPoint getSize() {
		return get(positions, ID_SIZE);
	}

	public Double getDepth() {
		return get(lengths, ID_DEPTH);
	}

	public void setDepth(final Double d) {
		setLength(ID_DEPTH, d);
	}

	public void setLocationIfAbsent(final GamaPoint point) {
		if (getLocation() == null) {
			setLocation(point);
		}
	}

	public abstract List<?> getTextures();

	public abstract boolean isEmpty();

	public abstract AgentIdentifier getAgentIdentifier();

	public GamaColor[] getColors() {
		return colors;
	}

	public abstract GamaMaterial getMaterial();

	public String getSpeciesName() {
		return null;
	}

	public Double getLineWidth() {
		final Double lw = get(lengths, ID_LINE_WIDTH);
		return lw == null ? GamaPreferences.CORE_LINE_WIDTH.getValue() : lw;
	}

	public void setLocation(final GamaPoint location) {
		setPosition(ID_LOCATION, location);
	}

	public void setEmpty(final boolean b) {}

	protected void setPosition(final int index, final GamaPoint object) {
		if (positions == null)
			positions = (GamaPoint[]) Array.newInstance(GamaPoint.class, index + 1);
		else if (positions.length < index + 1)
			positions = Arrays.copyOf(positions, index + 1);
		positions[index] = object;
	}

	protected void setLength(final int index, final Double object) {
		if (lengths == null)
			lengths = (Double[]) Array.newInstance(Double.class, index + 1);
		else if (lengths.length < index + 1)
			lengths = Arrays.copyOf(lengths, index + 1);
		lengths[index] = object;
	}

	protected void setFlag(final int index, final Boolean object) {
		if (flags == null)
			flags = (Boolean[]) Array.newInstance(Boolean.class, index + 1);
		else if (flags.length < index + 1)
			flags = Arrays.copyOf(flags, index + 1);
		flags[index] = object;
	}

	protected void setColor(final int index, final GamaColor object) {
		if (colors == null)
			colors = (GamaColor[]) Array.newInstance(GamaColor.class, index + 1);
		else if (colors.length < index + 1)
			colors = Arrays.copyOf(colors, index + 1);
		colors[index] = object;
	}

	protected <T> T get(final T[] array, final int index) {
		if (array == null)
			return null;
		if (array.length < index + 1)
			return null;
		return array[index];
	}

	public Double getAngle() {
		return get(lengths, ID_ANGLE);
	}

	public GamaPoint getAxis() {
		return get(positions, ID_AXIS);
	}
}