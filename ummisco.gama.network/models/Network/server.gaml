/**
* Name: Server Ship
* Author: damienphilippon
* Description: This model must be used with an other instance of Gama having the  User Ship model launched. It shows 
* 	how to use the skill network to produce a client-server architecture. The server model do computations according
*	to the interactions sent by the clients, and send to them the new attributes of the agents. 
* Tags: User Interaction, Network
*/

model server

global
{
	//Id of the bullets that will be incrementally updated when a Bullet is created
	int idBullet<-0;
	//Shapefile of the bounds of the world
	file shp_bounds<-shape_file("./includes/bounds.shp");
	//Shapefile of the recif in the world
	file shp_recif<-shape_file("./includes/recif.shp");
	//Space of the world on which boats can naviguate
	geometry free_space;
	
	//Server agent of the experiment
	NetworkingServer theServer;
	//Wind vector which represent firstly the direction of the wind, then the force of the wind
	point wind<-{rnd(360),rnd(100)};
	
	init
	{
		//The shape of the world is set as the envelope of the bounds contained in the shapefile
		shape<-envelope(shp_bounds);
		//The free space is firstly se to the shape of the world
		free_space<-copy(shape);
		//Create the server agent
		create NetworkingServer number:1
		{
			//Set the name of the agent equals to Server
			name<-"Server";
			//We do the connection to the port to listen for the server, and set the protocol
			do connect to:"localhost" protocol:"tcp_server" port: 3001 with_name:name;
			theServer<-self;
		}
		//We create the recif according to the shapefile of recif
		create Recif from:shp_recif
		{
			//The space of the navigation allowed for the ships is equals to the shape of the world with the shape of recifs removed
			free_space<-free_space-(self.shape*(1.1));
		}	
		//We create then a Bot ship that will not move and will be place randomly among the free space
		create ship number:1
		{
			name<-"bot";
			location<-any_point_in(free_space);
		}
	}
	//Reflex to update the wind randomly but with a certain amplitude
	reflex updateWind
	{
		float valueX <- wind.x+rnd(1)-rnd(1);
		//The bounds of the value of the angle are 0 and 360
		valueX<-valueX>0?float(valueX mod 360):0;
		
		float valueY <- wind.y+rnd(10)-rnd(10);
		//The bounds of the value of the speed are 0 and 100
		valueY<-valueY>0?(valueY>100?100:valueY):0;
		
		wind<-{valueX,valueY};
	}
}

