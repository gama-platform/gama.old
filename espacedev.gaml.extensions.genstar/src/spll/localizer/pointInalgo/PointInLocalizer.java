package spll.localizer.pointInalgo;

import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public interface PointInLocalizer {

	public Point pointIn(Geometry geom);

	public List<Point> pointIn(Geometry geom, int nb);
	
	public void setRand(Random rand);
}
