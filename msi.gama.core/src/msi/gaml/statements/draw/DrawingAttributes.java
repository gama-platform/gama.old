/*******************************************************************************************************
 *
 * DrawingAttributes.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.constants.GamlCoreConstants;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class DrawingAttributes.
 */
public class DrawingAttributes {

	static {
		DEBUG.ON();
	}

	/**
	 * The Enum Flag.
	 */
	public enum Flag {
		/** The Empty. */
		Empty,
		/** The Selected. */
		Selected,
		/** The Synthetic. */
		Synthetic,
		/** The Lighted. */
		Lighted,
		/** The Use cache. */
		UseCache,
		/** The Grayscaled. */
		Grayscaled,
		/** The Triangulated. */
		Triangulated,
		/** The With text. */
		WithText,
		/** The Perspective. */
		Perspective
	}

	/**
	 * The Enum DrawerType.
	 */
	public enum DrawerType {
		/** The geometry. */
		GEOMETRY,
		/** The string. */
		STRING,
		/** The mesh. */
		MESH,
		/** The resource. */
		RESOURCE
	}

	/** The index. */
	private static int INDEX = 0;

	/** The Constant TEXTURED_COLOR. */
	public static final GamaColor TEXTURED_COLOR = new GamaColor(Color.white);

	/** The Constant SELECTED_COLOR. */
	public static final GamaColor SELECTED_COLOR = new GamaColor(Color.red);

	/** The flags. */
	EnumSet<Flag> flags = EnumSet.of(Flag.Lighted);

	/** The unique index. */
	private final int uniqueIndex;

	/** The location. */
	GamaPoint location;

	/** The size. */
	Scaling3D size;

	/** The rotation. */
	AxisAngle rotation;

	/** The line width. */
	Double depth = null, lineWidth = GamaPreferences.Displays.CORE_LINE_WIDTH.getValue();

	/** The type. */
	public IShape.Type type;

	/** The border. */
	GamaColor fill, highlight, border;

	/** The textures. */
	List<?> textures;

	/** The material. */
	// GamaMaterial material;

	/**
	 * Instantiates a new drawing attributes.
	 */
	private DrawingAttributes() {
		uniqueIndex = INDEX++;

	}

