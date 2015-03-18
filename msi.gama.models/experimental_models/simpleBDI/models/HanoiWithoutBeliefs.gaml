/**
 *  Hanoi
 *  Author: Philippe et Patrick
 *  Description: 
 */
model Hanoi


global
{
	int sizeblock <- 5;
	table thetable;
	init
	{
		create table
		{
			shape <- line([{ 25, 50.0 }, { 75, 50.0 }]);
			thetable <- self;
		}

		int cpt <- 0;
		create block number: 5
		{
			no <- cpt;
			cpt <- cpt + 1;
			onwhat <- thetable;
			color <- rgb(cpt * (255 / 6), cpt * (255 / 6), 255 - cpt * (255 / 6));
		}

		block[1].onwhat <- block[0];
		block[2].onwhat <- block[1];
		block[4].onwhat <- block[3];
		ask block where (each.onwhat != thetable)
		{
			location <- myself.location;
		}

		ask block
		{
			do mise_aj_position;
		}

		create hanoiphilosopher
		{
			location <- { 2, 75 };
			predicate d1 <- new_predicate("onblock", true, ["over"::block[0], "under"::block[1]], 100);
			predicate d2 <- new_predicate("onblock", true, ["over"::block[1], "under"::block[2]], 100);
			predicate d3 <- new_predicate("onblock", true, ["over"::block[2], "under"::block[3]], 100);
			predicate d4 <- new_predicate("onblock", true, ["over"::block[3], "under"::block[4]], 100);
			predicate da12 <- new_predicate("anddesire", true, ["first"::d1, "second"::d2], 100);
			predicate da34 <- new_predicate("anddesire", true, ["first"::d3, "second"::d4], 100);
			predicate masterplan <- new_predicate("anddesire", true, ["first"::da12, "second"::da34], 100);
			do add_desire(masterplan);
			isPlanning<-false;
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
			draw shape + 2.0 color: # blue;
		}

	}

	species block
	{
		rgb color;
		geometry shape <- cube(sizeblock);
		int no;
		action mise_aj_position
		{
			if (onwhat = thetable)
			{
				geometry place_libre <- copy(thetable.shape);
				ask (block where (each.onwhat = thetable))
				{
					place_libre <- place_libre - (shape + sizeblock);
				}

				location <- any_location_in(place_libre) - { 0.0, sizeblock / 2.0 };
			} else
			{
				location <- { onwhat.location.x, onwhat.location.y, onwhat.location.z + sizeblock };
			}

		}

		agent onwhat;
		aspect default
		{
			draw shape;
			draw string("" + no) size: sizeblock color: °purple;
		}

	}

	species hanoiphilosopher control: simple_bdi
	{
		bool isPlanning;

		aspect default
		{
			draw circle(1) color: #white;
			draw (thinking as string) size: 5 color: #red;
			write ("B:" + length(belief_base) + ":" + belief_base);
			write ("D:" + length(desire_base) + ":" + desire_base);
			write ("I:" + length(intention_base) + ":" + intention_base);
			write ("G:" + get_current_intention());
		
			draw circle(0.5) color: isPlanning ? #red : #green;	
			
			
		}

		action move (block over, agent under)
		{   isPlanning<-false;
			write ("move " + over + " on " + under + " from " + over.onwhat);
			over.onwhat <- under;
			ask over
			{
				do mise_aj_position;
			}

		}

		bool testgoal (predicate goal)
		{
			bool res <- false;
			string goaltype <- goal.name;
			string objvalue <- string(goal.value);
			map goalparams <- goal.parameters;
			if (goaltype = "anddesire")
			{
				predicate subdesire1 <- predicate(goalparams["first"]);
				predicate subdesire2 <- predicate(goalparams["second"]);
				if (testgoal(subdesire1) and testgoal(subdesire2))
				{
					return true;
				}

			}

			if (goaltype = "onblock")
			{
				block overblock <- block(goalparams at "over");
				block underblock <- block(goalparams at "under");
				if (overblock.onwhat = underblock)
				{
					return true;
				}

			}

			if (goaltype = "free")
			{
				block underblock <- block(goalparams at "block");
				if (length(block where (each.onwhat = underblock)) > 0)
				{
					return true;
				}

			}

			return res;
		}

		plan anddesire when: is_current_intention(new_predicate("anddesire")) priority: 3 finished_when: true
		{  isPlanning<-true;
			predicate currentgoal <- get_current_intention();
			map goalparams <- currentgoal.parameters;
			predicate subdesire1 <- predicate(goalparams at "first");
			predicate subdesire2 <- predicate(goalparams at "second");
			
			if (testgoal(currentgoal))
			{
				write ("AND REMOVED!" + currentgoal);
				do remove_intention(currentgoal, true);
			} else
			{
				if (!testgoal(subdesire1))
				{
					do add_subintention(currentgoal, subdesire1);
					do add_desire(subdesire1);
				}

				if (!testgoal(subdesire2))
				{
					do add_subintention(currentgoal, subdesire2);
					do add_desire(subdesire2);
				}

				do current_intention_on_hold();
			}

		}

		plan onblock when: is_current_intention(new_predicate("onblock")) priority: 2 finished_when: true
		{   isPlanning<-true;
			predicate currentgoal <- get_current_intention();
			map goalparams <- currentgoal.parameters;
			block overblock <- block(goalparams at "over");
			block underblock <- block(goalparams at "under");
			if (overblock.onwhat = underblock)
			{
				do remove_intention(currentgoal, true);
			} else
			{
				if (length(block where (each.onwhat=underblock))>0)
				{
					do add_desire(new_predicate("free", true, ["block"::underblock], 100), currentgoal);
					do current_intention_on_hold();
				}

				if (length(block where (each.onwhat = overblock)) > 0)
				{
					do add_desire(new_predicate("free", true, ["block"::overblock], 100), currentgoal);
					do current_intention_on_hold();
				}

				if ((length(block where (each.onwhat = underblock)) = 0) and (length(block where (each.onwhat = overblock)) = 0))
				{
					do move(overblock, underblock);
				}

			}

		}

		plan freeblock when: is_current_intention(new_predicate("free")) priority: 2 finished_when: true
		{   isPlanning<-true;
			predicate currentgoal <- get_current_intention();
			map goalparams <- currentgoal.parameters;
			block underblock <- block(goalparams at "block");
			if (length(block where (each.onwhat = underblock)) = 0)
			{
				do remove_intention(currentgoal, true);
			} else
			{
				block overblock <- any(block where (each.onwhat = underblock));
				if (length(block where (each.onwhat = overblock)) > 0)
				{
					do add_desire(new_predicate("free", true, ["block"::overblock], 100), currentgoal);
					do current_intention_on_hold();
				} else
				{
					do move(overblock, thetable);
				}

			}

		}

	}

}

experiment bditest type: gui
{
	output
	{
		display oi type: opengl// camera_pos: { 50, -120, 70 } camera_up_vector: { 0.0, 1.0, 0.0 }
		{
			species table refresh: false;
			species block;
			species hanoiphilosopher;
		}

	}

}

