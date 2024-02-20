/**
* Name: SerializeAndRestoreAgents
* Shows how to save / read agents using the statements and operators defined in GAMA 
* Author: A. Drogoul
* Tags: serialize, create, restore
*/


model SerializeAndRestoreAgents



global {
	
	list<string> serialized_people <- [];
	
	int number_of_people <- 100;
	
	init {
		create people number: number_of_people;
		ask people {do create_family;}
	}
	
	/**
	 * This action saves all the people on disk, each in its own file, using the uncompressed json format
	 */
	action save_people_on_disk {
		ask people {
			save self to: '../people/save'+int(self)+'.agent' format: 'json';
		}
	}
	
	/**
	 * This action saves all the people in memory, each in its own string, using the binary format
	 */
	action save_people_in_memory {
		serialized_people <- [];
		ask people {
			serialized_people << serialize(self);
		}
		write serialized_people;
	}
	
	/**
	 * This action asks people to "restore" themselves using a random file, effectively replacing their attributes by the one of the agent saved
	 */
	action exchange_people_from_disk {
		if folder_exists("../people/") and !empty(folder("../people")) {
			ask people {
				restore self from: string(folder("../people"))+'/save'+rnd(number_of_people - 1)+'.agent';
			}
		}
	}
	
	/**
	 * This action asks people to "restore" themselves using a random string, effectively replacing their attributes by the one of the agent saved
	 */
	action exchange_people_from_memory {
		ask people {
			restore self from: any(serialized_people);
		}
		serialized_people <- [];
	}
	
	/**
	 * This action creates new people using the previously saved strings
	 */
	action create_clones_from_memory {
		loop saved over: serialized_people {
			create people from: saved;
		}
	}
	
	action create_clones_from_disk {
		loop saved over: folder("../people") {
			create people from: file(string(folder("../people"))+"/"+saved);
		}
	}

	
}

species people {
	list<people> family;
	
	action create_family {
		family <- rnd(5) among people;
	}
	
}


experiment "Save, Restore, Create and Clone" {
	category "Saving" color: #green;
	text "These actions save all the people on disk or in memory, each in its own file/string, using the uncompressed json format" color: #green category: "Saving";
	user_command "Save on disk" color: #green category: "Saving" { ask simulation { do save_people_on_disk;}}
	user_command "Save in memory" color: #green category: "Saving" { ask simulation { do save_people_in_memory;}}
	category "Restoring existing agents" color: #orange;
	text "These actions ask people to 'restore' themselves using a random file/string, effectively replacing their attributes by the ones of the agent saved" color: #orange category: "Restoring existing agents";
	user_command "Exchange from disk" color: #orange category: "Restoring existing agents" { ask simulation { do exchange_people_from_disk;}}
	user_command "Exchange in memory" color: #orange category: "Restoring existing agents" { ask simulation { do exchange_people_from_memory;}}
	category "Creating new agents" color: #red;
	text "These actions create new people from the files/strings of the previously saved agents" color: #red category: "Creating new agents";
	user_command "Create clones from disk" color: #red category: "Creating new agents" { ask simulation { do create_clones_from_disk;}}	
	user_command "Create clones from memory" color: #red category: "Creating new agents" { ask simulation { do create_clones_from_memory;}}

	
	output {
		browse people;
	}
}
