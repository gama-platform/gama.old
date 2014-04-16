/**
 *  m1
 *  Author: hqnghi
 *  Description: 
 */

model comodel
global {
	geometry shape<-envelope(50);
		
	file refModel<-gaml_file("Referencebuilding.gaml","road_traffic","refModel");
	file viewModel<-gaml_file("ModelViewbuilding.gaml","road_trafficview","viewModel");
	list buildingOut;
	
	list roadOut;
	
	list peopleOut; 
	init{ 
		int i<-0;
//			array_comodel<+gaml_file("grass.gaml","life_cycle","sim"+i);
//			array_comodel<+gaml_file("m2.gaml","expm2","sim"+i);
//			array_comodel<+gaml_file("SIR_switch2.gaml","expSIR","sim"+i);
//			array_comodel<+gaml_file("SIR.gaml","maths","sim"+i);
		
			simulate comodel:refModel repeat:1;// with_output:["buildingOut"::"buildingOut","roadOut"::"roadOut","peopleOut"::"peopleOut"];
			simulate comodel:viewModel repeat:1 ;//with_input:["buildingOut"::buildingOut,"roadOut"::roadOut,"peopleOut"::peopleOut];
		create A number:20;
	}
	
	action dosim{
		
			simulate comodel:refModel repeat:1;// with_output:["buildingOut"::buildingOut,"roadOut"::roadOut,"peopleOut"::peopleOut];
			simulate comodel:viewModel repeat:1;// with_input:["buildingOut"::buildingOut,"roadOut"::roadOut,"peopleOut"::peopleOut];
	}


}
 
species A skills:[moving]{
	int n; 
	reflex dosomething{  
		do wander;
	}
	aspect base{
		draw circle(1) color:rgb("red");
	}
} 
 
experiment comodel2exp type: gui {

	output {
		display "a_disp" background:rgb("black"){
				species A aspect:base; 
		} 

	}
	
}
