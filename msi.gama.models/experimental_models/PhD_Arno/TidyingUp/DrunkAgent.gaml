/**
 *  bubbleSortPat
 *  Author: Arno
 *  Description: 
 */

model bubbleSortPat


global {

float interval <-10.0;
list<point> drunkPoint;

geometry line1 <-line([{0,0},{world.shape.width,0}]);
geometry line2 <-line([{world.shape.width,interval},{0,interval}]);
geometry line3 <-line([{0,interval*2},{world.shape.width,interval*2}]);
geometry line4 <-line([{world.shape.width,3*interval},{0,3*interval}]);
geometry line5 <-line([{0,interval*4},{world.shape.width,interval*4}]);
geometry line6 <-line([{world.shape.width,5*interval},{0,5*interval}]);
geometry line7 <-line([{0,interval*6},{world.shape.width,interval*6}]);
geometry line8 <-line([{world.shape.width,7*interval},{0,7*interval}]);
geometry line9 <-line([{0,interval*8},{world.shape.width,interval*8}]);
geometry line10 <-line([{world.shape.width,9*interval},{0,9*interval}]);
geometry line11 <-line([{world.shape.width,10*interval},{0,10*interval}]);

geometry circle1 <-circle(interval/2) at_location {world.shape.width,interval/2};
geometry circle2 <-circle(interval/2) at_location {0,interval+ interval/2};
geometry circle3 <-circle(interval/2) at_location {world.shape.width,2*interval+interval/2};
geometry circle4 <-circle(interval/2) at_location {0,3*interval+ interval/2};
geometry circle5 <-circle(interval/2) at_location {world.shape.width,4 *interval+interval/2};
geometry circle6 <-circle(interval/2) at_location {0,5*interval+ interval/2};
geometry circle7 <-circle(interval/2) at_location {world.shape.width,6 *interval+interval/2};
geometry circle8 <-circle(interval/2) at_location {0,7*interval+ interval/2};
geometry circle9 <-circle(interval/2) at_location {world.shape.width,8 *interval+interval/2};
geometry circle10 <-circle(interval/2) at_location {0,9*interval+ interval/2};
geometry circle11 <-circle(interval/2) at_location {world.shape.width,10 *interval+interval/2};
init {
	list<point> p <- circle1.contour.points where (each.x >= world.shape.width) sort_by each.y;
	drunkPoint<-line1.points + p + (line2.points) + 
	circle2.contour.points where (each.x <0) sort_by each.y + line3.points +
	circle3.contour.points where (each.x >= world.shape.width) sort_by each.y + line4.points +
	circle4.contour.points where (each.x <0) sort_by each.y + line5.points +
	circle5.contour.points where (each.x >= world.shape.width) sort_by each.y + line6.points+
	circle6.contour.points where (each.x <0) sort_by each.y + line7.points +
	circle7.contour.points where (each.x >= world.shape.width) sort_by each.y + line8.points +
	circle8.contour.points where (each.x <0) sort_by each.y + line9.points +
	circle9.contour.points where (each.x >= world.shape.width) sort_by each.y + line10.points +
	circle10.contour.points where (each.x <0) sort_by each.y + line11.points;
	
	geometry lineGeom <- line(drunkPoint);
	list<point> places <- lineGeom points_exterior_ring (lineGeom.perimeter / 100.0);
	loop pt over: places {
		create cell with:[location::pt];	
	}
}

}



species cell{
	aspect default {
		draw circle(2) color:°blue;
	}
}


experiment Display type: gui {
	output {
		display View1 type:opengl draw_env:false{
			species cell;
			graphics z{
				draw line(drunkPoint) color:°black;
			}
		}
	}
}