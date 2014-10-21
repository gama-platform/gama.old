/**
 *  Hanoi
 *  Author: Philippe
 *  Description: 
 */

model Hanoi

global
{
	int sizeblock<-5;		
	table thetable;
	//list<block> theblocks;
	init
	{
		create table
		{
			shape <- line([{25,50.0},{75,50.0}]);
			thetable<-self;
		}
		int cpt<-0;
		create block number:5
		{
			no<-cpt;
			cpt<-cpt+1;
			 onwhat<-thetable;
			color<-rgb(cpt*(255/6),cpt*(255/6),255-cpt*(255/6));
			 
			//add self to:theblocks;
		}
		
		block[1].onwhat <- block[0];
		block[2].onwhat <- block[1];
		block[4].onwhat <- block[3];
//		block[3].onwhat <- block[5];
		
		ask block where (each.onwhat != thetable) {location <- myself.location;}
		/*(theblocks[1] as block).onwhat<-block[0];
		(theblocks[2] as block).onwhat<-block[1];
		(theblocks[4] as block).onwhat<-block[3];
		*/
		ask block{do mise_aj_position;}

		

		create hanoiphilosopher
		{
			set location<-{2,75}; 
			map d1<-new_predicate(name:"onblock",value:true,parameters:["over"::block[0],"under"::block[1]],priority:100);
			map d2<-new_predicate(name:"onblock",value:true,parameters:["over"::block[1],"under"::block[2]],priority:100);
			map d3<-new_predicate(name:"onblock",value:true,parameters:["over"::block[2],"under"::block[3]],priority:100);			
			map d4<-new_predicate(name:"onblock",value:true,parameters:["over"::block[3],"under"::block[4]],priority:100);
			map da12<-new_predicate(name:"anddesire",value:true,parameters:["first"::d1,"second"::d2],priority:100);
			map da34<-new_predicate(name:"anddesire",value:true,parameters:["first"::d3,"second"::d4],priority:100);
			map masterplan<-new_predicate(name:"anddesire",value:true,parameters:["first"::da12,"second"::da34],priority:100);
			do add_desire(predicate:masterplan); 
		}
	}
	
}

