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
	grid_file dem_file <- file("../includes/DEM_100m_PP.asc");
	field terrain <- field(dem_file) ;
	field flow <- field(terrain.columns,terrain.rows);
	//Shape of the environment using the dem file
	geometry shape <- envelope(dem_file);
	bool fill <- false;

	//Diffusion rate
	float diffusion_rate <- 0.8;
	int frequence_input <- 3;
	list<point> drain_cells <- [];
	list<point> source_cells <- [];
	map<point, float> heights <- [];
	list<point> points <- flow points_in shape;
	map<point, list<point>> neighbors <- points as_map (each::(flow neighbors_of each));
	map<point, bool> done <- points as_map (each::false);
	map<point, float> h <- points as_map (each::terrain[each]);
	float input_water;
	init {
		geometry river_g <- first( file("../includes/river.shp"));
		float c_h <- shape.height/flow.rows;
		list<point>  rivers_pt <- points where ((each overlaps river_g) and (terrain[each] < 100.0)) ;
		if (fill) {
			loop pt over: rivers_pt  {
				flow[pt] <- 1.0;
			}
		}
		
		loop pt over: rivers_pt  {
			if (pt.y <  (c_h)) {
				source_cells <<pt;
			}
		}	
		loop pt over: rivers_pt  {
			if (pt.y > (shape.height - (c_h) )) {
				drain_cells <<pt;
			}
		}	
		
	}

	//Reflex to add water among the water cells
	reflex adding_input_water when: every(frequence_input#cycle){
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
		heights <- points as_map (each::height(each));
		list<point> water <- points where (flow[each] > 0) sort_by (heights[each]);
		loop p over: points - water {
			done[p] <- true;
		}
		loop p over: water {
			float height <- height(p);
			loop flow_cell over: (neighbors[p] where (done[each] and height > heights[each])) sort_by heights[each]  {
				float water_flowing <- max(0.0, min((height - heights[flow_cell]), flow[p] * diffusion_rate));
				flow[p] <- flow[p] - water_flowing;
				flow[flow_cell] <- flow[flow_cell] + water_flowing;
				heights[p] <- height(p) ;
				heights[flow_cell] <- height(flow_cell) ;
			}
			done[p] <- true;
		}
	}
}


experiment hydro type: gui {
	parameter "Input water at source" var: input_water <- 1.0 min: 0.0 max: 3.0 step: 0.1;
	parameter "Fill the river" var: fill <- true;
	output {
		display d type: 3d {
			camera 'default' location: {7071.9529,10484.5136,5477.0823} target: {3450.0,3220.0,0.0};
			mesh terrain scale: 10 triangulation: true  color: palette([#burlywood, #saddlebrown, #darkgreen, #green]) refresh: false smooth: true;
			mesh flow scale: 10 triangulation: true color: palette(reverse(brewer_colors("Blues"))) transparency: 0.5 no_data:0.0 ;
		}

	}

}
