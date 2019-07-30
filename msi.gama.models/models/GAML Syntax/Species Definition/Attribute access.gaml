/***
* Name: Attribute Access
* Author: Benoit Gaudou
* Description: Model on accessing agents' attributes and which attributes are present when loading shape files.
* Built-in attibutes are also presented.
* Tags: GAML, attribute, access
***/


model AttributeAccess

global {

	shape_file buildings_simple0_shape_file <- shape_file("includes/buildings_simple.shp");

	people my_people;
	people_moving my_moving_people;
		
	init {
		create people returns: list_people;
		my_people <- first(list_people);
		
		create people_moving returns: list_people_moving;
		my_moving_people <- first(list_people_moving);
		
		// access_attribute the various ways to access to agents' attributes.
		do access_attribute;
		
		// Agents have several built-in attributes
		do built_in_attributes;

		// Agents with a skill have even more attributes 		
		do built_in_attributes_with_species;
		
		// Agents attirbutes can get attributes value from a shapefile 			
		do attributes_from_data;
	}
	
	// 
	action access_attribute {
		write "============== ACCESS ATTRIBUTES ==============";		
		
		// The access to agents' attributes can be done in several ways
		// using the . (dot)
		write sample(my_people.name);
		// or using brackets
		// (in this case the attribute is get through its name as a string
		write sample(my_people["name"]);
		// or the of operators
		write sample(name of my_people);
		
	}
	
	action built_in_attributes {
		write "============== Built-in ATTRIBUTES ==============";		
		
		// Any species has some built-in attributes
		write sample(people.attributes);
		
		// The attributes list can be used to get the value of each of these attributes.
		loop attr_name over: people.attributes {
			write attr_name;
			write "    my_people['"+attr_name+"'] -: " + my_people[attr_name] ; 
			write "    type_of(my_people['"+attr_name+"']) -: " + type_of(my_people[attr_name]);
		}
	}
	
	action attributes_from_data {
		write "============== ATTRIBUTES read from a shapefile ==============";		
		
		// Agents created from a shapefile can initialize attribute values from the shapefile attributes, 
		// either explicity, using the with: facet
		// or implicitely, when the attribute has the same name as the shapefile attribute.
		// The shape of the agent is initialized from the geometry of the shapefile.
		create building_from_shapefile from: buildings_simple0_shape_file with: [building_nature::string(read("NATURE"))];
			
		ask building_from_shapefile {
			write sample(self.building_nature);
			write sample(self.HEIGHT);
			write sample(self.name);
			write sample(self.shape);
		}	
	}
	
	action built_in_attributes_with_species {
		write "============== Built-in ATTRIBUTES for a species with skills ==============";		
		
		// Any species has some built-in attributes
		write sample(people_moving.attributes);
		
		// The attributes list can be used to get the value of each of these attributes.
		loop attr_name over: people_moving.attributes {
			write attr_name;
			write "    my_people['"+attr_name+"'] -: " + my_moving_people[attr_name] ; 
			write "    type_of(my_people['"+attr_name+"']) -: " + type_of(my_moving_people[attr_name]);
		}
	}
	
}

species people {
	string name <- "init_in_species";
}

species building_from_shapefile {
	float HEIGHT;
	string building_nature;
}

species people_moving skills: [moving] {
}

experiment Attributeaccess type: gui { }
