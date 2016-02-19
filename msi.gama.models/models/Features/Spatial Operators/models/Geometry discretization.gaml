/**
* Name: Discretization of Topology
* Author: Patrick Taillandier
* Description: A model which shows how to use the different convert topologies operators : to_square, to_rectangle, points_on, triangulate, voronoi.
* Tags: Topology
*/

model discretization

global 
{
	//Geometry that will be used for each display : a circle
	geometry init_geom <- circle (35);
}

experiment main type: gui {
	output {
		//Display that will show a circle converted in squares
		display decretization_squares {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				//Convert the initial circle into squares of 5.0 
				loop g over: to_squares(init_geom, 5.0) {
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in squares overlapping the shape
		display squares_overlapping {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "squares" transparency: 0.5{
				//Convert the initial circle into rectangles of 5.0 and keep the squares going out of the borders of the circle but still overlapping it
				loop g over: to_squares(init_geom,5.0, true) {
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in squares inside the shape
		display squares_inside {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "squares" transparency: 0.5{
				//Convert the initial circle into squares of 5.0 and remove the squares going out of the borders of the circle
				loop g over: to_squares(init_geom, 5.0, false) {
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in rectangles with a size of 10.0 and 5.0
		display decretization_rectangles {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: to_rectangles(init_geom, {10.0,5.0}) {
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in rectangles forming a grid of 15 cols and 20 rows
		display decretization_rectangles_grid {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: to_rectangles(init_geom, 15,20) {
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in rectangles overlapping the shape
		display rectangles_overlapping {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "rectangles" transparency: 0.5{
				//Convert the initial circle into rectangles of 5.0 and keep the rectangles going out of the borders of the circle but still overlapping it
				loop g over:  to_rectangles(init_geom, {10.0,5.0}, true){
					draw g color: °red;
				} 
			} 
		}
		//Display that will show a circle converted in rectangles inside the shape
		display rectangles_inside {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "rectangles" transparency: 0.5{
				//Convert the initial circle into rectangles of 5.0 and remove the rectanges going out of the borders of the circle
				loop g over: to_rectangles(init_geom, {10.0,5.0}, false) {
					draw g color: °red;
				} 
			} 
		}
		
		//Display that will show a circle triangulated
		display triangles {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "triangles" transparency: 0.5{
				loop g over: triangulate(init_geom) {
					draw g color: °red;
				}
			} 
		}
		
		//Display that will show a circle with points on its perimeter every 10.0
		display points_on_contours {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: points_on(init_geom,10.0) {
					draw circle(2) at: g color: °red;
				} 
			} 
		}
		
		display voronoi {
			//Creates a voronoi diagram from the points given and display them
			graphics "geoms" transparency: 0.5{
				loop g over: voronoi([{10,10},{10,10},{80,80},{80,10},{10,80}, {40,40}]) {
					draw g color: °red;
				}
			}
			graphics "points" {
				loop pt over: [{10,10},{10,10},{80,80},{80,10},{10,80}, {40,40}] {
					draw circle(2) at: pt color: °black;
				}
			} 
		}
	}
}
