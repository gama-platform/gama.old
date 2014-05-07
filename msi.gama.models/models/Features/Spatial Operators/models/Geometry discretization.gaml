/**
 *  discretization
 *  Author: Taillandier
 *  Description: shows the geometry discrtization operators
 */

model discretization

global {
	geometry init_geom <- circle (35);
}

experiment main type: gui {
	output {
		
		display decretization_squares {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: to_squares(init_geom, 5.0) {
					draw g color: °red;
				} 
			} 
		}
		display squares_overlapping {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "squares" transparency: 0.5{
				loop g over: to_squares(init_geom,5.0, true) {
					draw g color: °red;
				} 
			} 
		}
		display squares_inside {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "squares" transparency: 0.5{
				loop g over: to_squares(init_geom, 5.0, false) {
					draw g color: °red;
				} 
			} 
		}
		display decretization_rectangles {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: to_rectangles(init_geom, {10.0,5.0}) {
					draw g color: °red;
				} 
			} 
		}
		display decretization_rectangles_grid {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: to_rectangles(init_geom, 15,20) {
					draw g color: °red;
				} 
			} 
		}
		display rectangles_overlapping {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "rectangles" transparency: 0.5{
				loop g over:  to_rectangles(init_geom, {10.0,5.0}, true){
					draw g color: °red;
				} 
			} 
		}
		display rectangles_inside {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "rectangles" transparency: 0.5{
				loop g over: to_rectangles(init_geom, {10.0,5.0}, false) {
					draw g color: °red;
				} 
			} 
		}
		
		display triangles {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "triangles" transparency: 0.5{
				loop g over: triangulate(init_geom) {
					draw g color: °red;
				}
			} 
		}
		
		
		display points_on_contours {
			graphics "init_geom" {draw init_geom color: °black;}
			graphics "geoms" transparency: 0.5{
				loop g over: points_on(init_geom,10.0) {
					draw circle(2) at: g color: °red;
				} 
			} 
		}
		
		display voronoi {
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
