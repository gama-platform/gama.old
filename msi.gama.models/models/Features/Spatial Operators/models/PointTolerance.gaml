/**
* Name: PointTolerance
* Author: patrick
* Description: A model which shows how to modify the tolerance for point comparison
* Tags: point,  spatial_computation, tolerance
*/

model PointTolerance

global {
	init {
		point pt1 <- {50.0, 50.0};
		point pt2 <- {50.0001, 49.9999};
		float old <- gama.pref_point_tolerance;
		
		write "Tolerance: " + gama.pref_point_tolerance + ": " + pt1 + " = " + pt2 + " -> " + (pt1 = pt2) ;
		
		gama.pref_point_tolerance <- 0.001;
		write "Tolerance: " + gama.pref_point_tolerance + ": "  + pt1 + " = " + pt2 + " -> " + (pt1 = pt2);
		
		gama.pref_point_tolerance <- old;
	}
}

experiment PointTolerance ;