/**
* Name: CameraSharedZoom
* A simple demonstration on how to implement shared zooms 
* Author: A. Drogoul
* Tags: 3D, zoom, camera
*/
experiment "Shared Zoom Example" model: 'Building Elevation.gaml' type: gui {
	float factor <- 1.0;
	int target1 <- 50;
	int target2 <- 20;
	int target4 <- 75;
	int target3 <- 5;
	float distance -> 500 / factor;
	parameter "Shared zoom" var: factor min: 0.1 max: 2.0;
	parameter "Display 1 focuses on building " var: target1;
	parameter "Display 2 focuses on building " var: target2;
	parameter "Display 3 focuses on building " var: target3;
	parameter "Display 4 focuses on building " var: target4;
	
	output {
		display base type: 3d light: true virtual: true toolbar: false {
			species building aspect: base refresh: false;
			species road aspect: base refresh: false;
			species people refresh: true;
		}

		layout #split;
		display "1" parent: base {
			camera #default location: #isometric target: building at target1 distance: distance dynamic: true;
		}

		display "2" parent: base {
			camera #default target: building at target2 distance: distance dynamic: true;
		}

		display "3" parent: base {
			camera #default target: building at target3 distance: distance dynamic: true;
		}

		display "4" parent: base {
			camera #default target: building at target4 distance: distance dynamic: true;
		}

	}

}
