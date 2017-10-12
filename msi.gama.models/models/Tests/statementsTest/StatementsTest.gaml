/**
 *  StatementsTest
 *  Author: automatic generator
 *  Description: Unity Test of all statements.
 */

model StatementsTest

global {
	init {
		create testStatements number: 1;
		ask testStatements {do _step_;}
	}
}


	species testStatements {

	
		test EqualsStatement {
			//float t;
			//float S;
			//float I;
			//equation SI { 
			//   diff(S,t) = (- 0.3 * S * I / 100);
			//   diff(I,t) = (0.3 * S * I / 100);
			//} 

		}
	
		test actionStatement {
			//action simple_action {
			//   // [set of statements]
			//}
			//action action_parameters(int i, string s){
			//   // [set of statements using i and s]
			//}
			//int action_return_val(int i, string s){
			//   // [set of statements using i and s]
			//   return i + i;
			//}
			//species parent_species {
			//   int virtual_action(int i, string s);
			//}
			//
			//species children parent: parent_species {
			//   int virtual_action(int i, string s) {
			//      return i + i;
			//   }
			//}

		}
	
		test addStatement {
			//add expr to: expr_container;    // Add at the end
			//add expr at: expr to: expr_container;   // Add at position expr
			list<int> workingList <- [];
			add 0 at: 0 to: workingList ; 	// workingList equals [0]
			assert workingList equals: [0]; 
			add 10 at: 0 to: workingList ; 	// workingList equals [10,0]
			assert workingList equals: [10,0]; 
			add 20 at: 2 to: workingList ; 	// workingList equals [10,0,20]
			assert workingList equals: [10,0,20]; 
			add 50 to: workingList; 	// workingList equals [10,0,20,50]
			assert workingList equals: [10,0,20,50]; 
			add [60,70] all: true to: workingList; 	// workingList equals [10,0,20,50,60,70]
			assert workingList equals: [10,0,20,50,60,70]; 
			map<string,string> workingMap <- [];
			add "val1" at: "x" to: workingMap; 	// workingMap equals ["x"::"val1"]
			assert workingMap equals: ["x"::"val1"]; 
			add "val2" to: workingMap; 	// workingMap equals ["x"::"val1", "val2"::"val2"]
			assert workingMap equals: ["x"::"val1", "val2"::"val2"]; 
			add "5"::"val4" to: workingMap;  	// workingMap equals ["x"::"val1", "val2"::"val2", "5"::"val4"]
			assert workingMap equals: ["x"::"val1", "val2"::"val2", "5"::"val4"]; 
			add "val3" at: "x" to: workingMap; 	// workingMap equals ["x"::"val3", "val2"::"val2", "5"::"val4"]
			assert workingMap equals: ["x"::"val3", "val2"::"val2", "5"::"val4"]; 
			add ["val4","val5"] all: true at: "x" to: workingMap; 	// workingMap equals ["x"::"val3", "val2"::"val2", "5"::"val4","val4"::"val4","val5"::"val5"]
			assert workingMap equals: ["x"::"val3", "val2"::"val2", "5"::"val4","val4"::"val4","val5"::"val5"]; 
			graph g <- as_edge_graph([{1,5}::{12,45}]);
			add edge: {1,5}::{2,3} to: g;
			list var16 <- g.vertices; 	// var16 equals [{1,5},{12,45},{2,3}]
			assert var16 equals: [{1,5},{12,45},{2,3}]; 
			list var17 <- g.edges; 	// var17 equals [polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]
			assert var17 equals: [polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]; 
			add node: {5,5} to: g;
			list var19 <- g.vertices; 	// var19 equals [{1.0,5.0},{12.0,45.0},{2.0,3.0},{5.0,5.0}]
			assert var19 equals: [{1.0,5.0},{12.0,45.0},{2.0,3.0},{5.0,5.0}]; 
			list var20 <- g.edges; 	// var20 equals [polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]
			assert var20 equals: [polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]; 

		}
	
		test agentsStatement {
			//display my_display {
			//   agents layer_name value: expression [additional options];
			//}
			//display Segregation {
			//   agents agentDisappear value: people as list where (each.is_happy = false) aspect: with_group_color;
			//}

		}
	
		test annealingStatement {
			//method annealing [facet: value];
			//method annealing temp_init: 100  temp_end: 1 temp_decrease: 0.5 nb_iter_cst_temp: 5 maximize: food_gathered;

		}
	
		test askStatement {
			//ask ${receiver_agents} {
			//     ${cursor}
			//}
			//ask ${one_agent} {
			//     ${cursor}
			//}
			//ask${receiver_agent(s)} as: ${a_species_expression} {
			//     ${cursor}
			//}
			//ask ${receiver_agents} of_species ${species_name} {
			//     ${cursor}
			//}
			//species animal {
			//    float energy <- rnd (1000) min: 0.0 {
			//    reflex when: energy > 500 { // executed when the energy is above the given threshold
			//         list<animal> others <- (animal at_distance 5); // find all the neighboring animals in a radius of 5 meters
			//         float shared_energy  <- (energy - 500) / length (others); // compute the amount of energy to share with each of them
			//         ask others { // no need to cast, since others has already been filtered to only include animals
			//              if (energy < 500) { // refers to the energy of each animal in others
			//                   energy <- energy + myself.shared_energy; // increases the energy of each animal
			//                   myself.energy <- myself.energy - myself.shared_energy; // decreases the energy of the sender
			//              }
			//         }
			//    }
			//}

		}
	
		test aspectStatement {
			//species one_species {
			//	int a <- rnd(10);
			//	aspect aspect1 {
			//		if(a mod 2 = 0) { draw circle(a);}
			//		else {draw square(a);}
			//		draw text: "a= " + a color: #black size: 5;
			//	}
			//}

		}
	
		test assertStatement {
			assert (2+2) = 4;
			assert self != nil;
			int t <- 0; assert is_error(3/t);
			(1 / 2) is float
			assert 'abc' is string warning: true

		}
	
		test benchmarkStatement {

		}
	
		test breakStatement {

		}
	
		test cameraStatement {

		}
	
		test captureStatement {
			//species A {
			//...
			//}
			//species B {
			//...
			//   species C parent: A {
			//   ...
			//   }
			//...
			//}
			//capture list(B) as: C;
			//capture target: list (B) as: C;

		}
	
		test catchStatement {

		}
	
		test chartStatement {
			//display chart_display {
			//   chart "chart name" type: series [additional options] {
			//      [Set of data, datalists statements]
			//   }
			//}

		}
	
		test conscious_contagionStatement {
			conscious_contagion emotion_detected:fear emotion_created:fearConfirmed;
			conscious_contagion emotion_detected:fear emotion_created:fearConfirmed charisma: 0.5 receptivity: 0.5;

		}
	
		test createStatement {
			//create a_species number: an_int;
			create species_of(self) number: 5 returns: list5Agents;
			//5
			//create a_species from: the_shapefile with: [type:: read('TYPE_OCC'), nature::read('NATURE')];
			//create toto from: "toto.csv" header: true with:[att1::read("NAME"), att2::read("TYPE")];
			//or
			//create toto from: "toto.csv" with:[att1::read(0), att2::read(1)]; //with read(int), the index of the column
			create species_of(self) from: [square(4),circle(4)]; 	// 2 agents have been created, with shapes respectively square(4) and circle(4)
			create species_of(self) from: [square(4),circle(4)] returns: new_agt;
			geometry var9 <- new_agt[0].shape; 	// var9 equals square(4)
			assert var9 equals: square(4); 
			geometry var10 <- new_agt[1].shape; 	// var10 equals circle(4)
			assert var10 equals: circle(4); 
			create species (self) number: rnd (4) returns: children;
			ask children {
			        // ...
			}
			//create a_species number: an_int {
			//     [statements]
			//}
			//create species(self) number: rnd (4) returns: children {
			//     set location <- myself.location + {rnd (2), rnd (2)}; // tells the children to be initially located close to me
			//     set parent <- myself; // tells the children that their parent is me (provided the variable parent is declared in this species) 
			//}
			//// Simple syntax
			//create species: a_species number: an_int;
			//

		}
	
		test dataStatement {

		}
	
		test datalistStatement {

		}
	
		test defaultStatement {

		}
	
		test diffuseStatement {
			//matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);
			//diffuse var: phero on: cells mat_diffu: math_diff;
			//diffuse var: phero on: cells mat_diffu: math_diff mask: mymask;
			//diffuse var: phero on: cells proportion: 1/9 radius: 1;

		}
	
		test displayStatement {
			//display my_display [additional options] { ... }
			//display gridWithElevationTriangulated type: opengl ambient_light: 100 {
			//	grid cell elevation: true triangulation: true;
			//	species people aspect: base;
			//}

		}
	
		test display_gridStatement {
			//display my_display {
			//   grid ant_grid lines: #black position: { 0.5, 0 } size: {0.5,0.5};
			//}
			//display my_display {
			//    grid cell texture: texture_file text: false triangulation: true elevation: true;
			//}

		}
	
		test display_populationStatement {
			//display my_display {
			//   species species_name [additional options];
			//}
			//display my_display {
			//   species agent1 aspect: base;
			//   species agent2 aspect: base;
			//   species agent3 aspect: base;
			//}
			//display my_display type: opengl{
			//   species agent1 aspect: base ;
			//   species agent2 aspect: base position:{0,0,0.5};
			//   species agent3 aspect: base position:{0,0,1};
			//}

		}
	
		test doStatement {
			//do name_of_action_or_primitive;
			//do name_of_action_or_primitive arg1: expression1 arg2: expression2;
			//type_returned_by_action result <- self name_of_action_or_primitive [];
			//type_returned_by_action result <- self name_of_action_or_primitive [arg1::expression1, arg2::expression2];
			// Simple syntax: 
			//do action: name_of_action_or_primitive;
			
			// In case the result of the action needs to be made available to the agent, the `returns` keyword can be defined; the result will then be referred to by the temporary variable declared in this attribute:
			//do name_of_action_or_primitive returns: result;
			//do name_of_action_or_primitive arg1: expression1 arg2: expression2 returns: result;
			//type_returned_by_action result <- name_of_action_or_primitive(self, [arg1::expression1, arg2::expression2]);
			
			// In case the result of the action needs to be made available to the agent
			//let result <- name_of_action_or_primitive(self, []);
			
			// In case the action expects one or more arguments to be passed, they can also be defined by using enclosed `arg` statements, or the `with` facet with a map of parameters:
			//do name_of_action_or_primitive with: [arg1::expression1, arg2::expression2];
			//
			//or
			//
			//do name_of_action_or_primitive {
			//     arg arg1 value: expression1;
			//     arg arg2 value: expression2;
			//     ...
			//}

		}
	
		test drawStatement {
			//aspect geometryAspect {
			//	draw circle(1.0) empty: !hasFood color: #orange ;
			//}
			//aspect arrowAspect {
			//	draw "Current state= "+state at: location + {-3,1.5} color: #white font: font('Default', 12, #bold) ;
			//	draw file(ant_shape_full) rotate: heading at: location size: 5
			//}
			//aspect arrowAspect {
			//	draw line([{20, 20}, {40, 40}]) color: #black begin_arrow:5;
			//	draw line([{10, 10},{20, 50}, {40, 70}]) color: #green end_arrow: 2 begin_arrow: 2 empty: true;
			//	draw square(10) at: {80,20} color: #purple begin_arrow: 2 empty: true;
			//}

		}
	
		test elseStatement {

		}
	
		test emotional_contagionStatement {
			emotional_contagion emotion_detected:fearConfirmed;
			emotional_contagion emotion_detected:fear emotion_created:fearConfirmed;
			emotional_contagion emotion_detected:fear emotion_created:fearConfirmed charisma: 0.5 receptivity: 0.5;

		}
	
		test enterStatement {
			//	state s_init {
			//		enter { write "Enter in" + state; }
			//			write "Enter in" + state;
			//		}
			//		write state;
			//	}

		}
	
		test equationStatement {
			//float t;
			//float S;
			//float I;
			//equation SI { 
			//   diff(S,t) = (- 0.3 * S * I / 100);
			//   diff(I,t) = (0.3 * S * I / 100);
			//} 
			//equation eqSI type: SI vars: [S,I,t] params: [N,beta];
			//equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma];
			//equation eqSIR type:SIR vars:[S,I,R,t] params:[N,beta,gamma];
			//equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu];
			//equation eqSEIR type: SEIR vars: [S,E,I,R,t] params: [N,beta,gamma,sigma,mu];
			//equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] ;

		}
	
		test errorStatement {
			error 'This is an error raised by ' + self;

		}
	
		test eventStatement {
			//event [event_type] action: myAction;
			//global {
			//   // ... 
			//   action myAction () {
			//      point loc <- #user_location; // contains the location of the mouse in the world
			//      list<agent> selected_agents <- agents inside (10#m around loc); // contains agents clicked by the event
			//      
			//      // code written by modelers
			//   }
			//}
			//
			//experiment Simple type:gui {
			//   display my_display {
			//      event mouse_up action: myAction;
			//   }
			//}

		}
	
		test exhaustiveStatement {
			//method exhaustive [facet: value];
			//method exhaustive maximize: food_gathered;

		}
	
		test exitStatement {
			//	state s_init initial: true {
			//		write state;
			//		transition to: s1 when: (cycle > 2) {
			//			write "transition s_init -> s1";
			//		}
			//		exit {
			//			write "EXIT from "+state;
			//		}
			//	}

		}
	
		test experimentStatement {

		}
	
		test focusStatement {
			focus var:speed /*where speed is a variable from a species that is being perceived*/

		}
	
		test focus_onStatement {
			focus_on my_species(0);

		}
	
		test geneticStatement {
			//method genetic [facet: value];
			//method genetic maximize: food_gathered pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1 nb_prelim_gen: 1 max_gen: 20; 

		}
	
		test graphicsStatement {
			//display my_display {
			//   graphics "my new layer" {
			//      draw circle(5) at: {10,10} color: #red;
			//      draw "test" at: {10,10} size: 20 color: #black;
			//   }
			//}

		}
	
		test highlightStatement {
			highlight my_species(0) color: #blue;

		}
	
		test hill_climbingStatement {
			//method hill_climbing [facet: value];
			//method hill_climbing iter_max: 50 maximize : food_gathered; 

		}
	
		test ifStatement {
			//if bool_expr {
			//    [statements]
			//}
			//if bool_expr {
			//    [statements]
			//}
			//else {
			//    [statements]
			//}
			string valTrue <- "";
			if true {
				valTrue <- "true";
			}
			else {
				valTrue <- "false";
			}
			 	// valTrue equals "true"
			assert valTrue equals: "true"; 
			string valFalse <- "";
			if false {
				valFalse <- "true";
			}
			else {
				valFalse <- "false";
			}
			 	// valFalse equals "false"
			assert valFalse equals: "false"; 
			//if bool_expr {
			//    [statements]
			//}
			//else if bool_expr2 {
			//    [statements]
			//}
			//else {
			//    [statements]
			//}

		}
	
		test imageStatement {
			//display my_display {
			//   image layer_name file: image_file [additional options];
			//}
			//display my_display {
			//   image background file:"../images/my_backgound.jpg";
			//}
			//display my_display {
			//   image testGIS gis: "../includes/building.shp" color: rgb('blue');
			//}
			//display my_display {
			//  image image1 file:"../images/image1.jpg";
			//  image image2 file:"../images/image2.jpg";
			//  image image3 file:"../images/image3.jpg" position: {0,0,0.5};
			//}

		}
	
		test inspectStatement {
			//inspect "my_inspector" value: ant attributes: ["name", "location"];

		}
	
		test letStatement {

		}
	
		test lightStatement {
			//light 1 type:point position:{20,20,20} color:255, linear_attenuation:0.01 quadratic_attenuation:0.0001 draw_light:true update:false
			//light 2 type:spot position:{20,20,20} direction:{0,0,-1} color:255 spot_angle:25 linear_attenuation:0.01 quadratic_attenuation:0.0001 draw_light:true update:false
			//light 3 type:point direction:{1,1,-1} color:255 draw_light:true update:false

		}
	
		test loopStatement {
			//loop times: an_int_expression {
			//     // [statements]
			//}
			int sumTimes <- 1;
			loop times: 3 {sumTimes <- sumTimes + sumTimes;}
			 	// sumTimes equals 8
			assert sumTimes equals: 8; 
			//loop while: a_bool_expression {
			//     // [statements]
			//}
			int sumWhile <- 1;
			loop while: (sumWhile < 5) {sumWhile <- sumWhile + sumWhile;}
			 	// sumWhile equals 8
			assert sumWhile equals: 8; 
			//loop a_temp_var over: a_collection_expression {
			//     // [statements]
			//}
			//loop a_temp_var from: int_expression_1 to: int_expression_2 {
			//     // [statements]
			//}
			//loop a_temp_var from: int_expression_1 to: int_expression_2 step: int_expression3 {
			//     // [statements]
			//}
			int sumFor <- 0;
			loop i from: 10 to: 30 step: 10 {sumFor <- sumFor + i;}
			 	// sumFor equals 60
			assert sumFor equals: 60; 
			int a <- 0;
			loop i over: [10, 20, 30] {
			     a <- a + i;
			} // a now equals 60
			 	// a equals 60
			assert a equals: 60; 
			list the_list <-list (species_of (self));
			loop i from: 0 to: length (the_list) - 1 {
			     ask the_list at i {
			        // ...
			     }
			} // every  agent of the list is asked to do something

		}
	
		test matchStatement {
			switch 3 {
			   match 1 {write "Match 1"; }
			   match 3 {write "Match 2"; }
			}
			switch 3 {
			   match_between [1,2] {write "Match OK between [1,2]"; }
			   match_between [2,5] {write "Match OK between [2,5]"; }
			}
			switch 3 {
			   match_one [0,1,2] {write "Match OK with one of [0,1,2]"; }
			   match_between [2,3,4,5] {write "Match OK with one of [2,3,4,5]"; }
			}

		}
	
		test migrateStatement {
			//migrate ball_in_group target: ball_in_cloud;

		}
	
		test monitorStatement {
			//monitor "nb preys" value: length(prey as list) refresh_every: 5;  

		}
	
		test outputStatement {
			//experiment exp_name type: gui {
			//   // [inputs]
			//   output {
			//      // [display, file, inspect, layout or monitor statements]
			//   }
			//}

		}
	
		test output_fileStatement {

		}
	
		test overlayStatement {
			//overlay "Cycle: " + (cycle) center: "Duration: " + total_duration + "ms" right: "Model time: " + as_date(time,"") color: [#yellow, #orange, #yellow];

		}
	
		test parameterStatement {
			//parameter title var: global_var category: cat;
			//parameter 'Value of toto:' var: toto among: [1, 3, 7, 15, 100]; 
			//parameter 'Value of titi:' var: titi min: 1 max: 100 step: 2; 

		}
	
		test perceiveStatement {
			//perceive name_of-perception target: the_agents_you_want_to_perceive in: a_distance when: a_certain_condition {
			//Here you are in the context of the perceived agents. To refer to the agent who does the perception, use myself.
			//If you want to make an action (such as adding a belief for example), use ask myself{ do the_action}
			//}

		}
	
		test permanentStatement {
			//permanent {
			//	display Ants background: rgb('white') refresh_every: 1 {
			//		chart "Food Gathered" type: series {
			//			data "Food" value: food_gathered;
			//		}
			//	}
			//}

		}
	
		test planStatement {

		}
	
		test putStatement {
			//put expr at: expr in: expr_container;
			//put all: expr in: expr_container;
			list<int> putList <- [1,2,3,4,5]; 	// putList equals [1,2,3,4,5]
			assert putList equals: [1,2,3,4,5]; 
			put -10 at: 1 in: putList; 	// putList equals [1,-10,3,4,5]
			assert putList equals: [1,-10,3,4,5]; 
			put 10 all: true in: putList; 	// putList equals [10,10,10,10,10]
			assert putList equals: [10,10,10,10,10]; 
			matrix<int> putMatrix <- matrix([[0,1],[2,3]]); 	// putMatrix equals matrix([[0,1],[2,3]])
			assert putMatrix equals: matrix([[0,1],[2,3]]); 
			put -10 at: {1,1} in: putMatrix; 	// putMatrix equals matrix([[0,1],[2,-10]])
			assert putMatrix equals: matrix([[0,1],[2,-10]]); 
			put 10 all: true in: putMatrix; 	// putMatrix equals matrix([[10,10],[10,10]])
			assert putMatrix equals: matrix([[10,10],[10,10]]); 
			map<string,int> putMap <- ["x"::4,"y"::7]; 	// putMap equals ["x"::4,"y"::7]
			assert putMap equals: ["x"::4,"y"::7]; 
			put -10 key: "y" in: putMap; 	// putMap equals ["x"::4,"y"::-10]
			assert putMap equals: ["x"::4,"y"::-10]; 
			put -20 key: "z" in: putMap; 	// putMap equals ["x"::4,"y"::-10, "z"::-20]
			assert putMap equals: ["x"::4,"y"::-10, "z"::-20]; 
			put -30 all: true in: putMap; 	// putMap equals ["x"::-30,"y"::-30, "z"::-30]
			assert putMap equals: ["x"::-30,"y"::-30, "z"::-30]; 

		}
	
		test reactive_tabuStatement {
			//method reactive_tabu [facet: value];
			//method reactive_tabu iter_max: 50 tabu_list_size_init: 5 tabu_list_size_min: 2 tabu_list_size_max: 10 nb_tests_wthout_col_max: 20 cycle_size_min: 2 cycle_size_max: 20 maximize: food_gathered;

		}
	
		test reflexStatement {
			//reflex my_reflex when: flip (0.5){ 		//Only executed when flip returns true
			//    write "Executing the unconditional reflex";
			//}

		}
	
		test releaseStatement {
			//species A {
			//...
			//}
			//species B {
			//...
			//   species C parent: A {
			//   ...
			//   }
			//   species D {
			//   ...
			//   }
			//...
			//}
			//release list(C);
			//release list (C) as: new_species in: new host;

		}
	
		test removeStatement {
			//remove expr from: expr_container;
			//remove index: expr from: expr_container;
			//remove key: expr from: expr_container;
			//remove all: expr from: expr_container;
			list<int> removeList <- [3,2,1,2,3];
			remove 2 from: removeList; 	// removeList equals [3,1,2,3]
			assert removeList equals: [3,1,2,3]; 
			remove 3 all: true from: removeList; 	// removeList equals [1,2]
			assert removeList equals: [1,2]; 
			remove index: 1 from: removeList; 	// removeList equals [1]
			assert removeList equals: [1]; 
			map<string,int> removeMap <- ["x"::5, "y"::7, "z"::7];
			remove key: "x" from: removeMap; 	// removeMap equals ["y"::7, "z"::7]
			assert removeMap equals: ["y"::7, "z"::7]; 
			remove 7 all: true from: removeMap; 	// removeMap equals map([])
			assert removeMap equals: map([]); 
			map<string,int> removeMapList <- ["x"::5, "y"::7, "z"::7, "t"::5];
			remove 7 from: removeMapList; 	// removeMapList equals ["x"::5, "z"::7, "t"::5]
			assert removeMapList equals: ["x"::5, "z"::7, "t"::5]; 
			remove [5,7] all: true from: removeMapList; 	// removeMapList equals ["t"::5]
			assert removeMapList equals: ["t"::5]; 
			remove index: "t" from: removeMapList; 	// removeMapList equals map([])
			assert removeMapList equals: map([]); 
			graph removeGraph <- as_edge_graph([{1,2}::{3,4},{3,4}::{5,6}]);
			remove node: {1,2} from: removeGraph;
			remove node(1,2) from: removeGraph;
			list var18 <- removeGraph.vertices; 	// var18 equals [{3,4},{5,6}]
			assert var18 equals: [{3,4},{5,6}]; 
			list var19 <- removeGraph.edges; 	// var19 equals [polyline({3,4}::{5,6})]
			assert var19 equals: [polyline({3,4}::{5,6})]; 
			remove edge: {3,4}::{5,6} from: removeGraph;
			remove edge({3,4},{5,6}) from: removeGraph;
			list var22 <- removeGraph.vertices; 	// var22 equals [{3,4},{5,6}]
			assert var22 equals: [{3,4},{5,6}]; 
			list var23 <- removeGraph.edges; 	// var23 equals []
			assert var23 equals: []; 
			//global {
			//   init {
			//      create speciesRemove;
			//      speciesRemove sR <- speciesRemove(0); 	// sR.a now equals 100
			//      remove key:"a" from: sR; 	// sR.a now equals nil
			//   }
			//}
			//
			//species speciesRemove {
			//   int a <- 100; 
			//}

		}
	
		test returnStatement {
			//string foo {
			//     return "foo";
			//}
			//
			//reflex {
			//    string foo_result <- foo(); 	// foos_result is now equals to "foo"
			//}
			//// In Species A:
			//string foo_different {
			//     return "foo_not_same";
			//}
			///// ....
			//// In Species B:
			//reflex writing {
			//    string temp <- some_agent_A.foo_different []; 	// temp is now equals to "foo_not_same" 
			//}

		}
	
		test ruleStatement {
			rule belief: new_predicate("test") when: flip(0.5) new_desire: new_predicate("test")

		}
	
		test runStatement {

		}
	
		test saveStatement {
			//save data to: output_file type: a_type_file;
			save (string(cycle) + "->"  + name + ":" + location) to: "save_data.txt" type: "text";
			save [name, location, host] to: "save_data.csv" type: "csv";
			save species_of(self) to: "save_csvfile.csv" type: "csv" header: false;
			save species_of(self) to: "save_shapefile.shp" type: "shp" with: [name::"nameAgent", location::"locationAgent"] crs: "EPSG:4326";
			save grid to: "save_grid.asc" type: "asc";
			save grid to: "save_grid.tif" type: "geotiff";
			save grid to: "save_grid.png" type: "image";

		}
	
		test setStatement {

		}
	
		test setupStatement {
			//species Tester {
			//    int val_to_test;
			//
			//    setup {
			//        val_to_test <- 0;
			//    }
			//
			//    test t1 {
			//       // [set of instructions, including asserts]
			//    }
			//}

		}
	
		test simulateStatement {
			//ask receiver_agent(s) {
			//     // [statements]
			//}

		}
	
		test socializeStatement {
			socialize;

		}
	
		test solveStatement {
			//solve SIR method: "rk4" step:0.001;

		}
	
		test speciesStatement {
			//species ant skills: [moving] control: fsm {
			//species node_agent mirrors: list(bug) parent: graph_node edge_species: edge_agent {
			//grid ant_grid width: gridwidth height: gridheight file: grid_file neighbors: 8 use_regular_agents: false { 
			//grid ant_grid file: grid_file neighbors: 8 use_regular_agents: false { 

		}
	
		test start_simulationStatement {

		}
	
		test stateStatement {
			//	state s_init initial: true {
			//		enter { write "Enter in" + state; }
			//			write "Enter in" + state;
			//		}
			//
			//		write state;
			//
			//		transition to: s1 when: (cycle > 2) {
			//			write "transition s_init -> s1";
			//		}
			//
			//		exit {
			//			write "EXIT from "+state;
			//		}
			//	}
			//	state s1 {
			//
			//	enter {write 'Enter in '+state;}
			//
			//	write state;
			//
			//	exit {write 'EXIT from '+state;}
			//}

		}
	
		test statusStatement {
			status ('This is my status ' + self) color: #yellow;

		}
	
		test switchStatement {
			//switch an_expression {
			//        match value1 {...}
			//        match_one [value1, value2, value3] {...}
			//        match_between [value1, value2] {...}
			//        default {...}
			//}
			switch 3 {
			   match 1 {write "Match 1"; }
			   match 2 {write "Match 2"; }
			   match 3 {write "Match 3"; }
			   match_one [4,4,6,3,7]  {write "Match one_of"; }
			   match_between [2, 4] {write "Match between"; }
			   default {write "Match Default"; }
			}
			string val1 <- "";
			switch 1 {
			   match 1 {val1 <- val1 + "1"; }
			   match 2 {val1 <- val1 + "2"; }
			   match_one [1,1,6,4,7]  {val1 <- val1 + "One_of"; }
			   match_between [2, 4] {val1 <- val1 + "Between"; }
			   default {val1 <- val1 + "Default"; }
			}
			 	// val1 equals '1One_of'
			assert val1 equals: '1One_of'; 
			string val2 <- "";
			switch 2 {
			   match 1 {val2 <- val2 + "1"; }
			   match 2 {val2 <- val2 + "2"; }
			   match_one [1,1,6,4,7]  {val2 <- val2 + "One_of"; }
			   match_between [2, 4] {val2 <- val2 + "Between"; }
			   default {val2 <- val2 + "Default"; }
			}
			 	// val2 equals '2Between'
			assert val2 equals: '2Between'; 
			string val10 <- "";
			switch 10 {
			   match 1 {val10 <- val10 + "1"; }
			   match 2 {val10 <- val10 + "2"; }
			   match_one [1,1,6,4,7]  {val10 <- val10 + "One_of"; }
			   match_between [2, 4] {val10 <- val10 + "Between"; }
			   default {val10 <- val10 + "Default"; }
			}
			 	// val10 equals 'Default'
			assert val10 equals: 'Default'; 

		}
	
		test tabuStatement {
			//method tabu [facet: value];
			//method tabu iter_max: 50 tabu_list_size: 5 maximize: food_gathered;

		}
	
		test taskStatement {

		}
	
		test testStatement {
			//species Tester {
			//    // set of attributes that will be used in test
			//
			//    setup {
			//        // [set of instructions... in particular initializations]
			//    }
			//
			//    test t1 {
			//       // [set of instructions, including asserts]
			//    }
			//}

		}
	
		test traceStatement {

		}
	
		test transitionStatement {
			//	state s_init initial: true {
			//		write state;
			//		transition to: s1 when: (cycle > 2) {
			//			write "transition s_init -> s1";
			//		}
			//	}

		}
	
		test tryStatement {
			//try {
			//    [statements]
			//}
			//try {
			//    [statements]
			//}
			//catch {
			//    [statements]
			//}

		}
	
		test unconscious_contagionStatement {
			unconscious_contagion emotion:fearConfirmed; 
			unconscious_contagion emotion:fearConfirmed charisma: 0.5 receptivity: 0.5;

		}
	
		test user_commandStatement {
			//user_command kill_myself action: some_action with: [arg1::val1, arg2::val2, ...];

		}
	
		test user_initStatement {

		}
	
		test user_inputStatement {
			//user_panel "Advanced Control" {
			//	user_input "Location" returns: loc type: point <- {0,0};
			//	create cells number: 10 with: [location::loc];
			//}

		}
	
		test user_panelStatement {
			//user_panel default initial: true {
			//	user_input 'Number' returns: number type: int <- 10;
			//	ask (number among list(cells)){ do die; }
			//	transition to: "Advanced Control" when: every (10);
			//}
			//
			//user_panel "Advanced Control" {
			//	user_input "Location" returns: loc type: point <- {0,0};
			//	create cells number: 10 with: [location::loc];
			//}

		}
	
		test usingStatement {
			//float dist <- 0.0;
			//using topology(grid_ant) {
			//	d (self.location distance_to target.location);
			//}

		}
	
		test Variable_containerStatement {

		}
	
		test Variable_numberStatement {

		}
	
		test Variable_regularStatement {

		}
	
		test warnStatement {
			warn 'This is a warning from ' + self;

		}
	
		test writeStatement {
			write 'This is a message from ' + self;

		}
	
	}


experiment testStatementsExp type: gui {}	
	