entities
{
	species table
	{
		geometry shape_display;
		aspect default
		{
			//geometry theline<-polyline([{0,100},{(10*sizeblock),100}]);
			draw shape + 2.0 color:°blue;
		}
	}
	
	species block
	{
		rgb color;
		geometry shape <- cube(sizeblock);
		int no;
		/*reflex mise_en_position {
			do position;
		}*/
		action mise_aj_position
		{
			if (onwhat=thetable)
			{
				//location<-{sizeblock*(no+1),100-sizeblock};
				geometry place_libre <- copy(thetable.shape);
				ask (block where (each.onwhat = thetable)){
					place_libre <- place_libre - (shape + sizeblock) ;
				}
				location <- any_location_in (place_libre) - {0.0, sizeblock/2.0};
			} else 
			//if (onwhat!=thetable)
			{
				location<-{onwhat.location.x,onwhat.location.y,onwhat.location.z+sizeblock};
			}
			
		}
		agent onwhat;
		aspect default
		{
			draw shape;
			draw string(""+no) size:sizeblock color:°purple;
		}
	}
	
	species hanoiphilosopher control:simple_bdi {
	
	aspect default
	{
		draw circle(1) color:rgb("white");
		draw text:(thinking as string) size:5 color:rgb("red");
		write("B:"+length(belief_base)+":"+belief_base);			
		write("D:"+length(desire_base)+":"+desire_base);			
		write("I:"+length(intension_base)+":"+intension_base);			
		write("G:"+get_current_goal());			
		
	}
	
	action move(block over,agent under)
	{
		write("move "+over+ " on "+under+" from "+over.onwhat);
		over.onwhat<-under;
		ask over
		{
			do mise_aj_position;
		}
	}
	
	bool testgoal(map goal)
	{
		bool res<-false;

		string goaltype<-goal at "name";
		string objvalue<-goal at "value";
		map goalparams<-((goal at "parameters") as map);
		
		if (goaltype="anddesire")
		{
		map subdesire1<-goalparams at "first";
		map subdesire2<-goalparams at "second";
//		list subdesire1<-eval_gaml((goalparams at "first") as string);
//		list subdesire2<-eval_gaml((goalparams at "second") as string);
		if (testgoal(subdesire1) and testgoal(subdesire2)) 
			{
			return true;
			}			
		}

		if (goaltype="onblock")
		{
		block overblock<-goalparams at "over";
		block underblock<-goalparams at "under";
		if (overblock.onwhat=underblock) 
			{
			return true;
			}			
		}
		if (goaltype="free")
		{
		block underblock<-goalparams at "block";
		if (length(block where (each.onwhat=underblock))>0)
			{
				return true;
			
			}
			
		}
		return res;
	}

	plan anddesire when:is_current_goal(new_predicate(name::"anddesire")) priority:3 executed_when:true
	{
			map currentgoal<-get_current_goal();
		write("and "+currentgoal);
		map goalparams<-((currentgoal at "parameters") as map);
		map subdesire1<-goalparams at "first";
		map subdesire2<-goalparams at "second";
//		list subdesire1<-eval_gaml((goalparams at "first") as string);
//		list subdesire2<-eval_gaml((goalparams at "second") as string);
		write("s1 "+subdesire1);
		if (testgoal(currentgoal))
		{
		write("AND REMOVED!"+currentgoal);
//			do remove_intention(currentgoal);
//			do remove_intention with: currentgoal;
//			do remove_desire with: currentgoal;
			do remove_intention(currentgoal,true);
		}
		else
		{
			if (!testgoal(subdesire1))
			{
				do add_subgoal(currentgoal,subdesire1);
//				do add_desire with:currentgoal+["todo"::currentgoal];							
//				subdesire1<-subdesire1+["todo"::currentgoal];
				do add_desire(subdesire1);							
			}
			if (!testgoal(subdesire2))
			{
				do add_subgoal(currentgoal,subdesire2);
//				subdesire2<-subdesire2+["todo"::currentgoal];
				do add_desire(subdesire2);							
			}
				do currentgoal_on_hold();
		}
		
	}

	
	plan onblock when:is_current_goal(new_predicate(name::"onblock")) priority:2 executed_when:true
	{
		map currentgoal<-get_current_goal();
		map goalparams<-((currentgoal at "parameters") as map);
		block overblock<-goalparams at "over";
		block underblock<-goalparams at "under"; 
		if (overblock.onwhat=underblock) 
		{
			do remove_intention(currentgoal,true);
		}
		else
		{
			if (length(block where (each.onwhat=underblock))>0)
			{
				do add_desire(new_predicate(name::"free",value::true,priority::100,parameters::["block"::underblock]),currentgoal);							
				do currentgoal_on_hold();
			}
			if (length(block where (each.onwhat=overblock))>0)
			{
				do add_desire(new_predicate(name::"free",value::true,priority::100,parameters::["block"::overblock]),currentgoal);							
				do currentgoal_on_hold();
			}
			if ((length(block where (each.onwhat=underblock))=0) and (length(block where (each.onwhat=overblock))=0))
			{
				do move(overblock,underblock);							
			}
		}
//		write("vals "+overblock+ " on "+underblock+" from "+overblock.onwhat+"/"+(length(block where (each.onwhat=underblock)))+"/"+(length(block where (each.onwhat=underblock))));
		
	}
	plan freeblock when:is_current_goal(new_predicate(name::"free")) priority:2 executed_when:true
	{
		map currentgoal<-get_current_goal();
		map goalparams<-((currentgoal at "parameters") as map);
		block underblock<-goalparams at "block";
		if (length(block where (each.onwhat=underblock))=0)
		{
			do remove_intention(currentgoal,true);
		}
		else
		{
			block overblock<-any(block where (each.onwhat=underblock));
							
			if (length(block where (each.onwhat=overblock))>0)
			{
				do add_desire(new_predicate(name::"free",value::true,priority::100,parameters::["block"::overblock]),currentgoal);							
				do currentgoal_on_hold();
			}
			else
			{
				do move(overblock,thetable);							
			}
		}
		
	}
	
}
	
	
}

experiment bditest type:gui
{
	output 
	{
		
		display oi type:opengl
			camera_pos: {50,-120,70}
//			camera_look_pos:{50,50,0} 	
			camera_up_vector:{0.0,1.0,0.0}		
			{
			species table refresh: false;
			species block;
			species hanoiphilosopher;
		}
	}
	
}
/* Insert your model definition here */

