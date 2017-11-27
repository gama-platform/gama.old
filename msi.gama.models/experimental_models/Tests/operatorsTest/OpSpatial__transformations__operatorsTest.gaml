/**
 *  OpOpSpatial__transformations__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__transformations__operatorsTest.
 */

model OpSpatial__transformations__operatorsTest

global {
	init {
		create testOpSpatial__transformations__operatorsTest number: 1;
		ask testOpSpatial__transformations__operatorsTest {do _step_;}
	}
}


	species testOpSpatial__transformations__operatorsTest {

	
		test as_4_gridOp {
			matrix var0 <- self as_4_grid {10, 5}; 	// var0 equals the matrix of square geometries (grid with 4-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_gridOp {
			matrix var0 <- self as_grid {10, 5}; 	// var0 equals a matrix of square geometries (grid with 8-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_hexagonal_gridOp {
			list<geometry> var0 <- self as_hexagonal_grid {10, 5}; 	// var0 equals list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry

		}
	
		test at_locationOp {
			geometry var0 <- self at_location {10, 20}; 	// var0 equals the geometry resulting from a translation to the location {10, 20} of the left-hand geometry (or agent).

		}
	
		test bufferOp {

		}
	
		test cleanOp {
			geometry var0 <- clean(self); 	// var0 equals returns the geometry resulting from the cleaning of the geometry of the agent applying the operator.

		}
	
		test convex_hullOp {
			geometry var0 <- convex_hull(self); 	// var0 equals the convex hull of the geometry of the agent applying the operator

		}
	
		test CRS_transformOp {
			geometry var0 <- shape CRS_transform("EPSG:4326"); 	// var0 equals a geometry corresponding to the agent geometry transformed into the EPSG:4326 CRS
			geometry var1 <- CRS_transform(shape); 	// var1 equals a geometry corresponding to the agent geometry transformed into the current CRS

		}
	
		test enlarged_byOp {

		}
	
		test MinusOp {
			rgb var7 <- rgb([255, 128, 32]) - 3; 	// var7 equals rgb([252,125,29])
			assert var7 equals: rgb([252,125,29]); 
			rgb var8 <- rgb([255, 128, 32]) - rgb('red'); 	// var8 equals rgb([0,128,32])
			assert var8 equals: rgb([0,128,32]); 
			int var9 <- 1 - 1; 	// var9 equals 0
			assert var9 equals: 0; 
			int var10 <- 1.0 - 1; 	// var10 equals 0.0
			assert var10 equals: 0.0; 
			int var11 <- 3.7 - 1.2; 	// var11 equals 2.5
			assert var11 equals: 2.5; 
			int var12 <- 3 - 1.2; 	// var12 equals 1.8
			assert var12 equals: 1.8; 
			point var13 <- {1, 2} - {4, 5}; 	// var13 equals {-3.0, -3.0}
			assert var13 equals: {-3.0, -3.0}; 
			list<int> var14 <- [1,2,3,4,5,6] - [2,4,9]; 	// var14 equals [1,3,5,6]
			assert var14 equals: [1,3,5,6]; 
			list<int> var15 <- [1,2,3,4,5,6] - [0,8]; 	// var15 equals [1,2,3,4,5,6]
			assert var15 equals: [1,2,3,4,5,6]; 
			matrix var16 <- 3.5 - matrix([[2,5],[3,4]]); 	// var16 equals matrix([[1.5,-1.5],[0.5,-0.5]])
			assert var16 equals: matrix([[1.5,-1.5],[0.5,-0.5]]); 
			date1 - 200
			geometry var18 <- shape - 5; 	// var18 equals a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5
			point var19 <- {1, 2} - 4.5; 	// var19 equals {-3.5, -2.5, -4.5}
			assert var19 equals: {-3.5, -2.5, -4.5}; 
			point var20 <- {1, 2} - 4; 	// var20 equals {-3.0,-2.0,-4.0}
			assert var20 equals: {-3.0,-2.0,-4.0}; 
			float var21 <- date1 - date2; 	// var21 equals 598
			assert var21 equals: 598; 
			list<int> var22 <- [1,2,3,4,5,6] - 2; 	// var22 equals [1,3,4,5,6]
			assert var22 equals: [1,3,4,5,6]; 
			list<int> var23 <- [1,2,3,4,5,6] - 0; 	// var23 equals [1,2,3,4,5,6]
			assert var23 equals: [1,2,3,4,5,6]; 
			geometry var24 <- rectangle(10,10) - [circle(2), square(2)]; 	// var24 equals rectangle(10,10) - (circle(2) + square(2))
			assert var24 equals: rectangle(10,10) - (circle(2) + square(2)); 
			//geometry var25 <- geom1 - geom2; 	// var25 equals a geometry corresponding to difference between geom1 and geom2
			map var0 <- ['a'::1,'b'::2] - ['b'::2]; 	// var0 equals ['a'::1]
			assert var0 equals: ['a'::1]; 
			map var1 <- ['a'::1,'b'::2] - ['b'::2,'c'::3]; 	// var1 equals ['a'::1]
			assert var1 equals: ['a'::1]; 
			map var2 <- ['a'::1,'b'::2] - ('b'::2); 	// var2 equals ['a'::1]
			assert var2 equals: ['a'::1]; 
			map var3 <- ['a'::1,'b'::2] - ('c'::3); 	// var3 equals ['a'::1,'b'::2]
			assert var3 equals: ['a'::1,'b'::2]; 
			point var4 <- -{3.0,5.0}; 	// var4 equals {-3.0,-5.0}
			assert var4 equals: {-3.0,-5.0}; 
			point var5 <- -{1.0,6.0,7.0}; 	// var5 equals {-1.0,-6.0,-7.0}
			assert var5 equals: {-1.0,-6.0,-7.0}; 
			int var6 <- - (-56); 	// var6 equals 56
			assert var6 equals: 56; 

		}
	
		test MultiplyOp {
			point var1 <- {2,5} * 4; 	// var1 equals {8.0, 20.0}
			assert var1 equals: {8.0, 20.0}; 
			point var2 <- {2, 4} * 2.5; 	// var2 equals {5.0, 10.0}
			assert var2 equals: {5.0, 10.0}; 
			rgb var3 <- rgb([255, 128, 32]) * 2; 	// var3 equals rgb([255,255,64])
			assert var3 equals: rgb([255,255,64]); 
			matrix<float> m <- (3.5 * matrix([[2,5],[3,4]]));	//m equals matrix([[7.0,17.5],[10.5,14]])
			geometry var5 <- shape * {0.5,0.5,2}; 	// var5 equals a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 0.5 in x, 0.5 in y and 2 in z
			int var6 <- 1 * 1; 	// var6 equals 1
			assert var6 equals: 1; 
			float var7 <- {2,5} * {4.5, 5}; 	// var7 equals 34.0
			assert var7 equals: 34.0; 
			geometry var8 <- circle(10) * 2; 	// var8 equals circle(20)
			assert var8 equals: circle(20); 
			float var0 <- 2.5 * 2; 	// var0 equals 5.0
			assert var0 equals: 5.0; 

		}
	
		test PlusOp {
			point var4 <- {1, 2} + 4; 	// var4 equals {5.0, 6.0,4.0}
			assert var4 equals: {5.0, 6.0,4.0}; 
			point var5 <- {1, 2} + 4.5; 	// var5 equals {5.5, 6.5,4.5}
			assert var5 equals: {5.5, 6.5,4.5}; 
			string var6 <- "hello " + 12; 	// var6 equals "hello 12"
			assert var6 equals: "hello 12"; 
			list<int> var7 <- [1,2,3,4,5,6] + 2; 	// var7 equals [1,2,3,4,5,6,2]
			assert var7 equals: [1,2,3,4,5,6,2]; 
			list<int> var8 <- [1,2,3,4,5,6] + 0; 	// var8 equals [1,2,3,4,5,6,0]
			assert var8 equals: [1,2,3,4,5,6,0]; 
			geometry var9 <- circle(5) + (5,32); 	// var9 equals circle(10)
			assert var9 equals: circle(10); 
			//geometry var10 <- geom1 + geom2; 	// var10 equals a geometry corresponding to union between geom1 and geom2
			date1 + 200
			matrix var12 <- 3.5 + matrix([[2,5],[3,4]]); 	// var12 equals matrix([[5.5,8.5],[6.5,7.5]])
			assert var12 equals: matrix([[5.5,8.5],[6.5,7.5]]); 
			point var13 <- {1, 2} + {4, 5}; 	// var13 equals {5.0, 7.0}
			assert var13 equals: {5.0, 7.0}; 
			rgb var14 <- rgb([255, 128, 32]) + 3; 	// var14 equals rgb([255,131,35])
			assert var14 equals: rgb([255,131,35]); 
			int var15 <- 1 + 1; 	// var15 equals 2
			assert var15 equals: 2; 
			int var16 <- 1.0 + 1; 	// var16 equals 2.0
			assert var16 equals: 2.0; 
			int var17 <- 1.0 + 2.5; 	// var17 equals 3.5
			assert var17 equals: 3.5; 
			geometry var18 <- circle(5) + 5; 	// var18 equals circle(10)
			assert var18 equals: circle(10); 
			geometry var19 <- circle(5) + (5,32,#round); 	// var19 equals circle(10)
			assert var19 equals: circle(10); 
			list<int> var20 <- [1,2,3,4,5,6] + [2,4,9]; 	// var20 equals [1,2,3,4,5,6,2,4,9]
			assert var20 equals: [1,2,3,4,5,6,2,4,9]; 
			list<int> var21 <- [1,2,3,4,5,6] + [0,8]; 	// var21 equals [1,2,3,4,5,6,0,8]
			assert var21 equals: [1,2,3,4,5,6,0,8]; 
			rgb var22 <- rgb([255, 128, 32]) + rgb('red'); 	// var22 equals rgb([255,128,32])
			assert var22 equals: rgb([255,128,32]); 
			map var0 <- ['a'::1,'b'::2] + ['c'::3]; 	// var0 equals ['a'::1,'b'::2,'c'::3]
			assert var0 equals: ['a'::1,'b'::2,'c'::3]; 
			map var1 <- ['a'::1,'b'::2] + [5::3.0]; 	// var1 equals ['a'::1.0,'b'::2.0,5::3.0]
			assert var1 equals: ['a'::1.0,'b'::2.0,5::3.0]; 
			map var2 <- ['a'::1,'b'::2] + ('c'::3); 	// var2 equals ['a'::1,'b'::2,'c'::3]
			assert var2 equals: ['a'::1,'b'::2,'c'::3]; 
			map var3 <- ['a'::1,'b'::2] + ('c'::3); 	// var3 equals ['a'::1,'b'::2,'c'::3]
			assert var3 equals: ['a'::1,'b'::2,'c'::3]; 

		}
	
		test reduced_byOp {

		}
	
		test rotated_byOp {
			geometry var0 <- self rotated_by 45; 	// var0 equals the geometry resulting from a 45 degrees rotation to the geometry of the agent applying the operator.
			geometry var1 <- rotated_by(pyramid(10),45, {1,0,0}); 	// var1 equals the geometry resulting from a 45 degrees rotation along the {1,0,0} vector to the geometry of the agent applying the operator.

		}
	
		test scaled_byOp {

		}
	
		test scaled_toOp {
			geometry var0 <- shape scaled_to {10,10}; 	// var0 equals a geometry corresponding to the geometry of the agent applying the operator scaled so that it fits a square of 10x10

		}
	
		test simplificationOp {
			geometry var0 <- self simplification 0.1; 	// var0 equals the geometry resulting from the application of the Douglas-Peuker algorithm on the geometry of the agent applying the operator with a tolerance distance of 0.1.

		}
	
		test skeletonizeOp {
			list<geometry> var0 <- skeletonize(self); 	// var0 equals the list of geometries corresponding to the skeleton of the geometry of the agent applying the operator.

		}
	
		test smoothOp {
			geometry var0 <- smooth(square(10), 0.0); 	// var0 equals a 'rounded' square

		}
	
		test solidOp {

		}
	
		test split_geometryOp {
			list<geometry> var0 <- to_rectangles(self, {10.0, 15.0}); 	// var0 equals the list of the geometries corresponding to the decomposition of the geometry by rectangles of size 10.0, 15.0
			list<geometry> var1 <- to_squares(self, 10.0); 	// var1 equals the list of the geometries corresponding to the decomposition of the geometry by squares of side size 10.0
			list<geometry> var2 <- to_rectangles(self, 10,20); 	// var2 equals the list of the geometries corresponding to the decomposition of the geometry of the agent applying the operator

		}
	
		test split_linesOp {
			list<geometry> var0 <- split_lines([line([{0,10}, {20,10}]), line([{0,10}, {20,10}])]); 	// var0 equals a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])

		}
	
		test to_GAMA_CRSOp {
			geometry var0 <- to_GAMA_CRS({121,14}, "EPSG:4326"); 	// var0 equals a geometry corresponding to the agent geometry transformed into the GAMA CRS
			geometry var1 <- to_GAMA_CRS({121,14}); 	// var1 equals a geometry corresponding to the agent geometry transformed into the GAMA CRS

		}
	
		test to_rectanglesOp {
			list<geometry> var0 <- to_rectangles(self, 5, 20, true); 	// var0 equals the list of rectangles corresponding to the discretization by a grid of 5 columns and 20 rows into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept
			list<geometry> var1 <- to_rectangles(self, {10.0, 15.0}, true); 	// var1 equals the list of rectangles of size {10.0, 15.0} corresponding to the discretization into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept

		}
	
		test to_squaresOp {
			list<geometry> var0 <- to_squares(self, 10, true, 0.99); 	// var0 equals the list of 10 squares corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept
			list<geometry> var1 <- to_squares(self, 10, true); 	// var1 equals the list of 10 squares corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept
			list<geometry> var2 <- to_squares(self, 10.0, true); 	// var2 equals the list of squares of side size 10.0 corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept

		}
	
		test to_trianglesOp {

		}
	
		test transformed_byOp {
			geometry var0 <- self transformed_by {45, 0.5}; 	// var0 equals the geometry resulting from 45 degrees rotation and 50% scaling of the geometry of the agent applying the operator.

		}
	
		test translated_byOp {
			geometry var0 <- self translated_by {10,10,10}; 	// var0 equals the geometry resulting from applying the translation to the left-hand geometry (or agent).

		}
	
		test translated_toOp {

		}
	
		test triangulateOp {
			list<geometry> var0 <- triangulate(self); 	// var0 equals the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.
			list<geometry> var1 <- triangulate(self); 	// var1 equals the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.

		}
	
		test voronoiOp {
			list<geometry> var0 <- voronoi([{10,10},{50,50},{90,90},{10,90},{90,10}], square(300)); 	// var0 equals the list of geometries corresponding to the Voronoi Diagram built from the list of points with a square of 300m side size as clip.
			list<geometry> var1 <- voronoi([{10,10},{50,50},{90,90},{10,90},{90,10}]); 	// var1 equals the list of geometries corresponding to the Voronoi Diagram built from the list of points.

		}
	
		test with_precisionOp {
			geometry var0 <- self with_precision 2; 	// var0 equals the geometry resulting from the rounding of points of the geometry with a precision of 0.1.
			float var1 <- 12345.78943 with_precision 2; 	// var1 equals 12345.79
			assert var1 equals: 12345.79; 
			float var2 <- 123 with_precision 2; 	// var2 equals 123.00
			assert var2 equals: 123.00; 
			point var3 <- {12345.78943, 12345.78943, 12345.78943} with_precision 2 ; 	// var3 equals {12345.79, 12345.79, 12345.79}
			assert var3 equals: {12345.79, 12345.79, 12345.79}; 

		}
	
		test without_holesOp {
			geometry var0 <- solid(self); 	// var0 equals the geometry corresponding to the geometry of the agent applying the operator without its holes.

		}
	
	}


experiment testOpSpatial__transformations__operatorsTestExp type: gui {}	
	