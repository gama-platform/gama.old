package msi.gama.metamodel.topology;

import msi.gama.metamodel.shape.GamaPoint;

public interface IEnvelope {

	boolean intersects(IEnvelope bounds);

	boolean covers(IEnvelope bounds);

	GamaPoint getLocation();

	boolean isPoint();

	double getMaxX();

	double getMaxY();

	double getMinX();

	double getMinY();

	default double getEnvWidth() {
		return getMaxX() - getMinX();
	}

	default double getEnvHeight() {
		return getMaxY() - getMinY();
	}

	boolean isNull();

}
