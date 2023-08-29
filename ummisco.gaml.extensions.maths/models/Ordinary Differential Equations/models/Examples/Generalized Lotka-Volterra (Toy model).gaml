/**
 *  Author: Tri Nguyen-Huu
 *  Description: A generalized Lotka-Volterra model. 
 * The left column and upper row allow to add or remove animal species. 
 * Each animal species has a population that evolves by default independantly from others, according to a logistic law (with a carrying capacity).
 * The buttons in the matrix can be pressed in order to add interactions, "+" denoting a positive action from the upper species towards the left species, 
 * and a "-" denotes a negative interaction. A positive interaction of species "A" towards "B" means that a high density of species high increases the
 * density of species B (for example A is predated by B). A negative interaction of species "A" towards "B" means that a high density of species high 
 * decreases the density of species B (for example B is predated by A).
 * 
 * Common Lotka-Volterra interactions between two spices A and B ares:
 * mutalism: A -> B: +, B -> A: +
 * A predating B: A -> B: -, B -> A: +
 * competition: A -> B: -, B -> A: -
 */

model GeneralizedLotkaVolterra

global {
	int max_species <- 8;
	string dummy <- '';

	string language <- "english" among: ["french","english"];

	map<string,list<list<string>>> animal_names <- map(
										"french"::[["Goé","land"],["Ga","zelle"],["Tama","noir"],["La","pin"],["Cou","cou"],["Ca","nard"],["Cha","mois"],["Ecu","reuil"],["Elé","phant"],
										["Droma","daire"],["Pé","lican"],["Sou","ris"],["Pou","let"],["Perro","quet"],["Rossi","gnol"],["Gre","nouille"],["Phaco","chère"],["Maque","reau"],
										["Sar","dine"],["Mou","ton"],["Ser","pent"],["Tor","tue"],["Pu","tois"]],
										"english"::[["Chee","tah"],["Gi","raffe"],["Ele","phant"],["Ra","bbit"],["Squi","rrel"],
										["Chame","leon"],["Bumble","bee"],["Bu","ffalo"],["Ze","bra"],
										["Rattle","snake"],["Bea","ver"],["Sala","mander"],["Hippo","potamus"],["Pa","rrot"],
										["Rhino","ceros"],["Kanga","roo"],["Leo","pard"],["Alli","gator"],
										["Go","rilla"],["Croco","dile"],["Platy","pus"],["Octo","pus"],["Porcu","pine"]]
										);


	list<string> possible_type <-["neutral","positive","negative"];
	map<string,rgb> type_color <- ["neutral"::rgb(240,240,240),"negative"::rgb(250,65,65),"positive"::rgb(150,217,100)];
	
	image_file arrow <- image_file("../../includes/arrow.png");
	
	list<rgb> color_list <- list_with(max_species,rgb(0,0,0,0));
	list<animal> species_list <- list_with(max_species, nil);
	
	geometry shape<-square(1000);
	graph the_graph <- [] ;
	map<pair<animal,animal>,string> edge_type <- [];
	float hKR4 <- 0.01;
	
	init{
	//	create population_count;
		loop i from: 0 to: max_species-1{
			color_list[i] <- rgb(int(240/max_species*i),int(240/max_species*i),255);
		}
		create solver_and_scheduler;
	}
	
	reflex layout_graph {
		the_graph <- layout_circle(the_graph,rectangle(world.shape.width * 0.7, world.shape.height*0.7),false);
	}
	
	action activate_act {
		button selected_button <- first(button overlapping (circle(1) at_location #user_location));
		if selected_button != nil {
			if ((selected_button.grid_x > 0) and (selected_button.grid_y = 0)){
				selected_button <- button[selected_button.grid_y,selected_button.grid_x];
			}
			selected_button.button_pressed <- true;					
		}
	}	
}


species animal{
	float t;
	float pop;
	float r;
	float k;
	
	list<animal> positive_species <-[];
	list<animal> negative_species <-[];
	map<animal,float> interaction_coef <- [];
	
	action change_type(animal ani, string type){
		remove ani from: positive_species;
		remove ani from: negative_species;
		remove key: ani from: interaction_coef;
		if type = "positive" { positive_species <+ ani;}
		if type = "negative" { negative_species <+ ani;}
		if type != "neutral" {interaction_coef <+ ani::(rnd(100)/100);}
	}
	

	equation dynamics simultaneously: [animal]{ 
		diff(pop,t) = r*pop * (1 - pop/k + sum((positive_species where (!dead(each))) collect(interaction_coef[each]*each.pop/k)) - sum((negative_species where (!dead(each))) collect(interaction_coef[each]*each.pop/k)));		
        }
        
 
	aspect default{
		draw circle(20) color: #blue;
		draw name anchor: #left_center at: location+{30,0,0} color: #black font:font("SansSerif", 13, #bold);
	}
}


species solver_and_scheduler{
	float t;
	float dummy;
	list<float> pop <- list_with(max_species,0.0);
	
	
	equation dynamics simultaneously: [animal]{ 
		diff(dummy,t)=0;		
    }
    
    	
	reflex solveEquation {      
 
    	loop selected_button over: (button where each.button_pressed){
    		// action for buttons in left column or upper row
			if ((selected_button.grid_x = 0) or (selected_button.grid_y = 0)) and (selected_button.grid_y != selected_button.grid_x){
				if (selected_button.grid_x > 0) {selected_button <- button[selected_button.grid_y,selected_button.grid_x];}
				button opposite_button <- button[selected_button.grid_y,selected_button.grid_x];
				if (selected_button.active){
					// remove animal species and reset interactions
					animal species_to_be_removed <- species_list[selected_button.grid_y - 1];
					the_graph <- species_to_be_removed remove_node_from the_graph;
					ask button where ((each.grid_x = selected_button.grid_y) or (each.grid_y = selected_button.grid_y)) {self.type <- "neutral";}
					ask animal {
						remove species_to_be_removed from: self.positive_species;
						remove key: species_to_be_removed from: self.interaction_coef;
					}
					ask species_to_be_removed {do die;}
					species_list[selected_button.grid_y - 1] <- nil;
				}else{
					// add a new animal species
					create animal{
						name <- animal_names[language][rnd(length(animal_names[language])-1)][0]+animal_names[language][rnd(length(animal_names[language])-1)][1];
						species_list[selected_button.grid_y - 1] <- self;
						r <- rnd(100)/100;
						k <- 30.0+rnd(50);
						pop <- rnd(k);
						the_graph <- the_graph add_node species_list[selected_button.grid_y - 1];
					}
				}
				selected_button.active <- !selected_button.active;
				opposite_button.active <- !opposite_button.active;
				ask button where (each.grid_x > 0 and each.grid_y > 0){
					self.active <- (species_list[self.grid_x-1] != nil) and (species_list[self.grid_y-1] != nil);
				}
			}
				
			// action for buttons in the main matrix
			if (selected_button.active) and (selected_button.grid_x > 0) and (selected_button.grid_y > 0) and (selected_button.grid_x != selected_button.grid_y){
				string new_type <- possible_type[mod(possible_type index_of(selected_button.type)+1,length(possible_type))];
				selected_button.type <- new_type;
				ask species_list[selected_button.grid_y - 1] {do change_type(species_list[selected_button.grid_x - 1], new_type);}
				add edge(species_list[selected_button.grid_x - 1], species_list[selected_button.grid_y - 1]) to: the_graph;
				add (species_list[selected_button.grid_x - 1]::species_list[selected_button.grid_y - 1])::new_type to: edge_type;
			}
			selected_button.button_pressed <- false;
    	} 	
    solve dynamics method: "rk4" step_size:0.01;
    }

	reflex update_count{
		loop i from: 0 to: max_species-1{
			pop[i] <- (species_list[i] != nil)?species_list[i].pop:0;
		}
	}
	
	
}



grid button width:max_species+1 height:max_species+1 
{
	string type <- "neutral";
	bool active <- false;
	bool button_pressed <- false;
	
	aspect classic {
		if (grid_x = 0 and grid_y > 0) {
			draw rectangle(shape.width,shape.height * 0.8) at: location - {0.1*shape.width,0,0} color: active?color_list[grid_y - 1]:rgb(230,230,230) ;
			if (species_list[grid_y - 1] != nil)  {draw species_list[grid_y -1].name font:font("SansSerif", 13, #bold) anchor: #left_center at: location - {shape.width*0.48,0,0} color: #black;}

		} else if (grid_y = 0 and grid_x > 0) {
			draw rectangle(shape.width*0.8,shape.height) at: location - {0,0.1*shape.height,0} color: active?color_list[grid_x - 1]:rgb(230,230,230) ;
			if (species_list[grid_x - 1] != nil)  {draw species_list[grid_x -1].name font:font("SansSerif", 13, #bold) anchor: #left_center at: location - {0,shape.height*0.0,0} rotate: -90 color: #black;}
		} else if (grid_x = grid_y){
			if grid_x != 0 {draw rectangle(shape.width * 0.8,shape.height * 0.8) color: active?rgb(200,200,200):rgb(240,240,240) ;}
		} else {
			draw rectangle(shape.width * 0.8,shape.height * 0.8) color: active?type_color[type]:rgb(240,240,240) ;
		}
	}
	
	
	aspect modern {
		if (grid_x = 0 and grid_y > 0) {
			draw rectangle(shape.width,shape.height * 0.8) at: location - {0.1*shape.width,0,0} color: active?color_list[grid_y - 1]:rgb(230,230,230) ;
			if (species_list[grid_y - 1] != nil)  {
				draw species_list[grid_y -1].name font:font("SansSerif", 12, #bold) anchor: #center at: location - {shape.width*0.05,0,0} color: #white;
			}
			if !active {
				draw "?" font:font("Arial", 60, #bold)  at: location - {0.1*shape.width,0.06 * shape.height,0} anchor: #center color: #white;
			}
		} else if (grid_y = 0 and grid_x > 0) {
			draw rectangle(shape.width*0.8,shape.height) at: location - {0,0.1*shape.height,0} color: active?color_list[grid_x - 1]:rgb(230,230,230) ;
			if !active {
				draw "?" font:font("Arial", 60, #bold)  at: location - {0.0*shape.width, 0.1 * shape.height,0} anchor: #center color: #white;
			}
			if (species_list[grid_x - 1] != nil)  {draw species_list[grid_x -1].name font:font("SansSerif", 12, #bold) anchor: #center at: location + {0.2 * shape.width,-shape.height*0.25,0} rotate: -90 color: #white;}
		} else if (grid_x = grid_y){
			
			if grid_x != 0 {
				draw rectangle(shape.width * 0.8,shape.height * 0.8) color: #white  border: rgb(240,240,240);
			} else {
				draw arrow size: shape.width *0.7;
			}
		} else {
			if active{
				draw rectangle(shape.width * 0.8,shape.height * 0.8) color: type_color[type];	
				if type != "neutral" {draw rectangle(shape.width * 0.55,shape.height * 0.15) color: #white;}
				if type = "positive" {draw rectangle(shape.width * 0.15,shape.height * 0.55) color: #white;}
			}else{
				draw rectangle(shape.width * 0.8,shape.height * 0.8) color: #white  border: rgb(240,240,240);
			}
		}
	}
}





experiment Simulation type: gui autorun: true  {
	float minimum_cycle_duration <- 0.1;
	parameter "Language for animal names" var: language category: "language";
 	text "Click on '?'s to add animal species, then click on grey squares to change among 3 types of interactions:
'+' means that the upper species has a positive impact on the left species (e.g. the left one eats the top one). 
'-' is for negative impact.
grey is neutral."
 		category: "Help";
 			
	output { 
		layout value: horizontal([0::50,vertical([1::50,2::50])::50]) tabs:true;
		display action_button name:"Interaction matrix" toolbar: false type:2d{
			species button aspect:modern ;
			event #mouse_down {ask simulation {do activate_act;}} 
		}
		display LV name: "Time series" refresh: every(1#cycle) type: 2d toolbar: false{
			chart "Population size" type: series background: rgb('white') x_range: 200 x_tick_line_visible: false{
				loop i from: 0 to: max_species-1{
					data "Species "+i value: first(solver_and_scheduler).pop[i] color: (species_list[i] != nil)?color_list[i]:rgb(0,0,0,0) marker: false;
				}
			}
		}
		display "Interaction graph" type: 2d {
			graphics "edges" {
				loop edge over: the_graph.edges {
					float angle <- (pair<animal,animal>(edge)).key towards (pair<animal,animal>(edge)).value;
					point centre <- centroid(polyline([pair<animal,animal>(edge).key.location,pair<animal,animal>(edge).value.location]));
					draw geometry(edge) + 2 at: centre + {2.5*sin(angle),-2.5*cos(angle),0}color: edge_type[pair<animal,animal>(edge)] = "negative"?#red:#green;
					draw triangle(20) rotate: angle + 90 at: centre + {12*cos(angle),12*sin(angle),0} color: edge_type[pair<animal,animal>(edge)] = "negative"?#red:#green;
				}
 			}
 			species animal aspect: default;
		}
	
	}
}
