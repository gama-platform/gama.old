/**
* Name: User Ship
* Author: damienphilippon
* Description: This model must be used with an other instance of Gama having the Server Ship model launched. It shows 
* 	how to use the skill network to produce a client-server architecture. The client model has user interaction, which are
* 	sent to the server model to change the location of the ship, or to launch bullets. 
* Tags: User Interaction, Network
*/

model client


global {	
	//The network agent representing the client of the User that will send messages to the server
	NetworkingAgent theUser;
	//The shapefile of the bounds, to set the size of the environment
	file shp_bounds<-shape_file("./includes/bounds.shp");
	//The shape of the world, initially set to the envelope of the bounds shapefile
	geometry shape<-envelope(shp_bounds);
	//The wind vector, representing as a vector of two values : the direction (in degree) and its intensity
	point wind;
	//The pseudo of the user, used to identify the client by the server
	string pseudo<-"myPseudo2";
	
	init
	{
		//Creation of the networking agent, considered as the client
		create NetworkingAgent number:1
		{
			//We set the name of the client equals to the pseudo of the user
			name<-pseudo;
			//We connect the client to the server written IP Adress, with the server port, and the type of protocol (must be the same that server but with client, like x_client and x_server)
			do connect to:"127.0.0.1" protocol:"tcp_client" port:"3001" with_name:pseudo;
			theUser<-self;
			//Set the message to send to the server equals to 'Connexion:pseudo' to say to the server  that we are a new client connected
			myMessage<-"Connexion:"+pseudo;
		}
		//Creation of the ship managed by the client
		create ship number:1
		{
			//We set the identifiant of the ship equals to the name of the client (also equals to the pseudo)
			name<-theUser.name;
			//We then set the initial location (before being set by the server) to 3,3
			location<-point(3,3);
		}
	}
	//Action launched when the key to go up is pressed, and add it to the message that will be sent to the server to compute locution
	action doUp//(point mLUseless, list<agent> sAUseless)
	{
		theUser.myMessage<-theUser.myMessage+":up";
	}
	
	//Action launched when the key to go left is pressed, and add it to the message that will be sent to the server to compute locution
	action doLeft//(point mLUseless, list<agent> sAUseless)
	{
		theUser.myMessage<-theUser.myMessage+":left";
	}
	
	//Action launched when the key to go right is pressed, and add it to the message that will be sent to the server to compute locution
	action doRight//(point mLUseless, list<agent> sAUseless)
	{
		theUser.myMessage<-theUser.myMessage+":right";
	}
	
	//Action launched when the key to fire to the left is pressed, and add it to the message that will be sent to the server to compute locution
	action doBulletLeft//(point mLUseless, list<agent> sAUseless)
	{
		theUser.myMessage<-theUser.myMessage+":bulletleft";
	}
	
	//Action launched when the key to fire to the right is pressed, and add it to the message that will be sent to the server to compute locution
	action doBulletRight//(point mLUseless, list<agent> sAUseless)
	{
		theUser.myMessage<-theUser.myMessage+":bulletright";
	}
}
//Species representing the client that will interacts with the server, using the skill network
species NetworkingAgent skills:[network]{
	//Name of the client, used to be identified by the server
	string name;
	//Message that will be sent to the server
	string myMessage<-"";

	//Reflex to read the message of the server and process its content
	reflex readMessage
	{	
		//Get the map of all the messages sent to the client
		list mess <- mailbox collect each.contents;
		//If we have messages then we can try to process them
		if(mess != nil and length(mess)>0){
			//We get the first string sent by the server and we know that the server send only one string but equals to something like 'Server:value1:value2:value3'
			string infoMess<- mess[0];
			
			//As we have a string like 'Server:value1:value2:value3', we split the string using ':' to get a list of values
			list<string> dataMess <- infoMess split_with ":";
			//If we have a lot of values, then the process can start
			if(length(dataMess)>0)
			{
				//First of all, we know that the wind is sent at the second and third position like 'Server:windValue1:windValue2" so we set/update the wind
				wind<-point(float(dataMess[1]),float(dataMess[2]));
				//We don't need those two first values as they appears only one, but we need to loop over all the remaining values to get ship and bullet information
				int i<-3;
				//We loop until we reached the end of the map
				loop while: i <= length(dataMess)-1
				{
					//If wen encouter ship, then we process it knowing how a ship string is built (ship:pseudo:x:y:heading:life:status)
					if(dataMess[i]="ship")
					{
						//We first get the ship concerned by this information by getting the ship having the same name
						ship concernedShip<-first(ship where(each.name=dataMess[i+1]));
						//If there is none, we create one, because it means an other client or a bot has been connected
						if(concernedShip=nil)
						{
							//We finally fulfill its attributes using the rest of the values
							create ship number: 1
							{
								//We set the name equals to the string after 'ship' (as the string is ship:pseudo:x:y:heading:life:status)
								name<-dataMess[i+1];
								//We set the location x equals to the string after the name and the y equals to the string after the x and both are casted to float
								location<-point(float(dataMess[i+2]),float(dataMess[i+3]),0.0);
								//We set the heading equals to the string after the y of the ship
								heading<-int(dataMess[i+4]);
								//We set the life equals to the string after the heading of the ship
								life<-int(dataMess[i+5]);
								//We set the status (dead or not) equals to the string after the life of the ship
								dead<-bool(dataMess[i+6]);
							}
						}
						//If we already know the ship, then we just update its attributes
						else
						{
							//We set the location x equals to the string after the name and the y equals to the string after the x and both are casted to float
							concernedShip.location<-point(float(dataMess[i+2]),float(dataMess[i+3]),0.0);
							//We set the heading equals to the string after the y of the ship
							concernedShip.heading<-int(dataMess[i+4]);
							//We set the life equals to the string after the heading of the ship
							concernedShip.life<-int(dataMess[i+5]);
							//We set the status (dead or not) equals to the string after the life of the ship
							concernedShip.dead<-bool(dataMess[i+6]);
						}
						//We add 7 to the index to process the next type of agent (ship,bullet or another)  because there are 7 string used for the setting of the ship
						i<-i+7;
					}
					else
					{
						//In the case the string is not "ship" but "bullet", it means the server want us to process a bullet agent string (built as bullet:idB:x:y:status)
						if(dataMess[i]="bullet")
						{
							//As for the ship, we try to find if the bullet sent is already existing or not by trying to find it by its id
							bullet concernedBullet<-first(bullet where(each.idB=dataMess[i+1]));
							
							//If there is no bullet with this id, we create a new one
							if(concernedBullet=nil)
							{
								//Create a bullet with the string 'bullet:id:x:y:status'
								create bullet number:1
								{
									//We set the identifiant equals to the string after 'bullet'
									idB<-dataMess[i+1];
									//We set the x location equals to the string after the id, the y location equals to the string after the x location, and cast them in float
									location<-point(float(dataMess[i+2]),float(dataMess[i+3]),0.0);
									//We set the status (dead or not) equals to the string after the y location
									concernedBullet.dead<-bool(dataMess[i+4]);
								}
							}
							//If there is already one, we update its attributes
							else
							{
								//We set the x location equals to the string after the id, the y location equals to the string after the x location, and cast them in float
								concernedBullet.location<-point(float(dataMess[i+2]),float(dataMess[i+3]),0.0);
								//We set the status (dead or not) equals to the string after the y location
								concernedBullet.dead<-bool(dataMess[i+4]);
							}
							//We add 5 to the index to process the next type of agent (ship, bullet or another) because there are 5 string used for the setting of the bullet
							i<-i+5;
						}
						//if it is neither a ship or a bullet, we simply write it in the console
						else
						{
							//We write the message
							write dataMess[i+1];
							//We add 2 to the index to process the next type of agent (ship, bullet or another)
							i<-i+2;
						}
					}
				}
			}
		}
	}
	//Reflex to send a message to the server
	reflex send
	{
		//It will only send the message if there are changes, if none, we don't send it, it will avoid overload of the server
		if(myMessage!=name)
		{
			//We finally send the message to the server
			do send to:"Server" contents:myMessage;
		}
		//We reinitialize the message to the name of the client
		myMessage<-name;
	}
}
//Species that represent the ship species
species ship
{
	//Name / Id of the ship
	string name;
	//Location of the ship
	point location;
	//Heading or direction angle of the ship
	int heading;
	//Life of the ship
	int life;
	//Status to determine if the ship is dead or not
	bool dead;
	//The image of the ship that will be displayed (useful for the death animation)
	image_file myImage<-image_file("./images/user.png");
	//The Index of the animation frame (useful for the death animation too)
	int deadIndex<-0;
	
	//Reflex to update the status of the ship
	reflex updateDead
	{
		//If the ship is dead, then we change it's image to another, representing a ship sinking slowly
		if(dead)
		{
			//We increase the index frame of the animation
			deadIndex<-deadIndex+1;
			//We change the image
			myImage<-image_file("./images/dying"+deadIndex+".png");
			//If the deadIndex is reached, which means the animation ended, we kill the agent
			if(deadIndex>6)
			{
				do die;
			}
		}
	}
	
	//Aspect of the ship
	aspect default
	{
		//Draw the concerned image at the location of the ship, a little bit higher of the sea level, and we rotate it according to the direction angle of the ship (heading)
		draw myImage at: {location.x,location.y,0.1} size:50#m rotate: heading;

		//If the name of the ship is equals to the pseudo,  then it will have it's GUI Information displayed (the wind direction, force, it's life and the map)
		if(name=pseudo)
		{
			//Draw the name of the agent at the location of the agent with the gold color
			draw name at:{location.x,location.y,0.1} color:#gold;
			//Draw the life of the agent in the "Indicators" information area
			draw rectangle(life,16) color:#green at:{location.x-(250-(life)),location.y+120,0.1};
			//Draw the wind direction in the "Indicators" information area 
			draw image_file("./images/arrow.png") size:30 rotate:(wind.x+180)mod 360 at: {location.x-250,location.y+175, 1};
			//Draw the wind power in the "Indicators" information area 
			draw rectangle((wind.y/100)*200,16) color:wind.y>50?rgb(255,125-(((wind.y-50)/50)*125),0):rgb(255*(wind.y/50),255-((wind.y/50)*125),0) at: {(location.x-200)+((wind.y/100)*100),location.y+170,1};
			//Draw the map in the "Indicators" information area
			draw image_file("./images/mer.jpg") size:{world.shape.width/(world.shape.width/100),world.shape.height/(world.shape.height/100)} at: {location.x-320,location.y+135, 1};
			//We compute all the virtual location of the ship to display them on the map in the 'Indicators' information area
			loop aShip over: ship
			{
				draw circle(2) color:aShip=self?#gold:#red at: {(aShip.location.x/(world.shape.width/100)) +(location.x-370), (aShip.location.y/(world.shape.height/100))+location.y+85,1};
			}
		}
		//If it is not the ship controlled by the user, then its name and life are simply displayed at its location
		else
		{
			//Draw the name at the location of the agent with the silver color
			draw name at:{location.x,location.y,0.1} color:#silver;
			//Draw the life of the ship at its location
			draw rectangle(life/2,4) color:#green at:{location.x,location.y,0.1};
		}
	}
}
//Species which represent the bullet launched by the ships
species bullet
{
	//Location of the bullet
	point location;
	//Heading of the agent
	int heading;
	//Identifiant of the agent
	string idB;
	//Status of the agent (dead or not)
	bool dead;
	//Image of the bullet
	image_file myImage<-image_file("./images/bullet.png");
	
	//Reflex to kill the agent if its status is dead
	reflex updateDead when:dead
	{
		do die;
	}
	//Aspect of the bullet agent, which apply a rotation angle equals to heading for the image
	aspect default
	{
		draw image_file("./images/bullet.png") at: location size:10#m rotate: heading;
	}
}

