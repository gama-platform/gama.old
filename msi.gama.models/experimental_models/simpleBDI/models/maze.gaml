/**
 *  newbdi
 *  Author: Truong Chi Quang
 *  Description: 
 */

model maze

/* Insert your model definition here */

global{
	int dimensions <- 40 max: 400 min: 10 parameter: "Width and height of the maze:" category: "Environment";
	var perception_distance type: int parameter: 'Distance de perception : ' init: 15 min: 1 category: 'Robot';
	file<int> labfile <- csv_file("../images/lab3.csv",";") parameter: "Name of image csv to load:" category: "Environment" ;
	int wallpngcolor<- 1;
	int keypngcolor<- 3;
	int monsterpngcolor<- 2;
	int doorpngcolor<- 4;
	int adventurercolor<- 20;
		
	
	float sizecell<-world.shape.width/dimensions;
	init{
		do initmaze;
	}
	
	action initmaze 
	{
		loop i from:0 to:dimensions-1
		{
		loop j from:0 to:dimensions-1
		{
			if (wallpngcolor=labfile[i,j])
			{
				create wall
				{
					set location<-(cell[i,j]).location;	
					set (cell[i,j] as cell).haswall<-true;				
				}	
			}
			if (keypngcolor=labfile[i,j])
			{
				create key
				{
					set location<-(cell[i,j]).location;	
//					set (cell[i,j] as cell).haswall<-true;				
				}	
			}
			if (monsterpngcolor=labfile[i,j])
			{
				/*create Monster
				{
					speed<-0.4;
					shape<-circle(1);
					percept_dist<-2;
					set location<-(cell[i,j]).location;	
//					set (cell[i,j] as cell).haswall<-true;				
				}*/	
			}
			if (doorpngcolor=labfile[i,j])
			{
				create door
				{
					set location<-(cell[i,j]).location;	
					set (cell[i,j] as cell).haswall<-true;				
				}	
			}
			if (adventurercolor=labfile[i,j])
			{
				create Adventurer
				{
					speed<-0.5;
					shape<-circle(1);
					plan_persistence <- 1.0;
					intention_persistence <- 1.0;
					set location<-(cell[i,j]).location;					
					my_cell<-cell[i,j];
					write("new adventurer at"+i+j+" loc "+(cell at {i,j}).location);
					do add_belief(new_predicate("be_rich",false));

				}	
			}
		}
			
		}
		
		
	}
	
}

grid cell width: dimensions height: dimensions neighbours: 4 {
	list<cell> neighbours <- self neighbours_at 1;
	list<cell> neighboursPercept <- self neighbours_at perception_distance;
	bool haswall<-false;
}

species wall {
	string typecell<-"vide"; 
	rgb color;
  	aspect default
	{
		color<-rgb("red");						
		draw square(sizecell/2);
	}
	
	
}

species door {
	string typecell<-"vide"; 
	rgb color;
  	aspect default
	{
		color<-rgb("brown");						
		draw circle(sizecell/2);
	}
	
	
}

species key {
	string typecell<-"vide"; 
	rgb color;
  	aspect default
	{
		color<-rgb("yellow");						
		draw circle(sizecell/2);
	}	
}

species PerceiveAgent skills:[moving] 
{

		var my_cell type: cell;
		var chemin type: list of: cell init: [ ];
		var cible type: cell init: nil;
		var zone_libre type: list<cell> init: [ ];
		var zone_exploree type: list<cell> init: [ ];
		var frontiere type: list<cell> init: [ ];
		var candidats type: list<cell> init: [ ];
		var percept_dist type: int value: perception_distance;
		path mycurrentpath;
		graph mygraph;
	
		action perception_reelle {
			let perception type: list of: cell value: [ ];
			add to: perception item: my_cell;
			let cpt type: int value: 0;
			let neighb type: list of: cell value: ( my_cell.neighbours );
			let est_fini type: bool value: false;
			let obstacles type: list of: cell value: ( my_cell.neighboursPercept) where ( each.haswall );
			loop while: ! ( est_fini ) {
				set cpt value: cpt + 1 ;
				let neighb2 type: list of: cell value: [ ];
				let cel type: cell value: nil;
				loop cel over: neighb {
					if condition: ! ( cel.haswall ) {
						let vois type: list of: cell value: ( cel.neighbours) where ( !
						( each in neighb ) and ! ( each in neighb2 ) );
						loop cel_vois over: vois {
							let est_visible type: bool value: true;
							let ligne type: geometry value: line ( [ cel_vois . location , my_cell .
							location ] );
							loop obst over: obstacles {
								if condition: ( est_visible ) and ( ligne overlaps obst . shape ) {
									set est_visible value: false ;
								}
							}
							if condition: ( est_visible ) {
								add item: cel_vois to: neighb2;
							}
						}
					}
				}
				set neighb value: neighb2 ; set perception value: perception union neighb ;
				if condition: ( empty ( neighb ) ) or ( cpt = percept_dist ) {
					set est_fini value: true ;
				}
			}
			return value: perception;
		}
	
	
}

