/**
 * Created by drogoul, 3 févr. 2016
 *
 */
package msi.gaml.statements.draw;

import java.util.List;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;

public abstract class DrawingAttributes {

	public GamaPoint size;
	public final GamaPair<Double, GamaPoint> rotation;
	public GamaPoint location;
	public GamaColor color;

	public DrawingAttributes(final ILocation size, final GamaPair<Double, GamaPoint> rotation, final ILocation location,
		final GamaColor color) {
		this.size = size == null ? null : new GamaPoint(size);
		this.rotation = rotation;
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
		if ( location == null ) {
			location = point;
		}
	}

	public abstract List getTextures();

	public abstract boolean isEmpty();

	public abstract IAgent getAgent();

	public abstract GamaColor getBorder();

	public abstract double getDepth();

	public String getSpeciesName() {
		return null;
	}
}