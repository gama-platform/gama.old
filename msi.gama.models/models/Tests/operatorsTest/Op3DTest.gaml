/**
 *  OpOp3DTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category Op3DTest.
 */

model Op3DTest

global {
	init {
		create testOp3DTest number: 1;
		ask testOp3DTest {do _step_;}
	}
}


	species testOp3DTest {

	
		test boxOp {
			geometry var0 <- box(10, 5 , 5); 	// var0 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.
			geometry var1 <- box({10, 5 , 5}); 	// var1 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.

		}
	
		test cone3DOp {
			geometry var0 <- cone3D(10.0,5.0); 	// var0 equals a geometry as a cone with a base circle of radius 10 and a height of 5.

		}
	
		test cubeOp {
			geometry var0 <- cube(10); 	// var0 equals a geometry as a square of side size 10.

		}
	
		test cylinderOp {
			geometry var0 <- cylinder(10,10); 	// var0 equals a geometry as a circle of radius 10.

		}
	
		test demOp {
			//geometry var0 <- dem(dem,texture,z_factor); 	// var0 equals a geometry as a rectangle of width and height equal to the texture.
			//geometry var1 <- dem(dem,texture); 	// var1 equals a geometry as a rectangle of weight and height equal to the texture.
			//geometry var2 <- dem(dem,z_factor); 	// var2 equals a geometry as a rectangle of weight and height equal to the texture.
			//geometry var3 <- dem(dem); 	// var3 equals returns a geometry as a rectangle of width and height equal to the texture.

		}
	
		test hexagonOp {
			geometry var0 <- hexagon(10,5); 	// var0 equals a geometry as a hexagon of width of 10 and height of 5.
			geometry var1 <- hexagon(10); 	// var1 equals a geometry as a hexagon of width of 10 and height of 10.
			geometry var2 <- hexagon({10,5}); 	// var2 equals a geometry as a hexagon of width of 10 and height of 5.

		}
	
		test pyramidOp {
			geometry var0 <- pyramid(5); 	// var0 equals a geometry as a square with side_size = 5.

		}
	
		test rgb_to_xyzOp {
			//list<point> var0 <- rgb_to_xyz(texture); 	// var0 equals a list of points

		}
	
		test set_zOp {
			loop i from: 0 to: length(shape.points) - 1{set shape <-  set_z (shape, i, 3.0);}
			shape <- triangle(3) set_z [5,10,14];

		}
	
		test sphereOp {
			geometry var0 <- sphere(10); 	// var0 equals a geometry as a circle of radius 10 but displays a sphere.

		}
	
		test teapotOp {
			geometry var0 <- teapot(10); 	// var0 equals a geometry as a circle of radius 10 but displays a teapot.

		}
	
	}


experiment testOp3DTestExp type: gui {}	
	