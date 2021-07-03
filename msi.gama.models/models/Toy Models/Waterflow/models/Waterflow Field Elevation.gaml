/***
* Name: Waterflowgridelevation
* Author: ben
* Description: 
* Tags: water, dem, grid
***/

/***
* Name: Water flow in a river represented by two fields (one for the terrain, one for the flow)
* Author: Benoit Gaudou & Alexis Drogoul
* Description: In this model, the space is discretised using two fields, the 'river' being a set of cells, each of them with an elevation.
* 	The data comes from a DEM (Digital Elevation Model) file.
* 	The upstream cells (i.e. the source cells) and the downstream cells (i.e. the drain cells) are determined automatically ('up' and 'down' of the cells with an altitude < 0)
* 	At each step, the cells in the flow field transmits a part of their water to their neighbor cells that are lower (their height is computed taken into account their elevation and height of water. 
* Tags: grid, gui, hydrology, water flow, DEM
***/
model WaterOnFields


global {
	file dem_file <- file("../includes/DEM_100m_PP.asc");
	file river_file <- file("../includes/rivers_PP.shp");
	// We accentuate the slope slightly, to show the algorithm working
	field terrain <- field(dem_file) * 10;
	field flow <- field(terrain.columns,terrain.rows);
	//Shape of the environment using the dem file
	geometry shape <- envelope(dem_file);
	bool fill <- true;

	//Diffusion rate
	float diffusion_rate <- 0.6;
	list<point> drain_cells <- [];
	list<point> source_cells <- [];
	list<point> points <- flow points_in shape;
	map<point, list<point>> neighbors <- points as_map (each::(flow neighbors_of each));
	map<point, bool> done <- points as_map (each::false);
	map<point, float> h <- points as_map (each::terrain[each]);
	float input_water;

	init {
		if (fill) {
			loop pp over: points where (height(each) < 0) {
				flow[pp] <- 3.0;
			}	
		}

		loop i from: 0 to: terrain.columns - 1 {
			if (terrain[i, 0] < 0) {
				source_cells <<+ flow points_in (terrain cell_at (i, 0));
			}
			if (terrain[i, terrain.rows - 1] < 0) {
				drain_cells <<+ flow points_in (terrain cell_at (i, terrain.rows - 1));
			}
		}
		source_cells <- remove_duplicates(source_cells);
		drain_cells <- remove_duplicates(drain_cells);
	}

	//Reflex to add water among the water cells
	reflex adding_input_water {
		loop p over: source_cells {
			flow[p] <- flow[p] + input_water;
		}
	}

	//Reflex for the drain cells to drain water
	reflex draining  {
		loop p over: drain_cells {
			flow[p] <- 0.0;
		}
	}


	float height (point c) {
		return h[c] + flow[c];
	}

	//Reflex to flow the water according to the altitude and the obstacle
	reflex flowing {
		done[] <- false;
		list<point> water <- points where (flow[each] > 0) sort_by (height(each));
		loop p over: points - water {
			done[p] <- true;
		}
		loop p over: water {
			float height <- height(p);
			loop flow_cell over: neighbors[p] where (done[each] and height > height(each)) {
				float water_flowing <- max(0.0, min((height - height(flow_cell)), flow[p] * diffusion_rate));
				flow[p] <- flow[p] - water_flowing;
				flow[flow_cell] <- flow[flow_cell] + water_flowing;
			}
		done[p] <- true;
		}
	}
}


experiment hydro type: gui {
	parameter "Input water at source" var: input_water <- 1.0;
	parameter "Fill the river" var: fill <- true;
	output {
		display d type: opengl {
			mesh terrain scale: 3 triangulation: true  color: palette([#burlywood, #saddlebrown, #darkgreen, #green]) refresh: false smooth: true;
			mesh flow scale: 3 triangulation: true color: palette(reverse(brewer_colors("Blues"))) transparency: 0.5 no_data:0.0 ;
		}

	}

}
