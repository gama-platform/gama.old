/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;

public class FieldDrawingAttributes extends FileDrawingAttributes {

	private static GamaPoint zeroPoint = new GamaPoint(0, 0);
	// empty whether or not we apply the texture and/or color
	// border whether or not we draw the lines

	public double depth;
	public boolean empty;
	public List textures;
	public String speciesName;
	public boolean triangulated;
	public boolean grayScaled;
	public boolean withText;
	public GamaPoint fieldSize;
	public GamaPoint cellSize;

	public FieldDrawingAttributes(final ILocation size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
		final ILocation location, final Boolean empty, final GamaColor color, final GamaColor border,
		final List textures, final IAgent agent) {
		super(size, rotation, location, color, border, agent);
		this.depth = depth == null ? 1.0 : depth.doubleValue();
		this.empty = empty == null ? false : empty.booleanValue();
		this.border = border == null && this.empty ? color : border;
		this.textures = textures == null ? null : new ArrayList(textures);
	}

	/**
	 * @param name
	 * @param lineColor
	 */
	public FieldDrawingAttributes(final String name, final GamaColor border) {
		super(zeroPoint, null, border);
		speciesName = name;
		textures = null;
	}

	@Override
	public List getTextures() {
		return textures;
	}

	public void setSpeciesName(final String name) {
		speciesName = name;
	}

	@Override
	public String getSpeciesName() {
		return speciesName;
	}

	@Override
	public double getDepth() {
		return depth;
	}

}