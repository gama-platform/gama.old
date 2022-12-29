/***
* Name: GUI Interactive Elements
* Author: Benoit Gaudou/Alexis Drogoul
* Description: This model illustrates the various possible interactive elements that can be used in the parameters pane.
* Tags: experiment, GUI, parameter
***/

model GUIInteractiveElements


global {
	// The various global variables can be used as parameter of an experiment.
	// In the simulation parameter view, their display can depend on their type or on other facets (e.g. min and max).

	// Definition of a set of global parameters of several types.
	int an_integer_without_limits <- 0;
	int an_integer <-0;
	float a_float_variable <- 0.0;
	float another <- 10.0;	
	bool a_boolean_variable <- true;
	bool a_boolean_variable2 <- true;
	bool a_boolean_variable3 <- true;
	
	string a_string ;
	rgb a_color <- #yellow;
	file a_fil <- shape_file("../../Data/Data Importation/includes/test.shp");
	file a_fil2 <- file("../../Data/Data Importation/includes/hab10.asc");
	list<string> list_of_string <- ["A","B","C"];
	matrix<string> matrix_of_string <- matrix([["R1C1","R2C1"],["R1C2","R2C2"],["R1C3","R2C3"]]);
	
	bool a_boolean_to_disable_parameters <- true;
	pair<string,int> pair_of_string_int <- "B"::2;
	point point_3D <- {12,45,2};

	bool a_boolean_to_enable_parameters <- true;
	string multiple_choice;
	
	float float_on_change <- 1.0;
	
	// Action that will be called from the parameter pane
	action writing_parameters {
		write "Float on change: " + float_on_change;
	}
} 

experiment "Show Parameters" type: gui {
	
	// Variables can also be declared in the experiment
	int an_int_to_build_a_list <- 10;
	string a_string_among_others <- "10";
	int an_int_with_an_updated_slider <- 2;
	
	
	// Texts can be inserted in the parameters pane to explain, for instance, how the model works
	// Category: Explanation	
	////////////////////////////////////////////// 
	category "Explanation" expanded: false color: #green; 
	category "Various types" expanded: false color: #orange;
	category "Monitors" expanded: false color: #red;
	text "This is a simple text using default values. It adapts automatically to the light/dark themes" category: "Explanation";
	text "This is in a different color and a background" color: #white background: #violet category: "Explanation";
	text "This is a text in a different font, in italic and a size of 12" category: "Explanation" color: #orange font: font("Times New Roman",16,#italic); 
	text "Texts are justified on both sides by default. There is no possiblity so far to specify a left, right or no justification" category: "Explanation" color: #lightgray background: #black font: font("Courier New",12); 
	text "This bold light green text \rspans over \r3 lines." category: "Explanation" color: #lightgreen background: #black font: font("Helvetica",12,#bold); 
	
	// Category: Various types	
	//////////////////////////////////////////////
	// When min: and max: facets are used, the input chooser for a numerical value appears as a slider. 
	parameter "An integer with limits" category:"Various types" var: an_integer_without_limits min: 0 max: 100 step: 1;
	// Without these facets, the parameter value chooser appears only as a textfield. 	
	parameter "An integer without limit" category:"Various types" var: an_integer ;
	parameter "Slider of a float" category:"Various types" var: a_float_variable min: 0.0 max: 100.0 step: 0.5;
	parameter "Slider of a float with a color" category:"Various types" var: another min: 0.0 max: 100.0 step: 0.5 colors: [#red, #purple, #blue];
	// When a variable of type boolean is a parameter, its input chooser appears as a check box.
	parameter "Boolean variable" category:"Various types" var: a_boolean_variable;
	parameter "Boolean variable with different colors" category:"Various types" var: a_boolean_variable2 colors: [#blue, #lightskyblue];
	parameter "Boolean variable with the same colors" category:"Various types" var: a_boolean_variable3 colors: [#orange];
	// A color parameter can be modified using a color chooser.
	parameter "A color" category:"Various types" var: a_color;
	// For any parameter, if the possible values are described using the among: facet, a ComboBox is used to choose the parameter value.
	parameter "Choice Box 1" category:"Various types" var: a_string <- "choice1" among: ["choice1","choice2","choice3"];
	// For a file variable, a FileChooser is used to choose the file. 
	parameter "File chooser" category:"Files" var: a_fil ; 
	parameter "File Chooser (only .asc and .shp)" category: "Files" var: a_fil2 extensions: ["asc","shp"] in_workspace: true;
	// For both lists and matrices, an list/matrix modifier can be opened to modify the list or matrix.
	parameter "List parameter" category:"Various types" var: list_of_string ; 
	parameter "Matrix parameter" category:"Various types" var: matrix_of_string ; 
	
	// Category: interactive disable
	//////////////////////////////////////////////
	// When the parameter corresponds to a boolean attribute, the facet disables: (resp. enables:) can be added to 
	// disable (resp. enable) the edition of some parameters (listed as a list of global variables).
	// In the following, when a_boolean_to_disable_parameters is true, this disables the input chooser for point_3D
	// and pair_of_string_int parameters.
	parameter "Disable following parameters" category:"Disable parameters" var: a_boolean_to_disable_parameters disables: [point_3D,pair_of_string_int];		
	parameter "Pair parameter" category: "Disable parameters" var: pair_of_string_int ; 
	parameter "Point parameter" category: "Disable parameters" var: point_3D;
	
	// Category: interactive enable
	//////////////////////////////////////////////
	// In the following, when a_boolean_to_enable_parameters is true, it enables the input chooser for multiple_choice.
	parameter "Activate the following parameter" category:"Enable parameters" var:a_boolean_to_enable_parameters enables: [multiple_choice];
	parameter "Choice Box 2" category:"Activable parameters" var: multiple_choice <- "choice1" among: ["choice1","choice2","choice3"];
	
	// Category: interactive update
	//////////////////////////////////////////////
	parameter "Update the among and max values of next parameters" category: "Update parameters" var: an_int_to_build_a_list min: 1 updates: [a_string_among_others, an_int_with_an_updated_slider];
	parameter "A string to choose among updated values" category: "Update parameters" var: a_string_among_others among: (0 to an_int_to_build_a_list) collect string(each);
	parameter "An int with an updated slider" slider: true category: "Update parameters" var: an_int_with_an_updated_slider min: 0 max: an_int_to_build_a_list;
	
	// Category: interaction through button	
	//////////////////////////////////////////////
	// Any input chooser can have the on_change: facet in order to call a set of statements (or a single action) every time the associated 
	// parameter is modified.
	parameter "Float (with on_change listener)" category:"Interactive" var: float_on_change {write ""+float_on_change;}
	// A user_command adds a button to the interface in order to call an action or a set of statements when it is clicked.
	user_command "Display parameter" category: "Interactive" color:#darkblue {ask world {do writing_parameters;}}
	user_command "Light color for commands" category: "Interactive" color:#lightgray {ask world {do writing_parameters;}}
	user_command "Default color for commands" category: "Interactive" {ask world {do writing_parameters;}}
	text "Monitors can now be added to the parameters view (see Preferences>Interface)" category: "Monitors" color: #gray font: font("Helvetica",14, #bold);

	
	output {
		monitor "A simple random monitor" value: rnd(100) color: #red;
		monitor "A monitor with the color of the simulation" value: 1000 + rnd(10) ;
		monitor "A monitor on an agent" value: simulation color: #lightgray;
	}
}