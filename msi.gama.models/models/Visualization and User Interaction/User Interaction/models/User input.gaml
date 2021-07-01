/**
* Name: UserInputdialog_example
* Author: Patrick Taillandier
* Description: Model which shows how to use user_input_dialog 
* Tags: gui
 */

model Confirmdialog_example

global {
	init {
		//2 types of elements can be added: enter (enter a value) and choose (choose a value among a list of possible values)
		map  result <- user_input_dialog("Main title",[enter("Enter a value", 0.0) , choose("Choose a value",string,"value 1", ["value 1","value 2"])]);
		
		write sample(result);
	}
}

experiment UserInputdialog_example type: gui ;