model tutorial_gis_city_traffic

import 'Referencebuilding.gaml'

global {
		
	//Layout Parameter
	
	//Building
	float abstractBuildingWorkPos <-world.shape.width*0.1;
	float abstractBuildingHomePos <-world.shape.width*0.2;
	float buildingWorkInterval <-100.0;
	float buildingHomeInterval <-5.0;
	
	//Road
	float abstractRoadPos <-world.shape.width*0.3;
	
	//People
	float abstractSPos <-world.shape.width*0.8;
	float abstractIPos <-world.shape.width*0.9;
	float abstractRPos <-world.shape.width*1.0;
	
	float peopleInterval <-5.0;
	
	init {
		
		

	}

	
	reflex cleanMirrorsWithDeadTarget{
		ask abstractPeople{
			if(dead(target)){
				do die;
			}
		} 
	}
	//////////////////////////LAYOUT INIT//////////////////////////////////////////
	reflex InitAbstractBuildingLayout when: time = 1{
		int curIndus<-0;
		int curRes <-0;
		ask abstractBuilding{	
			if(building(target).type = "Industrial"){
				location <-{abstractBuildingWorkPos ,world.shape.height - curIndus*buildingWorkInterval};
				curIndus<-curIndus+1;
			}
			else{
				location <-{abstractBuildingHomePos ,world.shape.height - curRes*buildingHomeInterval};
				curRes<-curRes+1;
			}	
		}		
	}
	
	reflex InitAbstractRoadLayout when: time = 1{
		int curRoad<-0;
		ask abstractRoad{	
		  shape<-line([{abstractRoadPos+curRoad*3,world.shape.height},{abstractRoadPos+curRoad*3,(world.shape.height-road(target).shape.perimeter)}]);
		  curRoad<-curRoad+1;
		}		
	}
	
	reflex InitAbstractPeopleLayout{
		int curS<-0;
		int curI<-0;
		int curR<-0;
		
		ask abstractPeople{			
			if (people(target).is_susceptible = true){
				location <-{abstractSPos ,world.shape.height - curS*peopleInterval};
				curS<-curS+1;
			}	
				
			if(people(target).is_infected = true){
				location <-{abstractIPos ,world.shape.height - curI*peopleInterval};
				curI<-curI+1;
			}
			
			if(people(target).is_immune =true){
				location <-{abstractRPos ,world.shape.height - curR*peopleInterval};
				curR<-curR+1;
			}
			
		}		
	}	

}
entities {


	species abstractBuilding mirrors: list(building) {
	list<abstractBuilding> neigbhours update: abstractBuilding where ((each.location distance_to location < 200) and (location.y > each.location.y) and (location.x = each.location.x));
	abstractBuilding upper_cell update: neigbhours with_min_of (location distance_to each.location);
	float my_area <- building(target).shape.area; 


 	bool is_satisfied <- true update: not ((upper_cell != nil) and ( my_area< upper_cell.my_area) and (time >1));

    reflex upperSwap when: !is_satisfied{
		point tmp1Loc <- location;
		location <- upper_cell.location;
		upper_cell.location <- tmp1Loc;
	}	
	
	  aspect abstract {
	  	draw building(target).shape  color: building(target).color border: °black  at: location;	
	  }
	  
	  aspect area {	
	  	draw circle(my_area / 100)  color: rgb(rnd(255),rnd(255),rnd(255))  at: location;	
	  }

   }
	

	species abstractRoad mirrors: list(road) {	
	  point ref_point <- shape.points[0] update: shape.points[0];
	  list<abstractRoad> neigbhours update: abstractRoad where ((each.ref_point distance_to ref_point < 400) and (location.x > each.location.x));
	  abstractRoad side_cell update: neigbhours with_min_of (ref_point distance_to each.ref_point);
	  float my_length <- road(target).shape.perimeter; 


 	  bool is_satisfied <- true update: not ((side_cell != nil) and ( my_length< side_cell.my_length) and (time >1));

      reflex sideSwap when: !is_satisfied{
		float tmp1Locx <- location.x;
		location <- {side_cell.location.x,location.y};
		side_cell.location <- {tmp1Locx,side_cell.location.y};
	  }	
		
	  aspect abstract {
	    draw shape  color: road(target).color border: °black  at: location;	  	
	  }
	}
	
	species abstractPeople mirrors: list(people) {
	  aspect abstract {
	    draw people(target).shape  color: people(target).color border: °black  at: location;	
	  	
	  }
	}
	

	

}

experiment road_trafficview type: gui {	
	output {
		display city_display type:opengl focus:focusOnShape{
			species abstractBuilding aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
			species abstractRoad aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
		    species abstractPeople aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };		    	
		}	
	}
}

