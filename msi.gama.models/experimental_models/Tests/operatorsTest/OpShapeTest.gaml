/**
 *  OpOpShapeTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpShapeTest.
 */

model OpShapeTest

global {
	init {
		create testOpShapeTest number: 1;
		ask testOpShapeTest {do _step_;}
	}
}


	species testOpShapeTest {

	
		test arcOp {
			geometry var0 <- arc(4,45,90, false); 	// var0 equals a geometry as an arc of radius 4, in a direction of 45Â° and an amplitude of 90Â°, which only contains the points on the arc
			geometry var1 <- arc(4,45,90); 	// var1 equals a geometry as an arc of radius 4, in a direction of 45Â° and an amplitude of 90Â°

		}
	
		test boxOp {
			geometry var0 <- box(10, 5 , 5); 	// var0 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.
			geometry var1 <- box({10, 5 , 5}); 	// var1 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.

		}
	
		test circleOp {
			geometry var0 <- circle(10,{80,30}); 	// var0 equals a geometry as a circle of radius 10, the center will be in the location {80,30}.
			geometry var1 <- circle(10); 	// var1 equals a geometry as a circle of radius 10.

		}
	
		test coneOp {
			geometry var0 <- cone({0, 45}); 	// var0 equals a geometry as a cone with min angle is 0 and max angle is 45.
			geometry var1 <- cone(0, 45); 	// var1 equals a geometry as a cone with min angle is 0 and max angle is 45.

		}
	
		test cone3DOp {
			geometry var0 <- cone3D(10.0,5.0); 	// var0 equals a geometry as a cone with a base circle of radius 10 and a height of 5.

		}
	
		test crossOp {
			geometry var0 <- cross(10); 	// var0 equals a geometry as a cross of radius 10
			geometry var1 <- cross(10,2); 	// var1 equals a geometry as a cross of radius 10, and with a width of 2 for the lines 

		}
	
		test cubeOp {
			geometry var0 <- cube(10); 	// var0 equals a geometry as a square of side size 10.

		}
	
		test curveOp {
			geometry var0 <- curve({0,0}, {0,10}, {10,10}); 	// var0 equals a cubic Bezier curve geometry composed of 10 points from p0 to p3.
			geometry var1 <- curve({0,0}, {0,10}, {10,10}); 	// var1 equals a quadratic Bezier curve geometry composed of 10 points from p0 to p2.
			geometry var2 <- curve({0,0}, {0,10}, {10,10}); 	// var2 equals a cubic Bezier curve geometry composed of 10 points from p0 to p3.
			geometry var3 <- curve({0,0}, {0,10}, {10,10}, 20); 	// var3 equals a quadratic Bezier curve geometry composed of 20 points from p0 to p2.

		}
	
		test cylinderOp {
			geometry var0 <- cylinder(10,10); 	// var0 equals a geometry as a circle of radius 10.

		}
	
		test ellipseOp {
			geometry var0 <- ellipse(10, 10); 	// var0 equals a geometry as an ellipse of width 10 and height 10.

		}
	
		test envelopeOp {

		}
	
		test geometry_collectionOp {
			geometry var0 <- geometry_collection([{0,0}, {0,10}, {10,10}, {10,0}]); 	// var0 equals a geometry composed of the 4 points (multi-point).

		}
	
		test hexagonOp {
			geometry var0 <- hexagon(10,5); 	// var0 equals a geometry as a hexagon of width of 10 and height of 5.
			geometry var1 <- hexagon(10); 	// var1 equals a geometry as a hexagon of width of 10 and height of 10.
			geometry var2 <- hexagon({10,5}); 	// var2 equals a geometry as a hexagon of width of 10 and height of 5.

		}
	
		test lineOp {
			geometry var1 <- polyline([{0,0}, {0,10}, {10,10}, {10,0}],0.2); 	// var1 equals a polyline geometry composed of the 4 points.
			geometry var0 <- polyline([{0,0}, {0,10}, {10,10}, {10,0}]); 	// var0 equals a polyline geometry composed of the 4 points.

		}
	
		test linkOp {
			//geometry var0 <- link (geom1,geom2); 	// var0 equals a link geometry between geom1 and geom2.

		}
	
		test planOp {
			geometry var0 <- polyplan([{0,0}, {0,10}, {10,10}, {10,0}],10); 	// var0 equals a polyline geometry composed of the 4 points with a depth of 10.

		}
	
		test polygonOp {
			geometry var0 <- polygon([{0,0}, {0,10}, {10,10}, {10,0}]); 	// var0 equals a polygon geometry composed of the 4 points.

		}
	
		test polyhedronOp {
			geometry var0 <- polyhedron([{0,0}, {0,10}, {10,10}, {10,0}],10); 	// var0 equals a polygon geometry composed of the 4 points and of depth 10.

		}
	
		test polylineOp {

		}
	
		test polyplanOp {

		}
	
		test pyramidOp {
			geometry var0 <- pyramid(5); 	// var0 equals a geometry as a square with side_size = 5.

		}
	
		test rectangleOp {
			geometry var0 <- rectangle({2.0,6.0}, {6.0,20.0}); 	// var0 equals a geometry as a rectangle with {2.0,6.0} as the upper-left corner, {6.0,20.0} as the lower-right corner.
			geometry var1 <- rectangle(10, 5); 	// var1 equals a geometry as a rectangle with width = 10 and height = 5.
			geometry var2 <- rectangle({10, 5}); 	// var2 equals a geometry as a rectangle with width = 10 and height = 5.

		}
	
		test sphereOp {
			geometry var0 <- sphere(10); 	// var0 equals a geometry as a circle of radius 10 but displays a sphere.

		}
	
		test squareOp {
			geometry var0 <- square(10); 	// var0 equals a geometry as a square of side size 10.

		}
	
		test squircleOp {
			geometry var0 <- squircle(4,4); 	// var0 equals a geometry as a squircle of side 4 with a power of 4.

		}
	
		test teapotOp {
			geometry var0 <- teapot(10); 	// var0 equals a geometry as a circle of radius 10 but displays a teapot.

		}
	
		test triangleOp {
			geometry var0 <- triangle(5); 	// var0 equals a geometry as a triangle with side_size = 5.

		}
	
	}


experiment testOpShapeTestExp type: gui {}	
	