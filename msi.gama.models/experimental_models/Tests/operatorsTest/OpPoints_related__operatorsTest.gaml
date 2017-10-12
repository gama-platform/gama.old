/**
 *  OpOpPoints_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpPoints_related__operatorsTest.
 */

model OpPoints_related__operatorsTest

global {
	init {
		create testOpPoints_related__operatorsTest number: 1;
		ask testOpPoints_related__operatorsTest {do _step_;}
	}
}

	species testOpPoints_related__operatorsTest {

	
		test add_pointOp {
			geometry var0 <- polygon([{10,10},{10,20},{20,20}]) add_point {20,10}; 	// var0 equals polygon([{10,10},{10,20},{20,20},{20,10}])
			assert var0 equals: polygon([{10,10},{10,20},{20,20},{20,10}]); 

		}
	
		test angle_betweenOp {
			int var0 <- angle_between({5,5},{10,5},{5,10}); 	// var0 equals 90
			assert var0 equals: 90; 

		}
	
		test any_location_inOp {
			point var0 <- any_location_in(square(5)); 	// var0 equals a point in the square, for example : {3,4.6}.

		}
	
		test any_point_inOp {

		}
	
		test centroidOp {
			point var0 <- centroid(world); 	// var0 equals the centroid of the square, for example : {50.0,50.0}.

		}
	
		test closest_points_withOp {
			//list<point> var0 <- geom1 closest_points_with(geom2); 	// var0 equals [pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1

		}
	
		test DivideOp {
			rgb var0 <- rgb([255, 128, 32]) / 2; 	// var0 equals rgb([127,64,16])
			assert var0 equals: rgb([127,64,16]); 
			rgb var1 <- rgb([255, 128, 32]) / 2.5; 	// var1 equals rgb([102,51,13])
			assert var1 equals: rgb([102,51,13]); 
			float var2 <- 3 / 5.0; 	// var2 equals 0.6
			assert var2 equals: 0.6; 
			point var3 <- {5, 7.5} / 2.5; 	// var3 equals {2, 3}
			assert var3 equals: {2, 3}; 
			point var4 <- {2,5} / 4; 	// var4 equals {0.5,1.25}
			assert var4 equals: {0.5,1.25}; 

		}
	
		test farthest_point_toOp {
			//point var0 <- geom farthest_point_to(pt); 	// var0 equals the farthest point of geom to pt

		}
	
		test GEOp {
			bool var0 <- 'abc' >= 'aeb'; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- 'abc' >= 'abc'; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- {5,7} >= {4,6}; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- {5,7} >= {4,8}; 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- 3.5 >= 3.5; 	// var4 equals true
			assert var4 equals: true; 
			#now >= #now minus_hours 1 :- true
			bool var6 <- 3 >= 2.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3.5 >= 7; 	// var7 equals false
			assert var7 equals: false; 
			bool var8 <- 3 >= 7; 	// var8 equals false
			assert var8 equals: false; 

		}
	
		test grid_atOp {
			//agent var0 <- grid_cell grid_at {1,2}; 	// var0 equals the agent grid_cell with grid_x=1 and grid_y = 2

		}
	
		test GTOp {
			bool var0 <- {5,7} > {4,6}; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- {5,7} > {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' > 'aeb'; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- 3.5 > 7; 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- 3.5 > 7.6; 	// var4 equals false
			assert var4 equals: false; 
			#now > #now minus_hours 1 :- true
			bool var6 <- 3 > 2.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 > 7; 	// var7 equals false
			assert var7 equals: false; 

		}
	
		test LEOp {
			bool var0 <- {5,7} <= {4,6}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- {5,7} <= {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' <= 'aeb'; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- 7.0 <= 7; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- 3 <= 2.5; 	// var4 equals false
			assert var4 equals: false; 
			#now <= #now minus_hours 1 :- false
			bool var6 <- 3.5 <= 3.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 <= 7; 	// var7 equals true
			assert var7 equals: true; 

		}
	
		test LTOp {
			bool var0 <- {5,7} < {4,6}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- {5,7} < {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' < 'aeb'; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- 3.5 < 7.6; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- 3.5 < 7; 	// var4 equals true
			assert var4 equals: true; 
			#now < #now minus_hours 1 :- false
			bool var6 <- 3 < 7; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 < 2.5; 	// var7 equals false
			assert var7 equals: false; 

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
	
		test normOp {
			float var0 <- norm({3,4}); 	// var0 equals 5.0
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
	
		test pointOp {

		}
	
		test points_alongOp {
			container var0 <-  line([{10,10},{80,80}]) points_along ([0.3, 0.5, 0.9]); 	// var0 equals the list of following points: [{31.0,31.0,0.0},{45.0,45.0,0.0},{73.0,73.0,0.0}]

		}
	
		test points_atOp {
			list<point> var0 <- 3 points_at(20.0); 	// var0 equals returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location

		}
	
		test points_onOp {
			container var0 <-  square(5) points_on(2); 	// var0 equals a list of points belonging to the exterior ring of the square distant from each other of 2.

		}
	
	}
experiment testOpPoints_related__operatorsTestExp type: gui {}	
	