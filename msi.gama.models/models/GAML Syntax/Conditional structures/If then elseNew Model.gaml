/***
* Name: IfthenelseNewModel
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model IfthenelseNewModel

/* Insert your model definition here */

global {
	
	bool condition1 parameter:true init:true;
	bool condition2 parameter:true init:true;
	
	init {
		
		do condional_hello_world;
		
		do bi_conditional_hello_world;
		
		do if_then_hello_world_else_goodbye;
		
		do if_then_tic_elseif_then_tac_else_toc;
		
		do tertiary_hello_world;
		
	}
	
	action hello_world {
		write "Hello world !";
	}
	
	action condional_hello_world {
		if(condition1){
			do hello_world;
		}
	}
		
	action bi_conditional_hello_world {
		if(condition1 and condition2){
			do hello_world;
		}
	}
	
	action if_then_hello_world_else_goodbye {
		if(condition1) {
			do hello_world;
		} else {
			write "Goodbye";
		}
	}
	
	action if_then_tic_elseif_then_tac_else_toc {
		if(condition1) {
			write "tic !";
		} else if (condition2) {
			write "tac !";
		} else {
			write "toc !";
		}
	}
	
	action tertiary_hello_world {
		write condition1 ? "Hello world !" : "";
		write condition1 and condition2 ? "Hello world !" : "";
		write condition1 ? "Hello world !" : "Goodbye";
		write condition1 ? "tic !" : (condition2 ? "tac !" : "toc !");
	}
		
}

experiment my_xp type:gui {
	output {
		
	}
}
