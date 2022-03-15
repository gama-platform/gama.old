package spll.localizer.pointInalgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class CentroidPointinLocalizer implements PointInLocalizer{

	public static GeometryFactory FACTORY = new GeometryFactory();
	
	@Override
	public Point pointIn(Geometry geom) {
		return FACTORY.createPoint(Centroid.getCentroid(geom));
	}

	@Override
	public List<Point> pointIn(Geometry geom, int nb) {
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < nb; i++)
			points.add(pointIn(geom));
		return points;
	}

	@Override
	public void setRand(Random rand) {
		// do nothing
	}

	
}
