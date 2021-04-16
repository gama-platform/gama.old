/**
* Name: Wizard_example
* Author: Patrick Taillandier
* Description: Model which shows how to use wizards 
* Tags: gui
 */

model Wizard_example

global {
	init {
		map results <-  wizard("My wizard", eval_finish,
			[ 
			wizard_page("page1","enter info page 1" ,[enter("file" , file), choose("shape", string, "circle", ["circle", "square"])], font("Helvetica", 14 , #bold)),
			wizard_page("page2","enter info page 2" ,[enter("var2",string), enter("to consider", bool, true)], font("Arial", 10 , #bold))
			] 
		);
		write sample(results);  
	}
	
	bool eval_finish(map<string,map> input_map) {
		 return input_map["page1"]["file"] != nil;
	}
}

experiment Wizard_example type: gui ;