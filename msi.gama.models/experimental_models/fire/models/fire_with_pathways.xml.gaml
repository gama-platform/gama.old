model fire
// gen by Xml2Gaml


import "../tutorial/fire_tutorial_6.xml.gaml"
global {
	var number_of_h_pathways type: int parameter: 'Number of horizontal pathways:' init: 1 min: 0 max: 10 category: 'Pathways';
	var number_of_v_pathways type: int parameter: 'Number of vertical pathways:' init: 2 min: 0 max: 10 category: 'Pathways';
	var pathways_width type: float parameter: 'Width of pathways (in meters):' init: 10 min: 1 max: 20 category: 'Pathways';
	init {
		create species: pathway number: number_of_v_pathways {
			set horizontal value: false;
		}
		create species: pathway number: number_of_h_pathways {
			set horizontal value: true;
		}
	}
}
entities {
	species pathway skills: situated {
		var horizontal type: bool;
		var location type: point init: {horizontal ? 0 : rnd(width),horizontal ? rnd(height) : 0};
		init {

			loop the_tree over: tree as list  {
				let tree_coord value: horizontal ? last(the_tree.location) : first(the_tree.location);
				let self_coord value: horizontal ? last(location) : first(location);
				if condition: (tree_coord < (self_coord + (pathways_width / 2))) and (tree_coord > (self_coord - (pathways_width / 2))) {
					ask target: the_tree {
						do action: die;
					}
				}
			}
			do action: die;
		}
	}
}