//Experiment of the client model
experiment launchClient type: gui {
	//Minimum cycle duration set to .1 second, for a good mix between fast animation and slow speed for receiving messages
	float minimum_cycle_duration<-0.10#s;
	output {
		//Set the camera of the display equals to the location of the ship of the user
		display Client type: opengl camera_interaction:false camera_pos:{int(first(ship where(each.name=pseudo)).location.x),int(first(ship where(each.name=pseudo)).location.y),650} 
		camera_look_pos:{int(first(ship where(each.name=pseudo)).location.x),(first(ship where(each.name=pseudo)).location.y),0} camera_up_vector:{0.0,-1.0,0.0}
		{
			//Set the image of the sea
			image "./images/mer.jpg" position:{0,0,-0.1};
			species ship aspect: default ;
			species bullet aspect: default;
			
			//The different controls allowed for the user, having event[y] means the action will be launched when the y key is pressed
			/*
			event["w"] action: doUp;
			event["a"] action: doLeft;
			event["d"] action: doRight;
			event["q"] action: doBulletLeft;
			event["e"] action: doBulletRight;
			*/
			event["8"] action: doUp;
			event["4"] action: doLeft;
			event["6"] action: doRight;
			event["7"] action: doBulletLeft;
			event["9"] action: doBulletRight;
		}
	}
}
