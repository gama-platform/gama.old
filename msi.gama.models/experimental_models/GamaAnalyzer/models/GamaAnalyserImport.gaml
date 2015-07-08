model GamAnalyzer

import 'ReferenceModel.gaml'

global {

	agent_group_follower peoplefollower;
	list<building> residential_buildings;
	list<building>  industrial_buildings;
	list<float> testlist3<-[2,3,4,5];
	list<float> testlist4<-[3,2];
	list<float> testlist5<-[3,2];
	list<list>  testlist6<-[testlist3,testlist4,testlist4];
	list<list>  testlist<-[testlist3,testlist3,testlist3];
	list<list>  testlist2<-["A","B","C","D"];
	
	init {
		create agentfollower 
		{
		  do analyse_cluster species_to_analyse:"people";
		  peoplefollower<-self;
		}
	}
	
	reflex update_List{		
		if (cycle>0)
		{
			testlist<-peoplefollower at_cycle ("multi_distribhistory","speed");
			testlist3<-peoplefollower at_cycle ("multi_minhistory","speed");
			testlist4<-peoplefollower at_cycle ("multi_averagehistory","speed");
			testlist5<-peoplefollower at_cycle ("multi_maxhistory","speed");
			testlist2<-[testlist2,testlist3,testlist4];
			testlist2<-peoplefollower distrib_legend ("speed");
			testlist4<-((peoplefollower.averagehistory at {(reverse(peoplefollower.numvarmap)["speed"]) as int,cycle-1}) as list);
			testlist4<-((peoplefollower.averagehistory at {(reverse(peoplefollower.numvarmap)["heading"]) as int,cycle-1}) as list);
			testlist5<-((peoplefollower.distribhistory at {(reverse(peoplefollower.numvarmap)["speed"]) as int,cycle-1}) as list);			
		}
		if (cycle=0) 
		{
			testlist<-[0,0,0];
			
		}
		//write ""+reverse(peoplefollower.numvarmap)["speed"]; 
		write ""+((peoplefollower.distribhistory at {(reverse(peoplefollower.numvarmap)["cestmoi"]) as int,cycle-1}) );   
//		write ""+(((peoplefollower.distribhistory at {(reverse(peoplefollower.numvarmap)["cestmoi"]) as int,cycle-1}) as list) at 0);
	}
}

species agentfollower parent:agent_group_follower;


experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS' ;
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	parameter 'Number of people agents' var: nb_people category: 'People' ;
	parameter 'Earliest hour to start work' var: min_work_start category: 'People' ;
	parameter 'Latest hour to start work' var: max_work_start category: 'People' ;
	parameter 'Earliest hour to end work' var: min_work_end category: 'People' ;
	parameter 'Latest hour to end work' var: max_work_end category: 'People' ;
	parameter 'minimal speed' var: min_speed category: 'People' ;
	parameter 'maximal speed' var: max_speed category: 'People' ;
	parameter 'Value of destruction when a people agent takes a road' var: destroy category: 'Road' ;
	parameter 'Number of steps between two road repairs' var: repair_time category: 'Road' ;
	
	output {
		display city_display refresh_every: 1 {
			species building aspect: base ;
			species road aspect: base ;
			species people aspect: base ;
		}
		display chart_display refresh_every: 10 { 
			chart name: 'Road Status' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
				data name:'Mean road destruction' value: mean (road collect each.destruction_coeff) style: line color: rgb('green') ;
				data name:'Max road destruction' value: road max_of each.destruction_coeff style: line color: rgb('red') ;
			}
			chart name: 'People follower' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
		//		data name:'people nb' value: ((peoplefollower.metadatahistory at {7,cycle}) as int) style: line color: rgb('green') ;
			}
		}
			display chart_displayHisto refresh_every: 1 { 
			chart name: 'Average tout st' type: histogram style: stack{
				datalist value: (testlist)  style:stack;
//				datalist value: (testlist) categoriesnames:(testlist2) style:stack;
//				datalist value: (testlist) categoriesnames:testlist6 style:stack;
			}


//			display chart_display4 refresh_every: 1 { 
//			chart name: 'Average tout st' type: histogram style: stack{
//				datalist value: (testlist) categoriesnames:(testlist2) style:stack;
////				datalist value: (testlist) categoriesnames:testlist6 style:stack;
//			}
			
		}
	}
}