//Species which represent the recif in the worlds
species Recif
{
	//We simply draw the shape of the recif
	aspect default
	{
		draw shape at:{world.shape.width/2,world.shape.height/2,0};
	}
}
//Species which represent the server using the skill network
species NetworkingServer skills:[network]
{
	//Name of the server
	string name;
	//List of the previous Message
	list<string> prevInfoInMess<-nil;
	
	//Reflex to get all the messages and process them
	reflex readMessage
	{
		//Get all the messages
		map mess<-fetch_message();
		
		//If we have message then we can start the processing
		if(mess!=nil and length(mess.pairs)>0)
		{
			//We loop over all the messages to process them one by one
			loop aMess over: mess.pairs
			{

				//We split the message by : to get a list of values for each message
				list<string> infoInMess<-aMess.value[0] split_with ":";
				
				//If there are more than 0 value and it's different from the previous message, we can process it
				if(length(infoInMess)>0)and(infoInMess!=prevInfoInMess)
				{
					//We update the value of the variable prevInfoInMess as this will be the new previous message
					prevInfoInMess<-infoInMess;
					//If the message first value is Connexion, it means it is a new client connecting
					if(infoInMess[0]="Connexion")
					{
						//We create a new ship for the new client and set its location randomly
						string nameShip<- infoInMess[1];
						create ship number:1
						{
							name<-nameShip;
							location<-any_location_in(free_space);
						}
					}
					else
					{
						//Else it must be a Ship sending its interactions, in that case, we just ask the ship to update itself according to the interaction done
						string nameShip <- infoInMess[0];
						ship concernedShip <- first(ship where(each.name=nameShip));
						if(concernedShip!=nil)
						{
							loop info over: infoInMess-nameShip
							{
								ask concernedShip
								{
									do doAction(info);
								}
							}
						}
					}
				}
			}
			
			
		}
	}
	//Reflex to send the message at each step to the clients to update their ships and bullets
	reflex send
	{
		//We build the string that will be sent according to the ships and bullets existing, sending to the clients the same message but only with some attributes importants
		string infoModel <-name+":"+wind.x+":"+wind.y;
		loop aShip over: ship
		{
			infoModel<-infoModel+":ship:"+aShip.name+":"+aShip.location.x+":"+aShip.location.y+":"+aShip.heading+":"+aShip.life+":"+aShip.dead;
		}
		ask ship where(each.dead)
		{
			infoModel<-infoModel+":info:"+self.name+" has been killed by "+self.killer;
			do die;
		}
		loop aBullet over: bullet
		{
			infoModel<-infoModel+":bullet:"+aBullet.idB+":"+aBullet.location.x+":"+aBullet.location.y+":"+aBullet.dead;
		}
		ask bullet where(each.dead)
		{
			do die;
		}
		//In the case we have no ship and bullet, we will not send the message
		if(infoModel!=name+":"+wind.x+":"+wind.y)
		{	
			loop aClient over: network_groups
			{
				do send_message to: aClient content:infoModel;
			}
		}
	}
}
//Species which represent the bullets, using the skill moving
species bullet skills:[moving]
{
	//Id of the bullet
	string idB<-string(idBullet);
	//Ship which has launched the bullet
	ship myShip;
	//Distance maximum reachable by a bullet, after that, it sinks in water
	float maxDistance<-0.0;
	//Distance already done by the bullet
	float distance<-0.0;
	//Location of the bullet
	point location;
	//Damage that will be done by the bullet to a ship
	int damage;
	//Speed of the bullet
	float speed<-10.0;
	//Previous location of the bullet, used to compute trajectory of the bullet
	point oldLocation;
	//Original heading of the bullet
	int headingT;
	//Heading of the bullet
	int heading;
	//Status of the bullet (dead or not)
	bool dead<-false;
	
	//reflex to update the bullet
	reflex update
	{
		//Set the oldlocation as the actual location of the bullet before computation
		oldLocation<-location;
		//Move the bullet with the speed used and following the angle of direction
		do move speed: speed heading: headingT;
		//To be sure that if a bullet collides with the bounds of the world, it will die
		if(heading!=headingT)
		{
			dead<-true;
		}
		//Compute the distance already done by the bullet agent
		distance<-distance+(oldLocation distance_to location);
		//Compute the trajectory of the bullet
		geometry trajectory<-line([location,oldLocation],10);
		//List of all the ships touched by the bullet trajectory
		list<ship> touchedShip <- (ship-myShip) where (each.shape overlaps trajectory);
		//If there are ships touched by the bullet, then we determine the first touched
		if(length(touchedShip)>0)
		{
			write ""+length(touchedShip)+" "+touchedShip closest_to oldLocation;
			ship concernedShip;
			//If there is only one ship on the trajectory, it will be this one that will be touched
			if(length(touchedShip)=1)
			{
				concernedShip<-touchedShip[0];
			}
			//Else, it will be the closest ship to the previous location of the bullet
			else
			{
				concernedShip<-touchedShip closest_to oldLocation;
			}
			//We apply the damage of the bullet on the touched ship
			concernedShip.life<-int(concernedShip.life-damage);
			
			//If the life of the ship is under 0, it will set its status to dead
			if(concernedShip.life<0)
			{
				concernedShip.dead<-true;
				concernedShip.killer<-self.myShip.name;
			}
			//The bullet will now be dead
			dead<-true;
		}
		//If the bullet has reached its maximum distance, it will set its status to dead
		if(distance>=maxDistance)
		{
			dead<-true;
		}
	}
	//Aspect to draw the bullet agent
	aspect default
	{
		draw image_file("./images/bullet.png") at: location size:10#m rotate: heading;
	}
}

