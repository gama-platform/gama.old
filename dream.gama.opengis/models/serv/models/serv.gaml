/**
* Name: serv
* Author: damienphilippon
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model serv

/* Insert your model definition here */

global
{
	int pp<-3001;
	init 
	{
		create server number:1
		{
			////write pp;
			port<-pp;
			do open_socket;
		}
		
//		create server number:1
//		{
//			port<-3003;
//			do open_socket;
//		}
	}
}

species server skills: [socket]
{
	string msg;
	action listening_client(string id)
	{
//		do listen_client;
		
		msg <- get_from_client (cID::id) ;
		if(msg!=nil and msg != "")
		{
			//write id+"\n \t have sent :"+msg;			
			list<string> infos<- msg split_with":";
			//String of type connexion:pseudo:color
			if(length(infos)>0)
			{
				if(infos[0]="connexion")
				{
					create user number:1 returns: theUsers
					{
						pseudo<-infos[1];
						create ship number:1 returns: theShip
						{
							my_user<-myself;
							color<-"purple";
							location<-{50,50};
						}
						my_ship<-first(theShip);
						my_ship.client_id<-id;
					}
//					do send_to_client cliendID: id msg: "Ser send to " ;
					do send_to_client cID: id msg: "ship:"+first(theUsers).pseudo+":"+first(theUsers).my_ship.color+":50:../images/user.png:"+first(theUsers).my_ship.size+":"+first(theUsers).my_ship.heading+":"+first(theUsers).my_ship.location.x+":"+first(theUsers).my_ship.location.y+":"+first(theUsers).my_ship.number_bullet;

				}
				else
				{
					//String of type move:pseudo:heading:locationx:locationy
					if (infos[0]="move")
					{
						ask user where(each.pseudo=infos[1])
						{
							ask my_ship
							{
								self.heading<-int(infos[2]);
								self.location<-point([int(infos[3]),int(infos[4])]);
							}
						}
					}
					else
					{
						//String of type shoot:pseudo:direction
						if(infos[0]="shoot")
						{
							ask user where(each.pseudo=infos[1])
							{
								ask my_ship
								{
									do useBullet(infos[2]);
								}
							}
						}
					}
				}
				
			}
			msg<-"";
		}
	}
	action send_client(string id)
	{
		string message_client<-"";
		//For ship, we have ship:user_pseudo:color:life:path_image:size:heading:locationx:locationy:number_bullet
		//write ship as list;
		loop aShip over: ship where(each.client_id!=id)
		{			
			if(message_client!="")
			{
				message_client<-message_client+":";
			}
			message_client<-message_client+"ship:"+aShip.my_user.pseudo+":"+aShip.color+":50:../images/user.png:"+aShip.size+":"+aShip.heading+":"+aShip.location.x+":"+aShip.location.y+":"+aShip.number_bullet;
		}
		//For bullet, we have bullet:location
		loop aBullet over:bullet
		{
			if(message_client!="")
			{
				message_client<-message_client+":";
			}
			message_client<-message_client+"bullet:"+aBullet.id+":"+aBullet.location.x+":"+aBullet.location.y;
		}
		if(message_client!="")
		{
			//write "Sending "+message_client+" \n \t to "+id;
			do send_to_client cID:id msg: message_client;	
			message_client<-"";
		}
	}
	reflex update
	{	
		loop id over:clients
		{
		//			if(is_closed(cID::id)){				 
		//				ask (ship where (each.client_id = id)) { do die;}
		//				return;
		//				//write "Dead                   "+id;
		//			}else{				
			do listening_client(id);
		}

		loop id over: clients
		{
			do send_client(id);
			//			}
		}
	}
}
species user
{
	string pseudo;
	ship my_ship;
	string address;
	string port;
}
species ship skills:[moving]
{
	string state<-"living";
	string path_image<-"../images/user.png";
	int life<-30;
	int number_bullet<-5;
	int size<-20;
	int heading<-90;
	geometry shape<-square(size);
	string color;
	user my_user;
	point location;
	int damage;
	string client_id;
	
	action useBullet(string direction)
	{
		create bullet number:1 
		{
			location<-myself.location;
			heading<-direction="left"?myself.heading-90:myself.heading+90;
			distance_max<-float(myself.damage);
			damage<-myself.damage;
			the_ship<-myself;
		}
	}
	action downLife(int damageReceived)
	{
		self.life<-self.life-damageReceived;
	}
}
species bullet skills:[moving]
{
	point location;
	float speed;
	float distance;
	float distance_max;
	int damage;
	geometry traj_shape;
	int id<-first(bullet where(each.id=max(bullet collect (each.id)))).id+1;
	ship the_ship;
	
	reflex updateBullet
	{
		point oldLocation<-self.location;
		int oldHeading<-heading;
		do move heading: heading speed: 10;
		if(heading!=oldHeading)
		{
			do die;
		}
		distance<-distance+int(topology(world) distance_between [oldLocation,self.location]);
		traj_shape<-line([oldLocation,location]);
		float distanceMin<--1.0;
		ship touched<-nil;
		
		loop aShip over: ship - the_ship
		{
			if(traj_shape intersects aShip.shape)
			{
				float distanceBetween<-topology(world) distance_between [oldLocation, aShip.location];
				if(touched=nil)
				{
					touched<-aShip;
					distanceMin<-distanceBetween;
				}	
				else
				{
					if(distanceMin>distanceBetween)
					{
						distanceMin<-distanceBetween;
						touched<-aShip;
					}
				}
			}
		}
		if(touched!=nil)
		{
			ask touched
			{
				//write myself.damage;
				do downLife(myself.damage);
			}
			do die;
		}
		if(distance_max<distance)
		{
			do die;
		}
	}
}
experiment launchServ type: gui
{
	float minimum_cycle_duration<-0.1#s;
	init
	{
		//create simulation with:[pp::3003];
	}
}