/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;

public class DrawingAttributesOld {

	public GamaPoint size;
	public Double depth = 0.0;
	public final GamaPair<Double, GamaPoint> rotation;
	public GamaPoint location;
	public Boolean empty;
	public GamaColor border;
	public final Boolean hasBorder;
	public final Boolean hasColor;
	public GamaColor color;
	public final GamaFont font;
	public List textures;
	public Boolean perspective = true;
	public IAgent agent;
	public IShape.Type type;
	public String speciesName = null;
	public Boolean isDynamic = false;

	public DrawingAttributesOld(final ILocation size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
		final ILocation location, final Boolean empty, final GamaColor border, final Boolean hasBorder,
		final GamaColor color, final GamaFont font, final List textures, final Boolean perspective,
		final Boolean hasColor, final IAgent agent) {
		this.size = size == null ? null : new GamaPoint(size);
		this.depth = depth == null ? 0.0 : depth;
		this.rotation = rotation;
		// To make sure no side effect can happen
		this.location = location == null ? null : new GamaPoint(location);
		this.empty = empty;
		this.border = border == null && empty ? color : border;
		this.hasBorder = hasBorder || empty;
		this.color = color;
		this.font = font;
		this.textures = textures == null ? null : new ArrayList(textures);
		this.perspective = perspective;
		this.hasColor = hasColor;
		this.agent = agent;
	}

	public DrawingAttributesOld(final GamaPoint location) {
		this(location, null, null);
	}

	public DrawingAttributesOld(final GamaPoint location, final GamaColor color, final GamaColor border) {
		this.location = location;
		this.size = null;
		this.rotation = null;
		this.empty = color == null;
		this.border = border;
		this.hasBorder = border != null;
		this.color = color;
		this.font = null;
		this.textures = null;
		this.hasColor = color != null;
		this.agent = null;
	}

	public void setShapeType(final IShape.Type type) {
		this.type = type;
	}

	public void setSpeciesName(final String name) {
		speciesName = name;
	}

	public void setDynamic(final Boolean b) {
		isDynamic = b;
	}

	/**
	 * @param attribute
	 */
	public void setDepthIfAbsent(final Double d) {
		if ( depth != 0.0 ) { return; }
		depth = d == null ? 0.0 : d;
	}

	/**
	 * @param gamaPoint
	 */
	public void setLocationIfAbsent(final GamaPoint point) {
		if ( location == null ) {
			location = point;
		}
	}

}