//Species that represent the ships using the skill moving
species ship skills:[moving]
{
	//Name of the ship, used as ID
	string name;
	//Location of the ship
	point location;
	//Rotation maximum done by a ship when it turns
	int rotation_max<-3;
	//Initial Life of the ship
	int life<-100;
	//Power of the ship, used as the damage done by the bullet
	int powerPuissance<-40;
	//Speed of the ship
	float speed<-5.0;
	//Bounding shape of the ship, used for collision detection with bullets
	geometry shape<-circle(25#m);
	//Last cycle when the ship shot, used to avoid bullet spamming
	int last_cycle_shooting<-0;
	//Status of the ship (dead or not)
	bool dead<-false;
	//Name of the ship responsible for the death of the ship
	string killer;
	
	//Action to process a message to update the ship
	action doAction(string message)
	{
		//If the message is up, will move the boat according to its direction and the wind vector
		if(message="up")
		{
			//Computation of the wind factor on the speed of the ship
			float diff_wind<-abs(heading-wind.x);
			float factor;
			
			if(diff_wind>180)
			{
				factor<-0.3+((((diff_wind-180)/180)*0.7)*(wind.y/100));
			}
			else
			{
				factor<-1.0-(((diff_wind/180)*0.7)*(wind.y/100));
			}
			//Make the ship move on the space naviguable, with the factor of the wind applied on the speed
			do move speed: (speed*2)*factor heading: heading bounds: free_space;
		}
		//If the message is right, will set the direction a little bit on the right and move the boat according to its direction and the wind vector
		if(message="right")
		{
			self.heading<-self.heading+rotation_max;
			//Computation of the wind factor on the speed of the ship
			float diff_wind<-abs(heading-wind.x);
			float factor;
			
			if(diff_wind>180)
			{
				factor<-0.3+((((diff_wind-180)/180)*0.7)*(wind.y/100));
			}
			else
			{
				factor<-1.0-(((diff_wind/180)*0.7)*(wind.y/100));
			}
			//Make the ship move on the space naviguable, with the factor of the wind applied on the speed
			do move speed: speed*factor heading: heading bounds: free_space;
		}
		//If the message is left, will set the direction a little bit on the left and move the boat according to its direction and the wind vector
		if(message="left")
		{
			self.heading<-self.heading-rotation_max;
			//Computation of the wind factor on the speed of the ship
			float diff_wind<-abs(heading-wind.x);
			float factor;
			
			if(diff_wind>180)
			{
				factor<-0.3+((((diff_wind-180)/180)*0.7)*(wind.y/100));
			}
			else
			{
				factor<-1.0-(((diff_wind/180)*0.7)*(wind.y/100));
			}
			
			//Make the ship move on the space naviguable, with the factor of the wind applied on the speed
			do move speed: speed*factor heading: heading bounds: free_space;
		}
		
		//if the message is bulletleft, will make the ship shoot a bullet on the left part of the ship
		if(message="bulletleft")
		{
			//Will allow the shoot only if the step between the cycle and the last shooting cycle is equals to 1.5
			if(cycle-last_cycle_shooting>15)
			{
				//Update the last cycle shooting to the current cycle
				last_cycle_shooting<-cycle;
				//Create a bullet and set its attribute according to the direction and the ship responsible of the launch
				create bullet number:1
				{
					myShip<-myself;
					write myShip;
					maxDistance<-300.0;
					distance<-0.0;
					headingT<-myShip.heading+270;
					if(headingT>360)
					{
						headingT<-headingT-360;
					}
					location<-myShip.location;
					idBullet<-idBullet+1;
					damage<-myShip.powerPuissance;
				}
			}
		}
		//if the message is bulletleft, will make the ship shoot a bullet on the left part of the ship
		if(message="bulletright")
		{
			if(cycle-last_cycle_shooting>15)
			{
				//Update the last cycle shooting to the current cycle
				last_cycle_shooting<-cycle;
				//Create a bullet and set its attribute according to the direction and the ship responsible of the launch
				create bullet number:1
				{
					myShip<-myself;
					maxDistance<-300.0;
					distance<-0.0;
					headingT<-myShip.heading+90;
					if(headingT>360)
					{
						headingT<-headingT-360;
					}
					location<-myShip.location;
					idBullet<-idBullet+1;
					damage<-myShip.powerPuissance;
				}
			}
		}
	}
	//Aspect to draw the ship using the image, its life and its name
	aspect default
	{
		draw image_file("./images/user.png") at: location size:50#m rotate: heading;
		draw rectangle(life/2,2) color:#red rotate:heading+90;
		draw name at:location color:#silver;
	}
}
//Experiment to launch the server
experiment launchServer type: gui
{
	float minimum_cycle_duration<-0.10#s;
	output
	{
		display info type:java2D 
		{
			image "./images/mer.jpg";
			graphics "wind_layer"
			{
				draw image_file("./images/arrow.png") size:100 rotate:(wind.x+180)mod 360 at: {shape.width-500, 50};
				draw rectangle((wind.y/100)*200,60) color:wind.y>50?rgb(255,125-(((wind.y-50)/50)*125),0):rgb(255*(wind.y/50),255-((wind.y/50)*125),0) at: {(shape.width-400)+((wind.y/100)*100),50};
			}
			//species Recif aspect:default;
			species ship aspect: default;
			species bullet aspect: default;
			
		}
	}
}