species Monster skills:[moving] parent:PerceiveAgent
{

	reflex seekadventurer
	{
		my_cell<-first(cell overlapping self);
		if length(Adventurer overlapping my_cell)>0
		{
			ask (any(Adventurer overlapping self))
			{
				write("Adventurer savagely eaten by nice monster : " +self);
				do die;
			}
		}
		
		cell mytarget<-any(my_cell.neighbours);
		list<cell> perc<-perception_reelle();
		loop ad over:perc
		{
			if length(Adventurer overlapping ad)>0
			{
				mytarget<-ad;
			}
		}
		
		if (!mytarget.haswall)
		{
			do goto(mytarget);
		}
		my_cell<-first(cell overlapping self);
		if length(Adventurer overlapping my_cell)>0
		{
			ask (any(Adventurer overlapping self))
			{
				write("Adventurer savagely eaten by nice monster : " +self);
				do die;
			}
		}
	}
	
}


species Adventurer skills:[moving] control:simple_bdi parent:PerceiveAgent{
	
	list myitems<-[];
	
	reflex myperceive
	{
		my_cell<-first(cell overlapping self);
		do add_belief(new_predicate("explored","true",["cell"::my_cell],1));
		add my_cell to:zone_exploree;
		if (!(zone_libre contains my_cell))
		{
			add my_cell to:zone_libre;
			add my_cell to:zone_exploree;
		}
		if ((candidats contains my_cell))
		{
			remove my_cell from:candidats;
		}
		list<cell> vision<-perception_reelle() as list<cell>;
		list<cell> visiontotest<-[];
		loop c over:vision
		{
			c.color<-rgb("blue");
			if (length(key overlapping c)>0)
			{
				do add_belief(new_predicate("key",true,["key"::first(key overlapping c),"at"::c],1));											
				do add_desire(new_predicate("take",true,["what"::first(key overlapping c),"at"::c],10));							
			}
			if (length(door overlapping c)>0)
			{
				do add_belief(new_predicate("door",true,["door"::first(door overlapping c),"at"::c],1));				
				if (length(myitems)>0)
				{
						do add_desire(new_predicate("open",true,["door"::first(door overlapping c),"at"::c],10));							
					
				}
			}
			if (length(Monster overlapping c)>0)
			{
				do add_belief(new_predicate("flee",false,["monster"::first(Monster overlapping c)],1));				
				do add_belief(new_predicate("monster",c,["monster"::first(Monster overlapping c)],1));				
				do add_desire(new_predicate("flee",true,["monster"::first(Monster overlapping c)],1000));							
			}


		if (!(zone_exploree contains c))
		{
			add c to:visiontotest;
			if (c.haswall=true)
			{
				add c to:zone_exploree;
			}
			if (c.haswall=false)
			{
				add c to:zone_exploree;
				add c to:zone_libre;
			}
		}	
		}
		mygraph<-grid_cells_to_graph(zone_libre);
		if (length(visiontotest)>0)
		{
		loop c over:visiontotest
		{
			if (c.haswall=false)
			{
				bool inc<-false;
				loop cand over:c.neighbours
				{
					if !(zone_exploree contains cand)
					{
						path mypath<-path_between(mygraph,my_cell,c);
						
						if (mypath!=nil)
						{
							inc<-true;
						}
					}
				}
				if (inc)
				{
					add c to:candidats;
					do add_desire(new_predicate("explored",true,["cell"::c],1));							
					
				}
			}
			
		}
			
		}
	}

	plan be_rich when:is_current_intention(new_predicate("be_rich",true)) priority:3    {
		predicate p <-new_predicate("find_treasure",true,nil,100);
		do add_desire(p);
		do add_subintention(get_current_intention(),p);
		do current_intention_on_hold();
//		write "plan 1 "+" current goal: "+get_current_intention();
	}

	plan find_treasure when:is_current_intention(new_predicate("find_treasure",true)) priority:2  {
		do add_desire(new_predicate("everything_explored",true,nil,100));
		do current_intention_on_hold();
	}		
		

	plan explore  priority:2 when:is_current_intention(new_predicate("explored")) finished_when:false {
		predicate currentgoal<-get_current_intention();
		map goalparams<-currentgoal.parameters;
		cell target<-goalparams at "cell";
		mycurrentpath<-path_between(mygraph,my_cell,target);
		target.color<-rgb("green");
		write("plan: goto "+target+" via "+mycurrentpath);
		do follow(path::mycurrentpath);
	}

	plan plantake  priority:2 when:is_current_intention(new_predicate("take")) finished_when:false {
//		write "exploring ... "+get_current_intention();
		predicate currentgoal<-get_current_intention();
		map goalparams<-currentgoal.parameters;
		cell target<-goalparams at "at";
		agent something<-goalparams at "what";
		if (target=my_cell)
		{
			add something to:myitems;
			do add_belief(new_predicate("take",true,["what"::something,"at"::target],1));
			ask something
			{
				set location<-{-1,-1};
			}
				if (has_belief("door"))
				{
					list bel<-get_belief("door");
					map belparams<-((bel at "parameters") as map);
					cell target<-belparams at "at";
					agent something<-belparams at "door";
					do add_desire(new_predicate("open",true,["door"::something,"at"::target],10));							
					
				}
			
		}
		else
		{
		mycurrentpath<-path_between(mygraph,my_cell,target);
		do follow(path::mycurrentpath);			
		}
	}

	plan planopen  priority:2 when:is_current_intention(new_predicate("open")) finished_when:false {
//		write "exploring ... "+get_current_intention();
		predicate currentgoal<-get_current_intention();
		map goalparams<-currentgoal.parameters;
		cell targetc<-goalparams at "at";
		agent something<-goalparams at "door";
		if (my_cell.neighbours contains targetc)
		{
//			add something to:myitems;
			do add_belief(new_predicate("open",true,["door"::something,"at"::targetc],1));
			write("at the door "+something);
			ask something
			{
				do die;
			}
			add targetc to:zone_libre;
			ask targetc
			{
				haswall<-false;
			}
		}
		else
		{
			cell nearcell<-any(targetc.neighbours inter zone_libre);
			mycurrentpath<-path_between(mygraph,my_cell,nearcell);
			do follow(path::mycurrentpath);			
			write("plan: goto "+nearcell+" via "+mycurrentpath);
		}
//		target.color<-rgb("green");
//		write("plan: goto "+target+" via "+mycurrentpath);
	}

	plan planflee  priority:2 when:is_current_intention(new_predicate("flee")) finished_when:false {
		predicate currentgoal<-get_current_intention();
		map goalparams<-currentgoal.parameters;
		Monster target<-goalparams at "monster";
		write("dist "+self distance_to target);
			list<cell> possiblecells<-my_cell.neighbours inter zone_libre;
			cell fleetarget<-(possiblecells with_max_of (each distance_to target));			
			if fleetarget!=nil
			{
				mycurrentpath<-path_between(mygraph,my_cell,fleetarget);
//				fleetarget.color<-rgb("green");
				do follow(path::mycurrentpath);
			write("to "+fleetarget);
				
			}
		if ((self distance_to target)>(percept_dist+1))
		{
		do add_belief(new_predicate("flee",true,["monster"::target],1));
		}
		else
		{
		}
	}

	plan plan3  priority:1 finished_when:true   {
		do add_desire(new_predicate("be_rich",true,nil,100));
	}
	aspect path
	{
	//		graphics "le graphe" 
		if (mycurrentpath!=nil)
		{
		draw mycurrentpath.shape;
		}
		if (mygraph!=nil)
		{
		draw mycurrentpath.shape;
//		loop ed over: mygraph.edges {
//			draw ed color: °pink;
//		}
			
		}
		draw circle(1) color:rgb("white");
		if ((thinking!=nil) and (length(thinking)>0))
		{
	//		draw "toto" size:10 color:°yellow;
		draw text:(thinking as string) size:2 color:rgb("yellow");
		write(thinking);			
		}
		
		write ("B:" + length(belief_base) + ":" + belief_base);
			write ("D:" + length(desire_base) + ":" + desire_base);
			write ("I:" + length(intention_base) + ":" + intention_base);
			write ("G:" + get_current_intention());
	}
}


experiment explore type:gui{
	output {
		display map type:opengl{
			grid cell;
			species wall;
			species Adventurer aspect:path; 
			species Monster; 
			species key; 
			species door; 
		} 
	}
} 