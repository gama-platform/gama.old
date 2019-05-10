/***
* Name: IfthenelseNewModel
* Author: ben
* Description: gives several examples of if then else statements. Also show how to use tertiary operator: 'condition ? then : else'
* Tags: if, else, ?
***/

model IfthenelseNewModel

/* Insert your model definition here */

global {
	
	bool condition1 parameter:true init:true;
	bool condition2 parameter:true init:true;
	
	init {
		
		do condional_hello_world;
		
		do bi_conditional_hello_world;
		
		do or_conditional_hello_world;
		
		do if_then_hello_world_else_goodbye;
		
		do if_then_tic_elseif_then_tac_else_toc;
		
		do tertiary_hello_world;
		
	}
	
	action condional_hello_world {
		write "------------------------";
		write "Test : if condition1 is true then print 'hello world'";
		string result;
		if(condition1){
			result <- "Hello world !";
		}
		write "Result : "+result;
	}
		
	action bi_conditional_hello_world {
		write "------------------------";
		write "Test : if condition1 and condition2 are true then print 'hello world'";
		string result;
		if(condition1 and condition2){
			result <- "Hello world !";
		}
		write "Result : "+result;
	}
	
	action or_conditional_hello_world {
		write "------------------------";
		write "Test : if condition1 or condition2 are true then print 'hello world'";
		string result;
		if(condition1 or condition2){
			result <- "Hello world !";
		}
		write "Result : "+result;
	}
	
	action if_then_hello_world_else_goodbye {
		write "------------------------";
		write "Test : if condition1 is true then print 'hello world' else print 'Goodbye'";
		string result;
		if(condition1) {
			result <- "Hello world !";
		} else {
			result <- "Goodbye";
		}
		write "Result : "+result;
	}
	
	action if_then_tic_elseif_then_tac_else_toc {
		write "------------------------";
		write "Test : if condition1 is true then print 'tic !' else if condition2 is true then print 'tac !' else print 'toc !'";
		string result;
		if(condition1) {
			result <- "tic !";
		} else if (condition2) {
			result <- "tac !";
		} else {
			result <- "toc !";
		}
		write "Result : "+result;
	}
	
	action tertiary_hello_world {
		write "------------------------";
		write "Test tertiary operator (condition ? then : else)";
		write "If condition1 true print 'Hello world !'";
		string result1 <- condition1 ? "Hello world !" : "";
		write "Result : "+result1;
		write "If condition1 and condition2 are true print 'Hello world !'";
		string result2 <- condition1 and condition2 ? "Hello world !" : ""; 
		write "Result : "+result2;
		write "If condition1 true print 'Hello world !' else print 'Goodbye'";
		string result3 <- condition1 ? "Hello world !" : "Goodbye"; 
		write "Result : "+result3;
		write "If condition1 true print 'tic !' else if condition2 is true print 'tac !' else print 'toc !'";
		string result4 <- condition1 ? "tic !" : (condition2 ? "tac !" : "toc !");
		write "Result : "+result4;
	}
		
}

experiment my_xp type:gui {
	output {
		
	}
}
