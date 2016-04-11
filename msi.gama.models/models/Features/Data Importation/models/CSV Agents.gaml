/**
* Name: CSV to Agents Model
* Author:  Patrick Taillandier
* Description: Model which shows how to create agents by importing data of a CSV file. The model read the CSV File and create an agent Iris for each line of the CSV, 
* 	linking its attributes to columns of the CSV File. 
* Tags: csv, load_file
*/

model CSVfileloading

global {
	
	init {
		//create iris agents from the CSV file (use of the header of the CSV file), the attributes of the agents are initialized from the CSV files: 
		//we set the header facet to true to directly read the values corresponding to the right column. If the header was set to false, we could use the index of the columns to initialize the agent attributes
		create iris from:csv_file( "../includes/iris.csv",true) with:
			[sepal_length::float(get("sepallength")), 
				sepal_width::float(get("sepalwidth")), 
				petal_length::float(get("petallength")),
				petal_width::float(get("petalwidth")), 
				type::string(get("type"))
			];	
	}
}

species iris {
	float sepal_length;
	float sepal_width;
	float petal_length;
	float petal_width;
	string type;
	rgb color ;
	
	init {
		color <- type ="Iris-setosa" ? #blue : ((type ="Iris-virginica") ? #red: #yellow);
	}
	
	aspect default {
		draw circle(petal_width) color: color; 
	}
}

experiment main type: gui{
	output {
		display map {
			species iris;
		}
	}
	
}
