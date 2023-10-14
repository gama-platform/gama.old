/***
* Name: Creation
* Author: Kevin Chapuis
* Description: Dummy test for geometry creation / mostly rely on JTS so it is pretty robust
* Tags: spatial, geometry, display
***/

model create_tests

global {
	
	float circle_tolerance <- 0.01;
	
	geometry circle1 <- circle(m_size, world.location);
	geometry inner_circle <- circle(m_size);
	list<point> p_circles <- 50 points_at m_size update: 50 points_at m_size;
	geometry c_square <- envelope(circle1);
	
	geometry s_ellipse <- ellipse(m_size,m_size*1.5); 
	geometry l_ellipse <- ellipse(m_size*2 + 100,m_size*2 + 100);
	
	geometry squircle1 <- squircle(m_size,2);
	
	float arc_tolerance <- 0.001;
	// Create an arc with a radius explicitly smaller (see #3907)
	geometry arc1 <- arc(m_size - 1, rnd(360), amplitude) update: arc(m_size - 1, rnd(360), amplitude);
	float amplitude <- rnd(360.0) update: rnd(360.0);
		
	int m_size <- 20 max:100;
	
}


experiment Visualisation type: gui {
	output {
		display "Geometries" {
			graphics my_geoms {
				draw l_ellipse color:#blue;
				
				draw buffer(circle1,(circle1.perimeter/#pi/2)*circle_tolerance) color:rgb(#yellow,0.2);
				draw circle1 color:#black;
				loop p over:p_circles {draw cross(0.5) at:p.location color:#red;}
				
				draw s_ellipse color:#grey;
			}
		}
		display "Squircle and Arc" {
			graphics other_geoms transparency:0.3 {
				draw squircle1 color:#green;
			} 
			graphics arc {
				draw circle1 color: #red;
				draw arc1 color:#black;
				draw curve({0,0},{0,10},{10,10}) color:#black;
			}
		}
		display "3D" type:3d {
			graphics cylinder {
				draw cylinder(10,10) at:{25,25} color:#deepskyblue;
				draw sphere(10) at:{25,50} color:#darkturquoise;
				draw teapot(10) at:{25,75} color:#mediumspringgreen;
			}
		} 
	}
}

experiment "Creation Test" type: test autorun: true {	
		
	test "Circle tests with tolerance" {
		assert p_circles all_match (each distance_to circle1.contour < (each distance_to circle1.location * circle_tolerance));
		assert p_circles all_match (each distance_to inner_circle.contour < (each distance_to inner_circle.location * circle_tolerance));
		
		assert c_square covers circle1;
	}
	
	test "Elipse test" {
		assert s_ellipse.area < circle1.area;
		assert circle1 covers s_ellipse;
		
		assert l_ellipse.area > circle1.area;
		assert l_ellipse covers circle1;
	}
	
	test "Arc test" {
		assert circle1 covers arc1;
		assert arc1.area - (circle1.area * amplitude / 360) < (circle1.area * amplitude / 360 * arc_tolerance);
	}
	
}
