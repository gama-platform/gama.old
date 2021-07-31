package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisposable;

public interface IIntersectable extends IDisposable {

	boolean intersects(Envelope env);

	boolean intersects(Coordinate env);

}
