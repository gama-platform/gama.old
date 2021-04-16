/**
* Name: Confirmdialog_example
* Author: Patrick Taillandier
* Description: Model which shows how to use confirm dialog box 
* Tags: gui
 */

model Confirmdialog_example

global {
	init {
		bool  result <- user_confirm("Confirmation dialog box","Do you want to confirm?");
		write sample(result);
	}
}

experiment Confirmdialog_example type: gui ;