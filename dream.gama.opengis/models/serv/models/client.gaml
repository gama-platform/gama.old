/**
* Name: client
* Author: damienphilippon
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model client

/* Insert your model definition here */

global
{
	string pseudo<-"Damien";
	string wanted_color<-"blue";
	string ip_serveur<-"192.168.0.35";
	geometry shape <- square(200);
	int rotation_max<-3;
	init
	{
		create user number:1;
	}
	action moveUp
	{
		ask first(user)
		{
			do sendUp;
		}
	}
	action moveLeft
	{
		ask first(user)
		{
			do sendLeft;
		}
	}
	action moveRight
	{
		ask first(user)
		{
			do sendRight;
		}
	}
	action shootLeft
	{
		ask first(user)
		{
			do useBullet("left");
		}
	}
	action shootRight
	{
		ask first(user)
		{
			do useBullet("right");
		}
	}
}
species bullet skills:[moving]
{
	int id;
	point location;
	aspect default
	{
		draw image_file("../images/bullet.png") size: 3 at: location;
	}
}
species ship skills:[moving]
{
	float size;
	string color;
	int heading;
	point location;
	int numberBullet;
	string pseudo;
	int life;
	string image_path<-"../images/user.png";
	aspect default
	{
		draw image_file(image_path) size: size rotate:heading at: location;
		draw pseudo color: rgb(color) at: {location.x,location.y+size} size:3;
	}
}
species user skills:[socket]
{
	string myClientID<-"";
	string messageS <-"";
	string msg<-"";
	init
	{
		port<-3001;
//		ip<-ip_serveur;
		myClientID<- connect_server();
		do send_to_server msg:"connexion:"+pseudo+":"+wanted_color;
	}
	reflex update
	{
//		write messages[myClientID];
		if(messageS != ""){			
			//write "Sending: "+messageS;
			do send_to_server msg:messageS;
			messageS<-"";
		}
		msg<- listen_server();
		if (msg!=nil and msg!="")
		{
//			//write "Server says: "+msg;
			list<string>infoMessage <- msg split_with ":"; 
			int index<-0;
			loop while: index<length(infoMessage)
			{
				//ship:user_pseudo:color:life:path_image:size:heading:location:number_bullet
				if(infoMessage[index]="ship")
				{
					if(length(ship where(each.pseudo=infoMessage[index+1])) =0)
					{
						create ship number: 1
						{
							self.pseudo<-infoMessage[index+1];
							self.color<-infoMessage[index+2];
							self.life<-int(infoMessage[index+3]);
							self.image_path<-infoMessage[index+4];
							self.size<-float(infoMessage[index+5]);
							self.heading<-int(infoMessage[index+6]);
							self.location<-point([infoMessage[index+7],infoMessage[index+8]]);
							self.numberBullet<-int(infoMessage[index+9]);
						}
					}
					else
					{
						ask first(ship where(each.pseudo=infoMessage[index+1]))
						{
							self.life<-int(infoMessage[index+3]);
							self.size<-float(infoMessage[index+5]);
							self.heading<-int(infoMessage[index+6]);
							self.location<-point([infoMessage[index+7],infoMessage[index+8]]);
							self.numberBullet<-int(infoMessage[index+9]);
						}
					}
					index<-index+8;
				}
				else
				{
					if (infoMessage[index]="bullet")
					{
						if(length(bullet where(each.id=infoMessage[index+1])) =0)
						{
							create bullet number: 1
							{
								self.location<-point([infoMessage[index+2],infoMessage[index+3]]);
							}
						}
						else
						{
							ask first(bullet where(each.id=infoMessage[index+1]))
							{
								self.location<-point([infoMessage[index+2],infoMessage[index+3]]);
							}
						}
						index<-index+2;
					}
				}
				index<-index+1;
			}
			msg<-"";
		}
	}
	action sendMoveMessage
	{
		messageS<-"move:"+pseudo+":"+first(ship where (each.pseudo=pseudo)).heading+":"+first(ship where (each.pseudo=pseudo)).location.x+":"+first(ship where (each.pseudo=pseudo)).location.y;
	}
	action sendFireMessage(string direction)
	{
		messageS<-"shoot:"+pseudo+":"+direction;
	}
	action sendUp
	{
		ask first(ship where (each.pseudo=pseudo))
		{
			do move speed: speed heading: heading;
		}
		do sendMoveMessage;
	}
	action sendLeft
	{
		
		ask first(ship where (each.pseudo=pseudo))
		{
			self.heading<-self.heading-rotation_max;
			do move speed: speed heading: heading;
		}
		do sendMoveMessage;
	}
	action sendRight
	{
		
		ask first(ship where (each.pseudo=pseudo))
		{
			self.heading<-self.heading+rotation_max;
			do move speed: speed heading: heading;
		}
		do sendMoveMessage;
	}
	action useBullet(string direction)
	{
		do sendFireMessage(direction);
	}
}
experiment lClient type:gui
{
	float minimum_cycle_duration<-0.1#s;
	output
	{
		display theWorld type: opengl background:#lightblue
		{
			species ship aspect: default;
			species bullet aspect: default;
			event["w"] action: moveUp;
			event["a"] action: moveLeft;
			event["d"] action: moveRight;
			event["q"] action: shootLeft;
			event["e"] action: shootRight;
		}
	}
}