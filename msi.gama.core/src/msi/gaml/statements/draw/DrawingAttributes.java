/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

public abstract class DrawingAttributes {

	public GamaPoint size;
	public final GamaPair<Double, GamaPoint> rotation;
	public GamaPoint location;
	public GamaColor color;
	public boolean wireframe = false;

	public DrawingAttributes(final ILocation size, final GamaPair<Double, GamaPoint> rotation, final ILocation location,
			final GamaColor color) {
		this.size = size == null ? null : new GamaPoint(size);

		if (rotation != null) {
			this.rotation = new GamaPair(Cast.asFloat(null, rotation.key), Cast.asPoint(null, rotation.value),
					Types.FLOAT, Types.POINT);
		} else {
			this.rotation = null;
		}
		// To make sure no side effect can happen
		this.location = location == null ? null : new GamaPoint(location);
		this.color = color;
	}

	public DrawingAttributes(final GamaPoint location) {
		this(location, null, null, null);
	}

	public DrawingAttributes(final GamaPoint location, final GamaColor color) {
		this(null, null, location, color);
	}

	public void setLocationIfAbsent(final GamaPoint point) {
		if (location == null) {
			location = point;
		}
	}

	public abstract List getTextures();

	public abstract boolean isEmpty();

	public abstract AgentIdentifier getAgentIdentifier();

	public abstract GamaColor getBorder();
	
	public List<GamaColor> getColors() {return null;}

	public abstract double getDepth();
	
	public abstract GamaMaterial getMaterial();

	public String getSpeciesName() {
		return null;
	}
}