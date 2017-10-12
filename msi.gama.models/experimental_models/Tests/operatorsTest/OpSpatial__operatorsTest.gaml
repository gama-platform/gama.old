/**
 *  OpOpSpatial__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__operatorsTest.
 */

model OpSpatial__operatorsTest

global {
	init {
		create testOpSpatial__operatorsTest number: 1;
		ask testOpSpatial__operatorsTest {do _step_;}
	}
}

	species testOpSpatial__operatorsTest {

	
		test add_pointOp {
			geometry var0 <- polygon([{10,10},{10,20},{20,20}]) add_point {20,10}; 	// var0 equals polygon([{10,10},{10,20},{20,20},{20,10}])
			assert var0 equals: polygon([{10,10},{10,20},{20,20},{20,10}]); 

		}
	
		test agent_closest_toOp {
			agent var0 <- agent_closest_to(self); 	// var0 equals the closest agent to the agent applying the operator.

		}
	
		test agent_farthest_toOp {
			agent var0 <- agent_farthest_to(self); 	// var0 equals the farthest agent to the agent applying the operator.

		}
	
		test agents_at_distanceOp {
			container var0 <- agents_at_distance(20); 	// var0 equals all the agents (excluding the caller) which distance to the caller is lower than 20

		}
	
		test agents_insideOp {
			list<agent> var0 <- agents_inside(self); 	// var0 equals the agents that are covered by the shape of the agent applying the operator.

		}
	
		test agents_overlappingOp {
			list<agent> var0 <- agents_overlapping(self); 	// var0 equals the agents that overlap the shape of the agent applying the operator.

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
	
		test arcOp {
			geometry var0 <- arc(4,45,90, false); 	// var0 equals a geometry as an arc of radius 4, in a direction of 45Â° and an amplitude of 90Â°, which only contains the points on the arc
			geometry var1 <- arc(4,45,90); 	// var1 equals a geometry as an arc of radius 4, in a direction of 45Â° and an amplitude of 90Â°

		}
	
		test aroundOp {
			geometry var0 <- 10 around circle(5); 	// var0 equals the ring geometry between 5 and 10.

		}
	
		test as_4_gridOp {
			matrix var0 <- self as_4_grid {10, 5}; 	// var0 equals the matrix of square geometries (grid with 4-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_gridOp {
			matrix var0 <- self as_grid {10, 5}; 	// var0 equals a matrix of square geometries (grid with 8-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_hexagonal_gridOp {
			list<geometry> var0 <- self as_hexagonal_grid {10, 5}; 	// var0 equals list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry

		}
	
		test at_distanceOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] at_distance 20; 	// var0 equals the agents of the list located at a distance <= 20 from the caller agent (in the same order).

		}
	
		test at_locationOp {
			geometry var0 <- self at_location {10, 20}; 	// var0 equals the geometry resulting from a translation to the location {10, 20} of the left-hand geometry (or agent).

		}
	
		test boxOp {
			geometry var0 <- box(10, 5 , 5); 	// var0 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.
			geometry var1 <- box({10, 5 , 5}); 	// var1 equals a geometry as a rectangle with width = 10, height = 5 depth= 5.

		}
	
		test bufferOp {

		}
	
		test centroidOp {
			point var0 <- centroid(world); 	// var0 equals the centroid of the square, for example : {50.0,50.0}.

		}
	
		test circleOp {
			geometry var0 <- circle(10,{80,30}); 	// var0 equals a geometry as a circle of radius 10, the center will be in the location {80,30}.
			geometry var1 <- circle(10); 	// var1 equals a geometry as a circle of radius 10.

		}
	
		test cleanOp {
			geometry var0 <- clean(self); 	// var0 equals returns the geometry resulting from the cleaning of the geometry of the agent applying the operator.

		}
	
		test closest_points_withOp {
			//list<point> var0 <- geom1 closest_points_with(geom2); 	// var0 equals [pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1

		}
	
		test closest_toOp {
			//geometry var0 <- [ag1, ag2, ag3] closest_to(self); 	// var0 equals return the closest agent among ag1, ag2 and ag3 to the agent applying the operator.
			//(species1 + species2) closest_to self

		}
	
		test coneOp {
			geometry var0 <- cone({0, 45}); 	// var0 equals a geometry as a cone with min angle is 0 and max angle is 45.
			geometry var1 <- cone(0, 45); 	// var1 equals a geometry as a cone with min angle is 0 and max angle is 45.

		}
	
		test cone3DOp {
			geometry var0 <- cone3D(10.0,5.0); 	// var0 equals a geometry as a cone with a base circle of radius 10 and a height of 5.

		}
	
		test convex_hullOp {
			geometry var0 <- convex_hull(self); 	// var0 equals the convex hull of the geometry of the agent applying the operator

		}
	
		test coversOp {
			bool var0 <- square(5) covers square(2); 	// var0 equals true
			assert var0 equals: true; 

		}
	
		test crossOp {
			geometry var0 <- cross(10); 	// var0 equals a geometry as a cross of radius 10
			geometry var1 <- cross(10,2); 	// var1 equals a geometry as a cross of radius 10, and with a width of 2 for the lines 

		}
	
		test crossesOp {
			bool var0 <- polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- polyline([{10,10},{20,20}]) crosses {15,15}; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}]); 	// var2 equals true
			assert var2 equals: true; 

		}
	
		test crsOp {
			//string var0 <- crs(my_shapefile); 	// var0 equals the crs of the shapefile

		}
	
		test CRS_transformOp {
			geometry var0 <- shape CRS_transform("EPSG:4326"); 	// var0 equals a geometry corresponding to the agent geometry transformed into the EPSG:4326 CRS
			geometry var1 <- CRS_transform(shape); 	// var1 equals a geometry corresponding to the agent geometry transformed into the current CRS

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
	
		test demOp {
			//geometry var0 <- dem(dem,texture,z_factor); 	// var0 equals a geometry as a rectangle of width and height equal to the texture.
			//geometry var1 <- dem(dem,texture); 	// var1 equals a geometry as a rectangle of weight and height equal to the texture.
			//geometry var2 <- dem(dem,z_factor); 	// var2 equals a geometry as a rectangle of weight and height equal to the texture.
			//geometry var3 <- dem(dem); 	// var3 equals returns a geometry as a rectangle of width and height equal to the texture.

		}
	
		test direction_betweenOp {
			//int var0 <- my_topology direction_between [ag1, ag2]; 	// var0 equals the direction between ag1 and ag2 considering the topology my_topology

		}
	
		test direction_toOp {

		}
	
		test disjoint_fromOp {
			bool var0 <- polyline([{10,10},{20,20}]) disjoint_from polyline([{15,15},{25,25}]); 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {15,15}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {25,25}; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{35,35},{35,45},{45,45},{45,35}]); 	// var4 equals true
			assert var4 equals: true; 

		}
	
		test distance_betweenOp {
			//float var0 <- my_topology distance_between [ag1, ag2, ag3]; 	// var0 equals the distance between ag1, ag2 and ag3 considering the topology my_topology

		}
	
		test distance_toOp {
			//float var0 <- ag1 distance_to ag2; 	// var0 equals the distance between ag1 and ag2 considering the topology of the agent applying the operator

		}
	
		test ellipseOp {
			geometry var0 <- ellipse(10, 10); 	// var0 equals a geometry as an ellipse of width 10 and height 10.

		}
	
		test enlarged_byOp {

		}
	
		test envelopeOp {

		}
	
		test farthest_point_toOp {
			//point var0 <- geom farthest_point_to(pt); 	// var0 equals the farthest point of geom to pt

		}
	
		test farthest_toOp {
			//geometry var0 <- [ag1, ag2, ag3] closest_to(self); 	// var0 equals return the farthest agent among ag1, ag2 and ag3 to the agent applying the operator.
			//(species1 + species2) closest_to self

		}
	
		test geometry_collectionOp {
			geometry var0 <- geometry_collection([{0,0}, {0,10}, {10,10}, {10,0}]); 	// var0 equals a geometry composed of the 4 points (multi-point).

		}
	
		test giniOp {
			float var0 <- gini([1.0, 0.5, 2.0]); 	// var0 equals the gini index computed

		}
	
		test hexagonOp {
			geometry var0 <- hexagon(10,5); 	// var0 equals a geometry as a hexagon of width of 10 and height of 5.
			geometry var1 <- hexagon(10); 	// var1 equals a geometry as a hexagon of width of 10 and height of 10.
			geometry var2 <- hexagon({10,5}); 	// var2 equals a geometry as a hexagon of width of 10 and height of 5.

		}
	
		test hierarchical_clusteringOp {
			//container var0 <- [ag1, ag2, ag3, ag4, ag5] hierarchical_clustering 20.0; 	// var0 equals for example, can return [[[ag1],[ag3]], [ag2], [[[ag4],[ag5]],[ag6]]

		}
	
		test IDWOp {
			//map<agent,float> var0 <- IDW([ag1, ag2, ag3, ag4, ag5],[{10,10}::25.0, {10,80}::10.0, {100,10}::15.0], 2); 	// var0 equals for example, can return [ag1::12.0, ag2::23.0,ag3::12.0,ag4::14.0,ag5::17.0]

		}
	
		test insideOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] inside(self); 	// var0 equals the agents among ag1, ag2 and ag3 that are covered by the shape of the right-hand argument.
			//list<geometry> var1 <- (species1 + species2) inside (self); 	// var1 equals the agents among species species1 and species2 that are covered by the shape of the right-hand argument.

		}
	
		test interOp {
			container var0 <- [1::2, 3::4, 5::6] inter [2,4]; 	// var0 equals [2,4]
			assert var0 equals: [2,4]; 
			container var1 <- [1::2, 3::4, 5::6] inter [1,3]; 	// var1 equals []
			assert var1 equals: []; 
			container var2 <- matrix([[1,2,3],[4,5,4]]) inter [3,4]; 	// var2 equals [3,4]
			assert var2 equals: [3,4]; 
			container var3 <- [1,2,3,4,5,6] inter [2,4]; 	// var3 equals [2,4]
			assert var3 equals: [2,4]; 
			container var4 <- [1,2,3,4,5,6] inter [0,8]; 	// var4 equals []
			assert var4 equals: []; 
			geometry var5 <- square(10) inter circle(5); 	// var5 equals circle(5)
			assert var5 equals: circle(5); 

		}
	
		test intersectionOp {

		}
	
		test intersectsOp {
			bool var0 <- square(5) intersects {10,10}; 	// var0 equals false
			assert var0 equals: false; 

		}
	
		test inverse_distance_weightingOp {

		}
	
		test lineOp {
			geometry var1 <- polyline([{0,0}, {0,10}, {10,10}, {10,0}],0.2); 	// var1 equals a polyline geometry composed of the 4 points.
			geometry var0 <- polyline([{0,0}, {0,10}, {10,10}, {10,0}]); 	// var0 equals a polyline geometry composed of the 4 points.

		}
	
		test linkOp {
			//geometry var0 <- link (geom1,geom2); 	// var0 equals a link geometry between geom1 and geom2.

		}
	
		test masked_byOp {
			//geometry var0 <- perception_geom masked_by obstacle_list; 	// var0 equals the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.
			//geometry var1 <- perception_geom masked_by obstacle_list; 	// var1 equals the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.

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
	
		test moranOp {
			float var0 <- moran([1.0, 0.5, 2.0], weight_matrix); 	// var0 equals the Moran index computed

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
	
		test neighbors_atOp {
			container var0 <- (self neighbors_at (10)); 	// var0 equals all the agents located at a distance lower or equal to 10 to the agent applying the operator.

		}
	
		test neighbors_ofOp {
			container var3 <- neighbors_of (topology(self), self,10); 	// var3 equals all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology.
			//container var0 <- graphEpidemio neighbors_of (node(3)); 	// var0 equals [node0,node2]
			//container var1 <- graphFromMap neighbors_of node({12,45}); 	// var1 equals [{1.0,5.0},{34.0,56.0}]
			container var2 <- topology(self) neighbors_of self; 	// var2 equals returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology.

		}
	
		test overlappingOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] overlapping(self); 	// var0 equals return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator.
			//(species1 + species2) overlapping self

		}
	
		test overlapsOp {
			bool var0 <- polyline([{10,10},{20,20}]) overlaps polyline([{15,15},{25,25}]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {25,25}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{35,35},{35,45},{45,45},{45,35}]); 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polyline([{10,10},{20,20}]); 	// var4 equals true
			assert var4 equals: true; 
			bool var5 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {15,15}; 	// var5 equals true
			assert var5 equals: true; 
			bool var6 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]); 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var7 equals true
			assert var7 equals: true; 
			bool var8 <- polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{10,20},{20,20},{20,30},{10,30}]); 	// var8 equals true
			assert var8 equals: true; 

		}
	
		test partially_overlapsOp {
			bool var0 <- polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {25,25}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}]); 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}]); 	// var4 equals false
			assert var4 equals: false; 
			bool var5 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {15,15}; 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]); 	// var6 equals false
			assert var6 equals: false; 
			bool var7 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var7 equals true
			assert var7 equals: true; 
			bool var8 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}]); 	// var8 equals false
			assert var8 equals: false; 

		}
	
		test path_betweenOp {
			//path var0 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), [ag1, ag2, ag3]); 	// var0 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents with minimal cost
			//path var1 <- my_topology path_between [ag1, ag2]; 	// var1 equals A path between ag1 and ag2
			//path var2 <- path_between (cell_grid where each.is_free, ag1, ag2); 	// var2 equals A path between ag1 and ag2 passing through the given cell_grid agents
			//path var3 <- my_topology path_between (ag1, ag2); 	// var3 equals A path between ag1 and ag2
			//path var4 <- path_between (my_graph, ag1, ag2); 	// var4 equals A path between ag1 and ag2
			//path var5 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), ag1, ag2); 	// var5 equals A path between ag1 and ag2 passing through the given cell_grid agents with a minimal cost
			//path var6 <- path_between (cell_grid where each.is_free, [ag1, ag2, ag3]); 	// var6 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents

		}
	
		test path_toOp {
			//path var0 <- ag1 path_to ag2; 	// var0 equals the path between ag1 and ag2 considering the topology of the agent applying the operator

		}
	
		test planOp {
			geometry var0 <- polyplan([{0,0}, {0,10}, {10,10}, {10,0}],10); 	// var0 equals a polyline geometry composed of the 4 points with a depth of 10.

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
	
		test points_alongOp {
			container var0 <-  line([{10,10},{80,80}]) points_along ([0.3, 0.5, 0.9]); 	// var0 equals the list of following points: [{31.0,31.0,0.0},{45.0,45.0,0.0},{73.0,73.0,0.0}]

		}
	
		test points_atOp {
			list<point> var0 <- 3 points_at(20.0); 	// var0 equals returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location

		}
	
		test points_onOp {
			container var0 <-  square(5) points_on(2); 	// var0 equals a list of points belonging to the exterior ring of the square distant from each other of 2.

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
	
		test reduced_byOp {

		}
	
		test rgb_to_xyzOp {
			//list<point> var0 <- rgb_to_xyz(texture); 	// var0 equals a list of points

		}
	
		test rotated_byOp {
			geometry var0 <- self rotated_by 45; 	// var0 equals the geometry resulting from a 45 degrees rotation to the geometry of the agent applying the operator.
			geometry var1 <- rotated_by(pyramid(10),45, {1,0,0}); 	// var1 equals the geometry resulting from a 45 degrees rotation along the {1,0,0} vector to the geometry of the agent applying the operator.

		}
	
		test roundOp {
			point var0 <- {12345.78943,  12345.78943, 12345.78943} with_precision 2; 	// var0 equals {12345.79,12345.79,12345.79}
			assert var0 equals: {12345.79,12345.79,12345.79}; 
			int var1 <- round (0.51); 	// var1 equals 1
			assert var1 equals: 1; 
			int var2 <- round (100.2); 	// var2 equals 100
			assert var2 equals: 100; 
			int var3 <- round(-0.51); 	// var3 equals -1
			assert var3 equals: -1; 

		}
	
		test scaled_byOp {

		}
	
		test scaled_toOp {
			geometry var0 <- shape scaled_to {10,10}; 	// var0 equals a geometry corresponding to the geometry of the agent applying the operator scaled so that it fits a square of 10x10

		}
	
		test set_zOp {
			loop i from: 0 to: length(shape.points) - 1{set shape <-  set_z (shape, i, 3.0);}
			shape <- triangle(3) set_z [5,10,14];

		}
	
		test simple_clustering_by_distanceOp {
			//list<list<agent>> var0 <- [ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0; 	// var0 equals for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]

		}
	
		test simple_clustering_by_envelope_distanceOp {

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
	
		test sphereOp {
			geometry var0 <- sphere(10); 	// var0 equals a geometry as a circle of radius 10 but displays a sphere.

		}
	
		test split_atOp {
			list<geometry> var0 <- polyline([{1,2},{4,6}]) split_at {7,6}; 	// var0 equals [polyline([{1.0,2.0},{7.0,6.0}]), polyline([{7.0,6.0},{4.0,6.0}])]
			assert var0 equals: [polyline([{1.0,2.0},{7.0,6.0}]), polyline([{7.0,6.0},{4.0,6.0}])]; 

		}
	
		test split_geometryOp {
			list<geometry> var0 <- to_rectangles(self, {10.0, 15.0}); 	// var0 equals the list of the geometries corresponding to the decomposition of the geometry by rectangles of size 10.0, 15.0
			list<geometry> var1 <- to_squares(self, 10.0); 	// var1 equals the list of the geometries corresponding to the decomposition of the geometry by squares of side size 10.0
			list<geometry> var2 <- to_rectangles(self, 10,20); 	// var2 equals the list of the geometries corresponding to the decomposition of the geometry of the agent applying the operator

		}
	
		test split_linesOp {
			list<geometry> var0 <- split_lines([line([{0,10}, {20,10}]), line([{0,10}, {20,10}])]); 	// var0 equals a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])

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
	
		test touchesOp {
			bool var0 <- polyline([{10,10},{20,20}]) touches {15,15}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- polyline([{10,10},{20,20}]) touches {10,10}; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- {15,15} touches {15,15}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}]); 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}]); 	// var4 equals false
			assert var4 equals: false; 
			bool var5 <- polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}]); 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var6 equals false
			assert var6 equals: false; 
			bool var7 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}]); 	// var7 equals true
			assert var7 equals: true; 
			bool var8 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}]); 	// var8 equals true
			assert var8 equals: true; 
			bool var9 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches {15,15}; 	// var9 equals false
			assert var9 equals: false; 
			bool var10 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches {10,15}; 	// var10 equals true
			assert var10 equals: true; 

		}
	
		test towardsOp {
			//int var0 <- ag1 towards ag2; 	// var0 equals the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator

		}
	
		test transformed_byOp {
			geometry var0 <- self transformed_by {45, 0.5}; 	// var0 equals the geometry resulting from 45 degrees rotation and 50% scaling of the geometry of the agent applying the operator.

		}
	
		test translated_byOp {
			geometry var0 <- self translated_by {10,10,10}; 	// var0 equals the geometry resulting from applying the translation to the left-hand geometry (or agent).

		}
	
		test translated_toOp {

		}
	
		test triangleOp {
			geometry var0 <- triangle(5); 	// var0 equals a geometry as a triangle with side_size = 5.

		}
	
		test triangulateOp {
			list<geometry> var0 <- triangulate(self); 	// var0 equals the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.
			list<geometry> var1 <- triangulate(self); 	// var1 equals the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.

		}
	
		test unionOp {
			container var0 <- [1,2,3,4,5,6] union [2,4,9]; 	// var0 equals [1,2,3,4,5,6,9]
			assert var0 equals: [1,2,3,4,5,6,9]; 
			container var1 <- [1,2,3,4,5,6] union [0,8]; 	// var1 equals [1,2,3,4,5,6,0,8]
			assert var1 equals: [1,2,3,4,5,6,0,8]; 
			container var2 <- [1,3,2,4,5,6,8,5,6] union [0,8]; 	// var2 equals [1,3,2,4,5,6,8,0]
			assert var2 equals: [1,3,2,4,5,6,8,0]; 
			//geometry var3 <- union([geom1, geom2, geom3]); 	// var3 equals a geometry corresponding to union between geom1, geom2 and geom3

		}
	
		test usingOp {
			unknown var0 <- (agents closest_to self) using topology(world); 	// var0 equals the closest agent to self (the caller) in the continuous topology of the world

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


experiment testOpSpatial__operatorsTestExp type: gui {}	
	