	/**
	 * Instantiates a new drawing attributes.
	 *
	 * @param size
	 *            the size
	 * @param rotation
	 *            the rotation
	 * @param location
	 *            the location
	 * @param color
	 *            the color
	 * @param border
	 *            the border
	 * @param lighting
	 *            the lighting
	 */
	public DrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final Boolean lighting) {
		this();
		setBorder(border);
		setFill(color);
		setSize(size);
		setLocation(location == null ? null : new GamaPoint(location));
		setRotation(rotation);
		setLighting(lighting);
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() { return uniqueIndex; }

	/**
	 * Sets the synthetic.
	 *
	 * @param s
	 *            the new synthetic
	 */
	public void setSynthetic(final boolean s) {
		setFlag(Flag.Synthetic, s);
	}

	/**
	 * Checks if is synthetic.
	 *
	 * @return true, if is synthetic
	 */
	public boolean isSynthetic() { return isSet(Flag.Synthetic); }

	/**
	 * Sets the lighting.
	 *
	 * @param lighting
	 *            the new lighting
	 */
	public void setLighting(final Boolean lighting) {
		if (lighting == null) return;
		setFlag(Flag.Lighted, lighting);
	}

	/**
	 * Sets the empty.
	 *
	 * @param b
	 *            the new empty
	 */
	public void setEmpty(final Boolean b) {
		if (b == null || !b) {
			setFilled();
		} else {
			setEmpty();
		}
	}

	/**
	 * Gets the agent identifier.
	 *
	 * @return the agent identifier
	 */
	public IAgent getAgentIdentifier() { return null; }

	/**
	 * Gets the species name.
	 *
	 * @return the species name
	 */
	public String getSpeciesName() { return null; }

	/**
	 * Returns the angle of the rotation in degrees (or null if no rotation is defined)
	 *
	 * @return
	 */
	public Double getAngle() {
		if (getRotation() == null) return null;
		return getRotation().angle;
	}

	/**
	 * Sets the texture.
	 *
	 * @param o
	 *            the new texture
	 */
	public void setTexture(final Object o) {
		if (o == null) {
			setTextures(null);
		} else {
			setTextures(Arrays.asList(o));
		}
	}

	/**
	 * Mark selected.
	 *
	 * @param pickedIndex
	 *            the picked index
	 */
	public void markSelected(final int pickedIndex) {
		setSelected(pickedIndex == uniqueIndex);
	}

	/**
	 * Gets the anchor.
	 *
	 * @return the anchor
	 */
	public GamaPoint getAnchor() { return GamlCoreConstants.bottom_left; }

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public GamaPoint getLocation() { return location; }

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public Scaling3D getSize() { return size; }

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	public Double getDepth() { return depth; }

	/**
	 * Sets the line width.
	 *
	 * @param d
	 *            the new line width
	 */
	public void setLineWidth(final Double d) {
		if (d == null) {
			lineWidth = GamaPreferences.Displays.CORE_LINE_WIDTH.getValue();
		} else {
			lineWidth = d;
		}
	}

	/**
	 * Gets the line width.
	 *
	 * @return the line width
	 */
	public Double getLineWidth() { return lineWidth; }

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public IShape.Type getType() { return type; }

	/**
	 * Use cache.
	 *
	 * @return true, if successful
	 */
	public boolean useCache() {
		return isSet(Flag.UseCache);
	}

	/**
	 * Sets the use cache.
	 *
	 * @param b
	 *            the new use cache
	 */
	public void setUseCache(final boolean b) {
		setFlag(Flag.UseCache, b);
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(final IShape.Type type) { this.type = type; }

	/**
	 * Gets the rotation.
	 *
	 * @return the rotation
	 */
	public AxisAngle getRotation() { return rotation; }

	/**
	 * Sets the location.
	 *
	 * @param loc
	 *            the new location
	 */
	public void setLocation(final GamaPoint loc) { location = loc; }

	/**
	 * Sets the size.
	 *
	 * @param size
	 *            the new size
	 */
	public void setSize(final Scaling3D size) { this.size = size; }

	/**
	 * Sets the rotation.
	 *
	 * @param rotation
	 *            the new rotation
	 */
	public void setRotation(final AxisAngle rotation) {
		if (rotation == null) return;
		this.rotation = rotation;
	}

	/**
	 * Sets the height.
	 *
	 * @param depth
	 *            the new height
	 */
	public void setHeight(final Double depth) {
		if (depth == null) return;
		this.depth = depth;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public GamaColor getColor() {
		if (isSelected()) // DEBUG.OUT("Selected agent: " + getAgentIdentifier() + " / index : " + uniqueIndex);
			return SELECTED_COLOR;
		if (highlight != null) return highlight;
		if (isSet(Flag.Empty)) return null;
		if (fill == null) {
			if (textures != null) return TEXTURED_COLOR;
			// Always returns the color as we are solid; so null cannot be an option
			// see issue #2724
			return GamaPreferences.Displays.CORE_COLOR.getValue();
			// }
			// return null;
		}
		return fill;
	}

	/**
	 * Gets the border.
	 *
	 * @return the border
	 */
	public GamaColor getBorder() {
		if (isSet(Flag.Empty) && border == null) return fill;
		return border;
	}

	/**
	 * Sets the empty.
	 */
	public void setEmpty() {
		setFlag(Flag.Empty, true);
	}

	/**
	 * Sets the filled.
	 */
	public void setFilled() {
		setFlag(Flag.Empty, false);
	}

	/**
	 * Sets the fill.
	 *
	 * @param color
	 *            the new fill
	 */
	public void setFill(final GamaColor color) { fill = color; }

	/**
	 * Sets the border.
	 *
	 * @param border
	 *            the new border
	 */
	public void setBorder(final GamaColor border) { this.border = border; }

	/**
	 * Sets the lighting.
	 *
	 * @param lighting
	 *            the new lighting
	 */
	void setLighting(final boolean lighting) {
		setFlag(Flag.Lighted, lighting);
	}

	/**
	 * Sets the no border.
	 */
	public void setNoBorder() {
		border = null;
	}

	/**
	 * Sets the textures.
	 *
	 * @param textures
	 *            the new textures
	 */
	public void setTextures(final List<?> textures) { this.textures = textures; }

	/**
	 * Gets the textures.
	 *
	 * @return the textures
	 */
	public List getTextures() { return textures; }

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() { return isSet(Flag.Empty); }

	/**
	 * Checks if is animated.
	 *
	 * @return true, if is animated
	 */
	public boolean isAnimated() {
		if (!useCache()) return true;
		if (textures == null) return false;
		final Object o = textures.get(0);
		if (!(o instanceof IImageProvider iip)) return false;
		return iip.isAnimated();
	}

	// /**
	// * Gets the frame count.
	// *
	// * @return the frame count
	// */
	// public int getFrameCount() {
	// if (textures == null) return 1;
	// final Object o = textures.get(0);
	// if (!(o instanceof GamaGifFile)) return 1;
	// return ((GamaGifFile) o).getFrameCount();
	//
	// }
	//
	// /**
	// * Gets the average delay.
	// *
	// * @return the average delay
	// */
	// public int getAverageDelay() {
	// if (textures == null) return 0;
	// final Object o = textures.get(0);
	// if (!(o instanceof GamaGifFile)) return 0;
	// return ((GamaGifFile) o).getAverageDelay();
	//
	// }

	/**
	 * Checks if is lighting.
	 *
	 * @return true, if is lighting
	 */
	public boolean isLighting() { return isSet(Flag.Lighted); }

	/**
	 * Sets the highlighted.
	 *
	 * @param color
	 *            the new highlighted
	 */
	public void setHighlighted(final GamaColor color) { highlight = color; }

	/**
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() { return isSet(Flag.Selected); }

	/**
	 * Sets the selected.
	 *
	 * @param b
	 *            the new selected
	 */
	public void setSelected(final boolean b) {
		setFlag(Flag.Selected, b);
	}

	/**
	 * Sets the material.
	 *
	 * @param m
	 *            the new material
	 */
	// public void setMaterial(final GamaMaterial m) {
	// material = m;
	//
	// }

	/**
	 * Checks if is sets the.
	 *
	 * @param value
	 *            the value
	 * @return true, if is sets the
	 */
	public boolean isSet(final Flag value) {
		return flags.contains(value);
	}

	/**
	 * Sets the flag.
	 *
	 * @param value
	 *            the value
	 * @param b
	 *            the b
	 */
	public void setFlag(final Flag value, final boolean b) {
		if (b) {
			flags.add(value);
		} else {
			flags.remove(value);
		}
	}

}